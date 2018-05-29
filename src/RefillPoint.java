import org.joml.*;
public class RefillPoint {
	public String name;
	public Vector3f loc;
	public RefillPoint(String name,Vector3f loc,ModelNode modelNode) {
		this.name = name;
		this.loc = loc;
		modelNode.defaultTransform.translateLocal(loc);
	}
}