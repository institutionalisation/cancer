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
		// initialize GLFW. most GLFW functions will not work before doing this.
		if(!glfwInit())
			throw new IllegalStateException("Unable to initialize GLFW");
		window = new Window(500,500,"Alternate Perspective",keyboard);
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
		glEnable(GL_DEPTH_TEST);
		glDepthFunc(GL_LEQUAL);
		// https://github.com/LWJGL/lwjgl3-demos/blob/master/src/org/lwjgl/demo/opengl/assimp/WavefrontObjDemo.java
		Model a = new Model("maze0",program);
		glClearColor(0,.5f,.5f,0);
		long prevTime = System.currentTimeMillis();
		Player player = new Player(keyboard,mouse);
		for(;!glfwWindowShouldClose(window.getId());) {
			glfwPollEvents();
			long nowTime = System.currentTimeMillis();
			int delta = (int)(nowTime-prevTime);
			prevTime = nowTime;
			player.handleInput(delta);
			glUniformMatrix4fv(program.getUniformLocation("view"),false,player.getView());
			glClear(GL_COLOR_BUFFER_BIT|GL_DEPTH_BUFFER_BIT);
			for(Mesh x : a.meshes)
				x.render();
			System.out.println("error:"+glGetError());
			glDisableVertexAttribArray(0);
			glBindVertexArray(0);
			window.swapBuffers();
		}
	}
	public static void main(String[] args) throws Exception { new Main().run(); }
}