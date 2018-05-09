import static org.lwjgl.opengl.GL11.*;
import static org.lwjgl.opengl.GL20.*;
public class Program {
	public int id;
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
	public void use() {
		glUseProgram(id);
	}
	public int getUniformLocation(String name) {
		return glGetUniformLocation(id,name);
	}
}