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
	public Window window;
	
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
		window = new Window(500,500,"Window Title",keyboard);
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
		Program program = new Program(
			new Shader("vertex",GL_VERTEX_SHADER),
			new Shader("fragment",GL_FRAGMENT_SHADER));
		program.use();

		int vaoId = glGenVertexArrays();
		glBindVertexArray(vaoId);
		
		float[] vertices = new float[]{
	        -1f, -1f, -3f,
	        -1f, 1f, -3f,
	        1f, -1f, -3f,
	        1f, 1f, -3f,
	    };
		FloatBuffer verticesBuffer = MemoryUtil.memAllocFloat(vertices.length);
		verticesBuffer.put(vertices).flip();
		int verticesId = glGenBuffers();
		glBindBuffer(GL_ARRAY_BUFFER,verticesId);
		glBufferData(GL_ARRAY_BUFFER,vertices,GL_STATIC_DRAW);
		memFree(verticesBuffer);
		glVertexAttribPointer(glGetAttribLocation(program.id,"position"), 3, GL_FLOAT, false, 0, 0);

		int[] indices = {0,1,2, 1,2,3};
		int indicesId = glGenBuffers();
		IntBuffer indicesBuffer = MemoryUtil.memAllocInt(indices.length);
		indicesBuffer.put(indices).flip();
		glBindBuffer(GL_ELEMENT_ARRAY_BUFFER,indicesId);
		glBufferData(GL_ELEMENT_ARRAY_BUFFER,indicesBuffer,GL_STATIC_DRAW);
		memFree(indicesBuffer);

		float[] colors = new float[]{
			1,0,0,
			0,1,1,
			0,1,0,
			0,0,1,
		};
		int colorsId = glGenBuffers();
		FloatBuffer colorsBuffer = MemoryUtil.memAllocFloat(colors.length);
		colorsBuffer.put(colors).flip();
		glBindBuffer(GL_ARRAY_BUFFER,colorsId);
		glBufferData(GL_ARRAY_BUFFER,colorsBuffer,GL_STATIC_DRAW);
		memFree(colorsBuffer);
		glVertexAttribPointer(glGetAttribLocation(program.id,"inColor"),3,GL_FLOAT,false,0,0);
		// unbind the VBO
		glBindBuffer(GL_ARRAY_BUFFER,0);
		// unbind the VAO
		glBindVertexArray(0);

		{
			final float FOV = (float) Math.toRadians(100f);
		    final float Z_NEAR = 0;
		    final float Z_FAR = 100;
		    Matrix4f perspectiveMatrix;
		    System.out.println("width:"+window.getWidth());
		    float aspectRatio = (float) window.getWidth() / window.getHeight();
		    System.out.println("ar:"+aspectRatio);
			perspectiveMatrix = new Matrix4f().perspective(FOV,aspectRatio,Z_NEAR,Z_FAR);
			FloatBuffer fb = MemoryUtil.memAllocFloat(16);
			perspectiveMatrix.get(fb);
			glUniformMatrix4fv(program.getUniformLocation("perspective"),false,fb);
			memFree(fb);
		}
		Matrix4f viewMatrix = new Matrix4f();
		glClearColor(0,.5f,.5f,0);
		for(;!glfwWindowShouldClose(window.getId());) {
			// clear from last frame
			glClear(GL_COLOR_BUFFER_BIT | GL_DEPTH_BUFFER_BIT);
			// check for keypresses
			glfwPollEvents();


		    if(keyboard.getKeysPressed().contains(GLFW_KEY_Q))
		    	viewMatrix.rotateLocalY(-.1f);
		    if(keyboard.getKeysPressed().contains(GLFW_KEY_E))
		    	viewMatrix.rotateLocalY(.1f);
		    if(keyboard.getKeysPressed().contains(GLFW_KEY_W))
		    	viewMatrix.translateLocal(0,0,.1f);
			if(keyboard.getKeysPressed().contains(GLFW_KEY_S))
				viewMatrix.translateLocal(0,0,-.1f);
			if(keyboard.getKeysPressed().contains(GLFW_KEY_A))
				viewMatrix.translateLocal(.1f,0,0);
			if(keyboard.getKeysPressed().contains(GLFW_KEY_D))
				viewMatrix.translateLocal(-.1f,0,0);
			
			{
				FloatBuffer fb = MemoryUtil.memAllocFloat(16);
				viewMatrix.get(fb);
				glUniformMatrix4fv(program.getUniformLocation("view"),false,fb);
				memFree(fb);
			}

			// bind to the VAO
		    glBindVertexArray(vaoId);
		    glEnableVertexAttribArray(glGetAttribLocation(program.id,"position"));
		    glEnableVertexAttribArray(glGetAttribLocation(program.id,"inColor"));
			// draw
			glDrawElements(GL_TRIANGLES, 6, GL_UNSIGNED_INT, 0);
			// Restore state
		    glDisableVertexAttribArray(0);
		    glBindVertexArray(0);
			// done frame
			window.swapBuffers();
		}

		// cleanup
		glDisableVertexAttribArray(0);
		// delete vertex buffer objects
		glBindBuffer(GL_ARRAY_BUFFER, 0);
		glDeleteBuffers(verticesId);
		glDeleteBuffers(indicesId);
		glDeleteBuffers(colorsId);
		// delete vertex array object
		glBindVertexArray(0);
		glDeleteVertexArrays(vaoId);
	}
	public static void main(String[] args) throws Exception { new Main().run(); }
}