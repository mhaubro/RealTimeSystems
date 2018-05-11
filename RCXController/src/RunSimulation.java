import bagsortsim.BagSortSim;

public class RunSimulation {
		
	public static void main(String[] args) {

		BagSortSim sim = BagSortSim.getSimulator();
		
		/* Set simulation parameters */
		sim.setActiveCheck(true);
		sim.setFeedCheck(false);
		sim.setSeparation(0);

		/* Start simulator and environment (travelers) */
		sim.start();
		new Travelers(sim).start(); 
		
		/* Run control program */
		DoubleSort.main(args);
	}
}

class Travelers extends Thread {
	
	BagSortSim sim;
	
	public Travelers (BagSortSim sim) {
		this.sim = sim;
	}
	
	public void run() {
		try {
			while (true) {
				sleep((int) (Math.random()*5000));
				int counter = (Math.random() < 0.5 ? 1 : 2); 
				int color   = (Math.random() < 0.6 ? 
						BagSortSim.YELLOW  : BagSortSim.BLACK);
				sim.checkin(counter,color);
			}
		} catch (InterruptedException e) {}
	}
}

