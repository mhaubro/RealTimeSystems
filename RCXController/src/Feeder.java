import josx.platform.rcx.*;

public class Feeder extends Thread {

	static final int BLOCKED = 70;
	static final int YELLOW = 60;
	static final int BLACK = 45;

	final int id;
	
	final DirectionLock myDirLock;
	final SimpleLock mySimpleLock, myOppositeLock;

	public Feeder(int id, DirectionLock myDirLock, SimpleLock mySimpleLock, SimpleLock myOppositeLock) {
		this.id = id;
		this.myDirLock = myDirLock;
		this.mySimpleLock = mySimpleLock;
		this.myOppositeLock = myOppositeLock;
	}

	public void run() {
		try {
			boolean dirA; // Current direction is towards A
			boolean destA; // Required destination is A
			boolean f1 = id == 1;
			
			final Motor 	myMotor 	 = f1 ? Motor.A : Motor.B;
			final Sensor 	mySensor 	 = f1 ? Sensor.S1 : Sensor.S2;
			final int 		myPollMask 	 = f1 ? Poll.SENSOR1_MASK : Poll.SENSOR2_MASK;

			Poll e = new Poll();
			
			Motor.C.forward();
			myMotor.forward();

			while (true) {
				// Await arrival of a bag (Polling sensor)
				mySensor.activate();
				while (mySensor.readValue() > BLOCKED) { e.poll(myPollMask, 0); }

				Thread.sleep(800); // Wait for color to be valid

				destA = (mySensor.readValue() > BLACK); // Determine destination
				mySensor.passivate();

				Thread.sleep(2600); // Advance beyond sensor
				dirA = Motor.C.isForward();
				if (dirA != destA) { // Decide whether to stop or not
					myMotor.stop();
					synchronized (myDirLock) {
						while (myDirLock.isLocked() 
								&& Motor.C.isForward() != destA)
							myDirLock.wait(); // Await direction lock
						if (Motor.C.isForward() != destA)
							Motor.C.reverseDirection();
					}
					myMotor.forward();
				}
				
				if (destA == f1) { // Short path
					if (mySimpleLock.isLocked()) {
						myMotor.stop();
						synchronized (mySimpleLock) {
							while (mySimpleLock.isLocked())
								mySimpleLock.wait(); // Await simple lock
						}
						myMotor.forward();
					}
					myDirLock.lockShort();
				}
				else { // Long path 
					myDirLock.lockLong();
					Thread.sleep(2300); // Wait before locking intersection
					myOppositeLock.lock();
				}
			}
		} catch (Exception e) {}
	}
}
