package se.kth.ict.id2203.assignment4.ac;

import se.sics.kompics.Init;

public class AcInit extends Init {
	private final int numberOfNodes;
	private final int nodeId;

	public AcInit(int numberOfNodes, int nodeId) {
		super();
		this.numberOfNodes = numberOfNodes;
		this.nodeId = nodeId;
	}

	public int getNumberOfNodes() {
		return numberOfNodes;
	}

	public int getNodeId() {
		return nodeId;
	}

}
