/*
 * David Jacewicz
 * May 26, 2018
 * Ms. Krasteva
 * A moving platform used in Level 2
 */

/*
 * Modification: refactor the hardcoded time into constants
 * David Jacewicz
 * May 27, 2018
 * 10 minutes
 * Version: 0.04
 */

import org.joml.*;
import java.lang.Math;
import static util.Util.*;
class Platform extends ModelNode {
	public long lastRaise;
	public static int
		RAISE_TIME = 2000,
		HOLD_TIME = 6000;
	/** @return The time it takes for this platform to raise and lower again */
	public int totalTime() { return RAISE_TIME*2 + HOLD_TIME; }
	/** @return The number of milliseconds since the platform was raised */
	public int delta() { return (int)(System.currentTimeMillis()-lastRaise); }
	private Matrix4f currentTransform = new Matrix4f();
	/**
	 * Compute the transform matrix for this platform
	 *
	 * @return The transform matrix
	 */
	public Matrix4f getLocalTransform() {
		float f = Math.abs(delta()-totalTime()/2);
		f-=HOLD_TIME/2;
		f=f<0?0:f;
		f/=1000;
		return identityMatrix
			.translate(new Vector3f(0,-f*f,0),currentTransform);
	}
	
	/** @return A lambda that raises this platform */
	public Runnable raise() {
		return ()->{
			long now = System.currentTimeMillis();
			int since = Math.abs((int)(now - lastRaise));
			if(since<totalTime())
				lastRaise = now
					- (totalTime()/2
						- Math.abs(totalTime()/2 - since));
			else
				lastRaise = now;
		};
	}
}
