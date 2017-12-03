package mesh;

import java.util.List;

import javafx.collections.ObservableFloatArray;
import javafx.scene.shape.Mesh;
import javafx.scene.shape.ObservableFaceArray;
import javafx.scene.shape.Rectangle;
import javafx.scene.shape.TriangleMesh;

import tools.Tools;

public abstract class MeshBuilder
{
	public Mesh buildMesh(boolean grabImage)
	{
		TriangleMesh merged = createMesh();
		if (grabImage)
		{
			modifyTexCoords(merged);
		}
		return merged;
	}

	private void modifyTexCoords(TriangleMesh mesh)
	{
		ObservableFloatArray points = mesh.getPoints();
		Rectangle controlPointBounds = Tools.getControlPointBounds(mesh.getPoints());
		float[] texCoords = new float[2 * points.size() / 3];

		int j = 0;
		for (int i = 0; i < points.size(); i += 3)
		{
			texCoords[j++] = (float)(points.get(i) - controlPointBounds.getX()) / (float)controlPointBounds.getWidth();
			texCoords[j++] = (float)(points.get(i + 1) - controlPointBounds.getY()) / (float)controlPointBounds.getHeight();
		}
		mesh.getTexCoords().setAll(texCoords);
	}

	public static TriangleMesh mergeMeshes(List<TriangleMesh> meshes)
	{
		TriangleMesh mergedMesh = new TriangleMesh();
		ObservableFloatArray points = mergedMesh.getPoints();
		ObservableFaceArray faces = mergedMesh.getFaces();
		int faceOffset = 0;
		for (TriangleMesh mesh : meshes)
		{
			points.addAll(mesh.getPoints());
			ObservableFaceArray meshFaces = mesh.getFaces();
			for (int i = 0; i < meshFaces.size(); i++)
			{
				meshFaces.set(i, meshFaces.get(i) + faceOffset);
			}
			faces.addAll(mesh.getFaces());
			faceOffset += mesh.getPoints().size() / 3;
		}
		return mergedMesh;
	}

	public abstract TriangleMesh createMesh();

}
