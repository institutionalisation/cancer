/*
 * David Jacewicz
 * June 7, 2018
 * Ms. Krasteva
 * A shader
 */

import static org.lwjgl.opengl.GL11.*;
import static org.lwjgl.opengl.GL20.*;
import static util.Util.*;
public class Shader {
	public int id;
	/**
	 * Loads a shader from the given file with the given type
	 *
	 * @param fileName The file to load this shader from
	 * @param type The integer type of this shader
	 */
	public Shader(String fileName,int type) throws Exception {
		id = glCreateShader(type);
		String code = readFile("shaders/" + fileName);
		glShaderSource(id,code);
		glCompileShader(id);
		if(glGetShaderi(id,GL_COMPILE_STATUS) == GL_FALSE)
			System.out.println("Vertex shader compilation failed: \n"+
				glGetShaderInfoLog(id,glGetShaderi(id,GL_INFO_LOG_LENGTH)));
	}
}
