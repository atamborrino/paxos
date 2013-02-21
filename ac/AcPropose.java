package se.kth.ict.id2203.assignment4.ac;

import se.sics.kompics.Event;

public class AcPropose extends Event {
	private final int consensusId;
	private final int value;

	public AcPropose(int consensusId, int value) {
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
