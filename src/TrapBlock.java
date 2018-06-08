import org.joml.*;
import static java.lang.Math.*;

public class TrapBlock extends ModelNode
{
	private long startTime = 0;
	private final long closeTime;
	private final Vector3f start;
	private final Vector3f diff;
	private final Matrix4f baseTransform;

	public TrapBlock(final ModelNode orig,final Matrix4f baseTransform,final Vector3f start,final Vector3f end,final long closeTime)
	{
		set(orig);
		this.baseTransform = baseTransform;
		this.start = start;
		this.diff = end.sub(start);
		this.closeTime = closeTime;
	}

	public void start()
	{
		startTime = System.nanoTime();
	}

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
