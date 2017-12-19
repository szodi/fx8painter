package mesh;

import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

import javafx.collections.ObservableList;
import javafx.geometry.Point2D;
import javafx.scene.shape.Polygon;

public class PolygonTriangulator
{
	public static Set<LinkedHashSet<Point2D>> triangulate(Polygon polygon)
	{
		Set<LinkedHashSet<Point2D>> triangles = new HashSet<>();
		ObservableList<Double> polygonPoints = polygon.getPoints();
		double[] points = new double[polygonPoints.size()];
		for (int i = 0; i < polygonPoints.size(); i++)
		{
			points[i] = polygonPoints.get(i);
		}
		List<Integer> triangleIndices = Earcut.earcut(points, null, 2);
		for (int i = 0; i < triangleIndices.size(); i += 3)
		{
			Point2D trianglePoint1 = getPoint(polygon, triangleIndices.get(i));
			Point2D trianglePoint2 = getPoint(polygon, triangleIndices.get(i + 1));
			Point2D trianglePoint3 = getPoint(polygon, triangleIndices.get(i + 2));
			LinkedHashSet<Point2D> triangle = new LinkedHashSet<>();
			triangle.add(trianglePoint1);
			triangle.add(trianglePoint2);
			triangle.add(trianglePoint3);
			triangles.add(triangle);
		}
		return triangles;
	}

	private static Point2D getPoint(Polygon polygon, int index)
	{
		ObservableList<Double> polygonPoints = polygon.getPoints();
		double x = polygonPoints.get((2 * index + polygonPoints.size()) % polygonPoints.size());
		double y = polygonPoints.get((2 * index + 1 + polygonPoints.size()) % polygonPoints.size());
		return new Point2D(x, y);
	}
}
