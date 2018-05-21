// https://github.com/LWJGL/lwjgl3-demos/blob/master/src/org/lwjgl/demo/opengl/assimp/WavefrontObjDemo.java
import org.lwjgl.*;
import org.lwjgl.assimp.*;
import static org.lwjgl.assimp.Assimp.*;
import static org.lwjgl.system.MemoryUtil.*;
import java.nio.*;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
public class Model {
	private AIScene scene;
	public Mesh[] meshes;
	public Model(String name,Program program) {
		scene = aiImportFile("models/"+name+"/a.dae",aiProcess_JoinIdenticalVertices|aiProcess_Triangulate);
		PointerBuffer meshBuffer = scene.mMeshes();
		meshes = new Mesh[scene.mNumMeshes()];
		//System.out.println("num materials:"+scene.mNumMaterials());
		PointerBuffer materials = scene.mMaterials();
		Texture[] textures = new Texture[scene.mNumMaterials()];
		for(int i = 0; materials.hasRemaining(); ++i) {
			AIMaterial material = AIMaterial.create(materials.get());
			//System.out.println("properties:"+material.mNumProperties());
			PointerBuffer properties = material.mProperties();
			for(;properties.hasRemaining();) {
				AIMaterialProperty property = AIMaterialProperty.create(properties.get());
				String propertyName = property.mKey().dataString();
				System.out.println(propertyName);
				if(propertyName.equals("$tex.file")) {
					String textureFileName = StandardCharsets.UTF_8.decode(property.mData()).toString();
					textureFileName = textureFileName.trim();
					//System.out.println("models/"+name+"/"+textureFileName);
					textures[i] = new Texture("models/"+name+"/"+textureFileName);
				}
			}
		}
		for(int i = 0; i < meshes.length; ++i) {
			AIMesh mesh = AIMesh.create(meshBuffer.get(i));
			meshes[i] = new Mesh(mesh,program,textures);
		}
	}
	public void free() {
		aiReleaseImport(scene); }
}