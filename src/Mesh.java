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
import org.lwjgl.*;
public class Mesh {
	private float scale;
	private AIMesh mesh;
	private int
		vertexBuffer,
		indexBuffer,
		UVBuffer,
		indexCount,
		vertexArrayObject;
	private Texture texture;
	private Program program;
	public Mesh(AIMesh mesh,Program program,Texture[] textures,float scale) {
		this.mesh = mesh;
		this.program = program;
		this.scale = scale;
		System.out.println("mat index:"+mesh.mMaterialIndex());
		this.texture = textures[mesh.mMaterialIndex()];
		vertexArrayObject = glGenVertexArrays();
		glBindVertexArray(vertexArrayObject);
		System.out.println("cc:"+glGetError());
		{
			AIVector3D.Buffer orig = mesh.mVertices();
			System.out.println("vertices:"+orig.capacity());
			FloatBuffer floats = memFloatBuffer(orig.address(),orig.capacity()*AIVector3D.SIZEOF/4);
			// flip y and z values (because blender and collada and hecc)
			for(int i = 0; i < floats.capacity(); i+=3) {
				floats.put(i,-floats.get(i));
				float tmp = floats.get(i+1);
				floats.put(i+1,floats.get(i+2));
				floats.put(i+2,tmp);
				//System.out.println(floats.get()+" "+floats.get()+" "+floats.get());
			}
			vertexBuffer = glGenBuffers();
			glBindBuffer(GL_ARRAY_BUFFER,vertexBuffer);
			glBufferData(GL_ARRAY_BUFFER,floats,GL_STATIC_DRAW);
			glVertexAttribPointer(glGetAttribLocation(program.getId(),"position"),3,GL_FLOAT,false,0,0);
		}
		{
			indexCount = mesh.mNumFaces()*3;
			System.out.println("elements:"+indexCount);
			AIFace.Buffer orig = mesh.mFaces();
			IntBuffer indexBufferData = memAllocInt(indexCount);
			for(AIFace x : orig) {
				indexBufferData.put(x.mIndices());
				IntBuffer indices = x.mIndices();
				//for(;indices.hasRemaining();)
					//System.out.println(indices.get());
			}
			indexBufferData.clear();
			indexBuffer = glGenBuffers();
			glBindBuffer(GL_ELEMENT_ARRAY_BUFFER,indexBuffer);
			glBufferData(GL_ELEMENT_ARRAY_BUFFER,indexBufferData,GL_STATIC_DRAW);
		}
		{
			System.out.println("texCoords:"+(mesh.mTextureCoords()!=null));
			PointerBuffer texCoordsBuffer = mesh.mTextureCoords();
			FloatBuffer UVBufferData = null;
			for(;texCoordsBuffer.hasRemaining();) {
				long pointer = texCoordsBuffer.get();
				//System.out.println("pointer:"+pointer);
				if(pointer != NULL) {
					AIVector3D.Buffer x = AIVector3D.create(pointer,mesh.mNumVertices());
					UVBufferData = memAllocFloat(mesh.mNumVertices()*2);
					for(;x.hasRemaining();) {
						AIVector3D y = x.get();
						UVBufferData.put(y.x()).put(y.y());
						//System.out.println(y.x()+" "+y.y()+" "+y.z());
					}
					UVBufferData.clear();
				}
			}
			UVBuffer = glGenBuffers();
			glBindBuffer(GL_ARRAY_BUFFER,UVBuffer);
			glBufferData(GL_ARRAY_BUFFER,UVBufferData,GL_STATIC_DRAW);
			glVertexAttribPointer(glGetAttribLocation(program.getId(),"vertexUV"),2,GL_FLOAT,false,0,0);
		}
	}
	public Mesh(AIMesh mesh,Program program,Texture[] textures) {
		this(mesh,program,textures,1f); }
	public void render() {
		if(texture != null)
			glBindTexture(GL_TEXTURE_2D,texture.id);
		glBindVertexArray(vertexArrayObject);
		glEnableVertexAttribArray(glGetAttribLocation(program.getId(),"position"));
		glEnableVertexAttribArray(glGetAttribLocation(program.getId(),"vertexUV"));
		glUniform1f(program.getUniformLocation("scale"),scale);
		glUniform1i(glGetUniformLocation(program.getId(),"myTextureSampler"),0);
		glDrawElements(GL_TRIANGLES,indexCount,GL_UNSIGNED_INT,0);
		//System.out.println("render error:"+glGetError());
	}
	public AIMesh getAIMesh() {
		return mesh; }
}
