import josx.util.TimerListener;

public class DirectionLock implements TimerListener {

	private Timer 	timer;
	private boolean locked;
	
	private static final int LONG_DELAY = 10400;
	private static final int SHORT_DELAY = 4700;

	public DirectionLock() {
		timer = new Timer(this);
	}
	
	public synchronized void lockLong() {
		timer.start(LONG_DELAY);
		locked = true;
	}
	
	public synchronized void lockShort() {
		timer.start(SHORT_DELAY);
		locked = true;			
	}
	
	public synchronized boolean isLocked() {
		return locked;
	}

	@Override
	public synchronized void timedOut() {
		timer.stop();
		locked = false;
		notifyAll();
	}

}
