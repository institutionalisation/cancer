import static org.lwjgl.opengl.GL11.*;
import static org.lwjgl.opengl.GL20.*;
public class Shader {
	public int id;
	public Shader(String fileName,int type) throws Exception {
		id = glCreateShader(type);
		String code = Util.readFile("shaders/" + fileName);
		glShaderSource(id,code);
		glCompileShader(id);
		if(glGetShaderi(id,GL_COMPILE_STATUS) == GL_FALSE)
			System.out.println("Vertex shader compilation failed: \n"+
				glGetShaderInfoLog(id, glGetShaderi(id, GL_INFO_LOG_LENGTH)));
	}
}