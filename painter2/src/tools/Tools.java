package tools;

import java.util.Collection;
import java.util.List;
import java.util.Objects;

import javafx.collections.ObservableFloatArray;
import javafx.geometry.Point3D;
import javafx.scene.Node;
import javafx.scene.shape.ObservableFaceArray;
import javafx.scene.shape.Rectangle;
import javafx.scene.shape.TriangleMesh;

import entity.ControlPoint;
import entity.MutablePoint3D;

public class Tools
{
	public static Rectangle getControlPointBounds(ObservableFloatArray lPoints)
	{
		float minX = Float.MAX_VALUE;
		float minY = Float.MAX_VALUE;
		float maxX = Float.MIN_VALUE;
		float maxY = Float.MIN_VALUE;
		for (int i = 0; i < lPoints.size(); i += 3)
		{
			float x = lPoints.get(i);
			float y = lPoints.get(i + 1);
			if (x < minX)
			{
				minX = x;
			}
			if (x > maxX)
			{
				maxX = x;
			}
			if (y < minY)
			{
				minY = y;
			}
			if (y > maxY)
			{
				maxY = y;
			}
		}
		return new Rectangle(minX, minY, maxX - minX, maxY - minY);
	}

	public static Rectangle getControlPointBounds(List<ControlPoint> controlPoints)
	{
		double minX = Double.MAX_VALUE;
		double minY = Double.MAX_VALUE;
		double maxX = Double.MIN_VALUE;
		double maxY = Double.MIN_VALUE;
		for (ControlPoint controlPoint : controlPoints)
		{
			if (controlPoint.getX() < minX)
			{
				minX = controlPoint.getX();
			}
			if (controlPoint.getX() > maxX)
			{
				maxX = controlPoint.getX();
			}
			if (controlPoint.getY() < minY)
			{
				minY = controlPoint.getY();
			}
			if (controlPoint.getY() > maxY)
			{
				maxY = controlPoint.getY();
			}
		}
		return new Rectangle(minX, minY, maxX - minX, maxY - minY);
	}

	public static TriangleMesh mergeMeshes(List<TriangleMesh> meshes)
	{
		TriangleMesh mergedMesh = new TriangleMesh();
		ObservableFloatArray points = mergedMesh.getPoints();
		ObservableFaceArray faces = mergedMesh.getFaces();
		int faceOffset = 0;
		for (TriangleMesh mesh : meshes)
		{
			points.addAll(mesh.getPoints());
			ObservableFaceArray meshFaces = mesh.getFaces();
			for (int i = 0; i < meshFaces.size(); i++)
			{
				meshFaces.set(i, meshFaces.get(i) + faceOffset);
			}
			faces.addAll(mesh.getFaces());
			faceOffset += mesh.getPoints().size() / 3;
		}
		return mergedMesh;
	}

	public static MutablePoint3D getCurvePoint(List<ControlPoint> controlPoints, double t)
	{
		if (Objects.isNull(controlPoints) || controlPoints.isEmpty())
		{
			return null;
		}
		else if (controlPoints.size() == 1)
		{
			return controlPoints.get(0);
		}
		double pt = t * (controlPoints.size() - 1);
		int beforeControlPointIndex = (int)Math.floor(pt);
		int afterControlPointIndex = (int)Math.ceil(pt);
		if (beforeControlPointIndex == afterControlPointIndex)
		{
			if (t < 1.0)
			{
				afterControlPointIndex++;
			}
			else
			{
				beforeControlPointIndex--;
			}
		}
		ControlPoint before = controlPoints.get(beforeControlPointIndex);
		ControlPoint after = controlPoints.get(afterControlPointIndex);
		return Tools.getBezierPoint(before, after, pt - beforeControlPointIndex);
	}

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
