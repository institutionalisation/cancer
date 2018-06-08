/*
 * David Jacewicz
 * May 14, 2018
 * Ms. Krasteva
 * An imported model
 */

/*
 * Modification: add model nodes
 * David Jacewicz
 * May 15, 2018
 * 15 minutes
 * Version: 0.02
 */

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
	
	/**
	 * Imports a model with the given anme and extension
	 *
	 * @param name The name of the model
	 * @param extension The file extension of the model
	 * @param program The shader program to load resources into
	 */
	public Model(String name,String extension,Program program) {
		this.program = program;
		scene = aiImportFile("models/"+name+"/a."+extension,aiProcess_JoinIdenticalVertices|aiProcess_Triangulate);
		PointerBuffer materials = scene.mMaterials();
		textures = new Texture[scene.mNumMaterials()];
		for(int i = 0; materials.hasRemaining(); ++i) {
			AIMaterial material = AIMaterial.create(materials.get());
			PointerBuffer properties = material.mProperties();
			for(;properties.hasRemaining();) {
				AIMaterialProperty property = AIMaterialProperty.create(properties.get());
				String propertyName = property.mKey().dataString();
				if(propertyName.equals("$tex.file")) {
					String textureFileName = StandardCharsets.UTF_8.decode(property.mData()).toString();
					textureFileName = textureFileName.trim();
					textures[i] = Texture.fromFile("models/"+name+"/",textureFileName);
				}
			}
		}
		
		meshes = new Mesh[scene.mNumMeshes()];
		PointerBuffer meshBuffer = scene.mMeshes();
		if(meshBuffer != null)
		for(int i = 0;i<meshes.length;++i) {
			AIMesh mesh = AIMesh.create(meshBuffer.get(i));
			meshes[i] = new Mesh(this,mesh);
		}

		rootNode = new ModelNode(this,scene.mRootNode());

		// default, don't break if there aren't any animations
		currentNodeAnimationMap = new HashMap<ModelNode,AINodeAnim>();
		PointerBuffer animations = scene.mAnimations();
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
			}
			animationNameNodeAnimationMap.put(animation.mName().dataString(),nodeAnimationMap);
			if(animations.capacity() == 1) {
				animationStartTime = System.currentTimeMillis();
				currentNodeAnimationMap = nodeAnimationMap;
			}
		}
	}

	/**
	 * Play an animation
	 *
	 * @param animationName The name of the animation to be played
	 */
	public void animate(String animationName) {
		animationStartTime = System.currentTimeMillis();
		currentNodeAnimationMap = animationNameNodeAnimationMap.get(animationName);
	}

	/** Frees native memory used by this model */
	public void free() {
		aiReleaseImport(scene); }
}
