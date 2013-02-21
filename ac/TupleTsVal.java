package se.kth.ict.id2203.assignment4.ac;

public class TupleTsVal {
	private final int timestamp;
	private final int value;

	public TupleTsVal(int timestamp, int value) {
		super();
		this.timestamp = timestamp;
		this.value = value;
	}

	public int getTimestamp() {
		return timestamp;
	}

	public int getValue() {
		return value;
	}

}
