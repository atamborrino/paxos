package se.kth.ict.id2203.assignment4;

import java.util.Arrays;
import java.util.List;
import java.util.Set;

import org.apache.log4j.PropertyConfigurator;

import se.kth.ict.id2203.application.Application0Init;
import se.kth.ict.id2203.assignment3.beb.BebBroadcastComponent;
import se.kth.ict.id2203.assignment3.beb.BebBroadcastInit;
import se.kth.ict.id2203.assignment3.beb.BebBroadcastPort;
import se.kth.ict.id2203.assignment4.ac.AcComponent;
import se.kth.ict.id2203.assignment4.ac.AcInit;
import se.kth.ict.id2203.assignment4.ac.AcPort;
import se.kth.ict.id2203.assignment4.eld.EldComponent;
import se.kth.ict.id2203.assignment4.eld.EldInit;
import se.kth.ict.id2203.assignment4.eld.EldPort;
import se.kth.ict.id2203.assignment4.paxos.PaxosComponent;
import se.kth.ict.id2203.assignment4.paxos.PaxosInit;
import se.kth.ict.id2203.assignment4.paxos.PaxosPort;
import se.kth.ict.id2203.console.Console;
import se.kth.ict.id2203.console.java.JavaConsole;
import se.kth.ict.id2203.pp2p.PerfectPointToPointLink;
import se.kth.ict.id2203.pp2p.delay.DelayLink;
import se.kth.ict.id2203.pp2p.delay.DelayLinkInit;
import se.sics.kompics.Component;
import se.sics.kompics.ComponentDefinition;
import se.sics.kompics.Fault;
import se.sics.kompics.Handler;
import se.sics.kompics.Kompics;
import se.sics.kompics.address.Address;
import se.sics.kompics.launch.Topology;
import se.sics.kompics.network.Network;
import se.sics.kompics.network.mina.MinaNetwork;
import se.sics.kompics.network.mina.MinaNetworkInit;
import se.sics.kompics.timer.Timer;
import se.sics.kompics.timer.java.JavaTimer;

public class PaxosMain extends ComponentDefinition {

	static {
		PropertyConfigurator.configureAndWatch("log4j.properties");
	}
	private static int selfId;
	private static String commandScript;

	Topology topology = Topology.load(System.getProperty("topology"), selfId);

	public PaxosMain() {
		// create components
		Component time = create(JavaTimer.class);
		Component network = create(MinaNetwork.class);
		Component con = create(JavaConsole.class);
		Component pp2p = create(DelayLink.class);
		Component beb = create(BebBroadcastComponent.class);
		Component eld = create(EldComponent.class);
		Component ac = create(AcComponent.class);
		Component paxos = create(PaxosComponent.class);
		Component app = create(PaxosApplication.class);

		// handle faults
		subscribe(handleFault, time.control());
		subscribe(handleFault, network.control());
		subscribe(handleFault, con.control());
		subscribe(handleFault, pp2p.control());
		subscribe(handleFault, beb.control());
		subscribe(handleFault, eld.control());
		subscribe(handleFault, ac.control());
		subscribe(handleFault, paxos.control());
		subscribe(handleFault, app.control());

		// initialize the components
		Address self = topology.getSelfAddress();
		Set<Address> neighborSet = topology.getNeighbors(self);
		int timeDelay = 4000;
		int delta = 2000;

		trigger(new MinaNetworkInit(self, 5), network.control());
		trigger(new DelayLinkInit(topology), pp2p.control());
		trigger(new BebBroadcastInit(self, neighborSet), beb.control());
		trigger(new EldInit(neighborSet, self, timeDelay, delta),eld.control());
		trigger(new AcInit(neighborSet.size() + 1, self.getId(), self), ac.control());
		trigger(new PaxosInit(self), paxos.control());
		trigger(new Application0Init(commandScript, neighborSet, self), app.control());

		// connect the components
		connect(app.required(Console.class), con.provided(Console.class));
		connect(app.required(PaxosPort.class), paxos.provided(PaxosPort.class));
		connect(app.required(Timer.class), time.provided(Timer.class));

		connect(paxos.required(BebBroadcastPort.class), beb.provided(BebBroadcastPort.class));
		connect(paxos.required(AcPort.class), ac.provided(AcPort.class));
		connect(paxos.required(EldPort.class), eld.provided(EldPort.class));

		connect(ac.required(PerfectPointToPointLink.class), pp2p.provided(PerfectPointToPointLink.class));
		connect(ac.required(BebBroadcastPort.class), beb.provided(BebBroadcastPort.class));

		connect(beb.required(PerfectPointToPointLink.class), pp2p.provided(PerfectPointToPointLink.class));

		connect(eld.required(PerfectPointToPointLink.class), pp2p.provided(PerfectPointToPointLink.class));
		connect(eld.required(Timer.class), time.provided(Timer.class));

		connect(pp2p.required(Timer.class), time.provided(Timer.class));
		connect(pp2p.required(Network.class), network.provided(Network.class));
	}

	public static void main(String[] args) {
		selfId = Integer.parseInt(args[0]);
		commandScript = args[1];
		List<String> arr = Arrays.asList(commandScript.split(":"));

		Kompics.createAndStart(PaxosMain.class);
	}

	Handler<Fault> handleFault = new Handler<Fault>() {
		public void handle(Fault fault) {
			fault.getFault().printStackTrace(System.err);
		}
	};

}
