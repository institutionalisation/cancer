import org.joml.*;
import java.lang.Math;
import static org.lwjgl.system.MemoryUtil.*;
import java.nio.*;
import static org.lwjgl.glfw.GLFW.*;
public class Player {
	private Keyboard keyboard;
	private Mouse mouse;
	private Vector3f loc = new Vector3f(0,0,0);
	private Matrix4f viewMatrix = new Matrix4f();
	private FloatBuffer viewMatrixBuffer = memAllocFloat(16);
	public Player(Keyboard keyboard,Mouse mouse) {
		this.keyboard = keyboard;
		this.mouse = mouse;
	}
	final float moveSpeed = .005f;
	final static Matrix4f IDENTITY = new Matrix4f();
	final static Vector3f UP = new Vector3f(0,1,0);
	Vector3f scaledUp = new Vector3f();
	public void handleInput(int delta) {
		Vector2f cursorPos = mouse.getCameraCursor();
		float
			ax = -cursorPos.x,
			ay = -cursorPos.y;
		Vector3f
			dir = new Vector3f(
				(float)Math.cos(ay) * (float)Math.sin(ax),
				(float)Math.sin(ay),
				(float)Math.cos(ay) * (float)Math.cos(ax)
			),
			forward = new Vector3f(
				(float)Math.sin(ax),
				0,
				(float)Math.cos(ax)
			),
			right = new Vector3f(
				(float)Math.sin(ax - (float)Math.PI/2),
				0,
				(float)Math.cos(ax - (float)Math.PI/2)
			);
		// kind of magic, but it works
		IDENTITY.lookAt(loc,loc.add(dir,new Vector3f()),right.cross(dir,new Vector3f()),viewMatrix);
		float distance = moveSpeed*delta;
		if(keyboard.getKeysPressed().contains(GLFW_KEY_W))
			loc.add(forward.mul(distance));
		if(keyboard.getKeysPressed().contains(GLFW_KEY_S))
			loc.sub(forward.mul(distance));
		if(keyboard.getKeysPressed().contains(GLFW_KEY_A))
			loc.sub(right.mul(distance));
		if(keyboard.getKeysPressed().contains(GLFW_KEY_D))
			loc.add(right.mul(distance));
		if(keyboard.getKeysPressed().contains(GLFW_KEY_SPACE))
			loc.add(UP.mul(distance,scaledUp));
		if(keyboard.getKeysPressed().contains(GLFW_KEY_LEFT_SHIFT))
			loc.sub(UP.mul(distance,scaledUp));
		System.out.println("view:"+viewMatrix);
	}
	public FloatBuffer getView() {
		return viewMatrix.get(viewMatrixBuffer);
	}
}