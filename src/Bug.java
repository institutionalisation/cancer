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
	private final Vector3f activationPoint;
	private final double activationDistance;
	private boolean activated = false;
	private double tilt = 0;
	private final double tiltSpeed = .5;
	private double targetTilt = 0;

	public void setPath(Vector2d[] path)
	{
		this.path = path;
		pos.x = (float)path[0].x;
		pos.z = (float)path[0].y;
		pathIdx = 1;
		dist = 0.0;
	}

	public Bug(final Matrix4f baseTransform,final ModelNode orig,final Vector3f activationPoint,final double activationDistance,final Vector3f pos)
	{
		set(orig);
		this.baseTransform = baseTransform;
		this.activationPoint = activationPoint;
		this.activationDistance = activationDistance;
		this.pos = pos;
	}

	@Override
	public Matrix4f getLocalTransform()
	{
		if(path != null)
		{
			//System.out.println("at " + pos);
			long now = System.nanoTime();
			long delta = now - lastTime;
			lastTime = now;
			dist += delta / 1000000.0 * speed;
			double pdist;
			while(pathIdx < path.length)
			{
				pdist = path[pathIdx - 1].distance(path[pathIdx]);
				if(pdist > dist)
					break;
				dist -= pdist;
				pos.x = (float)path[pathIdx].x;
				pos.z = (float)path[pathIdx].y;
				targetTilt = atan2(path[pathIdx].x - path[pathIdx - 1].x,path[pathIdx].y - path[pathIdx - 1].y) - PI / 2;
				++pathIdx;
			}
			if(pathIdx < path.length)
			{
				Vector2d dt = new Vector2d();
				path[pathIdx].sub(path[pathIdx - 1],dt);
				double percent = dist / path[pathIdx].distance(path[pathIdx - 1]);
				dt.mul(percent);
				pos.x = (float)(dt.x + path[pathIdx - 1].x);
				pos.z = (float)(dt.y + path[pathIdx - 1].y);
				targetTilt = atan2(path[pathIdx].x - path[pathIdx - 1].x,path[pathIdx].y - path[pathIdx - 1].y) - PI / 2;
			}
			double tDiff = targetTilt - tilt;
			double tsFactor = tDiff % PI / PI;
			double tDist = tsFactor * tsFactor * tiltSpeed;
			if(abs(tDist) < tDist)
				tilt = targetTilt;
			else
				tilt += signum(tDiff) * tDist;
		}
		return new Matrix4f(baseTransform).translate(pos).rotateY((float)tilt);
	}

	public boolean isActivated()
	{
		if(activated)
			return true;
		return activated = activationPoint.distance(pos) < activationDistance;
	}
}
