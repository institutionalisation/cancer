import org.joml.*;
import static org.lwjgl.assimp.Assimp.*;
import static org.lwjgl.system.MemoryUtil.*;
import org.lwjgl.assimp.*;
import java.util.*;
import static java.lang.Math.*;

public class LineCollide
{
	private final Line[] lines;

	public LineCollide(final Line[] lines)
	{
		this.lines = lines;
	}

	private static class LineIdx extends ComparableIntPair
	{
		public LineIdx(final int pointA,final int pointB)
		{
			super(min(pointA,pointB),max(pointA,pointB);
		}
	}

	/* Creates a LineCollider from the given .blend mesh file */
	public LineCollide(String filename)
	{
		AIScene scene = aiImportfile(filename,aiProcess_JoinIdenticalVertices);
		PointerBuffer meshBuffer = scene.mMeshes();
		AIMesh mesh = AIMesh.create(meshBuffer.get(0));
		AIVector3D.Buffer vecBuf = mesh.mVertices();
		FloatBuffer vecf = memFloatBuffer(vecBuf.address(),vecBuf.capacity() * AIVector3D.SIZEOF / 4);
		int vecfSize = vecf.capacity();
		Vector2d[] points = new Vector2d[vecfSize /3];
		/* Get all points; ignore z value */
		for(int i = 0;i < points.length;++i)
			points[i] = new Vector2d(vecf.get(i * 3),vecf.get(i * 3 + 1));
		int nFaces = mesh.mNumFaces();
		AIFace.Buffer faces = mesh.mFaces();
		Set<LineIdx> lineIdx = new TreeSet<>();
		/* Get lines from faces and deduplicate */
		for(AIFace x : faces)
		{
			IntBuffer indices = x.mIndices();
			int nIndices = x.mNumIndices();
			for(int i = 0;i < nIndices - 1;++i)
				lineIdx.add(new LineIdx(indices.get(i),indices.get(i + 1)));
			lineIdx.add(new LineIdx(indices.get(0),indices.get(nIndices - 1)));
		}
		lines = new Line[lineIdx.size()];
		Iterator<LineIdx> lineIt = lineIdx.iterator();
		/* Create the line objects from deduplicated array */
		for(int i = 0;i < lines.length;++i)
		{
			LineIdx cLineIdx = lineIt.next();
			lines[i] = new Line(points[cLineIdx.x],points[cLineIdx.y]);
		}
	}

	public boolean check(final Vector2d pointA,final Vector2d pointB)
	{
		for(final Line x : lines)
			if(x.collide(pointA,pointB))
				return true;
		return false;
	}
}
