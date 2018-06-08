/*
 * Junyi Wang
 * June 7, 2018
 * Ms. Krasteva
 * A bug minion
 */

/*
 * Modification: fix pathfinding
 * Junyi Wang
 * June 8, 2018
 * 2 hours
 * Version: 0.05
 */

/*
 * Modification: write main game logic, add collision points
 * Junyi Wang
 * June 8, 2018
 * 3 hours
 * Version: 0.05
 */

import org.joml.*;
import static java.lang.Math.*;

public class Bug extends ModelNode
{
	private long lastTime = System.nanoTime();
	private final Matrix4f baseTransform;
	private final Matrix4f curPos = new Matrix4f();
	private Vector2d[] path = null;
	private int pathIdx = 1;
	public double speed = 0.002;
	public final Vector3f pos;
	private double dist = 0.0;
	private Vector3f activationPoint = null;
	private double activationDistance = 0;
	private boolean active = false;
	private double tilt = 0;

	/**
	 * Sets the path the bug minion is currently following
	 *
	 * @param path The path to make this bug follow
	 */
	public synchronized void setPath(Vector2d[] path)
	{
		if((this.path = path) == null)
			return;
		pos.x = (float)path[0].x;
		pos.z = (float)path[0].y;
		pathIdx = 1;
		dist = 0.0;
	}

	/**
	 * Creates a new bug with a position, activation point and distance and base transform matrix
	 *
	 * @param orig The model node to copy
	 * @param baseTransform The transform matrix to use as a base for all animations; this controls the initial animation state of this object
	 * @param pos The initial position of the bug
	 * @param activationPoint The activation point; this bug is activated if the player moves within a certain distance of this point
	 * @param activationDistance If the player is within this distance of the activation point, the bug is activated
	 */
	public Bug(final ModelNode orig,final Matrix4f baseTransform,final Vector3f pos,final Vector3f activationPoint,final double activationDistance)
	{
		set(orig);
		this.baseTransform = baseTransform;
		this.activationPoint = activationPoint;
		this.activationDistance = activationDistance;
		this.pos = pos;
	}

	/**
	 * Creates a new bug with the given position, and activation state
	 *
	 * @param orig The original model node
	 * @param baseTransform The base transform matrix
	 * @param pos The initial position 
	 * @param active The initial activation state
	 */
	public Bug(final ModelNode orig,final Matrix4f baseTransform,final Vector3f pos,final boolean active)
	{
		set(orig);
		this.baseTransform = baseTransform;
		this.pos = pos;
		this.active = active;
	}

	/** Activates this minion */
	public void activate()
	{
		this.active = true;
	}

	/**
	 * Computes the distance this bug will travel before reaching the end of its path
	 *
	 * @return The distance this bug will travel before reaching the end of its path
	 */
	public synchronized double distToDest()
	{
		/* If the bug is not going anywhere, it is already at the destination */
		if(path == null)
			return 0.0;
		double dist = 0;
		for(int i = pathIdx;i < path.length;i++)
			dist += path[i].distance(path[i - 1]);
		return dist - this.dist;
	}

	/**
	 * Moves the bug as if delta nanoseconds has passed
	 *
	 * @param delta The amount of time to simulate the bug for
	 */
	public synchronized void runTime(long delta)
	{
		/* If the bug is not moving, there is no need to animate it */
		if(path != null)
		{
			dist += delta / 1000000.0 * speed;
			double pdist;
			/* Moves the bug over any complete segments of the path */
			while(pathIdx < path.length)
			{
				pdist = path[pathIdx - 1].distance(path[pathIdx]);
				if(pdist > dist)
					break;
				dist -= pdist;
				pos.x = (float)path[pathIdx].x;
				pos.z = (float)path[pathIdx].y;
				++pathIdx;
			}
			/* Interpolates for incomplete segments of the path */
			if(pathIdx < path.length)
			{
				Vector2d dt = new Vector2d();
				path[pathIdx].sub(path[pathIdx - 1],dt);
				double percent = dist / path[pathIdx].distance(path[pathIdx - 1]);
				dt.mul(percent);
				pos.x = (float)(dt.x + path[pathIdx - 1].x);
				pos.z = (float)(dt.y + path[pathIdx - 1].y);
			}
			tilt = atan2(path[path.length - 1].x - pos.x,path[path.length - 1].y - pos.z) - PI / 2;
		}
		lastTime = System.nanoTime();
	}

	/**
	 * Animates the bug by computing the transformation matrix
	 *
	 * @return The current transformation matrix to be used for this bug
	 */
	@Override
	public Matrix4f getLocalTransform()
	{
		synchronized(this)
		{
			long now = System.nanoTime();
			runTime(now - lastTime);
			lastTime = now;
		}
		return new Matrix4f().translate(pos).rotateY((float)tilt).mul(baseTransform);
	}

	/**
	 * Calculates whether the bug is already active, or meets the activation conditions
	 * 
	 * @param playerPos The player position
	 *
	 * @return true iff the bug should be activated, false otherwise
	 */
	public boolean isActive(Vector3f playerPos)
	{
		if(active)
			return true;
		if(activationPoint == null)
			return false;
		return active = activationPoint.distance(playerPos) < activationDistance;
	}
}
