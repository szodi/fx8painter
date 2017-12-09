package entity;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.Objects;
import java.util.Set;

import tools.Tools;

public class Path
{
	List<ControlPoint> controlPoints = new ArrayList<>();

	public Path()
	{

	}

	public Path(List<ControlPoint> controlPoints)
	{
		this.controlPoints = controlPoints;
	}

	public static Path create(List<ControlPoint> controlPoints)
	{
		if (Objects.isNull(controlPoints) || controlPoints.size() < 2)
		{
			throw new RuntimeException("Minimum 2 control point is neccessary");
		}
		int countEnds = 0;
		for (ControlPoint controlPoint : controlPoints)
		{
			Collection<MutablePoint3D> tangents = controlPoint.getTangents();
			if (tangents.isEmpty() || countEnds > 2)
			{
				throw new RuntimeException("Curve cannot be separated");
			}
			if (tangents.size() > 2)
			{
				throw new RuntimeException("Control point cannot have more than 2 connections, " + controlPoint + " has " + tangents.size());
			}
			if (tangents.size() == 1)
			{
				countEnds++;
			}
		}
		return new Path(controlPoints);
	}

	public Path morph(Path path, double pathSmoothness, double morphState)
	{
		List<ControlPoint> morphPoints = new ArrayList<>();
		for (double t = 0; t <= 1.0; t += pathSmoothness)
		{
			MutablePoint3D actualPathPoint = Tools.getCurvePoint(getControlPoints(), t);
			MutablePoint3D otherPathPoint = Tools.getCurvePoint(path.getControlPoints(), t);
			MutablePoint3D morphPoint = otherPathPoint.subtract(actualPathPoint).multiply(morphState).add(actualPathPoint);
			ControlPoint fakeControlPoint = new ControlPoint(morphPoint.getX(), morphPoint.getY(), morphPoint.getZ());
			morphPoints.add(fakeControlPoint);
		}
		for (int i = 0; i < morphPoints.size() - 1; i++)
		{
			ControlPoint actualFakeControlPoint = morphPoints.get(i);
			ControlPoint nextFakeControlPoint = morphPoints.get(i + 1);
			actualFakeControlPoint.setTangent(nextFakeControlPoint, nextFakeControlPoint);
			nextFakeControlPoint.setTangent(actualFakeControlPoint, actualFakeControlPoint);
		}
		return create(morphPoints);
	}

	// public List<MutablePoint3D> morph(Path path, double pathSmoothness, double morphState)
	// {
	// List<MutablePoint3D> morphPoints = new ArrayList<>();
	// for (double t = 0; t <= 1.0; t += pathSmoothness)
	// {
	// MutablePoint3D actualPathPoint = Tools.getCurvePoint(getControlPoints(), t);
	// MutablePoint3D otherPathPoint = Tools.getCurvePoint(path.getControlPoints(), t);
	// MutablePoint3D morphPoint = otherPathPoint.subtract(actualPathPoint).multiply(morphState).add(actualPathPoint);
	// morphPoints.add(morphPoint);
	// }
	// return morphPoints;
	// }
	//
	public Path clone()
	{
		List<ControlPoint> clonedControlPoints = new ArrayList<>();
		controlPoints.forEach(controlPoint -> clonedControlPoints.add(new ControlPoint(controlPoint.getX(), controlPoint.getY(), controlPoint.getZ())));
		for (int i = 0; i < controlPoints.size(); i++)
		{
			ControlPoint sourceControlPoint = controlPoints.get(i);
			Set<ControlPoint> sourceNeighbours = sourceControlPoint.getNeighbours();
			for (ControlPoint neighbour : sourceNeighbours)
			{
				int neighbourIndex = controlPoints.indexOf(neighbour);
				MutablePoint3D sourceTangent = sourceControlPoint.getTangent(neighbour);
				clonedControlPoints.get(i).setTangent(clonedControlPoints.get(neighbourIndex), sourceTangent.clone());
			}
		}
		return new Path(clonedControlPoints);
	}

	public void removeControlPoint(ControlPoint controlPoint)
	{
		if (Objects.isNull(controlPoints) || controlPoints.isEmpty())
		{
			return;
		}
		ControlPoint before = null;
		ControlPoint after = null;
		for (Iterator<ControlPoint> controlPointIterator = controlPoints.iterator(); controlPointIterator.hasNext();)
		{
			ControlPoint actual = controlPointIterator.next();
			if (actual != controlPoint)
			{
				before = actual;
			}
			else
			{
				controlPointIterator.remove();
				if (controlPointIterator.hasNext())
				{
					after = controlPointIterator.next();
					after.deleteTangent(controlPoint);
				}
				if (before != null)
				{
					before.deleteTangent(controlPoint);
					if (after != null)
					{
						before.setTangent(after, after);
						after.setTangent(before, before);
					}
				}
				break;
			}
		}
	}

	public void translate(double x, double y, double z)
	{
		controlPoints.forEach(cp -> cp.add(x, y, z));
	}

	public void translate(MutablePoint3D point)
	{
		translate(point.getX(), point.getY(), point.getZ());
	}

	public ControlPoint getHead()
	{
		if (Objects.isNull(controlPoints) || controlPoints.isEmpty())
		{
			return null;
		}
		return controlPoints.get(0);
	}

	public ControlPoint getTail()
	{
		if (Objects.isNull(controlPoints) || controlPoints.isEmpty())
		{
			return null;
		}
		return controlPoints.get(controlPoints.size() - 1);
	}

	public void reverse()
	{
		if (Objects.isNull(controlPoints) || controlPoints.isEmpty())
		{
			return;
		}
		Collections.reverse(controlPoints);
	}

	public List<ControlPoint> getControlPoints()
	{
		return controlPoints;
	}
}
