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
	final static float RADIUS = .1f; // bounding cylinder radius
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

		// collide
		for(Mesh meshWrapper : colliders) {
			AIMesh mesh = meshWrapper.getAIMesh();
			AIFace.Buffer faces = mesh.mFaces();
			AIVector3D.Buffer vertexBuffer = mesh.mVertices();
			//System.out.println("vertexBuffer:"+vertexBuffer);
			for(;faces.hasRemaining();) {
				AIFace face = faces.get();
				IntBuffer indices = face.mIndices();
				Vector2f[] vertices = new Vector2f[3];
				for(int i = 0; i < vertices.length; ++i) {
					int index = indices.get();
					//System.out.println("index:"+index);
					AIVector3D vertex = vertexBuffer.get(index);
					//System.out.println("vertex:"+vertex);
					vertices[i] = new Vector2f(vertex.x(),vertex.z());
				}
				Vector2f locXZ = new Vector2f(loc.x(),loc.z());
				Vector2f a=null, b=null;
				if(vertices[0].distance(vertices[1]) < .01f) {
					a = vertices[0]; b = vertices[2];
				} else
				if(vertices[0].distance(vertices[2]) < .01f) {
					a = vertices[0]; b = vertices[1];
				} else
				if(vertices[1].distance(vertices[2]) < .01f) {
					a = vertices[0]; b = vertices[1];
				} else {
					System.out.println("hecc");
					return;
				}

				// get area of triangle
				float AB = a.distance(b);
				float AP = a.distance(locXZ);
				float BP = b.distance(locXZ);
				if(AB < AP || AB < BP) {
					//System.out.println("aa:"+AB+" "+AP+" "+BP);
					continue;
				}
				float S = (AB+AP+BP)/2;
				float A = (float)Math.sqrt(S*(S-AB)*(S-AP)*(S-BP));

				float h = 2*A/AB;
				if(h<RADIUS) {
					System.out.println("height:"+h);
					System.out.println("area:"+A);
					System.out.println("oh nose!");
					Vector2f wall = a.sub(b,new Vector2f());
					Vector3f normal = new Vector3f(-wall.y(),0,wall.x()).normalize().mul(.1f);
					// but which way does the normal face?
					// oh boy
					// get the winding direction of the triangle via the sign of the following determinant XD
					float det = new Matrix3f(
						1,a.x(),a.y(),
						1,b.x(),b.y(),
						1,locXZ.x(),locXZ.y()
					).determinant();
					System.out.println(det);
					loc.add(normal.mul(0<det?-1:1));
					//System.exit(0);
				}
			}
		}
	}
	public void collide(Mesh meshWrapper) {
		
	}
	public FloatBuffer getView() {
		return viewMatrix.get(viewMatrixBuffer);
	}
}