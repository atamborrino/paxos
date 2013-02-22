package se.kth.ict.id2203.assignment4.paxos;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ThreadPoolExecutor.AbortPolicy;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import se.kth.ict.id2203.assignment3.beb.BebBroadcast;
import se.kth.ict.id2203.assignment3.beb.BebBroadcastPort;
import se.kth.ict.id2203.assignment4.PaxosApplication;
import se.kth.ict.id2203.assignment4.ac.AcDecide;
import se.kth.ict.id2203.assignment4.ac.AcPort;
import se.kth.ict.id2203.assignment4.ac.AcPropose;
import se.kth.ict.id2203.assignment4.eld.EldHeartbeatMsg;
import se.kth.ict.id2203.assignment4.eld.EldPort;
import se.kth.ict.id2203.assignment4.eld.EldTrustEvent;
import se.kth.ict.id2203.console.Console;
import se.kth.ict.id2203.pp2p.Pp2pDeliver;
import se.sics.kompics.ComponentDefinition;
import se.sics.kompics.Handler;
import se.sics.kompics.Negative;
import se.sics.kompics.Positive;
import se.sics.kompics.address.Address;

public class PaxosComponent extends ComponentDefinition{
	Positive<Console> con = requires(Console.class);
	
	Positive<BebBroadcastPort> beb = requires(BebBroadcastPort.class);
	Positive<AcPort> ac = requires(AcPort.class);
	Positive<EldPort> eld = requires(EldPort.class);
	Negative<PaxosPort> uc = provides(PaxosPort.class);
	
	private static final Logger logger = LoggerFactory.getLogger(PaxosApplication.class);
	
	
	private Boolean leader;
	private Address self;
	private List<Integer> seenIds;
	private Map<Integer, Integer> proposals;
	private Map<Integer, Boolean> proposed;
	private Map<Integer, Boolean> decided;
	
	public PaxosComponent(){
		subscribe(handleInit, control);
		subscribe(handleTrust, eld);
		subscribe(handleUcPropose, uc);
		subscribe(handleAcDecide, ac);
		subscribe(handleDecideMsg, beb);
		
		
	}
	
	Handler<PaxosInit> handleInit = new Handler<PaxosInit>() {

		@Override
		public void handle(PaxosInit event) {
			self = event.getSelf();
			proposals = new HashMap<Integer, Integer>();
			proposed = new HashMap<Integer, Boolean>();
			decided = new HashMap<Integer, Boolean>();
			seenIds = new ArrayList<Integer>();
			leader = false;
			logger.info("PAXOS -- paxos component initialized");
		}
	};
	
	Handler<EldTrustEvent> handleTrust = new Handler<EldTrustEvent>() {

		@Override
		public void handle(EldTrustEvent event) {
			logger.info("PAXOS -- got EldTrustEvent - trusted node: "+event.getLeader().getId());
			if (event.getLeader().getId() == self.getId()){
				logger.info("PAXOS -- I am the new leader");
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
			logger.info("PAXOS -- proposing value "+event.getVal()+" for instance "+event.getId());
			int id = event.getId();
			initInstance(id);
			proposals.put(id, event.getVal());
			tryPropose(id);
			
		}
	};
	
	Handler<AcDecide> handleAcDecide = new Handler<AcDecide>() {

		@Override
		public void handle(AcDecide event) {
			int consensusId = event.getConsensusId();
			if(event.getValue() != null){
				logger.info("PAXOS -- got AcDecide: instance: "+consensusId+" value: "+event.getValue());
				trigger(new BebBroadcast(new DecidedMsg(self, consensusId, 
						event.getValue())), beb);
			}else{
				proposed.put(consensusId, false);
				tryPropose(consensusId);
			}
		}
	};
	
	Handler<DecidedMsg> handleDecideMsg = new Handler<DecidedMsg>() {

		@Override
		public void handle(DecidedMsg event) {
			int id = event.getId();
			initInstance(id);
			if( decided.containsKey(id) && (!decided.get(id)) ){
				logger.info("PAXOS -- instance: "+id+" value: "+event.getVal());
				decided.put(id, true);
				trigger(new UcDecide(id, event.getVal()),uc);
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
		if( (leader) && ((proposed.containsKey(id)) && (!proposed.get(id))) && (proposals.containsKey(id))){
			proposed.put(id,true);
			trigger(new AcPropose(id, proposals.get(id)), ac);
		}
	}
}
