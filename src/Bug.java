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
	private boolean active = false;
	private double tilt = 0;

	public synchronized void setPath(Vector2d[] path)
	{
		if((this.path = path) == null)
			return;
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

	public synchronized double distToDest()
	{
		if(path == null)
			return 0.0;
		double dist = 0;
		for(int i = pathIdx;i < path.length;i++)
			dist += path[i].distance(path[i - 1]);
		return dist - this.dist;
	}

	public synchronized void runTime(long delta)
	{
		if(path != null)
		{
			//System.out.println("at " + pos);
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
			}
			tilt = atan2(path[path.length - 1].x - pos.x,path[path.length - 1].y - pos.z) - PI / 2;
		}
		lastTime = System.nanoTime();
	}

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

	public boolean isActive(Vector3f playerPos)
	{
		if(active)
			return true;
		return active = activationPoint.distance(playerPos) < activationDistance;
	}
}
