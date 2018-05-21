import org.joml.*;
import java.lang.Math;
import static org.lwjgl.system.MemoryUtil.*;
import java.nio.*;
import static org.lwjgl.glfw.GLFW.*;
import org.lwjgl.assimp.*;
public class Player {
	private Keyboard keyboard;
	private Mouse mouse;
	private Vector3f loc = new Vector3f(0,0,0);
	private Matrix4f viewMatrix = new Matrix4f();
	private FloatBuffer viewMatrixBuffer = memAllocFloat(16);
	private Mesh[] colliders;
	public Player(Keyboard keyboard,Mouse mouse,Mesh[]colliders) {
		this.keyboard = keyboard;
		this.mouse = mouse;
		this.colliders = colliders;
	}
	final float moveSpeed = .005f;
	final static Matrix4f IDENTITY = new Matrix4f();
	final static Vector3f UP = new Vector3f(0,1,0);
	final static float
		RADIUS = .5f, // bounding cylinder radius
		STEP_MAX_HEIGHT = .2f; // max height to step over
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
		keyRun(GLFW_KEY_SPACE,UP.mul(distance,scaledUp));
		keyRun(GLFW_KEY_LEFT_SHIFT,UP.mul(-distance,scaledUp));
		System.out.println("view:"+viewMatrix);
		// collide
		for(Mesh meshWrapper : colliders) {
			AIMesh mesh = meshWrapper.getAIMesh();
			AIFace.Buffer faces = mesh.mFaces();
			AIVector3D.Buffer vertexBuffer = mesh.mVertices();
			//System.out.println("vertexBuffer:"+vertexBuffer);
			for(;faces.hasRemaining();) {
				AIFace face = faces.get();
				IntBuffer indices = face.mIndices();
				float
					lowest=Float.MAX_VALUE,
					highest=Float.MIN_VALUE;
				for(;indices.hasRemaining();) {
					float y = vertexBuffer.get(indices.get()).y();
					lowest = Math.min(lowest,y);
					highest = Math.max(highest,y);
				}
				indices.clear();
				if(highest+RADIUS<loc.y() || loc.y()<lowest-RADIUS)
					continue;
				Vector2f[] vertices = new Vector2f[3];
				for(int i = 0; i < vertices.length; ++i) {
					int index = indices.get();
					AIVector3D vertex = vertexBuffer.get(index);
					vertices[i] = new Vector2f(vertex.x(),vertex.z());
				}
				Vector2f locXZ = new Vector2f(loc.x(),loc.z());
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
					return;
				// sidelengths of triangle. vertices are a,b,loc
				float AB = a.distance(b);
				float AP = a.distance(locXZ);
				float BP = b.distance(locXZ);
				// not in front of wall, don't bounce off wall
				if(AB < AP || AB < BP) {
					// did we just hit one vertex?
					for(Vector2f vertex : vertices) {
						float dist = vertex.distance(locXZ);
						if(dist < RADIUS) {
							Vector2f bounce = vertex.sub(locXZ,new Vector2f()).normalize().mul(dist-RADIUS);
							loc.x += bounce.x;
							loc.z += bounce.y;
							break;
						}
					}
					continue;
				}
				// use heron's formula to find height (distance to wall)
				float S = (AB+AP+BP)/2;
				float A = (float)Math.sqrt(S*(S-AB)*(S-AP)*(S-BP));
				float h = 2*A/AB;
				// if in wall, move to just outside wall
				if(h<RADIUS) {
					// but if the wall is small, then step over it
					if(highest-loc.y()<STEP_MAX_HEIGHT) {
						loc.y = highest+RADIUS;
						continue;
					}
					Vector2f wall = a.sub(b,new Vector2f());
					Vector3f normal = new Vector3f(wall.y(),0,-wall.x()).normalize().mul(RADIUS-h);
					// but which way does the normal face?
					// oh boy
					// https://en.wikipedia.org/wiki/Curve_orientation
					// get the winding direction of the triangle via the sign of the following determinant XD
					loc.add(normal.mul(Math.signum(new Matrix3f(
						1,a.x(),a.y(),
						1,b.x(),b.y(),
						1,locXZ.x(),locXZ.y()
					).determinant())));
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