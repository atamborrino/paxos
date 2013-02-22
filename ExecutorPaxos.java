package se.kth.ict.id2203.assignment4;

import se.kth.ict.id2203.assignment3.FailSilentAR.FailSilentARMain;
import se.sics.kompics.launch.Scenario;
import se.sics.kompics.launch.Topology;

public class ExecutorPaxos {

	public static final void main(String[] args) {

		Topology topo1 = new Topology() {
			{
				node(1, "127.0.0.1", 22055);
				node(2, "127.0.0.1", 22056);
				node(3, "127.0.0.1", 22057);

				defaultLinks(1000, 0);

			}
		};

		Scenario sc1 = new Scenario(FailSilentARMain.class) {
			{
				String cmd1 = "D2200:P1-1";
				String cmd2 = "D2000:P1-2";
				String cmd3 = "D2000:P1-3";

				command(1, cmd1);
				command(2, cmd2);
				command(3, cmd3);
			}
		};

		sc1.executeOn(topo1);

		System.exit(0);

	}

}
