package se.kth.ict.id2203.assignment4;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import se.kth.ict.id2203.application.Application0Init;
import se.kth.ict.id2203.application.ApplicationContinue;
import se.kth.ict.id2203.assignment4.paxos.PaxosPort;
import se.kth.ict.id2203.assignment4.paxos.UcDecide;
import se.kth.ict.id2203.assignment4.paxos.UcPropose;
import se.kth.ict.id2203.console.Console;
import se.kth.ict.id2203.console.ConsoleLine;
import se.sics.kompics.ComponentDefinition;
import se.sics.kompics.Handler;
import se.sics.kompics.Kompics;
import se.sics.kompics.Positive;
import se.sics.kompics.Start;
import se.sics.kompics.timer.ScheduleTimeout;
import se.sics.kompics.timer.Timer;

public class PaxosApplication extends ComponentDefinition{
	Positive<Timer> timer = requires(Timer.class);
	Positive<Console> con = requires(Console.class);
	Positive<PaxosPort> paxos = requires(PaxosPort.class);

	private static final Logger logger = LoggerFactory.getLogger(PaxosApplication.class);

	private List<String> commands;
	private Map<Integer, Integer> decisions;
	private Map<Integer, Integer> ongoingProp;
	private boolean blocking;
	private boolean d;
	private int sleep;

	public PaxosApplication(){

	}

	Handler<UcDecide> handleUcDecide = new Handler<UcDecide>() {

		@Override
		public void handle(UcDecide event) {
			int instanceId = event.getId();
			decisions.put(instanceId, event.getVal());
			ongoingProp.remove(instanceId);
			checkD();

		}
	};

	Handler<Application0Init> handleInit = new Handler<Application0Init>() {
		public void handle(Application0Init event) {
			decisions = new HashMap<Integer, Integer>();
			ongoingProp = new HashMap<Integer, Integer>();
			commands = new ArrayList<String>(Arrays.asList(event.getCommandScript().split(":")));
			for (String cmd : commands) {
				System.out.println("cmd: " + cmd);
			}
			commands.add("$DONE");
			logger.info("Initialization done");
			blocking = false;
			sleep = 0;
			d = true;
		}
	};

	Handler<Start> handleStart = new Handler<Start>() {
		public void handle(Start event) {
			doNextCommand();
		}
	};

	Handler<ApplicationContinue> handleContinue = new Handler<ApplicationContinue>() {
		public void handle(ApplicationContinue event) {
			logger.info("Woke up from sleep");
			blocking = false;
			doNextCommand();
		}
	};

	Handler<ConsoleLine> handleConsoleInput = new Handler<ConsoleLine>() {
		@Override
		public void handle(ConsoleLine event) {
			commands.addAll(Arrays.asList(event.getLine().trim().split(":")));
			doNextCommand();
		}
	};

	private final void doNextCommand() {
		while (!blocking && !commands.isEmpty()) {
			doCommand(commands.remove(0));
		}
	}

	private void doCommand(String cmd) {
		if (cmd.startsWith("D")) {
			blocking = true;
			if(ongoingProp.isEmpty()){
				logger.info("No previous proposals, going to sleep");
				doSleep(Integer.parseInt(cmd.substring(1)));
			}else{
				logger.info("Waiting for decisions for all previous proposals");
				d = true;
				sleep = Integer.parseInt(cmd.substring(1));
			}
		} else if (cmd.startsWith("P")) {
			// Proposing a value in a consensus instance
			List<String> pCmd = new ArrayList<String>(Arrays.asList(cmd.split("-")));
			int instanceId = Integer.parseInt(pCmd.get(0).substring(1));
			int value = Integer.parseInt(pCmd.get(1));
			ongoingProp.put(instanceId, value);
			logger.info("Proposing value "+value+" for instance "+instanceId);
			trigger(new UcPropose(instanceId, value), paxos);
			
		} else if (cmd.startsWith("W")) {
			// write decisions received thus far
			for(Integer id : decisions.keySet()){
				logger.info("DECISION - consensus instance: "+id+"\tdecided value: "+decisions.get(id));
			}
			decisions.clear();
		} else if (cmd.startsWith("X")) {
			doShutdown();
		} else if (cmd.equals("$DONE")) {
			logger.info("DONE ALL OPERATIONS");
		} else {
			logger.info("Bad command.", cmd);
		}
	}

	private void checkD(){
		if ( d ){
			boolean cont = true;
			for(int proposedId : ongoingProp.keySet()){
				if (!decisions.containsKey(proposedId)){
					cont= false;
					break;
				}
			}
			if(cont){
				logger.info("Got decisions for all previous proposals, going to sleep");
				ongoingProp.clear();
				d=false;
				doSleep(sleep);
			}

		}
	}

	private void doSleep(long delay) {
		logger.info("Sleeping {} milliseconds...", delay);

		ScheduleTimeout st = new ScheduleTimeout(delay);
		st.setTimeoutEvent(new ApplicationContinue(st));
		trigger(st, timer);

		blocking = true;
	}

	private void doShutdown() {
		System.out.println("2DIE");
		System.out.close();
		System.err.close();
		Kompics.shutdown();
		blocking = true;
	}


}
