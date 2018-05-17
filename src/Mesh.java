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
	public int
		vertexBuffer,
		indexBuffer,
		indexCount,
		vertexArrayObject;
	public Mesh(AIMesh mesh,Program program) {
		vertexArrayObject = glGenVertexArrays();
		glBindVertexArray(vertexArrayObject);
		System.out.println("cc:"+glGetError());
		vertexBuffer = glGenBuffers();
		{
			AIVector3D.Buffer orig = mesh.mVertices();
			//FloatBuffer vertexArrayBufferData = memAllocFloat(orig.)
			System.out.println("vertices:"+orig.capacity());
			ByteBuffer vertices = memByteBuffer(orig.address(),orig.capacity()*AIVector3D.SIZEOF);
			FloatBuffer floatBuffer = memFloatBuffer(orig.address(),orig.capacity()*3);
			for(;floatBuffer.hasRemaining();)
				System.out.print(floatBuffer.get()+" ");
			System.out.println();
			glBindBuffer(GL_ARRAY_BUFFER,vertexBuffer);
			glBufferData(GL_ARRAY_BUFFER,floatBuffer,GL_STATIC_DRAW);
			glVertexAttribPointer(glGetAttribLocation(program.getId(),"position"),3,GL_FLOAT,false,0,0);
			//glEnableVertexAttribArray(glGetAttribLocation(program.getId(),"position"));
		}
		indexCount = mesh.mNumFaces()*3;
		System.out.println("elements:"+indexCount);
		indexBuffer = glGenBuffers();
		{
			AIFace.Buffer orig = mesh.mFaces();
			IntBuffer indexBufferData = memAllocInt(indexCount);
			for(AIFace x : orig) {
				indexBufferData.put(x.mIndices());
				IntBuffer y = x.mIndices();
				// for(;y.hasRemaining();)
				// 	System.out.print(y.get()+" ");
				// System.out.println();
			}
			glBindBuffer(GL_ELEMENT_ARRAY_BUFFER,indexBuffer);
			glBufferData(GL_ELEMENT_ARRAY_BUFFER,indexBufferData,GL_STATIC_DRAW);
		}

	}
}