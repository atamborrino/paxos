package se.kth.ict.id2203.assignment4;

import se.sics.kompics.launch.Scenario;
import se.sics.kompics.launch.Topology;

public class ExecutorPaxos {

	public static final void main(String[] args) {

		Topology topo1 = new Topology() {
			{
				node(1, "127.0.0.1", 22055);
				node(2, "127.0.0.1", 22056);
				node(3, "127.0.0.1", 22057);

				defaultLinks(5000, 0);

			}
		};

		Scenario sc1 = new Scenario(PaxosMain.class) {
			{
				String cmd1 = "D2200:P1-1";
				String cmd2 = "D2000:P1-2";
				String cmd3 = "D2000:P1-3";

				command(1, cmd1);
				command(2, cmd2);
				command(3, cmd3);
			}
		};

		Scenario sc2 = new Scenario(PaxosMain.class) {
			{
				String cmd1 = "D2000:P1-1";
				String cmd2 = "D2200:P1-2";
				String cmd3 = "D2200:P1-3";

				command(1, cmd1);
				command(2, cmd2);
				command(3, cmd3);
			}
		};

		Scenario sc0 = new Scenario(PaxosMain.class) {
			{
				String cmd1 = "P1-7:D100:P3-3:P4-10:D20000:W";
				String cmd2 = "P1-8:D100:P3-4:P4-11:D20000:W";
				String cmd3 = "P1-9:D100:P3-5:P4-12:D20000:W";

				command(1, cmd1);
				command(2, cmd2);
				command(3, cmd3);
			}
		};

		Topology topo4 = new Topology() {
			{
				node(1, "127.0.0.1", 22055);
				node(2, "127.0.0.1", 22056);

				defaultLinks(1000, 0);

			}
		};

		Scenario sc4 = new Scenario(PaxosMain.class) {
			{
				String cmd1 = "D1100:P1-1:D1000:W";
				String cmd2 = "D1100:P1-2:D1000:W";

				command(1, cmd1);
				command(2, cmd2);
			}
		};

		sc4.executeOn(topo4);

		System.exit(0);
		// sc1.executeOn(topo1);
	}

}
