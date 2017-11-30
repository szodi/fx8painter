package entity;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.Objects;
import java.util.Set;

public class Path
{

	List<ControlPoint> controlPoints;

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

	public Path clone()
	{
		List<ControlPoint> sourceControlPoints = getControlPoints();
		List<ControlPoint> clonedControlPoints = new ArrayList<>();
		sourceControlPoints.forEach(controlPoint -> clonedControlPoints.add(new ControlPoint(controlPoint.getX(), controlPoint.getY(), controlPoint.getZ())));
		for (int i = 0; i < sourceControlPoints.size(); i++)
		{
			ControlPoint sourceControlPoint = sourceControlPoints.get(i);
			Set<ControlPoint> sourceNeighbours = sourceControlPoint.getNeighbours();
			for (ControlPoint neighbour : sourceNeighbours)
			{
				int neighbourIndex = sourceControlPoints.indexOf(neighbour);
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

	public void swapHeadAndTail()
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

	public void setControlPoints(List<ControlPoint> controlPoints)
	{
		this.controlPoints = controlPoints;
	}
}
