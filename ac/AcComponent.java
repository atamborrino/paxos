package se.kth.ict.id2203.assignment4.ac;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import se.kth.ict.id2203.assignment3.beb.BebBroadcastPort;
import se.kth.ict.id2203.pp2p.PerfectPointToPointLink;
import se.sics.kompics.ComponentDefinition;
import se.sics.kompics.Handler;
import se.sics.kompics.Positive;

public class AcComponent extends ComponentDefinition {

	private Positive<PerfectPointToPointLink> pp2pPort = requires(PerfectPointToPointLink.class);
	private Positive<BebBroadcastPort> bebPort = requires(BebBroadcastPort.class);

	private Set<Integer> seenIds = new HashSet<Integer>();
	private Map<Integer, Integer> tempValue = new HashMap<Integer, Integer>();
	private Map<Integer, Integer> value = new HashMap<Integer, Integer>();
	private Map<Integer, Integer> writeAck = new HashMap<Integer, Integer>();
	private Map<Integer, Integer> readTimestamp = new HashMap<Integer, Integer>();
	private Map<Integer, Integer> writeTimestamp = new HashMap<Integer, Integer>();
	private Map<Integer, Integer> timestamp = new HashMap<Integer, Integer>();
	private Map<Integer, Set<TupleTsVal>> readSet = new HashMap<Integer, Set<TupleTsVal>>();

	private int majority;

	public AcComponent() {
		subscribe(initHandler, control);
	}

	private void initInstance(int consensusId) {
		if (!seenIds.contains(consensusId)) {
			tempValue.put(consensusId, 0);
			value.put(consensusId, 0);
			// .put(consensusId, 0);
			value.put(consensusId, 0);
			value.put(consensusId, 0);

		}
	}

	Handler<AcInit> initHandler = new Handler<AcInit>() {
		public void handle(AcInit event) {
			majority = 1 + (event.getNumberOfNodes() / 2);
		}
	};




}
