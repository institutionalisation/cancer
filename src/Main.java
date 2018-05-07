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
import static org.lwjgl.system.MemoryUtil.*;
public class Main {
	// The window handle
	public long window;
	private Keyboard keyboard = new Keyboard();
	public void run() throws Exception {
		System.out.println("Hello LWJGL " + Version.getVersion() + "!");
		init();
		loop();
		deinit();
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
		glfwSetKeyCallback(window, keyboard.listener);
		glfwSetWindowSizeCallback(window, (window, width, height) -> {
			glViewport(0, 0, width, height);
		});
		// Make the OpenGL context current
		glfwMakeContextCurrent(window);
		GL.createCapabilities();
		// Enable v-sync
		glfwSwapInterval(1);
		// Make the window visible
		glfwShowWindow(window);
	}
	private void deinit() throws Exception {
		glfwFreeCallbacks(window);
		glfwDestroyWindow(window);
		glfwTerminate();
		glfwSetErrorCallback(null).free();
	}
	// loads shaders
	private int getProgramID() throws Exception {
		// vertex shader
		int vertexShaderID = glCreateShader(GL_VERTEX_SHADER);
		String vertexShaderCode = Util.readFile("shaders/vertex");
		glShaderSource(vertexShaderID,vertexShaderCode);
		glCompileShader(vertexShaderID);
		if(glGetShaderi(vertexShaderID,GL_COMPILE_STATUS) == GL_FALSE)
			System.out.println("Vertex shader compilation failed: \n"+
				glGetShaderInfoLog(vertexShaderID, glGetShaderi(vertexShaderID, GL_INFO_LOG_LENGTH)));
		// fragment shader
		int fragmentShaderID = glCreateShader(GL_FRAGMENT_SHADER);
		String fragmentShaderCode = Util.readFile("shaders/fragment");
		glShaderSource(fragmentShaderID,fragmentShaderCode);
		glCompileShader(fragmentShaderID);
		if(glGetShaderi(fragmentShaderID,GL_COMPILE_STATUS) == GL_FALSE)
			System.out.println("Vertex shader compilation failed: \n"+
				glGetShaderInfoLog(fragmentShaderID, glGetShaderi(fragmentShaderID, GL_INFO_LOG_LENGTH)));
		// create and link program (which I still don't understand, but ok)
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
	private void loop() throws Exception {
		float[] g_vertex_buffer_data = {
			-1, -1, 0,
			1, -1, 0,
			0, 1, 0,
		};
		int vertexBuffer = glGenBuffers();
		glBindBuffer(GL_ARRAY_BUFFER,vertexBuffer);
		glBufferData(GL_ARRAY_BUFFER,g_vertex_buffer_data,GL_STATIC_DRAW);
		glClearColor(0,.5f,.5f,0);
		glUseProgram(getProgramID());
		//long prev = System.currentTimeMillis();
		for(;!glfwWindowShouldClose(window);) {
			glClear(GL_COLOR_BUFFER_BIT | GL_DEPTH_BUFFER_BIT);
			glfwPollEvents();
			glEnableVertexAttribArray(0);
			glVertexAttribPointer(0, 3, GL_FLOAT, false, 0, 0);
			glDrawArrays(GL_TRIANGLES,0,3);
			glDisableVertexAttribArray(0);
			glfwSwapBuffers(window);
			// move lower-left vertex with A and D keys
			if(keyboard.keysPressed.contains(GLFW_KEY_A))	
				g_vertex_buffer_data[0] -= 0.01;
			if(keyboard.keysPressed.contains(GLFW_KEY_D))
				g_vertex_buffer_data[0] += 0.01;
			glBufferData(GL_ARRAY_BUFFER,g_vertex_buffer_data,GL_STATIC_DRAW);
			//long now = System.currentTimeMillis();
			//System.out.println("delta:"+(now-prev));
			//prev = now;
		}
	}
	public static void main(String[] args) throws Exception { new Main().run(); }
}