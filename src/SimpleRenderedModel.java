import org.joml.*;

public class SimpleRenderedModel extends ModelNode
{
	private final Matrix4f transform;

	public SimpleRenderedModel(final ModelNode orig,final Matrix4f transform)
	{
		set(orig);
		this.transform = transform;
	}

	public Matrix4f getLocalTransform()
	{
		return transform;
	}
}
