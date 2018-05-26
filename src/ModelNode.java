import org.joml.*;
import org.lwjgl.assimp.*;
import org.lwjgl.*;
import java.nio.*;
import java.util.*;
import static util.Util.*;
public class ModelNode {
	public String name;
	public Matrix4f
		defaultTransform,
		absoluteTransform = new Matrix4f(); // current state in animation
	public ModelNode[] children;
	public Model model;
	public ModelNode(Model model,AINode node) {
		this.model = model;
		name = node.mName().dataString();
		System.out.println("name:"+name);
		model.nameNodeMap.put(name,this);
		PointerBuffer children = node.mChildren();
		if(children != null && 0<children.capacity()) {
			this.children = new ModelNode[children.capacity()];
			System.out.println("chidren.capacity:"+children.capacity());
			for(int i = 0;children.hasRemaining();++i)
				this.children[i] = new ModelNode(model,AINode.create(children.get()));
		} else
			this.children = new ModelNode[0];

		IntBuffer meshes = node.mMeshes();
		if(meshes != null && 0<meshes.capacity()) {
			System.out.println("modelNode mesh count:"+meshes.capacity());
			for(int i = 0;meshes.hasRemaining();++i)
				model.meshParentMap.put(
					model.meshes[meshes.get()],
					this);
			System.out.println("end");
		}

		AIMatrix4x4 tran = node.mTransformation();
		defaultTransform = new Matrix4f(
			tran.a1(), tran.b1(), tran.c1(), tran.d1(),
			tran.a2(), tran.b2(), tran.c2(), tran.d2(),
			tran.a3(), tran.b3(), tran.c3(), tran.d3(),
			tran.a4(), tran.b4(), tran.c4(), tran.d4()
		);
		System.out.println("defaultTransform:"+defaultTransform);
	}
	private AIVectorKey.Buffer positionKeys;
	private AIQuatKey.Buffer rotationKeys;
	private AIVectorKey.Buffer scalingKeys;
	public void updateAnimation() {
		if(model.currentNodeAnimationMap.keySet().contains(this)) {
			AINodeAnim thisChannel = model.currentNodeAnimationMap.get(this);
			positionKeys = thisChannel.mPositionKeys();
			rotationKeys = thisChannel.mRotationKeys();
			scalingKeys = thisChannel.mScalingKeys();
		}
		for(ModelNode x : children)
			x.updateAnimation();
	}
	public void interpolate(Matrix4f transform) {
		absoluteTransform.set(defaultTransform);
		// if(model.currentNodeAnimationMap.keySet().contains(this))
		// 	System.out.println("I should be animating");
		transform.mul(absoluteTransform,absoluteTransform);
		for(ModelNode x : children)
			x.interpolate(absoluteTransform);
	}
}