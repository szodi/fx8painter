package mesh;

import java.util.Map;
import java.util.Set;

import javafx.geometry.Point2D;

import entity.ControlPoint;
import entity.MutablePoint3D;
import entity.Path;
import test.MainApp;
import tools.Tools;

public class Tunnel
{
	Map<ControlPoint, Path> controlPointPathMap;
	double curveSmoothness = 0.1;
	double pathSmoothness = 0.01;

	public Tunnel(Map<ControlPoint, Path> controlPointPathMap)
	{
		this.controlPointPathMap = controlPointPathMap;
	}

	public float[] createPoints()
	{
		Set<ControlPoint> controlPoints = controlPointPathMap.keySet();
		int horizontalSteps = (int)(1.0 / curveSmoothness);
		int verticalSteps = (int)(1.0 / pathSmoothness);
		float[] points = new float[(horizontalSteps + 1) * (verticalSteps + 1) * 3];
		int n = 0;
		for (int y = 0; y <= verticalSteps; y++)
		{
			double v = (double)y / (double)verticalSteps;
			for (ControlPoint controlPoint : controlPoints)
			{
				Path path = controlPointPathMap.get(controlPoint);
				MutablePoint3D pathPoint = Tools.getCurvePoint(path.getControlPoints(), v).subtract(controlPoint);
				controlPoint.add(pathPoint);
			}
			for (int x = 0; x <= horizontalSteps; x++)
			{
				double u = (double)x / (double)horizontalSteps;
				MutablePoint3D curvePoint = Tools.getCurvePoint(MainApp.controlPoints, u);
				points[n++] = (float)curvePoint.getX();
				points[n++] = (float)curvePoint.getY();
				points[n++] = (float)curvePoint.getZ();
			}
		}
		return points;
	}

	public int[] createFaces()
	{
		int horizontalSteps = (int)(1.0 / curveSmoothness);
		int verticalSteps = (int)(1.0 / pathSmoothness);
		int[] faces = new int[horizontalSteps * verticalSteps * 12];
		int n = 0;
		for (int y = 0; y < verticalSteps; y++)
		{
			for (int x = 0; x < horizontalSteps; x++)
			{
				int[] faceIndices = {0, horizontalSteps + 1, horizontalSteps + 2, 0, horizontalSteps + 2, 1};
				int faceIndex = y * (horizontalSteps + 1) + x;
				for (int k = 0; k < faceIndices.length; k++)
				{
					faces[n++] = faceIndex + faceIndices[k];
					faces[n++] = faceIndex + faceIndices[k];
				}
			}
		}
		return faces;
	}

	public static Point2D toPoint2D(MutablePoint3D point)
	{
		return new Point2D(point.getX(), point.getY());
	}
}
