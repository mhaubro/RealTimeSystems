import josx.util.TimerListener;

public class Timer {
	private TimerListener 	myListener;
	private Thread 			myThread;
	private int 			delay;
	private int 			start;

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
							int end = (int) System.currentTimeMillis();
							delay -= Math.min((end - start), delay);
						}
						if (delay == 0) myListener.timedOut();
					}
				}
			}
		};
	}
	
	/**
	 * Stops the timer, preventing it from invoking timedOut.
	 */
	public synchronized void stop() {
		delay = -1;
	}

	/**
	 * Start the timer, invoking timedOut after the given delay. <br>
	 * Note that the timer will not time out if it is started again prematurely.
	 */
	public synchronized void start(int _delay) {
		start = (int) System.currentTimeMillis();
		delay = Math.max(delay, _delay);
		if (!myThread.isAlive())
			myThread.start();
		notify();
	}
}
