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
import static org.lwjgl.opengl.GL33.*;
import static org.lwjgl.system.MemoryStack.*;
import static org.lwjgl.system.MemoryUtil.*;

public class HelloWorld {
	// The window handle
	private long window;
	public void run() {
		System.out.println("Hello LWJGL " + Version.getVersion() + "!");
		init();
		loop();
		// Free the window callbacks and destroy the window
		glfwFreeCallbacks(window);
		glfwDestroyWindow(window);
		// Terminate GLFW and free the error callback
		glfwTerminate();
		glfwSetErrorCallback(null).free();
	}
	private void init() {
		// Setup an error callback. The default implementation
		// will print the error message in System.err.
		GLFWErrorCallback.createPrint(System.err).set();

		// Initialize GLFW. Most GLFW functions will not work before doing this.
		if(!glfwInit())
			throw new IllegalStateException("Unable to initialize GLFW");
		// Configure GLFW
		glfwDefaultWindowHints(); // optional, the current window hints are already the default
		glfwWindowHint(GLFW_VISIBLE, GLFW_FALSE); // the window will stay hidden after creation
		glfwWindowHint(GLFW_RESIZABLE, GLFW_TRUE); // the window will be resizable
		// Create the window
		window = glfwCreateWindow(300, 300, "Hello World!", NULL, NULL);

		if(window == NULL)
			throw new RuntimeException("Failed to create the GLFW window");
		// Setup a key callback. It will be called every time a key is pressed, repeated or released.
		glfwSetKeyCallback(window, (window, key, scancode, action, mods) -> {
			if(key == GLFW_KEY_ESCAPE && action == GLFW_RELEASE)
				glfwSetWindowShouldClose(window, true); // We will detect this in the rendering loop
		});
		glfwSetWindowSizeCallback(window, (window, width, height) -> {
			//Updates the matrices
			glViewport(0, 0, width, height);
		});
		// Get the thread stack and push a new frame
		try ( MemoryStack stack = stackPush() ) {
			IntBuffer pWidth = stack.mallocInt(1); // int*
			IntBuffer pHeight = stack.mallocInt(1); // int*
			// Get the window size passed to glfwCreateWindow
			glfwGetWindowSize(window, pWidth, pHeight);
			// Get the resolution of the primary monitor
			GLFWVidMode vidmode = glfwGetVideoMode(glfwGetPrimaryMonitor());
			// Center the window
			glfwSetWindowPos(
				window,
				(vidmode.width() - pWidth.get(0)) / 2,
				(vidmode.height() - pHeight.get(0)) / 2
			);
		} // the stack frame is popped automatically

		// Make the OpenGL context current
		glfwMakeContextCurrent(window);
		GL.createCapabilities();
		System.out.print("hecc:");
		System.out.println(glGetError());
		// Enable v-sync
		glfwSwapInterval(1);
		// Make the window visible
		glfwShowWindow(window);
	}
	private int loadShaders() {
		// vertex shader
		int vertexShaderID = glCreateShader(GL_VERTEX_SHADER);
		String vertexShaderCode =
			"#version 310 es\n"+
			"layout(location = 0) in vec2 position;\n"+
			"void main() {\n"+
				"gl_Position = vec4(position, 0.0, 1.0);\n"+
			"}";
		glShaderSource(vertexShaderID,vertexShaderCode);
		glCompileShader(vertexShaderID);
		if(glGetShaderi(vertexShaderID,GL_COMPILE_STATUS) == GL_FALSE)
			System.out.println("Vertex shader compilation failed: \n"+
				glGetShaderInfoLog(vertexShaderID, glGetShaderi(vertexShaderID, GL_INFO_LOG_LENGTH)));
		// fragment shader
		int fragmentShaderID = glCreateShader(GL_FRAGMENT_SHADER);
		String fragmentShaderCode =
			"#version 310 es\n"+
			"precision mediump float;\n"+
			"out vec4 fragColor;\n"+
			"void main() {\n"+
				"fragColor = vec4(1,0,0,0);\n"+
			"}";
		glShaderSource(fragmentShaderID,fragmentShaderCode);
		glCompileShader(fragmentShaderID);
		if(glGetShaderi(fragmentShaderID,GL_COMPILE_STATUS) == GL_FALSE)
			System.out.println("Vertex shader compilation failed: \n"+
				glGetShaderInfoLog(fragmentShaderID, glGetShaderi(fragmentShaderID, GL_INFO_LOG_LENGTH)));
		// create and link program (which I guess is an OpenGL context with these shaders)
		int programID = glCreateProgram();
		glAttachShader(programID,vertexShaderID);
		glAttachShader(programID,fragmentShaderID);
		glLinkProgram(programID);
		if(glGetProgrami(programID,GL_LINK_STATUS) == GL_FALSE)
			System.out.println("Error linking program.");
		glDetachShader(programID,vertexShaderID);
		glDetachShader(programID,fragmentShaderID);
		glDeleteShader(vertexShaderID);
		glDeleteShader(fragmentShaderID);
		return programID;
	}
	private void loop() {
		// This line is critical for LWJGL's interoperation with GLFW's
		// OpenGL context, or any context that is managed externally.
		// LWJGL detects the context that is current in the current thread,
		// creates the GLCapabilities instance and makes the OpenGL
		// bindings available for use.
		int programID = loadShaders();
		float[] g_vertex_buffer_data = {
			-1, -1, 0,
			1, -1, 0,
			0, 1, 0,
		};
		int vertexBuffer = glGenBuffers();
		glBindBuffer(GL_ARRAY_BUFFER,vertexBuffer);
		glBufferData(GL_ARRAY_BUFFER,g_vertex_buffer_data,GL_STATIC_DRAW);
		glClearColor(0,.5f,.5f,0);
		glUseProgram(programID);
		long prev = System.currentTimeMillis();
		while(!glfwWindowShouldClose(window)) {
			glClear(GL_COLOR_BUFFER_BIT | GL_DEPTH_BUFFER_BIT);
			glfwPollEvents();
			glEnableVertexAttribArray(0);
			glVertexAttribPointer(0, 3, GL_FLOAT, false, 0, 0);
			glDrawArrays(GL_TRIANGLES,0,3);
			glDisableVertexAttribArray(0);
			glfwSwapBuffers(window);
			g_vertex_buffer_data[0] += 0.01;
			glBufferData(GL_ARRAY_BUFFER,g_vertex_buffer_data,GL_STATIC_DRAW);
			long now = System.currentTimeMillis();
			System.out.println("delta:"+(now-prev));
			prev = now;
		}
	}
	public static void main(String[] args) { new HelloWorld().run(); }
}