package mesh;

import javafx.scene.shape.Mesh;
import javafx.scene.shape.Rectangle;
import javafx.scene.shape.TriangleMesh;

public class RectangleMeshBuilder
{
	public static Mesh createMesh(Rectangle rectangle, int gridHorizontal, int gridVertical)
	{
		float[] points = new float[gridHorizontal * gridVertical * 3];
		float[] texCoords = new float[gridHorizontal * gridVertical * 2];
		int[] faces = new int[(gridHorizontal - 1) * (gridVertical - 1) * 12];

		double scaleHorizontal = rectangle.getWidth() / (gridHorizontal - 1);
		double scaleVertical = rectangle.getHeight() / (gridVertical - 1);
		int a = 0;
		int b = 0;
		int c = 0;
		for (int j = 0; j < gridVertical; j++)
		{
			for (int i = 0; i < gridHorizontal; i++)
			{
				points[a++] = (float)(i * scaleHorizontal);
				points[a++] = (float)(j * scaleVertical);
				points[a++] = (float)(Math.random() * 50 - 25);

				texCoords[b++] = (float)i / (float)(gridHorizontal - 1);
				texCoords[b++] = (float)j / (float)(gridVertical - 1);

				if (i < gridHorizontal - 1 && j < gridVertical - 1)
				{
					int actual = j * gridHorizontal + i;

					faces[c++] = actual;
					faces[c++] = actual;
					faces[c++] = actual + gridHorizontal;
					faces[c++] = actual + gridHorizontal;
					faces[c++] = actual + gridHorizontal + 1;
					faces[c++] = actual + gridHorizontal + 1;

					faces[c++] = actual;
					faces[c++] = actual;
					faces[c++] = actual + gridHorizontal + 1;
					faces[c++] = actual + gridHorizontal + 1;
					faces[c++] = actual + 1;
					faces[c++] = actual + 1;
				}
			}
		}

		TriangleMesh mesh = new TriangleMesh();
		mesh.getPoints().setAll(points);
		mesh.getTexCoords().setAll(texCoords);
		mesh.getFaces().setAll(faces);

		return mesh;
	}
}
