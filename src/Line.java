import org.joml.*;
import static java.lang.Math.*;

public class Line
{
	public final Vector2d pointA;
	public final Vector2d pointB;

	public Line(final Vector2d pointA,final Vector2d pointB)
	{
		this.pointA = pointA;
		this.pointB = pointB;
	}

	private static boolean inBox(Vector2d pointA,Vector2d pointB,Vector2d sect)
	{
		return min(pointA.x,pointB.x) <= sect.x && sect.x <= max(pointA.x,pointB.x)
			&& min(pointA.y,pointB.y) <= sect.y && sect.y <= max(pointA.y,pointB.y);
	}

	public boolean collide(final Vector2d pointA,final Vector2d pointB)
	{
		Vector2d sect = new Vector2d();
		boolean lineCollides = Intersectiond.intersectLineLine(this.pointA.x,this.pointA.y,this.pointB.x,this.pointB.y,pointA.x,pointA.y,pointB.x,pointB.y,sect);
		if(!lineCollides)
			return false;
		return inBox(this.pointA,this.pointB,sect) && inBox(pointA,pointB,sect);
	}

	public boolean collide(final Line line)
	{
		return collide(line.pointA,line.pointB);
	}
}
