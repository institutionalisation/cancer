import org.joml.*;
import org.lwjgl.assimp.*;
import org.lwjgl.*;
import java.nio.*;
public class ModelNode {
	public String name;
	public Matrix4f transform;
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
		transform = new Matrix4f(
			tran.a1(), tran.b1(), tran.c1(), tran.d1(),
			tran.a2(), tran.b2(), tran.c2(), tran.d2(),
			tran.a3(), tran.b3(), tran.c3(), tran.d3(),
			tran.a4(), tran.b4(), tran.c4(), tran.d4()
		);
		System.out.println("transform:"+transform);
	}
	private AIVectorKey[] positionKeys;
	private AIQuatKey[] rotationKeys;
	private AIVectorKey[] scalingKeys;
	public void interpolate(Matrix4f transform) {
		//System.out.println("model node interpolate input trans:\n"+transform);
		// figure out my local transform
		Matrix4f myTransform = new Matrix4f();
		System.out.println("model.currentNodeAnimationMap.keySet():"+model.currentNodeAnimationMap.keySet());
		if(model.currentNodeAnimationMap.keySet().contains(this))
			System.out.println("I should be animating");

		transform.mul(myTransform,myTransform);
		this.transform = myTransform;
		System.out.println("ModelNode interpolate this trans:\n"+this.transform);
		for(ModelNode x : children)
			x.interpolate(myTransform);
	}
}