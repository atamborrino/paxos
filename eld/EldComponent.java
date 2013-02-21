package se.kth.ict.id2203.assignment4.eld;

import java.util.ArrayList;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import se.kth.ict.id2203.assignment4.PaxosApplication;
import se.kth.ict.id2203.console.Console;
import se.kth.ict.id2203.pp2p.PerfectPointToPointLink;
import se.kth.ict.id2203.pp2p.Pp2pSend;
import se.sics.kompics.ComponentDefinition;
import se.sics.kompics.Handler;
import se.sics.kompics.Negative;
import se.sics.kompics.Positive;
import se.sics.kompics.address.Address;
import se.sics.kompics.timer.ScheduleTimeout;
import se.sics.kompics.timer.Timer;

public class EldComponent extends ComponentDefinition{
	
	Positive<Console> con = requires(Console.class);
	Positive<Timer> timer = requires(Timer.class);
	Positive<PerfectPointToPointLink> pp2p = requires(PerfectPointToPointLink.class);
	Negative<EldPort> eld = provides(EldPort.class);
	
	private static final Logger logger = LoggerFactory.getLogger(PaxosApplication.class);
	
	private List<Address> neighbors;
	private Address self;
	
	private int period;
	private int delta;
	
	private List<Address> candidates;
	private Address leader;

	
	public EldComponent(){
		subscribe(handleInit, control);
		subscribe(handleTimeout, timer);
		subscribe(handleHeartbeat, pp2p);
		
	}
	
	Handler<EldInit> handleInit = new Handler<EldInit>() {

		@Override
		public void handle(EldInit event) {
			
			neighbors = new ArrayList<Address>(event.getNeighbors());
			self = event.getSelf();
			period = event.getTimeDelay();
			delta = event.getDelta();
			
			leader = select(neighbors);
			trigger(new EldTrustEvent(leader),eld);
			
			sendHeartbeats(neighbors);
			candidates = new ArrayList<Address>();
			scheduleEldTimeout(period);
			
			logger.info("ELD initialized");
		}
	};
	
	Handler<EldTimeout> handleTimeout = new Handler<EldTimeout>() {

		@Override
		public void handle(EldTimeout event) {
			Address newLeader = select(candidates);
			if((leader.getId() != newLeader.getId()) && (newLeader != null) ){
				period += delta;
				leader = newLeader;
				
				logger.info("ELD -- trusting new leader: node "+leader.getId());
				
				trigger(new EldTrustEvent(newLeader), eld);
			}
			sendHeartbeats(neighbors);
			candidates.clear();
			scheduleEldTimeout(period);
		}
	};
	
	Handler<EldHeartbeatMsg> handleHeartbeat = new Handler<EldHeartbeatMsg>() {

		@Override
		public void handle(EldHeartbeatMsg event) {
			candidates.add(event.getSource());
		}
	};

	
	//TOOLS
	private Address select(List<Address> nodes){
		Address min = null;
		for(Address node : nodes){
			if (min == null){
				min = node;
			}else{
				if (node.getId() > min.getId()){
					min = node;
				}
			}
		}
		return min;
		
	}
	
    private void scheduleEldTimeout(int timeout){
		ScheduleTimeout stPeriod = new ScheduleTimeout(timeout);
		stPeriod.setTimeoutEvent(new EldTimeout(stPeriod));
		trigger(stPeriod, timer);
    }
    
    private void sendHeartbeats(List<Address> nodes){
		for(Address node : neighbors){
			trigger(new Pp2pSend(node, new EldHeartbeatMsg(self)),pp2p);
		}
    }
}
