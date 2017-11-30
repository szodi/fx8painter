package tools;

import java.util.Collection;

import javafx.geometry.Point3D;
import javafx.scene.Node;

import entity.ControlPoint;
import entity.MutablePoint3D;

public class Tools
{

	public static ControlPoint getControlPointAt(Node node, Collection<ControlPoint> controlPoints, double x, double y, double z, double tolerance)
	{
		for (ControlPoint controlPoint : controlPoints)
		{
			Point3D point = node.localToParent(controlPoint.getX(), controlPoint.getY(), controlPoint.getZ());
			if (Math.abs(point.getX() - x) <= tolerance && Math.abs(point.getY() - y) <= tolerance)
			{
				return controlPoint;
			}
		}
		return null;
	}

	public static MutablePoint3D getBezierPoint(MutablePoint3D point1, MutablePoint3D point2, MutablePoint3D point3, MutablePoint3D point4, double t)
	{
		double x = (1 - t) * (1 - t) * (1 - t) * point1.getX() + 3 * t * (1 - t) * (1 - t) * (point1.getX() + point2.getX()) + 3 * t * t * (1 - t) * (point4.getX() + point3.getX()) + t * t * t * point4.getX();
		double y = (1 - t) * (1 - t) * (1 - t) * point1.getY() + 3 * t * (1 - t) * (1 - t) * (point1.getY() + point2.getY()) + 3 * t * t * (1 - t) * (point4.getY() + point3.getY()) + t * t * t * point4.getY();
		double z = (1 - t) * (1 - t) * (1 - t) * point1.getZ() + 3 * t * (1 - t) * (1 - t) * (point1.getZ() + point2.getZ()) + 3 * t * t * (1 - t) * (point4.getZ() + point3.getZ()) + t * t * t * point4.getZ();
		return new MutablePoint3D(x, y, z);
	}

	public static MutablePoint3D getBezierPoint(ControlPoint controlPoint1, ControlPoint controlPoint2, double t)
	{
		return Tools.getBezierPoint(controlPoint1, controlPoint1.getTangent(controlPoint2), controlPoint2.getTangent(controlPoint1), controlPoint2, t);
	}
}
