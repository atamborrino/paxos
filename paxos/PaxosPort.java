package se.kth.ict.id2203.assignment4.paxos;

import se.sics.kompics.PortType;

public class PaxosPort extends PortType{
	{
		indication(UcDecide.class);
		request(UcPropose.class);
	}

}
