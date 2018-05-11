import bagsortsim.BagSortSim;

public class RunSimulation {
		
	public static void main(String[] args) {

		BagSortSim sim = BagSortSim.getSimulator();
		
		/* Set simulation parameters */
		sim.setActiveCheck(true);
		sim.setFeedCheck(false);
		sim.setSeparation(0);        // No need for extra separation when respecting sensor activation

		/* Start simulator and environment (travellers) */
		sim.start();
		new Travellers(sim).start(); 
		
		/* Run control program */
		DoubleSort.main(args);
	}
}

class Travellers extends Thread {
	
	BagSortSim sim;
	
	public Travellers (BagSortSim sim) {
		this.sim = sim;
	}
	
	public void run() {
		try {
			while (true) {
				sleep((int) (Math.random()*5000));
				int counter = (Math.random() < 0.5 ? 1 : 2);  // Use only Feed 2 for SingleSort
				int color   = (Math.random() < 0.6 ? BagSortSim.YELLOW  : BagSortSim.BLACK);
				sim.checkin(counter,color);
			}
		} catch (InterruptedException e) {}
	}
}

