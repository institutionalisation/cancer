/*
 * David Jacewicz
 * May 31, 2018
 * Ms. Krasteva
 * A triangle
 */

/*
 * Modification: use floats instead of doubles
 * Junyi Wang
 * June 7, 2018
 * 2 minutes
 * Version: 0.05
 */

import org.joml.*;
import java.lang.Math;
public class Triangle2f {
	private final float x3, y3;
	private final float y23, x32, y31, x13;
	private final float det, minD, maxD;

	/**
	 * Creates a new triangle from three vertices
	 *
	 * @param v1 The first vertex
	 * @param v2 The second vertex
	 * @param v3 The third vertex
	 */
	public Triangle2f(Vector2f v1,Vector2f v2,Vector2f v3) {
		this.x3 = v3.x;
		this.y3 = v3.y;
		y23 = v2.y - v3.y;
		x32 = v3.x - v2.x;
		y31 = v3.y - v1.y;
		x13 = v1.x - v3.x;
		det = y23*x13 - x32*y31;
		minD = Math.min(det,0);
		maxD = Math.max(det,0);
	}

	/**
	 * Checks if a point is contained in this triangle
	 *
	 * @param point The point to check
	 *
	 * @return true iff the point is contained in this triangle, false otherwise
	 */
	// TODO: access level
	boolean contains(Vector2f v) {
		double dx = v.x - x3;
		double dy = v.y - y3;
		double a = y23*dx + x32*dy;
		if(a<minD || maxD<a)
			return false;
		double b = y31*dx + x13*dy;
		if(b<minD || maxD<b)
			return false;
		double c = det - a - b;
		if(c<minD || maxD<c)
			return false;
		return true;
	}
}
