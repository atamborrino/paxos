package se.kth.ict.id2203.assignment4.paxos;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ThreadPoolExecutor.AbortPolicy;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import se.kth.ict.id2203.assignment3.beb.BebBroadcastPort;
import se.kth.ict.id2203.assignment4.PaxosApplication;
import se.kth.ict.id2203.assignment4.ac.AcDecide;
import se.kth.ict.id2203.assignment4.ac.AcPort;
import se.kth.ict.id2203.assignment4.ac.AcPropose;
import se.kth.ict.id2203.assignment4.eld.EldHeartbeatMsg;
import se.kth.ict.id2203.assignment4.eld.EldPort;
import se.kth.ict.id2203.assignment4.eld.EldTrustEvent;
import se.kth.ict.id2203.console.Console;
import se.sics.kompics.ComponentDefinition;
import se.sics.kompics.Handler;
import se.sics.kompics.Positive;
import se.sics.kompics.address.Address;

public class PaxosComponent extends ComponentDefinition{
	Positive<Console> con = requires(Console.class);
	
	Positive<BebBroadcastPort> beb = requires(BebBroadcastPort.class);
	Positive<AcPort> ac = requires(AcPort.class);
	Positive<EldPort> eld = requires(EldPort.class);
	
	private static final Logger logger = LoggerFactory.getLogger(PaxosApplication.class);
	
	
	private Boolean leader;
	private int selfId;
	private List<Integer> seenIds;
	private Map<Integer, Integer> proposals;
	private Map<Integer, Boolean> proposed;
	private Map<Integer, Boolean> decided;
	
	public PaxosComponent(){
		subscribe(handleInit, control);
		
	}
	
	Handler<PaxosInit> handleInit = new Handler<PaxosInit>() {

		@Override
		public void handle(PaxosInit event) {
			selfId = event.getSelf().getId();
			proposals = new HashMap<Integer, Integer>();
			proposed = new HashMap<Integer, Boolean>();
			decided = new HashMap<Integer, Boolean>();
			seenIds = new ArrayList<Integer>();
			leader = false;
		}
	};
	
	Handler<EldTrustEvent> handleTrust = new Handler<EldTrustEvent>() {

		@Override
		public void handle(EldTrustEvent event) {
			if (event.getLeader().getId() == selfId){
				leader = true;
				for(int id: seenIds){
					tryPropose(id);
				}
				
			}else{
				leader = false;
			}
		}
	};
	
	Handler<UcPropose> handleUcPropose = new Handler<UcPropose>() {

		@Override
		public void handle(UcPropose event) {
			int id = event.getId();
			initInstance(id);
			proposals.put(id, event.getVal());
			tryPropose(id);
			
		}
	};
	
	Handler<AcDecide> handleAcDecide = new Handler<AcDecide>() {

		@Override
		public void handle(AcDecide event) {
			if(event.getValue() != null){
				
			}
		}
	};
	
	//TOOLS
	private void initInstance(int id){
		if (!seenIds.contains(id)){
			proposed.put(id, false); 
			decided.put(id, false);
			seenIds.add(id);
		}
	}

	public void tryPropose(int id){
		if( (leader) && (!proposed.get(id)) && (proposals.containsKey(id))){
			proposed.put(id,true);
			trigger(new AcPropose(id, proposals.get(id)), ac);
		}
	}
}
