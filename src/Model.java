// https://github.com/LWJGL/lwjgl3-demos/blob/master/src/org/lwjgl/demo/opengl/assimp/WavefrontObjDemo.java
import org.lwjgl.*;
import org.lwjgl.assimp.*;
import static org.lwjgl.assimp.Assimp.*;
import static org.lwjgl.system.MemoryUtil.*;
import java.nio.*;
import java.nio.charset.StandardCharsets;
import org.joml.*;
public class Model {
	private AIScene scene;
	public Mesh[] meshes;
	public Texture[] textures;
	public ModelNode rootNode;
	public Program program;
	public Model(String name,Program program) {
		this.program = program;
		scene = aiImportFile("models/"+name+"/a.dae",aiProcess_JoinIdenticalVertices|aiProcess_Triangulate);
		PointerBuffer meshBuffer = scene.mMeshes();
		PointerBuffer materials = scene.mMaterials();
		textures = new Texture[scene.mNumMaterials()];
		for(int i = 0; materials.hasRemaining(); ++i) {
			AIMaterial material = AIMaterial.create(materials.get());
			PointerBuffer properties = material.mProperties();
			for(;properties.hasRemaining();) {
				AIMaterialProperty property = AIMaterialProperty.create(properties.get());
				String propertyName = property.mKey().dataString();
				System.out.println(propertyName);
				if(propertyName.equals("$tex.file")) {
					String textureFileName = StandardCharsets.UTF_8.decode(property.mData()).toString();
					textureFileName = textureFileName.trim();
					textures[i] = new Texture("models/"+name+"/"+textureFileName);
				}
			}
		}
		meshes = new Mesh[scene.mNumMeshes()];
		System.out.println("model mesh count:"+scene.mNumMeshes());
		if(meshBuffer != null)
		for(int i = 0;i<meshes.length;++i) {
			AIMesh mesh = AIMesh.create(meshBuffer.get(i));
			meshes[i] = new Mesh(this,mesh);
		}
		rootNode = new ModelNode(this,scene.mRootNode());
	}
	public void render() {
		System.out.println("model render");
		rootNode.render(new Matrix4f()); // identity matrix
	}
	public void free() {
		aiReleaseImport(scene); }
}