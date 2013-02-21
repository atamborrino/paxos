package se.kth.ict.id2203.assignment4;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import se.kth.ict.id2203.assignment3.failStopAR.FailStopARApplication;
import se.kth.ict.id2203.console.Console;
import se.sics.kompics.ComponentDefinition;
import se.sics.kompics.Positive;

public class PaxosApplication extends ComponentDefinition{
	Positive<Console> con = requires(Console.class);
	
	private static final Logger logger = LoggerFactory.getLogger(PaxosApplication.class);
	
	public PaxosApplication(){
		
	}

}
