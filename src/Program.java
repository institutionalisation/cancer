/*
 * David Jacewicz
 * May 14, 2018
 * Ms. Krasteva
 * Shader program
 */

/*
 * Modification: add a method to get the ID of the program
 * David Jacewicz
 * May 15, 2018
 * 2 minutes
 * Version: 0.01
 */

import static org.lwjgl.opengl.GL11.*;
import static org.lwjgl.opengl.GL20.*;
public class Program {
	private int id;
	/**
	 * Creates a shader program from the given shaders
	 *
	 * @param shaders The shaders to attach to this program
	 */
	public Program(Shader... shaders) {
		id = glCreateProgram();
		for(Shader shader : shaders)
			glAttachShader(id,shader.id);
		glLinkProgram(id);
		if(glGetProgrami(id,GL_LINK_STATUS) == GL_FALSE)
			System.out.println("Error linking program: \n"+
				glGetProgramInfoLog(id, glGetProgrami(id, GL_INFO_LOG_LENGTH)));
		for(Shader shader : shaders) {
			glDetachShader(id,shader.id);
			glDeleteShader(shader.id);
		}
	}
	/** Enables this program */
	public void use() {
		glUseProgram(id); }
	/**
	 * Gets the location of a uniform variable of this shader program with the specified name
	 *
	 * @param name The name of the uniform variable
	 */
	public int getUniformLocation(String name) {
		return glGetUniformLocation(id,name); }
	/** @return The shader program ID */
	public int getId() {
		return id; }
}
