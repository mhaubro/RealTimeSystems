import josx.util.TimerListener;

public class SimpleLock implements TimerListener {
	
	private Timer 	timer;
	private boolean locked;

	public static SimpleLock SL1 = new SimpleLock(1);
	public static SimpleLock SL2 = new SimpleLock(2);

	int no;

	public SimpleLock(int _no) {
		no = _no;
		timer = new Timer(this);
	}
	
	public synchronized void lock() {
		locked = true;
		timer.start(6200);
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
