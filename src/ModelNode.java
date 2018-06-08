/*
 * David Jacewicz
 * May 14, 2018
 * Ms. Krasteva
 * A node in the model tree
 */

/*
 * Modification: clean up source code and Javadoc
 * Junyi Wang
 * June 7, 2018
 * 2 minutes
 * Version: 0.05
 */

import org.joml.*;
import org.lwjgl.assimp.*;
import org.lwjgl.*;
import java.nio.*;
import java.util.*;
import static util.Util.*;
public class ModelNode {
	public String name;
	public boolean shouldRender=true,shouldCollide=true;
	private Matrix4f localTransform;
	public Matrix4f absoluteTransform = new Matrix4f(); // current state in animation
	public List<ModelNode> children = new ArrayList<>();
	public List<Runnable> collisionCallbacks = new ArrayList<>();
	public List<Mesh> meshes = new ArrayList<>();
	/** Creates an empty model node */
	public ModelNode() {
		localTransform = new Matrix4f(); }
	/**
	 * Creates a model node for the given model from the given Assimp model node
	 *
	 * @param model The model to create this node for
	 * @param node The Assimp model node to extract data from
	 */
	public ModelNode(Model model,AINode node) {
		name = node.mName().dataString();
		model.nameNodeMap.put(name,this);
		PointerBuffer children = node.mChildren();
		if(children != null && 0<children.capacity()) {
			for(;children.hasRemaining();)
				this.children.add(new ModelNode(model,AINode.create(children.get())));
		}

		IntBuffer meshBuffer = node.mMeshes();
		if(meshBuffer != null && 0<meshBuffer.capacity()) {
			for(;meshBuffer.hasRemaining();)
				meshes.add(model.meshes[meshBuffer.get()]);
		}

		AIMatrix4x4 tran = node.mTransformation();
		localTransform = new Matrix4f(
			tran.a1(), tran.b1(), tran.c1(), tran.d1(),
			tran.a2(), tran.b2(), tran.c2(), tran.d2(),
			tran.a3(), tran.b3(), tran.c3(), tran.d3(),
			tran.a4(), tran.b4(), tran.c4(), tran.d4()
		);
	}
	Matrix4f temp = new Matrix4f();

	/**
	 * Renders a model node
	 *
	 * @param parentTransform The transformation matirx of the parent node
	 */
	public void render(Matrix4f parentTransform) {
		parentTransform.mul(getLocalTransform(),absoluteTransform);
		for(ModelNode x : children)
			x.render(absoluteTransform);
		for(Mesh x : meshes)
			x.render(absoluteTransform);
	}
	private AIVectorKey.Buffer positionKeys;
	private AIQuatKey.Buffer rotationKeys;
	private AIVectorKey.Buffer scalingKeys;

	/**
	 * Copies the given model node
	 *
	 * @param node The model node to copy
	 */
	public void set(ModelNode node) {
		children = node.children;
		meshes = node.meshes;
		localTransform.set(node.localTransform);
	}

	/**
	 * Finds a child of this node by name recursively
	 *
	 * @param name The name of the node to search for
	 *
	 * @return The descendant of this node with the given name
	 */
	public ModelNode getChild(String name) {
		for(ModelNode x : children)
			if(x.name.equals(name))
				return x;
		for(ModelNode x : children) {
			ModelNode child = x.getChild(name);
			if(child != null)
				return child;
		}
		return null;
	}

	/** @return The transform matrix used for this node */
	public Matrix4f getLocalTransform() {
		return localTransform; }
}
