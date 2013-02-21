package se.kth.ict.id2203.assignment4.paxos;

import se.sics.kompics.Init;
import se.sics.kompics.address.Address;

public class PaxosInit extends Init{
	private Address self;
	
	public PaxosInit(Address self){
		self = self;
	}

	public Address getSelf() {
		return self;
	}

	
}
