import org.joml.*;

public class Bug extends ModelNode
{
	private long lastTime = System.nanoTime();
	private final Matrix4f baseTransform;
	private final Matrix4f curPos = new Matrix4f();
	private Vector2d[] path = null;
	private int pathIdx = 1;
	public double speed = 0.002;
	public final Vector3f pos = new Vector3f();
	private double dist = 0.0;

	public void setPath(Vector2d[] path)
	{
		this.path = path;
		pos.x = (float)path[0].x;
		pos.z = (float)path[0].y;
		pathIdx = 1;
		dist = 0.0;
	}

	public Bug(final Matrix4f baseTransform,final ModelNode orig)
	{
		orig.set(this);
		this.baseTransform = baseTransform;
	}

	@Override
	public Matrix4f getLocalTransform()
	{
		if(path == null)
			return new Matrix4f(baseTransform).translate(pos);
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
			++pathIdx;
		}
		if(pathIdx < path.length)
		{
			Vector2d dt = new Vector2d();
			path[pathIdx].sub(path[pathIdx - 1],dt);
			dt.mul(dist / path[pathIdx].distance(path[pathIdx - 1]));
			pos.x = (float)(dt.x + path[pathIdx - 1].x);
			pos.z = (float)(dt.y + path[pathIdx - 1].y);
		}
		return new Matrix4f(baseTransform).translate(pos);
	}
}
