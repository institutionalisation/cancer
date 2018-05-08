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
		glfwWindowHint(GLFW_VISIBLE,GLFW_FALSE); // the window will stay hidden after creation
		glfwWindowHint(GLFW_RESIZABLE,GLFW_TRUE); // the window will be resizable
		// Create the window
		window = glfwCreateWindow(300, 300, "Hello World!", NULL, NULL);
		if(window == NULL)
			throw new RuntimeException("Failed to create the GLFW window");
		glfwSetKeyCallback(window,keyboard.listener);
		keyboard.getImmediateKeys().put(GLFW_KEY_X,new Runnable() { public void run() {
			glfwSetWindowShouldClose(window, true);
		}});
		glfwSetWindowSizeCallback(window, (window,width,height) -> {
			glViewport(0,0,width,height);
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
	private int getProgramId() throws Exception {
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
		// init
		int programId = getProgramId();
		glUseProgram(programId);

		int vaoId = glGenVertexArrays();
		glBindVertexArray(vaoId);
		
		float[] vertices = new float[]{
	        -.5f, -.5f, 0,
	        -.5f, .5f, 0,
	        .5f, -.5f, 0,
	        .5f, .5f, 0,
	    };
		FloatBuffer verticesBuffer = MemoryUtil.memAllocFloat(vertices.length);
		verticesBuffer.put(vertices).flip();
		int verticesId = glGenBuffers();
		glBindBuffer(GL_ARRAY_BUFFER, verticesId);
		glBufferData(GL_ARRAY_BUFFER, verticesBuffer, GL_STATIC_DRAW);
		memFree(verticesBuffer);
		glVertexAttribPointer(0, 3, GL_FLOAT, false, 0, 0);

		int[] indices = {0,1,2, 1,2,3};
		int indicesId = glGenBuffers();
		IntBuffer indicesBuffer = MemoryUtil.memAllocInt(indices.length);
		indicesBuffer.put(indices).flip();
		glBindBuffer(GL_ELEMENT_ARRAY_BUFFER, indicesId);
		glBufferData(GL_ELEMENT_ARRAY_BUFFER, indicesBuffer, GL_STATIC_DRAW);
		memFree(indicesBuffer);
		
		// Unbind the VBO
		glBindBuffer(GL_ARRAY_BUFFER, 0);
		// Unbind the VAO
		glBindVertexArray(0);


		glClearColor(0,.5f,.5f,0);
		int uniformLoc = glGetUniformLocation(programId,"color");
		glUniform4f(uniformLoc,1,0,0,0);
		/*
		int indexBufferId = glGenBuffers();
		glBindBuffer(GL_ARRAY_BUFFER,indexBufferId);
		glBufferData(GL_ARRAY_BUFFER,indices,GL_STATIC_DRAW);*/
		
		for(;!glfwWindowShouldClose(window);) {
			// clear from last frame
			glClear(GL_COLOR_BUFFER_BIT | GL_DEPTH_BUFFER_BIT);
			// check for keypresses
			glfwPollEvents();
			// bind to the VAO
		    glBindVertexArray(vaoId);
		    glEnableVertexAttribArray(0);
		    // draw
			//glDrawArrays(GL_TRIANGLES,0,vertices.length);
			glDrawElements(GL_TRIANGLES, 6, GL_UNSIGNED_INT, 0);
			System.out.println(glGetError());
			// Restore state
		    glDisableVertexAttribArray(0);
		    glBindVertexArray(0);
			// done frame
			glfwSwapBuffers(window);
		}

		// cleanup
		glDisableVertexAttribArray(0);

		// delete vertex buffer objects
		glBindBuffer(GL_ARRAY_BUFFER, 0);
		glDeleteBuffers(verticesId);
		glDeleteBuffers(indicesId);

		// delete vertex array object
		glBindVertexArray(0);
		glDeleteVertexArrays(vaoId);
	}
	public static void main(String[] args) throws Exception { new Main().run(); }
}