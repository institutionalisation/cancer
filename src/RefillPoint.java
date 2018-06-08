/*
 * David Jacewicz
 * June 7, 2018
 * Ms. Krasteva
 * Refill point used in level 3
 */

import org.joml.*;
public class RefillPoint {
	public String name;
	public Vector3f loc;
	/**
	 * Creates a new refill point with the given name, location and model node
	 *
	 * @param name The name of this refill point
	 * @param loc The position of this refill point
	 * @param modelNode The model node of this refill point
	 */
	public RefillPoint(String name,Vector3f loc,ModelNode modelNode) {
		this.name = name;
		this.loc = loc;
		modelNode.getLocalTransform().translateLocal(loc);
	}
}
