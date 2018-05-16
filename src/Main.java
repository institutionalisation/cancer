import org.lwjgl.*;
import org.lwjgl.glfw.*;
import static org.lwjgl.glfw.GLFW.*;
import org.lwjgl.opengl.*;
import org.lwjgl.system.*;
import java.nio.*;
import org.lwjgl.assimp.*;
import static org.lwjgl.assimp.Assimp.*;
import static org.lwjgl.glfw.Callbacks.*;
import static org.lwjgl.opengl.GL11.*;
import static org.lwjgl.opengl.GL15.*;
import static org.lwjgl.opengl.GL20.*;
import static org.lwjgl.opengl.GL30.*;
import static org.lwjgl.system.MemoryUtil.*;
import org.joml.*;
import java.lang.Math;
public class Main {
	private Keyboard keyboard = new Keyboard();
	private Mouse mouse;
	public Window window;
	public Program program;
	public void run() throws Exception {
		System.out.println("LWJGL version:"+Version.getVersion());
		init();
		loop();
		deinit();
	}
	private void init() throws Exception {
		GLFWErrorCallback.createPrint(System.err).set();
		// Initialize GLFW. Most GLFW functions will not work before doing this.
		if(!glfwInit())
			throw new IllegalStateException("Unable to initialize GLFW");
		window = new Window(500,500,"Window Title",keyboard);
		mouse = new Mouse(window);
		new Runnable() { final Runnable capture = this;
			public void run() {
				mouse.capture(true);
				keyboard.getImmediateKeys().put(GLFW_KEY_ESCAPE, new Runnable() { public void run() {
					mouse.capture(false);
					keyboard.getImmediateKeys().put(GLFW_KEY_ESCAPE,capture);
				}});
			}
		}.run();
		window.makeContextCurrent();
		GL.createCapabilities();
		// Enable v-sync
		glfwSwapInterval(1);
		window.show();
		program = new Program(
			new Shader("vertex",GL_VERTEX_SHADER),
			new Shader("fragment",GL_FRAGMENT_SHADER));
		program.use();
		GLFWWindowSizeCallback sizeCallback = new GLFWWindowSizeCallback() { 
			public void invoke(long windowId,int width,int height) {
				System.out.println("resize");
				final float FOV = (float) Math.toRadians(100f);
			    final float Z_NEAR = .5f;
			    final float Z_FAR = 100;
			    Matrix4f perspectiveMatrix;
			    System.out.println("w:"+width+",h:"+height);
			    float aspectRatio = 1f*width/height;
				perspectiveMatrix = new Matrix4f().perspective(FOV,aspectRatio,Z_NEAR,Z_FAR);
				glUniformMatrix4fv(program.getUniformLocation("perspective"),false,
					perspectiveMatrix.get(new float[16]));
				glViewport(0,0,width,height);
			}
		};
		sizeCallback.invoke(0,300,300);
		glfwSetWindowSizeCallback(window.getId(),sizeCallback);
	}
	private void deinit() throws Exception {
		window.destroy();
		glfwTerminate();
		glfwSetErrorCallback(null).free();
	}
	private void loop() throws Exception {
		// https://github.com/LWJGL/lwjgl3-demos/blob/master/src/org/lwjgl/demo/opengl/assimp/WavefrontObjDemo.java
		Model a = new Model(aiImportFile("models/teapot.obj",0));
		Mesh b = a.meshes[0];
		glBindBuffer(GL_ARRAY_BUFFER,b.vertexArrayBuffer);
		System.out.println("aa:"+glGetError());
		glVertexAttribPointer(glGetAttribLocation(program.getId(),"position"),3,GL_FLOAT,false,0,0);
		glBindBuffer(GL_ELEMENT_ARRAY_BUFFER,b.elementArrayBuffer);
		glClearColor(0,.5f,.5f,0);
		Vector3f loc = new Vector3f(0,0,0);
		long prevTime = System.currentTimeMillis();
		System.out.println("bb:"+glGetError());
		for(;!glfwWindowShouldClose(window.getId());) {
			System.out.println("loc:"+loc);
			glfwPollEvents();
			long nowTime = System.currentTimeMillis();
			long delta = nowTime-prevTime;
			prevTime = nowTime;
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
			if(keyboard.getKeysPressed().contains(GLFW_KEY_W))
		    	loc.add(forward.mul(.005f).mul(delta));
			if(keyboard.getKeysPressed().contains(GLFW_KEY_S))
				loc.sub(forward.mul(.005f).mul(delta));
			if(keyboard.getKeysPressed().contains(GLFW_KEY_A))
				loc.sub(right.mul(.005f).mul(delta));
			if(keyboard.getKeysPressed().contains(GLFW_KEY_D))
				loc.add(right.mul(.005f).mul(delta));
			if(keyboard.getKeysPressed().contains(GLFW_KEY_SPACE))
				loc.add(new Vector3f(0,.005f,0).mul(delta));
			if(keyboard.getKeysPressed().contains(GLFW_KEY_LEFT_SHIFT))
				loc.sub(new Vector3f(0,.005f,0).mul(delta));
			Matrix4f viewMatrix = new Matrix4f().lookAt(loc,loc.add(dir,new Vector3f()),right.cross(dir,new Vector3f()));
			glUniformMatrix4fv(program.getUniformLocation("view"),false,viewMatrix.get(new float[16]));
			glClear(GL_COLOR_BUFFER_BIT | GL_DEPTH_BUFFER_BIT);
			glBindBuffer(GL_ARRAY_BUFFER,b.vertexArrayBuffer);
			glBindBuffer(GL_ELEMENT_ARRAY_BUFFER,b.elementArrayBuffer);
			glDrawElements(GL_TRIANGLES,b.elementCount,GL_UNSIGNED_INT,0);
			glBindBuffer(GL_ARRAY_BUFFER,0);
			glBindBuffer(GL_ELEMENT_ARRAY_BUFFER,0);
			window.swapBuffers();
		}
	}
	public static void main(String[] args) throws Exception { new Main().run(); }
}