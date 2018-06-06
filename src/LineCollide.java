import org.joml.*;
import org.lwjgl.*;
import static org.lwjgl.assimp.Assimp.*;
import static org.lwjgl.system.MemoryUtil.*;
import org.lwjgl.assimp.*;
import java.util.*;
import java.nio.*;
import static java.lang.Math.*;

public class LineCollide
{
	private Line[] lines;

	public LineCollide(final Line[] lines)
	{
		this.lines = lines;
	}

	private static class LineIdx extends ComparableIntPair
	{
		public LineIdx(final int pointA,final int pointB)
		{
			super(min(pointA,pointB),max(pointA,pointB));
		}
	}

	/* Creates a LineCollider from the given mesh file */
	public LineCollide(String filename)
	{
		AIScene scene = aiImportFile(filename,aiProcess_JoinIdenticalVertices);
		System.out.println(scene.mNumMeshes() + " meshes");
		PointerBuffer meshBuffer = scene.mMeshes();
		List<Line> listLines = new ArrayList<>();
		for(int k = 0;k < scene.mNumMeshes();k++)
		{
			AIMesh mesh = AIMesh.create(meshBuffer.get(k));
			AIVector3D.Buffer vecBuf = mesh.mVertices();
			FloatBuffer vecf = memFloatBuffer(vecBuf.address(),vecBuf.capacity() * AIVector3D.SIZEOF / 4);
			int vecfSize = vecf.capacity();
			Vector2d[] points = new Vector2d[vecfSize / 3];
			System.out.println(points.length + " unique vertices");
			/* Get all points; ignore z value */
			for(int i = 0;i < points.length;++i)
				points[i] = new Vector2d(vecf.get(i * 3),vecf.get(i * 3 + 2));
			int nFaces = mesh.mNumFaces();
			AIFace.Buffer faces = mesh.mFaces();
			Set<LineIdx> lineIdx = new TreeSet<>();
			/* Get lines from faces and deduplicate */
			for(AIFace x : faces)
			{
				IntBuffer indices = x.mIndices();
				int nIndices = x.mNumIndices();
				for(int i = 0;i < nIndices - 1;++i)
				{
					System.out.println(indices.get(i) + " " + indices.get(i + 1));
					lineIdx.add(new LineIdx(indices.get(i),indices.get(i + 1)));
				}
				lineIdx.add(new LineIdx(indices.get(0),indices.get(nIndices - 1)));
			}
			Iterator<LineIdx> lineIt = lineIdx.iterator();
			int numLines = lineIdx.size();
			/* Create the line objects from deduplicated array */
			for(int i = 0;i < numLines;++i)
			{
				LineIdx cLineIdx = lineIt.next();
				listLines.add(new Line(points[cLineIdx.x],points[cLineIdx.y]));
			}
		}
		lines = new Line[listLines.size()];
		lines = listLines.toArray(lines);
		for(int i = 0;i < lines.length;i++)
			System.out.println(lines[i].pointA + " " + lines[i].pointB);
	}

	public boolean check(final Vector2d pointA,final Vector2d pointB)
	{
		for(final Line x : lines)
			if(x.collide(pointA,pointB))
				return true;
		return false;
	}
}
