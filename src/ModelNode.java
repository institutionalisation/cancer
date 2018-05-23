import org.joml.*;
import org.lwjgl.assimp.*;
import org.lwjgl.*;
import java.nio.*;
public class ModelNode {
	String name;
	public Matrix4f transform;
	ModelNode[] children;
	Mesh[] meshes;
	public ModelNode(Model scene,AINode node) {
		name = node.mName().dataString();
		System.out.println("name:"+name);
		PointerBuffer children = node.mChildren();
		if(children != null && 0<children.capacity()) {
			this.children = new ModelNode[children.capacity()];
			System.out.println("chidren.capacity:"+children.capacity());
			for(int i = 0;children.hasRemaining();++i)
				this.children[i] = new ModelNode(scene,AINode.create(children.get()));
		} else
			this.children = new ModelNode[0];
		IntBuffer meshes = node.mMeshes();
		if(meshes != null && 0<meshes.capacity()) {
			System.out.println("modelNode mesh count:"+meshes.capacity());
			this.meshes = new Mesh[meshes.capacity()];
			for(int i = 0;meshes.hasRemaining();++i) {
				System.out.println("i:"+i);
				this.meshes[i] = scene.meshes[meshes.get()];
			}
			System.out.println("end");
		} else
			this.meshes = new Mesh[0];
		AIMatrix4x4 tran = node.mTransformation();
		transform = new Matrix4f(
			tran.a1(), tran.a2(), tran.a3(), tran.a4(),
			tran.b1(), tran.b2(), tran.b3(), tran.b4(),
			tran.c1(), tran.c2(), tran.c3(), tran.c4(),
			tran.d1(), tran.d2(), tran.d3(), tran.d4()
		);
	}
	public void render(Matrix4f transform) {
		Matrix4f combinedTransform = transform.mul(this.transform,new Matrix4f());
		for(ModelNode x : children)
			x.render(combinedTransform);
		for(Mesh x : meshes)
			x.render(combinedTransform);
	}
}