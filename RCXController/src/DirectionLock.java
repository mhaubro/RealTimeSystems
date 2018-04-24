import josx.util.TimerListener;

public class DirectionLock implements TimerListener {

	private Timer 	timer;
	private boolean locked;
	
	private static final int longDelay = 10300;
	private static final int shortDelay = 4600;

	public static DirectionLock DL = new DirectionLock();

	public DirectionLock() {
		timer = new Timer(this);
	}
	
	public synchronized void lockLong() {
		timer.start(longDelay);
		locked = true;
	}
	
	public synchronized void lockShort() {
		timer.start(shortDelay);
		locked = true;			
	}
	
	public synchronized boolean isLocked() {
		return locked;
	}

	@Override
	public synchronized void timedOut() {
		timer.stop();
		locked = false;
		DirectionLock.DL.notifyAll();
		System.out.println("TimedOut");
	}

}
