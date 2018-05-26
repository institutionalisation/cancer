import org.joml.*;
import java.lang.Math;
import static org.lwjgl.system.MemoryUtil.*;
import java.nio.*;
import static org.lwjgl.glfw.GLFW.*;
import org.lwjgl.assimp.*;
import java.util.*;
import static util.Util.*;
public class Player {
	private Keyboard keyboard;
	private Mouse mouse;
	public Vector3f loc = new Vector3f(0,2,0);
	private Matrix4f viewMatrix = new Matrix4f();
	private FloatBuffer viewMatrixBuffer = memAllocFloat(16);
	public List<Mesh> colliders = new ArrayList<>();
	public Player(Keyboard keyboard,Mouse mouse) {
		this.keyboard = keyboard;
		this.mouse = mouse;
		this.colliders = colliders;
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
		GRAVITY = .4f/1000f;
	float dy = 0;
	boolean grounded = true;
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
		keyRun(GLFW_KEY_W,forward.mul(distance,new Vector3f()));
		keyRun(GLFW_KEY_S,forward.mul(-distance,new Vector3f()));
		keyRun(GLFW_KEY_D,right.mul(distance,new Vector3f()));
		keyRun(GLFW_KEY_A,right.mul(-distance,new Vector3f()));
		// if(keyboard.getKeysPressed().contains(GLFW_KEY_SPACE) && dy==0)
		// 	dy = INITIAL_DY;
		// loc.y += dy*delta;
		// dy -= GRAVITY;
		// System.out.println("dy:"+dy);
		keyRun(GLFW_KEY_SPACE,UP.mul(distance,scaledUp));
		keyRun(GLFW_KEY_LEFT_SHIFT,UP.mul(-distance,scaledUp));
		//System.out.println("view:"+viewMatrix);
		// collide
		for(Mesh meshWrapper : colliders) {
			System.out.println("collider");
			AIMesh mesh = meshWrapper.getAIMesh();
			AIFace.Buffer faces = mesh.mFaces();
			AIVector3D.Buffer vertexBuffer = mesh.mVertices();
			//System.out.println("vertexBuffer:"+vertexBuffer);
			for(int j = 0;faces.hasRemaining();++j) {
				AIFace face = faces.get();
				IntBuffer indices = face.mIndices();
				float
					lowest=Float.MAX_VALUE,
					highest=Float.MIN_VALUE;
				for(;indices.hasRemaining();) {

					// LOOK HERE, I'm taking z as y

					float y = vertexBuffer.get(indices.get()).z();
					lowest = Math.min(lowest,y);
					highest = Math.max(highest,y);
				}
				indices.clear();
				if(loc.y()+HEAD_OFFSET<lowest || highest<loc.y()-FOOT_OFFSET)
					continue;
				//else
				//	System.out.println("ha");
				Vector2f[] vertices = new Vector2f[3];
				for(int i = 0; i < vertices.length; ++i) {
					int index = indices.get();
					AIVector3D vertex = vertexBuffer.get(index);

					// look here, I'm taking y as z (and negating it for some reason)

					vertices[i] = new Vector2f(vertex.x(),-vertex.y());
				}
				Vector2f locXZ = new Vector2f(loc.x(),loc.z());
				if(
					highest-lowest < .01f &&
					new Triangle2f(vertices[0],vertices[1],vertices[2]).contains(locXZ)
				) {
					//System.out.println("floor");
					dy = 0;
					loc.y = highest + FOOT_OFFSET;
					continue;
				}
				Vector2f a=null, b=null;
				// find which vertices are overlapping, ignore one of them
				if(vertices[0].distance(vertices[1]) < .01f) {
					a = vertices[0]; b = vertices[2];
				} else
				if(vertices[0].distance(vertices[2]) < .01f) {
					a = vertices[0]; b = vertices[1];
				} else
				if(vertices[1].distance(vertices[2]) < .01f) {
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