package mesh;

import java.util.ArrayList;
import java.util.List;

import javafx.scene.shape.TriangleMesh;

import editor.GridEditor;
import entity.ControlPoint;
import test.MainApp;

public class CoonsPatchBuilder extends MeshBuilder
{
	@Override
	public TriangleMesh createMesh()
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
				CoonsPatch coonsPatchCreator = new CoonsPatch(cpLeftTop, cpRightTop, cpLeftBottom, cpRightBottom);
				coonsMesh.getPoints().addAll(coonsPatchCreator.createPoints());
				coonsMesh.getFaces().addAll(coonsPatchCreator.createFaces());
				meshes.add(coonsMesh);
			}
		}
		return mergeMeshes(meshes);
	}
}
