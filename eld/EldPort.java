package se.kth.ict.id2203.assignment4.eld;

import se.sics.kompics.PortType;

public class EldPort extends PortType{
	{
		indication(EldTrustEvent.class);
	}

}
