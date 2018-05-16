// https://github.com/LWJGL/lwjgl3-demos/blob/master/src/org/lwjgl/demo/opengl/assimp/WavefrontObjDemo.java
import org.lwjgl.assimp.*;
import static org.lwjgl.assimp.Assimp.*;
import org.lwjgl.opengl.*;
import static org.lwjgl.opengl.GL11.*;
import static org.lwjgl.opengl.GL15.*;
import java.nio.*;
import static org.lwjgl.system.MemoryUtil.*;

public class Mesh {
	public int
		vertexArrayBuffer,
		elementArrayBuffer,
		elementCount;
	public Mesh(AIMesh mesh) {
		System.out.println("cc:"+glGetError());
		vertexArrayBuffer = glGenBuffers();
		{
			/*FloatBuffer vertices = FloatBuffer.allocate(mesh.mNumVertices()*3);
			AIVector3D.Buffer orig = mesh.mVertices();
			for(int i = 0; i < vertices.capacity(); i += 3) {
				AIVector3D vertex = orig.get();
				vertices
					.put(vertex.x())
					.put(vertex.y())
					.put(vertex.z());
			}*/
			AIVector3D.Buffer orig = mesh.mVertices();
			//for(AIVector3D x : orig)
				//System.out.println(x.x()+","+x.y()+","+x.z());
			ByteBuffer vertices = memByteBuffer(orig.address(),orig.capacity()*4*3);
			glBindBuffer(GL_ARRAY_BUFFER,vertexArrayBuffer);
			System.out.println("ee:"+glGetError());
			glBufferData(vertexArrayBuffer,vertices,GL_STATIC_DRAW);
			System.out.println("dd:"+glGetError());
		}
		elementCount = mesh.mNumFaces()*3;
		System.out.println("elements:"+elementCount);

		elementArrayBuffer = glGenBuffers();
		{
			AIFace.Buffer orig = mesh.mFaces();
			IntBuffer elementArrayBufferData = memAllocInt(elementCount);
			for(AIFace x : orig) {
				elementArrayBufferData.put(x.mIndices());
				//for(int y : x.mIndices())
				//	System.out.println(y);
			}
			glBindBuffer(GL_ELEMENT_ARRAY_BUFFER,elementArrayBuffer);
			glBufferData(elementArrayBuffer,elementArrayBufferData,GL_STATIC_DRAW);
		}

	}
}