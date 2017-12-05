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
		Path path = Path.create(MainApp.pathControlPoints);
		for (ControlPoint controlPoint : MainApp.controlPoints)
		{
			Path pathClone = path.clone();
			ControlPoint pathHead = pathClone.getHead();
			pathClone.translate(controlPoint.getX() - pathHead.getX(), controlPoint.getY() - pathHead.getY(), controlPoint.getZ() - pathHead.getZ());
			if (!controlPointPathMap.containsKey(controlPoint))
			{
				controlPointPathMap.put(controlPoint, pathClone);
			}
		}
		Tunnel tunnelCreator = new Tunnel(controlPointPathMap);
		TriangleMesh tunnelMesh = new TriangleMesh();
		tunnelMesh.getPoints().addAll(tunnelCreator.createPoints());
		tunnelMesh.getFaces().addAll(tunnelCreator.createFaces());
		return tunnelMesh;
	}
}
