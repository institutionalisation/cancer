// https://github.com/LWJGL/lwjgl3-demos/blob/master/src/org/lwjgl/demo/opengl/assimp/WavefrontObjDemo.java
import org.lwjgl.*;
import org.lwjgl.assimp.*;
import static org.lwjgl.assimp.Assimp.*;
import static org.lwjgl.system.MemoryUtil.*;
import java.nio.*;
import java.nio.charset.StandardCharsets;
import org.joml.*;
import java.util.*;
import static util.Util.*;
public class Model {
	private AIScene scene;
	public Mesh[] meshes;
	public Texture[] textures;
	public ModelNode rootNode;
	public Program program;
	public Map<Mesh,ModelNode> meshParentMap = new HashMap<Mesh,ModelNode>();
	public Map<String,ModelNode> nameNodeMap = new TreeMap<String,ModelNode>();
	public Map<String,Map<ModelNode,AINodeAnim>> animationNameNodeAnimationMap = new TreeMap<String,Map<ModelNode,AINodeAnim>>();
	public long animationStartTime;
	public Map<ModelNode,AINodeAnim> currentNodeAnimationMap;
	public Model(String name,String extension,Program program) {
		this.program = program;
		out.println("loading:"+"models/"+name+"/a."+extension);
		scene = aiImportFile("models/"+name+"/a."+extension,aiProcess_JoinIdenticalVertices|aiProcess_Triangulate);
		PointerBuffer materials = scene.mMaterials();
		textures = new Texture[scene.mNumMaterials()];
		for(int i = 0; materials.hasRemaining(); ++i) {
			AIMaterial material = AIMaterial.create(materials.get());
			PointerBuffer properties = material.mProperties();
			for(;properties.hasRemaining();) {
				AIMaterialProperty property = AIMaterialProperty.create(properties.get());
				String propertyName = property.mKey().dataString();
				//System.out.println(propertyName);
				if(propertyName.equals("$tex.file")) {
					String textureFileName = StandardCharsets.UTF_8.decode(property.mData()).toString();
					textureFileName = textureFileName.trim();
					textures[i] = new Texture("models/"+name+"/"+textureFileName);
				}
			}
		}
		// System.out.println("model material count:"+scene.mNumMaterials());
		// for(int i = 0; i < textures.length; ++i)
		// 	System.out.println("textures["+i+"]:"+textures[i]);
		
		meshes = new Mesh[scene.mNumMeshes()];
		PointerBuffer meshBuffer = scene.mMeshes();
		System.out.println("model mesh count:"+meshes.length);
		if(meshBuffer != null)
		for(int i = 0;i<meshes.length;++i) {
			AIMesh mesh = AIMesh.create(meshBuffer.get(i));
			meshes[i] = new Mesh(this,mesh);
		}

		rootNode = new ModelNode(this,scene.mRootNode());

		// default, don't break if there aren't any animations
		currentNodeAnimationMap = new HashMap<ModelNode,AINodeAnim>();
		System.out.println("model animation count:"+scene.mNumAnimations());
		PointerBuffer animations = scene.mAnimations();
		//System.out.println("model: animations capacity:"+animations.capacity());
		if(animations != null)
		for(;animations.hasRemaining();) {
			AIAnimation animation = AIAnimation.create(animations.get());
			Map<ModelNode,AINodeAnim> nodeAnimationMap = new HashMap<ModelNode,AINodeAnim>();
			PointerBuffer channels = animation.mChannels();
			for(;channels.hasRemaining();) {
				AINodeAnim channel = AINodeAnim.create(channels.get());
				nodeAnimationMap.put(
					nameNodeMap.get(channel.mNodeName().dataString()),
					channel);
				out.println("anim channel name:"+channel.mNodeName().dataString());
			}
			//System.out.println("animation channel count:"+channels.capacity());
			animationNameNodeAnimationMap.put(animation.mName().dataString(),nodeAnimationMap);
			System.out.println("model: animation name:"+animation.mName().dataString());
			if(animations.capacity() == 1) {
				out.println("there is only one animation, autoplay");
				animationStartTime = System.currentTimeMillis();
				currentNodeAnimationMap = nodeAnimationMap;
				rootNode.updateAnimation();
			}
		}
	}
	public void render(Matrix4f transform) {
		//System.out.println("model render");
		rootNode.interpolate(transform); // identity matrix
		// all nodes should now have local-to-global transform matrices
		for(Mesh x : meshes)
			x.render();
	}
	public void render()
	{
		render(new Matrix4f());
	}
	public void animate(String animationName) {
		animationStartTime = System.currentTimeMillis();
		currentNodeAnimationMap = animationNameNodeAnimationMap.get(animationName);
		rootNode.updateAnimation();
		System.out.println("currentNodeAnimationMap:"+currentNodeAnimationMap);
	}
	public void free() {
		aiReleaseImport(scene); }
}
