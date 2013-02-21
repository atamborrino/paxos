package se.kth.ict.id2203.assignment4.eld;

import se.kth.ict.id2203.pp2p.Pp2pDeliver;
import se.sics.kompics.address.Address;

public class EldHeartbeatMsg extends Pp2pDeliver{

	public EldHeartbeatMsg(Address source) {
		super(source);
	}

}
