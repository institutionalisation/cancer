import org.joml.*;
public class RefillPoint {
	public String name;
	public Vector3f loc;
	public RefillPoint(String name,Vector3f loc,Model model) {
		this.name = name;
		this.loc = loc;
		model.transforms.get(0).translateLocal(loc);
	}
}