package idc.gc.guards;

public class Timer {

	private long start = 0;

	private long stop = 0;

	public void start() {
		this.start = System.nanoTime();
		stop = 0;
	}

	public void stop() {
		this.stop = System.nanoTime();
	}

	public float duration() {
		long now;
		if (stop == 0) {
			now = System.nanoTime();
		} else {
			now = stop;
		}
		return (float) (now - start) / 1000000f;
	}

	public String durationStr() {
		float duration = duration();
		String tu = "ms";
		if (duration > 60 * 1000) {
			duration = duration / 1000;
			tu = "minutes";
		} else if (duration > 1000) {
			duration = duration / 1000;
			tu = "seconds";
		}

		return String.format("%.2f %s", duration, tu);
	}
}
