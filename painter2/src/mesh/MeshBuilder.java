package mesh;

import java.util.ArrayList;
import java.util.List;

import javafx.collections.ObservableFloatArray;
import javafx.scene.shape.Mesh;
import javafx.scene.shape.ObservableFaceArray;
import javafx.scene.shape.Rectangle;
import javafx.scene.shape.TriangleMesh;

import editor.GridEditor;
import entity.ControlPoint;
import test.MainApp;

public class MeshBuilder
{
	public Mesh buildMesh(boolean grabImage)
	{
		List<TriangleMesh> meshes = new ArrayList<>();
		for (int j = 0; j < GridEditor.verticalPointsCount - 1; j++)
		{
			for (int i = 0; i < GridEditor.horizontalPointsCount - 1; i++)
			{
				ControlPoint cpLeftTop = MainApp.controlPoints.get(j * GridEditor.horizontalPointsCount + i);
				ControlPoint cpRightTop = MainApp.controlPoints.get(j * GridEditor.horizontalPointsCount + i + 1);
				ControlPoint cpLeftBottom = MainApp.controlPoints.get(j * GridEditor.horizontalPointsCount + i + GridEditor.horizontalPointsCount);
				ControlPoint cpRightBottom = MainApp.controlPoints.get(j * GridEditor.horizontalPointsCount + i + GridEditor.horizontalPointsCount + 1);

				TriangleMesh coonsMesh = new TriangleMesh();
				CoonsPatchCreator coonsPatchCreator = new CoonsPatchCreator(cpLeftTop, cpRightTop, cpLeftBottom, cpRightBottom);
				coonsMesh.getPoints().addAll(coonsPatchCreator.createPoints());
				coonsMesh.getFaces().addAll(coonsPatchCreator.createFaces());
				meshes.add(coonsMesh);
			}
		}
		TriangleMesh merged = mergeMeshes(meshes);
		if (grabImage)
		{
			modifyTexCoords(merged);
		}
		return merged;
	}

	private void modifyTexCoords(TriangleMesh mesh)
	{
		ObservableFloatArray points = mesh.getPoints();
		Rectangle controlPointBounds = getControlPointBounds(mesh.getPoints());
		float[] texCoords = new float[2 * points.size() / 3];

		int j = 0;
		for (int i = 0; i < points.size(); i += 3)
		{
			texCoords[j++] = (float)(points.get(i) - controlPointBounds.getX()) / (float)controlPointBounds.getWidth();
			texCoords[j++] = (float)(points.get(i + 1) - controlPointBounds.getY()) / (float)controlPointBounds.getHeight();
		}
		mesh.getTexCoords().setAll(texCoords);
	}

	public static Rectangle getControlPointBounds(ObservableFloatArray lPoints)
	{
		float minX = Float.MAX_VALUE;
		float minY = Float.MAX_VALUE;
		float maxX = Float.MIN_VALUE;
		float maxY = Float.MIN_VALUE;
		for (int i = 0; i < lPoints.size(); i += 3)
		{
			float x = lPoints.get(i);
			float y = lPoints.get(i + 1);
			if (x < minX)
			{
				minX = x;
			}
			if (x > maxX)
			{
				maxX = x;
			}
			if (y < minY)
			{
				minY = y;
			}
			if (y > maxY)
			{
				maxY = y;
			}
		}
		return new Rectangle(minX, minY, maxX - minX, maxY - minY);
	}

	private static TriangleMesh mergeMeshes(List<TriangleMesh> meshes)
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
}
