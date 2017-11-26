package mesh;

import java.util.ArrayList;
import java.util.List;

import javafx.collections.ObservableFloatArray;
import javafx.scene.image.Image;
import javafx.scene.image.PixelReader;
import javafx.scene.image.WritableImage;
import javafx.scene.shape.Mesh;
import javafx.scene.shape.ObservableFaceArray;
import javafx.scene.shape.Rectangle;
import javafx.scene.shape.TriangleMesh;

import editor.GridEditor;
import entity.ControlPoint;
import test.MainApp;

public class MeshBuilder
{
	public Mesh buildMesh()
	{
		List<Mesh> meshes = new ArrayList<>();
		for (int j = 0; j < GridEditor.verticalPointsCount - 1; j++)
		{
			for (int i = 0; i < GridEditor.horizontalPointsCount - 1; i++)
			{
				ControlPoint cpLeftTop = MainApp.controlPoints.get(j * GridEditor.horizontalPointsCount + i);
				ControlPoint cpRightTop = MainApp.controlPoints.get(j * GridEditor.horizontalPointsCount + i + 1);
				ControlPoint cpLeftBottom = MainApp.controlPoints.get(j * GridEditor.horizontalPointsCount + i + GridEditor.horizontalPointsCount);
				ControlPoint cpRightBottom = MainApp.controlPoints.get(j * GridEditor.horizontalPointsCount + i + GridEditor.horizontalPointsCount + 1);

				Mesh mesh = new CoonsPatchCreator(cpLeftTop, cpRightTop, cpLeftBottom, cpRightBottom).createMesh();
				modifyTexCoords((TriangleMesh)mesh, i, j);
				meshes.add(mesh);
			}
		}
		if (meshes.size() == 1)
		{
			return meshes.get(0);
		}
		Mesh merged = mergeMeshes(meshes);
		return merged;
	}

	private void modifyTexCoords(TriangleMesh mesh, int i, int j)
	{
		ObservableFloatArray texCoords = mesh.getTexCoords();
		for (int k = 0; k < texCoords.size(); k += 2)
		{
			float x = texCoords.get(k);
			float y = texCoords.get(k + 1);
			x = (x + i) / (float)(GridEditor.horizontalPointsCount - 1);
			y = (y + j) / (float)(GridEditor.verticalPointsCount - 1);
			texCoords.set(k, x);
			texCoords.set(k + 1, y);
		}
	}

	public Image getTextureImageClip(TriangleMesh mesh, Image image)
	{
		Rectangle textureBounds = getControlPointBounds(mesh.getPoints());
		PixelReader reader = image.getPixelReader();
		WritableImage cropped = new WritableImage(reader, (int)textureBounds.getX(), (int)textureBounds.getY(), (int)textureBounds.getWidth(), (int)textureBounds.getHeight());
		return cropped;
	}

	private Rectangle getControlPointBounds(ObservableFloatArray lPoints)
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

	private static Mesh mergeMeshes(List<Mesh> meshes)
	{
		int offsetPoints = 0;
		int offsetTexCoords = 0;
		TriangleMesh mergedMesh = new TriangleMesh();
		for (Mesh mesh : meshes)
		{
			TriangleMesh triangleMesh = (TriangleMesh)mesh;
			ObservableFloatArray points = triangleMesh.getPoints();
			ObservableFloatArray texCoords = triangleMesh.getTexCoords();
			ObservableFaceArray faces = triangleMesh.getFaces();
			int[] facesTemp = faces.toArray(new int[1]);
			for (int i = 0; i < facesTemp.length; i += 2)
			{
				facesTemp[i] += offsetPoints;
				facesTemp[i + 1] += offsetTexCoords;
			}
			mergedMesh.getPoints().addAll(points);
			mergedMesh.getTexCoords().addAll(texCoords);
			mergedMesh.getFaces().addAll(facesTemp);
			offsetPoints += points.size() / 3;
			offsetTexCoords += texCoords.size() / 2;
		}
		return mergedMesh;
	}
}
