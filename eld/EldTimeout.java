package se.kth.ict.id2203.assignment4.eld;

import se.sics.kompics.timer.SchedulePeriodicTimeout;
import se.sics.kompics.timer.ScheduleTimeout;
import se.sics.kompics.timer.Timeout;

public class EldTimeout extends Timeout{

	public EldTimeout(ScheduleTimeout request) {
		super(request);
	}


}
