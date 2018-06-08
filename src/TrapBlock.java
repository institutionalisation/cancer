/*
 * Junyi Wang
 * June 8, 2018
 * Ms. Krasteva
 * The block that is dropped on top of the player in the trap
 */

/*
 * Modification: fix using the wrong time to calculate delta
 * Junyi Wang
 * June 8, 2018
 * 10 minutes
 * Version: 0.05
 */

import org.joml.*;
import static java.lang.Math.*;

public class TrapBlock extends ModelNode
{
	private long startTime = 0;
	private final long closeTime;
	private final Vector3f start;
	private final Vector3f diff;
	private final Matrix4f baseTransform;

	/**
	 * Creates a new trap block with the given animation parameters
	 *
	 * @param orig The orignal model
	 * @param baseTransform The default transform matrix
	 * @param start The starting point of the animation
	 * @param end The finishing point of the animation
	 * @param closeTime The time in nanoseconds it takes for the block to move
	 */
	public TrapBlock(final ModelNode orig,final Matrix4f baseTransform,final Vector3f start,final Vector3f end,final long closeTime)
	{
		set(orig);
		this.baseTransform = baseTransform;
		this.start = start;
		this.diff = end.sub(start);
		this.closeTime = closeTime;
	}

	/** Starts the animation */
	public void start()
	{
		startTime = System.nanoTime();
	}

	/** @return The current transform matrix */
	public Matrix4f getLocalTransform()
	{
		float delta;
		if(startTime == 0)
			delta = 0;
		else
			delta = (float)min(closeTime,System.nanoTime() - startTime) / closeTime;
		return new Matrix4f().translate(start.x + diff.x * delta,start.y + diff.y * delta,start.z + diff.z * delta).mul(baseTransform);
	}
}
