/*
 * Junyi Wang
 * June 7, 2018
 * Ms. Krasteva
 * A line segment
 */

/*
 * Modification: rename variables to be more clear
 * Junyi Wang
 * June 7, 2018
 * 2 minutes
 * Version: 0.05
 */

/*
 * Modification: add epsilon to box check to fix bug
 * Junyi Wang
 * June 8, 2018
 * 1 minute + 30 minutes to find the bug
 * Version: 0.05
 */

/*
 * Modification: use JOML math functions instead of custom functions
 * Junyi Wang
 * June 8, 2018
 * 20 minutes
 * Version: 0.05
 */

import org.joml.*;
import static java.lang.Math.*;

public class Line
{
	public final Vector2d pointA;
	public final Vector2d pointB;

	/**
	 * Creates a line segment with the specified endpoints
	 *
	 * @param pointA The first endpoint
	 * @param pointB The second endpoint
	 */
	public Line(final Vector2d pointA,final Vector2d pointB)
	{
		this.pointA = pointA;
		this.pointB = pointB;
	}

	private static double EPS = 0.01;

	/**
	 * Calculates whether a point is in a box specified by the corners
	 *
	 * @param pointA The first corner of the box
	 * @param pointB The second corner of the box
	 * @param sect The point to check if it is inside the box
	 *
	 * @return true iff the given point in inside the given box
	 */
	private static boolean inBox(Vector2d pointA,Vector2d pointB,Vector2d sect)
	{
		return min(pointA.x,pointB.x) - EPS <= sect.x && sect.x <= max(pointA.x,pointB.x) + EPS
			&& min(pointA.y,pointB.y) - EPS <= sect.y && sect.y <= max(pointA.y,pointB.y) + EPS;
	}

	/**
	 * Collides the given line segment specified by endpoints with this line
	 *
	 * @param pointA The first endpoint
	 * @param pointB The second endpoint
	 *
	 * @return true iff the given line segment intersects this line segment
	 */
	public boolean collide(final Vector2d pointA,final Vector2d pointB)
	{
		Vector2d sect = new Vector2d();
		/* Check for a line intersection(NOT line segment!) */
		boolean lineCollides = Intersectiond.intersectLineLine(this.pointA.x,this.pointA.y,this.pointB.x,this.pointB.y,pointA.x,pointA.y,pointB.x,pointB.y,sect);
		if(!lineCollides)
			return false;
		return inBox(this.pointA,this.pointB,sect) && inBox(pointA,pointB,sect);
	}

	/**
	 * Checks if the given line segment intersects this line segment
	 *
	 * @param line The line segment to check intersection against
	 *
	 * @return true iff the given line segment intersects this line segment
	 */
	public boolean collide(final Line line)
	{
		return collide(line.pointA,line.pointB);
	}
}
