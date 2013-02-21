package se.kth.ict.id2203.assignment4.eld;

import se.sics.kompics.Event;
import se.sics.kompics.address.Address;

public class EldTrustEvent extends Event{
	private Address leader;

	public EldTrustEvent(Address leader) {
		super();
		this.leader = leader;
	}

	public Address getLeader() {
		return leader;
	}
	
	

}
