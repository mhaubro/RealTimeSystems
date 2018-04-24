import java.lang.System;
import josx.platform.rcx.*;

class Feeder2 extends Thread {
 
  static final int BLOCKED = 70;
  static final int YELLOW  = 60;
  static final int BLACK   = 45;
  
  public void run() {
    try {
      boolean dirA;    // Current direction is towards A
      boolean destA;   // Required destination is A
    
      Poll e = new Poll();
      int done = (int) System.currentTimeMillis(); // When last bag is through
      
      Motor.B.forward();
      Motor.C.forward();  dirA = true;

      while (true) {
        // Await arrival of a bag
        Sensor.S2.activate();
        while(Sensor.S2.readValue() > BLOCKED) { e.poll(Poll.SENSOR2_MASK,0); }
	  
        Thread.sleep(800);           // Wait for colour to be valid

        destA = (Sensor.S2.readValue() > BLACK);   // Determine destination
        Sensor.S2.passivate();

        Thread.sleep(2000);          // Advance beyond sensor 
        if (dirA != destA) {         // Decide whether to stop or not
          Motor.B.stop();
          int now = (int) System.currentTimeMillis();
          if (now < done) Thread.sleep(done-now);
          Motor.C.reverseDirection();
          dirA = destA;
          Motor.B.forward();
        }

        done = ((int) System.currentTimeMillis()) + 6000;  
        if (dirA) done = done + 6000;       // Extra time for long path

        Thread.sleep(1200);                 // Follow to end of feed belt
       }
    } catch (Exception e) { }
  }
}

public class DoubleSort {

  static final int BELT_SPEED = 3;          // Do not change

  public static void main (String[] arg) {
      Motor.A.setPower(BELT_SPEED);  Motor.A.forward();
      Motor.B.setPower(BELT_SPEED);
      Motor.C.setPower(BELT_SPEED);
      
      Thread f2 = new Feeder2();  f2.start();

      try{ Button.RUN.waitForPressAndRelease();} catch (Exception e) {}
      System.exit(0);
  }
}



