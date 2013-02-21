package se.kth.ict.id2203.assignment4.ac;

import se.kth.ict.id2203.pp2p.Pp2pDeliver;
import se.sics.kompics.address.Address;

public class WriteAck extends Pp2pDeliver {

	/**
	 *
	 */
	private static final long serialVersionUID = -7135376313431745118L;

	private final int consensusId;
	private final int sentTimestamp;

	public WriteAck(Address source, int consensusId, int sentTimestamp) {
		super(source);
		this.consensusId = consensusId;
		this.sentTimestamp = sentTimestamp;
	}

	public int getConsensusId() {
		return consensusId;
	}

	public int getSentTimestamp() {
		return sentTimestamp;
	}

}
