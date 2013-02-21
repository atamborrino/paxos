package se.kth.ict.id2203.assignment4.eld;

import java.util.Set;

import se.sics.kompics.Init;
import se.sics.kompics.address.Address;


public class EldInit extends Init{
	private Set<Address> neighbors;
	private Address self;
	private int timeDelay;
	private int delta;
	
	public EldInit(Set<Address> neighbors, Address self, int timeDelay, int delta) {
		super();
		this.neighbors = neighbors;
		this.self = self;
		this.timeDelay = timeDelay;
		this.delta = delta;
	}
	
	public Set<Address> getNeighbors() {
		return neighbors;
	}

	public Address getSelf() {
		return self;
	}

	public int getTimeDelay() {
		return timeDelay;
	}

	public int getDelta() {
		return delta;
	}



	
}
