package se.kth.ict.id2203.assignment4.ac;

import se.sics.kompics.Init;

public class AcInit extends Init {
	private final int numberOfNodes;

	public AcInit(int numberOfNodes) {
		super();
		this.numberOfNodes = numberOfNodes;
	}

	public int getNumberOfNodes() {
		return numberOfNodes;
	}

}
