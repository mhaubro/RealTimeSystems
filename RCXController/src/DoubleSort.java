import java.lang.System;
import josx.platform.rcx.*;

class Feeder extends Thread {

	static final int BLOCKED = 70;
	static final int YELLOW = 60;
	static final int BLACK = 45;

	final int id;

	public Feeder(int id) {
		this.id = id;
	}

	public void run() {
		try {
			boolean dirA; // Current direction is towards A
			boolean destA; // Required destination is A
			boolean f1 = id == 1;
			
			Motor 		myMotor 	 = f1 ? Motor.A : Motor.B;
			Sensor 		mySensor 	 = f1 ? Sensor.S1 : Sensor.S2;
			int 		myPollMask 	 = f1 ? Poll.SENSOR1_MASK : Poll.SENSOR2_MASK;
			SimpleLock 	mySimpleLock = f1 ? SimpleLock.SL1 : SimpleLock.SL2;
			SimpleLock  oppositeLock = f1 ? SimpleLock.SL2 : SimpleLock.SL1;

			Poll e = new Poll();
			
			Motor.C.forward();
			myMotor.forward();

			while (true) {
				// Await arrival of a bag
				mySensor.activate();
				while (mySensor.readValue() > BLOCKED) { e.poll(myPollMask, 0); }

				Thread.sleep(800); // Wait for colour to be valid

				destA = (mySensor.readValue() > BLACK); // Determine destination
				mySensor.passivate();

				Thread.sleep(2600); // Advance beyond sensor
				dirA = Motor.C.isForward();
				if (dirA != destA) { // Decide whether to stop or not
					myMotor.stop();
					synchronized (DirectionLock.DL) {
						while (DirectionLock.DL.isLocked() 
								&& Motor.C.isForward() != destA)
							DirectionLock.DL.wait(); 
						synchronized (Motor.C) {
							if (Motor.C.isForward() != destA)
								Motor.C.reverseDirection();
						}
					}
					myMotor.forward();
				}
				
				if (destA == f1) { // Short path
					if (mySimpleLock.isLocked()) {
						myMotor.stop();
						synchronized (mySimpleLock) {
							while (mySimpleLock.isLocked())
								mySimpleLock.wait();
						}
						myMotor.forward();
					}
					DirectionLock.DL.lockShort();
				}
				else { // Long path 
					DirectionLock.DL.lockLong();
					Thread.sleep(2300); // Wait before locking intersection
					oppositeLock.lock();
				}
			}
		} catch (Exception e) {
		}
	}
}

public class DoubleSort {

	static final int BELT_SPEED = 3; // Do not change

	public static void main(String[] arg) {
		Motor.A.setPower(BELT_SPEED);
		Motor.B.setPower(BELT_SPEED);
		Motor.C.setPower(BELT_SPEED);

		Thread f1 = new Feeder(1); f1.start();
		Thread f2 = new Feeder(2); f2.start();

		try { Button.RUN.waitForPressAndRelease(); } catch (Exception e) {}
		System.exit(0);
	}
}
