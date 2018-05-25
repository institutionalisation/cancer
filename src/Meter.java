public class Meter {
	public long lastRefill;
	public float leakRate;
	public Meter(float leakRate) {
		this.leakRate = leakRate;
		lastRefill = System.currentTimeMillis();
	}
}