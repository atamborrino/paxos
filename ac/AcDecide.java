package se.kth.ict.id2203.assignment4.ac;

import se.sics.kompics.Event;

public class AcDecide extends Event {
	private final int consensusId;
	private final Integer value; // if == null, the consensus has been cancelled

	public AcDecide(int consensusId, Integer value) {
		super();
		this.consensusId = consensusId;
		this.value = value;
	}

	public int getConsensusId() {
		return consensusId;
	}

	public int getValue() {
		return value;
	}
}
