/*
 * Junyi Wang
 * June 8, 2018
 * Ms. Krasteva
 * A model node wrapper
 */

import org.joml.*;

public class SimpleRenderedModel extends ModelNode
{
	private final Matrix4f transform;

	/**
	 * Creates a model wrapper wrapping the specified model node with the given default transform matrix
	 *
	 * @param orig The wrapped mode node
	 * @param transform The default transform matrix
	 */
	public SimpleRenderedModel(final ModelNode orig,final Matrix4f transform)
	{
		set(orig);
		this.transform = transform;
	}

	/** @return The transform matrix */
	public Matrix4f getLocalTransform()
	{
		return transform;
	}
}
