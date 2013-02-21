package se.kth.ict.id2203.assignment4.paxos;

import se.sics.kompics.Event;

public class UcPropose extends Event{
	private int id;
	private int val;

	public UcPropose(int id, int val) {
		super();
		this.id = id;
		this.val = val;
	}

	public int getId() {
		return id;
	}
	
	public int getVal() {
		return val;
	}

}
