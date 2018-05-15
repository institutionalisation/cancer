import org.lwjgl.*;
import org.lwjgl.glfw.*;
import org.lwjgl.opengl.*;
import org.lwjgl.system.*;
import java.nio.*;
import static org.lwjgl.glfw.Callbacks.*;
import static org.lwjgl.glfw.GLFW.*;
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
	public void run() throws Exception {
		System.out.println("LWJGL version:"+Version.getVersion());
		init();
		loop();
		deinit();
	}
	private void init() {
		GLFWErrorCallback.createPrint(System.err).set();
		// Initialize GLFW. Most GLFW functions will not work before doing this.
		if(!glfwInit())
			throw new IllegalStateException("Unable to initialize GLFW");
		window = new Window(500,500,"Window Title",keyboard);
		mouse = new Mouse(window);
		//mouse.capture();
		new Runnable() { final Runnable capture = this;
			public void run() {
				mouse.capture();
				keyboard.getImmediateKeys().put(GLFW_KEY_ESCAPE, new Runnable() { public void run() {
					mouse.uncapture();
					keyboard.getImmediateKeys().put(GLFW_KEY_ESCAPE,capture);
				}});
			}
		}.run();
		// Make the OpenGL context current
		glfwMakeContextCurrent(window.getId());
		GL.createCapabilities();
		// Enable v-sync
		glfwSwapInterval(1);
		window.show();
	}
	private void deinit() throws Exception {
		window.destroy();
		glfwTerminate();
		glfwSetErrorCallback(null).free();
	}
	private void loop() throws Exception {
		// init
		Program program = new Program(
			new Shader("vertex",GL_VERTEX_SHADER),
			new Shader("fragment",GL_FRAGMENT_SHADER));
		program.use();
		int vaoId = glGenVertexArrays();
		glBindVertexArray(vaoId);
		float[] vertices = new float[]{
	        0,0,0,
	        0,0,1,
	        0,1,0,
	        0,1,1,
	        1,0,0,
	        1,0,1,
	        1,1,0,
	        1,1,1,
	    };
	    int verticesId = glGenBuffers();
		glBindBuffer(GL_ARRAY_BUFFER,verticesId);
		glBufferData(GL_ARRAY_BUFFER,vertices,GL_STATIC_DRAW);
		glVertexAttribPointer(glGetAttribLocation(program.id,"position"),3,GL_FLOAT,false,0,0);
		int[] indices = {
			0,1,2,
			1,2,3,
			4,5,6,
			5,6,7,
			0,1,4,
			1,4,5,
			2,3,6,
			3,6,7,
			0,2,4,
			2,4,6,
			1,3,5,
			3,5,7,
		};
		int indicesId = glGenBuffers();
		glBindBuffer(GL_ELEMENT_ARRAY_BUFFER,indicesId);
		glBufferData(GL_ELEMENT_ARRAY_BUFFER,indices,GL_STATIC_DRAW);
		float[] colors = new float[]{
			0,0,0,
	        0,0,1,
	        0,1,0,
	        0,1,1,
	        1,0,0,
	        1,0,1,
	        1,1,0,
	        1,1,1,
		};
		int colorsId = glGenBuffers();
		glBindBuffer(GL_ARRAY_BUFFER,colorsId);
		glBufferData(GL_ARRAY_BUFFER,colors,GL_STATIC_DRAW);
		glVertexAttribPointer(glGetAttribLocation(program.id,"inColor"),3,GL_FLOAT,false,0,0);
		// unbind the VBO
		glBindBuffer(GL_ARRAY_BUFFER,0);
		// unbind the VAO
		glBindVertexArray(0);
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
		glClearColor(0,.5f,.5f,0);
		Vector3f loc = new Vector3f(0,0,5f);
		glEnable(GL_DEPTH_TEST);
		glDepthFunc(GL_LEQUAL);
		for(;!glfwWindowShouldClose(window.getId());) {
			glClear(GL_COLOR_BUFFER_BIT | GL_DEPTH_BUFFER_BIT);
			// check for keypresses
			glfwPollEvents();
			Vector2f cursor = mouse.getCameraCursor();
			//System.out.println("loc:"+loc);
		    float
		    	ax = -cursor.x-(float)Math.PI,
		    	ay = -cursor.y;
		    Vector3f dir = new Vector3f(
			    (float)Math.cos(ay) * (float)Math.sin(ax),
			    (float)Math.sin(ay),
			    (float)Math.cos(ay) * (float)Math.cos(ax)
			);
			Vector3f forward = new Vector3f(
				(float)Math.sin(ax),
			    0,
			    (float)Math.cos(ax)
			);
			Vector3f right = new Vector3f(
			    (float)Math.sin(ax - (float)Math.PI/2),
			    0,
			    (float)Math.cos(ax - (float)Math.PI/2)
			);
			//System.out.println("dir:"+dir);
			//System.out.println("right:"+right);
		    if(keyboard.getKeysPressed().contains(GLFW_KEY_W))
		    	loc.add(forward.mul(.1f));
			if(keyboard.getKeysPressed().contains(GLFW_KEY_S))
				loc.sub(forward.mul(.1f));
			if(keyboard.getKeysPressed().contains(GLFW_KEY_A))
				loc.sub(right.mul(.1f));
			if(keyboard.getKeysPressed().contains(GLFW_KEY_D))
				loc.add(right.mul(.1f));
			if(keyboard.getKeysPressed().contains(GLFW_KEY_SPACE))
				loc.add(new Vector3f(0,.1f,0));
			if(keyboard.getKeysPressed().contains(GLFW_KEY_LEFT_SHIFT))
				loc.sub(new Vector3f(0,.1f,0));
			//System.out.println("up:"+right.cross(dir,new Vector3f()));
			//System.out.println("add:"+loc.add(dir,new Vector3f()));
			Matrix4f viewMatrix = new Matrix4f().lookAt(loc,loc.add(dir,new Vector3f()),right.cross(dir,new Vector3f()));
			//System.out.println("view:"+viewMatrix);
			glUniformMatrix4fv(program.getUniformLocation("view"),false,viewMatrix.get(new float[16]));
			// bind to the VAO
		    glBindVertexArray(vaoId);
		    glEnableVertexAttribArray(glGetAttribLocation(program.id,"position"));
		    glEnableVertexAttribArray(glGetAttribLocation(program.id,"inColor"));
			// draw
			glDrawElements(GL_TRIANGLES,indices.length,GL_UNSIGNED_INT,0);
			// restore state
		    glDisableVertexAttribArray(0);
		    glBindVertexArray(0);
			// done frame
			window.swapBuffers();
		}
		// disable VAO
		glDisableVertexAttribArray(0);
		// delete vertex buffer objects
		glBindBuffer(GL_ARRAY_BUFFER,0);
		glDeleteBuffers(verticesId);
		glDeleteBuffers(indicesId);
		glDeleteBuffers(colorsId);
		// delete vertex array object
		glBindVertexArray(0);
		glDeleteVertexArrays(vaoId);
	}
	public static void main(String[] args) throws Exception { new Main().run(); }
}
