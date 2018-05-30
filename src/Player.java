import org.joml.*;
import java.lang.Math;
import static org.lwjgl.system.MemoryUtil.*;
import java.nio.*;
import static org.lwjgl.glfw.GLFW.*;
import org.lwjgl.assimp.*;
import java.util.*;
import static util.Util.*;
public class Player { final Player player = this;
	private Keyboard keyboard;
	private Mouse mouse;
	public Vector3f loc = new Vector3f(0,1,0);
	private Matrix4f viewMatrix = new Matrix4f();
	private FloatBuffer viewMatrixBuffer = memAllocFloat(16);
	public List<ModelNode> colliders = new ArrayList<>();
	boolean flying = true;
	public Player(Keyboard keyboard,Mouse mouse) {
		this.keyboard = keyboard;
		this.mouse = mouse;
		this.colliders = colliders;
		keyboard.immediateKeys.put(GLFW_KEY_Q,new Runnable() {
			final Runnable stopFlying = this;
			public void run() {
				player.flying = false;
				keyboard.immediateKeys.put(GLFW_KEY_Q,()->{
					player.flying = true;
					keyboard.immediateKeys.put(GLFW_KEY_Q,stopFlying);
				});
			}
		});
	}
	final float moveSpeed = .005f;
	final static Matrix4f IDENTITY = new Matrix4f();
	final static Vector3f UP = new Vector3f(0,1,0);
	final static float
		RADIUS = .3f, // bounding cylinder radius
		STEP_MAX_HEIGHT = .2f, // max height to step over
		FOOT_OFFSET = 1.5f,
		HEAD_OFFSET = .4f,
		INITIAL_DY = 15/1000f,
		GRAVITY = .00004f;
	float dy = 0;
	boolean grounded = true;
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
		keyRun(GLFW_KEY_W,forward.mul(distance,new Vector3f()));
		keyRun(GLFW_KEY_S,forward.mul(-distance,new Vector3f()));
		keyRun(GLFW_KEY_D,right.mul(distance,new Vector3f()));
		keyRun(GLFW_KEY_A,right.mul(-distance,new Vector3f()));
		if(flying) {
			keyRun(GLFW_KEY_SPACE,UP.mul(distance,new Vector3f()));
			keyRun(GLFW_KEY_LEFT_SHIFT,UP.mul(-distance,new Vector3f()));
		} else {
			if(keyboard.getKeysPressed().contains(GLFW_KEY_SPACE) && dy==0)
				dy = INITIAL_DY;
			loc.y += dy*delta;
			dy -= GRAVITY*delta;
			//System.out.println("dy:"+dy);
		}
		//System.out.println("view:"+viewMatrix);
		// collide
		for(ModelNode modelNode : colliders)
			collide(modelNode);
	}
	private void collide(ModelNode modelNode) {
		for(ModelNode child : modelNode.children)
			collide(child);
		for(Mesh meshWrapper : modelNode.meshes) {
			AIMesh mesh = meshWrapper.getAIMesh();
			AIFace.Buffer faces = mesh.mFaces();
			AIVector3D.Buffer vertexBuffer = mesh.mVertices();
			for(int j = 0;faces.hasRemaining();++j) {
				AIFace face = faces.get();
				IntBuffer indices = face.mIndices();
				Vector3f[] vertices3D = new Vector3f[3];
				for(int i = 0; indices.hasRemaining(); ++i) {
					AIVector3D vertex = vertexBuffer.get(indices.get());
					vertices3D[i] = new Vector3f(vertex.x(),vertex.y(),vertex.z());
					modelNode.absoluteTransform.transformPosition(vertices3D[i]);
				}
				float
					lowest=Float.MAX_VALUE,
					highest=Float.MIN_VALUE;
				for(Vector3f x : vertices3D) {
					lowest = Math.min(lowest,x.y);
					highest = Math.max(highest,x.y);
				}
				if(loc.y()+HEAD_OFFSET<lowest || highest<loc.y()-FOOT_OFFSET)
					continue;
				//System.out.println("in level");
				//else
				//	System.out.println("ha");
				Vector2f[] vertices = new Vector2f[3];
				for(int i = 0; i < vertices.length; ++i)
					vertices[i] = new Vector2f(vertices3D[i].x(),vertices3D[i].z());
				Vector2f locXZ = new Vector2f(loc.x(),loc.z());
				if(
					highest-lowest < .01f &&
					new Triangle2f(vertices[0],vertices[1],vertices[2]).contains(locXZ)
				) {
					dy = 0;
					loc.y = highest + FOOT_OFFSET;
					continue;
				}
				Vector2f a=null, b=null;
				// find which vertices are overlapping, ignore one of them
				if(vertices[0].distance(vertices[1]) < .05f) {
					a = vertices[0]; b = vertices[2];
				} else
				if(vertices[0].distance(vertices[2]) < .05f) {
					a = vertices[0]; b = vertices[1];
				} else
				if(vertices[1].distance(vertices[2]) < .05f) {
					a = vertices[0]; b = vertices[1];
				} else
					continue;
				// sidelengths of triangle. vertices are a,b,loc
				float AB = a.distance(b);
				float AP = a.distance(locXZ);
				float BP = b.distance(locXZ);
				// not in front of wall and can't step over
				if((AB < AP || AB < BP) && loc.y-FOOT_OFFSET<highest) {
					// did we just hit one vertex?
					for(Vector2f vertex : vertices) {
						float dist = vertex.distance(locXZ);
						if(dist < RADIUS) {
							//System.out.println("vertex");
							Vector2f bounce = vertex.sub(locXZ,new Vector2f()).normalize().mul(dist-RADIUS);
							loc.x += bounce.x;
							loc.z += bounce.y;
							break;
						}
					}
					continue;
				}
				// use heron's formula to find height (distance to wall)
				final float S = (AB+AP+BP)/2;
				final float A = (float)Math.sqrt(S*(S-AB)*(S-AP)*(S-BP));
				final float h = 2*A/AB;
				// if in wall, move to just outside wall
				if(h<RADIUS) {
					//System.out.println("bounce:"+j);
					// if the wall is small enough, step over it
					if(highest<loc.y-STEP_MAX_HEIGHT+.1f) {
						//System.out.println("step");
						loc.y = highest+FOOT_OFFSET;
						dy = 0;
						continue;
					}
					Vector2f wall = a.sub(b,new Vector2f());
					Vector3f direction = new Vector3f(wall.y(),0,-wall.x()).normalize();
					// but which way does the normal face?
					// oh boy
					// https://en.wikipedia.org/wiki/Curve_orientation
					// get the winding direction of the triangle via the sign of the following determinant XD
					loc.add(direction.mul(Math.signum(new Matrix3f(
						1,a.x,a.y,
						1,b.x,b.y,
						1,locXZ.x,locXZ.y
					).determinant())).mul(RADIUS-h));
				}
			}
		}
	}
	public void keyRun(int key,Vector3f direction) {
		if(keyboard.getKeysPressed().contains(key))
			loc.add(direction);
	}
	public FloatBuffer getView() {
		return viewMatrix.get(viewMatrixBuffer);
	}
}