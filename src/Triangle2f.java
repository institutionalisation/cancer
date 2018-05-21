// https://stackoverflow.com/questions/2049582/how-to-determine-if-a-point-is-in-a-2d-triangle
import org.joml.*;
import java.lang.Math;
public class Triangle2f {
	private final double x3, y3;
	private final double y23, x32, y31, x13;
	private final double det, minD, maxD;
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