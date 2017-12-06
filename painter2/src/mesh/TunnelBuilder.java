package mesh;

import java.util.LinkedHashMap;
import java.util.Map;

import javafx.scene.shape.TriangleMesh;

import entity.ControlPoint;
import entity.Path;
import test.MainApp;

public class TunnelBuilder extends MeshBuilder
{
	@Override
	public TriangleMesh createMesh()
	{
		Map<ControlPoint, Path> controlPointPathMap = new LinkedHashMap<>();
		for (int i = 0; i < MainApp.controlPoints.size(); i++)
		{
			ControlPoint controlPoint = MainApp.controlPoints.get(i);
			Path firstPath = MainApp.paths.get(0);
			Path lastPath = MainApp.paths.get(MainApp.paths.size() - 1);
			Path path;
			if (i == 0)
			{
				path = firstPath;
			}
			else if (i == MainApp.controlPoints.size() - 1)
			{
				path = lastPath;
			}
			else
			{
				double u = (1.0 * i / (MainApp.controlPoints.size() - 1));
				path = firstPath.morph(lastPath, Tunnel.pathSmoothness, u);
			}
			path.translate(controlPoint.getX() - path.getHead().getX(), controlPoint.getY() - path.getHead().getY(), controlPoint.getZ() - path.getHead().getZ());
			controlPointPathMap.put(controlPoint, path);
		}
		// Path path = Path.create(MainApp.pathControlPoints);
		// for (ControlPoint controlPoint : MainApp.controlPoints)
		// {
		// Path pathClone = path.clone();
		// ControlPoint pathHead = pathClone.getHead();
		// pathClone.translate(controlPoint.getX() - pathHead.getX(), controlPoint.getY() - pathHead.getY(), controlPoint.getZ() - pathHead.getZ());
		// if (!controlPointPathMap.containsKey(controlPoint))
		// {
		// controlPointPathMap.put(controlPoint, pathClone);
		// }
		// }
		Tunnel tunnelCreator = new Tunnel(controlPointPathMap);
		TriangleMesh tunnelMesh = new TriangleMesh();
		tunnelMesh.getPoints().addAll(tunnelCreator.createPoints());
		tunnelMesh.getFaces().addAll(tunnelCreator.createFaces());
		return tunnelMesh;
	}
}
