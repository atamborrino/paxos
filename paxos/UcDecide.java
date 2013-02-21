package se.kth.ict.id2203.assignment4.paxos;

import se.sics.kompics.Event;
import se.sics.kompics.address.Address;

public class UcDecide extends Event{
	private int id;
	private Integer val;

	public UcDecide( int id, Integer val) {
		super();
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
