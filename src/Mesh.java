/*
 * David Jacewicz
 * May 14, 2018
 * Ms. Krasteva
 * Mesh imported from the asset importer
 */

/*
 * Modification: use model nodes instead of model
 * David Jacewicz
 * May 16, 2018
 * 2 hours
 * Version: 0.01
 */

/*
 * Modification: fix bug where imported models are empty
 * David Jacewicz & Junyi Wang
 * May 19, 2018
 * 2 hours
 * Version: 0.02
 */

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
import org.joml.*;
import static util.Util.*;
public class Mesh {
	private float scale = 1;
	private AIMesh mesh;
	private int
		vertexBuffer,
		indexBuffer,
		UVBuffer,
		indexCount,
		vertexArrayObject;
	private Texture texture;
	private Program program;
	private Model model;
	public ModelNode parentNode;
	/**
	 * Creates a mesh object for the given model with the given Assimp mesh
	 *
	 * @param model The model to create the mesh for
	 * @param mesh The Assimp mesh to create this mesh from
	 */
	public Mesh(Model model,AIMesh mesh) {
		this.model = model;
		this.mesh = mesh;
		this.program = model.program;
		this.texture = model.textures[mesh.mMaterialIndex()];
		vertexArrayObject = glGenVertexArrays();
		glBindVertexArray(vertexArrayObject);
		{
			AIVector3D.Buffer orig = mesh.mVertices();
			FloatBuffer floats = memFloatBuffer(orig.address(),orig.capacity()*AIVector3D.SIZEOF/4);
			vertexBuffer = glGenBuffers();
			glBindBuffer(GL_ARRAY_BUFFER,vertexBuffer);
			glBufferData(GL_ARRAY_BUFFER,floats,GL_STATIC_DRAW);
			glVertexAttribPointer(glGetAttribLocation(program.getId(),"position"),3,GL_FLOAT,false,0,0);
		}
		{
			indexCount = mesh.mNumFaces()*3;
			AIFace.Buffer orig = mesh.mFaces();
			IntBuffer indexBufferData = memAllocInt(indexCount);
			for(AIFace x : orig) {
				indexBufferData.put(x.mIndices());
				IntBuffer indices = x.mIndices();
			}
			indexBufferData.clear();
			indexBuffer = glGenBuffers();
			glBindBuffer(GL_ELEMENT_ARRAY_BUFFER,indexBuffer);
			glBufferData(GL_ELEMENT_ARRAY_BUFFER,indexBufferData,GL_STATIC_DRAW);
		}
		{
			PointerBuffer texCoordsBuffer = mesh.mTextureCoords();
			FloatBuffer UVBufferData = null;
			
			long pointer = texCoordsBuffer.get();
			if(pointer != NULL) {
				AIVector3D.Buffer x = AIVector3D.create(pointer,mesh.mNumVertices());
				UVBufferData = memAllocFloat(mesh.mNumVertices()*2);
				for(;x.hasRemaining();) {
					AIVector3D y = x.get();
					UVBufferData.put(y.x()).put(y.y());
				}
				UVBufferData.clear();
				UVBuffer = glGenBuffers();
				glBindBuffer(GL_ARRAY_BUFFER,UVBuffer);
				glBufferData(GL_ARRAY_BUFFER,UVBufferData,GL_STATIC_DRAW);
				glVertexAttribPointer(glGetAttribLocation(program.getId(),"vertexUV"),2,GL_FLOAT,false,0,0);
			}
		}
		parentNode = model.meshParentMap.get(this);
	}

	private FloatBuffer transformBuffer = memAllocFloat(16);

	/**
	 * Renders this mesh with the given base transform matrix
	 *
	 * @param transform The base transform matrix to use
	 */
	public void render(Matrix4f transform) {
		transform.get(transformBuffer);
		if(texture != null)
			glBindTexture(GL_TEXTURE_2D,texture.id);
		glBindVertexArray(vertexArrayObject);
		glEnableVertexAttribArray(glGetAttribLocation(program.getId(),"position"));
		glEnableVertexAttribArray(glGetAttribLocation(program.getId(),"vertexUV"));
		glUniform1f(program.getUniformLocation("scale"),scale);
		glUniform1i(glGetUniformLocation(program.getId(),"myTextureSampler"),0);
		// pass the transformation matrix to the shader
		glUniformMatrix4fv(program.getUniformLocation("transform"),false,transformBuffer);
		glDrawElements(GL_TRIANGLES,indexCount,GL_UNSIGNED_INT,0);
	}

	/** @return The Assimp mesh this mesh is created with */
	public AIMesh getAIMesh() {
		return mesh; }
}
