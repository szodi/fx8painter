package mesh;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javafx.collections.ObservableFloatArray;
import javafx.scene.shape.Mesh;
import javafx.scene.shape.Rectangle;
import javafx.scene.shape.TriangleMesh;

import editor.GridEditor;
import entity.ControlPoint;
import entity.Path;
import test.MainApp;
import tools.Tools;

public class MeshBuilder
{

	public Mesh buildMesh(boolean grabImage)
	{

		TriangleMesh merged = createTunnel();
		// TriangleMesh merged = createCoonsPatch();
		if (grabImage)
		{
			modifyTexCoords(merged);
		}
		return merged;
	}

	private TriangleMesh createCoonsPatch()
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
		return Tools.mergeMeshes(meshes);
	}

	private TriangleMesh createTunnel()
	{
		Map<ControlPoint, Path> controlPointPathMap = new HashMap<>();
		Path path = Path.create(MainApp.pathControlPoints);
		for (ControlPoint controlPoint : MainApp.controlPoints)
		{
			Path pathClone = path.clone();
			ControlPoint pathHead = pathClone.getHead();
			pathClone.translate(controlPoint.getX() - pathHead.getX(), controlPoint.getY() - pathHead.getY(), controlPoint.getZ() - pathHead.getZ());
			controlPointPathMap.put(controlPoint, pathClone);
		}
		TunnelCreator tunnelCreator = new TunnelCreator(controlPointPathMap);
		TriangleMesh tunnelMesh = new TriangleMesh();
		tunnelMesh.getPoints().addAll(tunnelCreator.createPoints());
		tunnelMesh.getFaces().addAll(tunnelCreator.createFaces());
		return tunnelMesh;
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
}
