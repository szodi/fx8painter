package mesh;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;

import editor.CurveDrawer;
import entity.ControlPoint;
import entity.MutablePoint3D;
import entity.Path;
import javafx.geometry.Point2D;
import tools.Tools;

public class TunnelCreator {
	Map<ControlPoint, Path> controlPointPathMap;
	double curveSmoothness = 0.1;
	double pathSmoothness = 0.1;

	public TunnelCreator(Map<ControlPoint, Path> controlPointPathMap) {
		this.controlPointPathMap = controlPointPathMap;
	}

	public float[] createPoints() {
		Set<ControlPoint> controlPoints = controlPointPathMap.keySet();
		List<MutablePoint3D> lPoints = new ArrayList<>();
		for (int t = 0; t <= 1.0; t += pathSmoothness) {
			for (ControlPoint controlPoint : controlPoints) {
				Path path = controlPointPathMap.get(controlPoint);
				MutablePoint3D pathPoint = path.getPathPoint(t);
				controlPoint.translate(pathPoint);
				for (ControlPoint neighbour : controlPoint.getNeighbours()) {
					for (double s = 0.0; s <= 1.0; s += curveSmoothness) {
						MutablePoint3D point = Tools.getBezierPoint(controlPoint, neighbour, s);
						lPoints.add(point);
					}
				}
			}
		}
		float[] points = new float[lPoints.size() * 3];
		for (int i = 0; i < lPoints.size(); i++) {
			points[3 * i + 0] = (float) lPoints.get(i).getX();
			points[3 * i + 1] = (float) lPoints.get(i).getY();
			points[3 * i + 2] = (float) lPoints.get(i).getZ();
		}
		return points;
	}

	public int[] createFaces() {
		int horizontalSteps = (int) (1.0 / CurveDrawer.smoothness);
		int verticalSteps = (int) (1.0 / CurveDrawer.smoothness);
		int[] faces = new int[horizontalSteps * verticalSteps * 12];
		int n = 0;
		for (int y = 0; y < verticalSteps; y++) {
			for (int x = 0; x < horizontalSteps; x++) {
				int[] faceIndices = { 0, horizontalSteps + 1, horizontalSteps + 2, 0, horizontalSteps + 2, 1 };
				int faceIndex = y * (horizontalSteps + 1) + x;
				for (int k = 0; k < faceIndices.length; k++) {
					faces[n++] = faceIndex + faceIndices[k];
					faces[n++] = faceIndex + faceIndices[k];
				}
			}
		}
		return faces;
	}

	public static Point2D toPoint2D(MutablePoint3D point) {
		return new Point2D(point.getX(), point.getY());
	}
}
