import org.joml.*;
import org.lwjgl.assimp.*;
import org.lwjgl.*;
import java.nio.*;
import java.util.*;
import static util.Util.*;
public class ModelNode {
	public String name;
	public boolean shouldRender=true,shouldCollide=true;
	public Matrix4f
		localTransform,
		absoluteTransform = new Matrix4f(); // current state in animation
	public List<ModelNode> children = new ArrayList<>();
	public List<Runnable> collisionCallbacks = new ArrayList<>();
	public List<Mesh> meshes = new ArrayList<>();
	public ModelNode() {
		localTransform = new Matrix4f(); }
	public ModelNode(Model model,AINode node) {
		name = node.mName().dataString();
		System.out.println("name:"+name);
		model.nameNodeMap.put(name,this);
		PointerBuffer children = node.mChildren();
		if(children != null && 0<children.capacity()) {
			System.out.println("chidren.capacity:"+children.capacity());
			for(;children.hasRemaining();)
				this.children.add(new ModelNode(model,AINode.create(children.get())));
		}

		IntBuffer meshBuffer = node.mMeshes();
		if(meshBuffer != null && 0<meshBuffer.capacity()) {
			System.out.println("modelNode mesh count:"+meshBuffer.capacity());
			for(;meshBuffer.hasRemaining();)
				meshes.add(model.meshes[meshBuffer.get()]);
			out.println("end");
		}

		AIMatrix4x4 tran = node.mTransformation();
		localTransform = new Matrix4f(
			tran.a1(), tran.b1(), tran.c1(), tran.d1(),
			tran.a2(), tran.b2(), tran.c2(), tran.d2(),
			tran.a3(), tran.b3(), tran.c3(), tran.d3(),
			tran.a4(), tran.b4(), tran.c4(), tran.d4()
		);
		System.out.println("localTransform:"+localTransform);
	}
	public void render(Matrix4f transform) {
		absoluteTransform.set(localTransform);
		// if(model.currentNodeAnimationMap.keySet().contains(this))
		// 	System.out.println("I should be animating");
		transform.mul(absoluteTransform,absoluteTransform);
		for(ModelNode x : children)
			x.render(absoluteTransform);
		for(Mesh x : meshes)
			x.render(absoluteTransform);
	}
	private AIVectorKey.Buffer positionKeys;
	private AIQuatKey.Buffer rotationKeys;
	private AIVectorKey.Buffer scalingKeys;
	public void updateAnimation() {
		// if(model.currentNodeAnimationMap.keySet().contains(this)) {
		// 	AINodeAnim thisChannel = model.currentNodeAnimationMap.get(this);
		// 	positionKeys = thisChannel.mPositionKeys();
		// 	rotationKeys = thisChannel.mRotationKeys();
		// 	scalingKeys = thisChannel.mScalingKeys();
		// }
		// for(ModelNode x : children)
		// 	x.updateAnimation();
	}
	// set $a with the values of $this
	public void set(ModelNode a) {
		a.children = children;
		a.meshes = meshes;
		Matrix4f transformCopy = 
		a.localTransform = new Matrix4f(){{
			localTransform.set(this);
		}};
	}
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
}