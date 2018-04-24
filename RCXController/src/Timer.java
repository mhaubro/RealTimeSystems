import josx.util.TimerListener;

/**
 * TestTimer
 */
public class Timer {
	private TimerListener 	myListener;
	private Thread 			myThread;
	private int 			delay;
	private long 			start;

	/**
	 * Create a Timer object. Every theDelay milliseconds the el.timedOut() function
	 * is called. You may change the delay with setDelay(int). You need to call
	 * start() explicitly.
	 */
	public Timer(TimerListener el) {
		myListener = el;

		myThread = new Thread() {
			public void run() {
				int d;
				while (true) {
					synchronized (Timer.this) {
						while (delay <= 0) {
							try { Timer.this.wait(); } 
							catch (InterruptedException e) {}
						}
						d = delay;
						delay = 0;
					}
					
					try { Thread.sleep(d); } 
					catch (InterruptedException e) {}
					
					synchronized (Timer.this) {
						if (delay > 0) {
							long end = System.currentTimeMillis();
							delay -= Math.min((end - start), delay);
						}
						if (delay == 0) myListener.timedOut();
					}
				}
			}
		};
	}
	public synchronized void stop() {
		delay = -1;
	}

	public synchronized void start(int _delay) {
		start = System.currentTimeMillis();
		delay = Math.max(delay, _delay);
		if (!myThread.isAlive())
			myThread.start();
		notify();
	}
}
