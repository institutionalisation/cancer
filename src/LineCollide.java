import org.joml.*;

public class LineCollide
{
	private final Line[] lines;

	public LineCollide(final Line[] lines)
	{
		this.lines = lines;
	}

	public boolean check(final Vector2d pointA,final Vector2d pointB)
	{
		for(final Line x : lines)
			if(x.collide(pointA,pointB))
				return true;
		return false;
	}
}
