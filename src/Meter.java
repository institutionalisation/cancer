/*
 * David Jacewicz
 * June 7, 2018
 * Ms. Krasteva
 * A meter class used in Level 2
 */

public class Meter {
	public long lastRefill;
	public float leakRate;
	/**
	 * Creates a meter with the given leak rate
	 *
	 * @param leakRate The rate whereby the meter drains
	 */
	public Meter(float leakRate) {
		this.leakRate = leakRate;
		lastRefill = System.currentTimeMillis();
	}
}
