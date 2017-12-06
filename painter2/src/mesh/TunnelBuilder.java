package mesh;

import javafx.scene.shape.TriangleMesh;

import entity.ControlPoint;
import entity.Path;
import test.MainApp;

public class TunnelBuilder extends MeshBuilder
{
	@Override
	public TriangleMesh createMesh()
	{
		int fromIndex = 0;
		int nextIndex = getNextAttachedControlPointIndex(fromIndex);
		Path pathFrom = null;
		while (nextIndex > -1)
		{
			ControlPoint nextControlPoint = MainApp.controlPoints.get(nextIndex);
			Path pathTo = MainApp.pathOfControlPoint.get(nextControlPoint);
			for (int i = fromIndex; i < nextIndex; i++)
			{
				ControlPoint controlPoint = MainApp.controlPoints.get(i);
				Path path = pathTo;
				if (pathFrom != null)
				{
					path = pathFrom.morph(pathTo, Tunnel.pathSmoothness, (i - fromIndex + 1.0) / (nextIndex - fromIndex + 1.0));
				}
				MainApp.pathOfControlPoint.put(controlPoint, path);
			}
			pathFrom = pathTo;
			fromIndex = nextIndex + 1;
			nextIndex = getNextAttachedControlPointIndex(fromIndex);
		}
		Tunnel tunnelCreator = new Tunnel(MainApp.pathOfControlPoint);
		TriangleMesh tunnelMesh = new TriangleMesh();
		tunnelMesh.getPoints().addAll(tunnelCreator.createPoints());
		tunnelMesh.getFaces().addAll(tunnelCreator.createFaces());
		return tunnelMesh;
	}

	private int getNextAttachedControlPointIndex(int fromIndex)
	{
		for (int i = fromIndex; i < MainApp.controlPoints.size(); i++)
		{
			ControlPoint cp = MainApp.controlPoints.get(i);
			if (MainApp.pathOfControlPoint.containsKey(cp) && MainApp.pathOfControlPoint.get(cp) != null)
			{
				return i;
			}
		}
		return -1;
	}
}
