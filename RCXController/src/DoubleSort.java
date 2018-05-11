import java.lang.System;
import josx.platform.rcx.*;

public class DoubleSort {

	static final int BELT_SPEED = 3; // Do not change

	public static void main(String[] arg) {
		Motor.A.setPower(BELT_SPEED);
		Motor.B.setPower(BELT_SPEED);
		Motor.C.setPower(BELT_SPEED);
		
		DirectionLock DL = new DirectionLock();
		SimpleLock SL1 = new SimpleLock(1);
		SimpleLock SL2 = new SimpleLock(2);

		Thread f1 = new Feeder(1, DL, SL1, SL2); f1.start();
		Thread f2 = new Feeder(2, DL, SL2, SL1); f2.start();

		try { Button.RUN.waitForPressAndRelease(); } catch (Exception e) {}
		System.exit(0);
	}
}
