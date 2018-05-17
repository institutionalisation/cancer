// https://github.com/LWJGL/lwjgl3-demos/blob/master/src/org/lwjgl/demo/opengl/assimp/WavefrontObjDemo.java
import org.lwjgl.assimp.*;
import static org.lwjgl.assimp.Assimp.*;
import org.lwjgl.opengl.*;
import static org.lwjgl.opengl.GL11.*;
import static org.lwjgl.opengl.GL15.*;
import static org.lwjgl.opengl.GL20.*;
import static org.lwjgl.opengl.GL30.*;
import java.nio.*;
import static org.lwjgl.system.MemoryUtil.*;
public class Mesh {
	float scale;
	public int
		vertexBuffer,
		indexBuffer,
		indexCount,
		vertexArrayObject;
	Program program;
	public Mesh(AIMesh mesh,Program program,float scale) {
		this.program = program;
		this.scale = scale;
		vertexArrayObject = glGenVertexArrays();
		glBindVertexArray(vertexArrayObject);
		System.out.println("cc:"+glGetError());
		vertexBuffer = glGenBuffers();
		{
			AIVector3D.Buffer orig = mesh.mVertices();
			System.out.println("vertices:"+orig.capacity());
			ByteBuffer vertices = memByteBuffer(orig.address(),orig.capacity()*AIVector3D.SIZEOF);
			vertices.clear();
			glBindBuffer(GL_ARRAY_BUFFER,vertexBuffer);
			glBufferData(GL_ARRAY_BUFFER,vertices,GL_STATIC_DRAW);
		}
		glVertexAttribPointer(glGetAttribLocation(program.getId(),"position"),3,GL_FLOAT,false,0,0);
		indexCount = mesh.mNumFaces()*3;
		System.out.println("elements:"+indexCount);
		indexBuffer = glGenBuffers();
		{		
			AIFace.Buffer orig = mesh.mFaces();
			IntBuffer indexBufferData = memAllocInt(indexCount);
			for(AIFace x : orig)
				indexBufferData.put(x.mIndices());
			indexBufferData.clear();
			glBindBuffer(GL_ELEMENT_ARRAY_BUFFER,indexBuffer);
			glBufferData(GL_ELEMENT_ARRAY_BUFFER,indexBufferData,GL_STATIC_DRAW);
		}
	}
	public Mesh(AIMesh mesh,Program program) {
		this(mesh,program,1f); }
	public void render() {
		glBindVertexArray(vertexArrayObject);
		glEnableVertexAttribArray(glGetAttribLocation(program.getId(),"position"));
		//glUniform1f(program.getUniformLocation("scale"),scale);
		glDrawElements(GL_TRIANGLES,indexCount,GL_UNSIGNED_INT,0);
	}
}
