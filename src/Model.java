// https://github.com/LWJGL/lwjgl3-demos/blob/master/src/org/lwjgl/demo/opengl/assimp/WavefrontObjDemo.java
import org.lwjgl.*;
import org.lwjgl.assimp.*;
import static org.lwjgl.assimp.Assimp.*;
public class Model {
	private AIScene scene;
	public Mesh[] meshes;
	public Model(AIScene scene) {
		this.scene = scene;
		PointerBuffer meshBuffer = scene.mMeshes();
		meshes = new Mesh[scene.mNumMeshes()];
		for(int i = 0; i < meshes.length; ++i)
			meshes[i] = new Mesh(AIMesh.create(meshBuffer.get(i)));
	}
	public void free() {
		aiReleaseImport(scene);
	}
}