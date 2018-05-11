import josx.util.TimerListener;

public class SimpleLock implements TimerListener {
	
	private Timer 	timer;
	private boolean locked;
	
	private static final int DELAY = 6200;

	public SimpleLock() {
		timer = new Timer(this);
	}
	
	public synchronized void lock() {
		locked = true;
		timer.start(DELAY);
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
