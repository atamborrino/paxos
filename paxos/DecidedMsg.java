package se.kth.ict.id2203.assignment4.paxos;

import se.kth.ict.id2203.assignment3.beb.BebDeliver;
import se.sics.kompics.address.Address;

public class DecidedMsg extends BebDeliver{
	private int id;
	private Integer val;

	public DecidedMsg(Address source, int id, Integer val) {
		super(source);
		this.id = id;
		this.val = val;
	}
	
	public int getId() {
		return id;
	}

	public Integer getVal() {
		return val;
	}

}
