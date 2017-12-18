package mesh;

import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

import entity.ControlPoint;

public class GraphCycleFinder
{
	List<ControlPoint> controlPoints;

	private Set<LinkedHashSet<ControlPoint>> cycles;

	public GraphCycleFinder(List<ControlPoint> controlPoints)
	{
		this.controlPoints = controlPoints;
	}

	public Set<LinkedHashSet<ControlPoint>> findCycles(int circleLength)
	{
		cycles = new HashSet<>();
		for (ControlPoint controlPoint : controlPoints)
		{
			LinkedHashSet<ControlPoint> pathPoints = new LinkedHashSet<>();
			pathPoints.add(controlPoint);
			findCycle(pathPoints, controlPoint, circleLength);
		}
		return cycles;
	}

	private void findCycle(LinkedHashSet<ControlPoint> path, ControlPoint lastControlPoint, int maxLength)
	{
		ControlPoint firstControlPoint = path.iterator().next();
		Set<ControlPoint> neighbours = lastControlPoint.getNeighbours();
		for (ControlPoint neighbour : neighbours)
		{
			if (path.contains(neighbour))
			{
				if (neighbour != firstControlPoint)
				{
					continue;
				}
				else if (path.size() > 2)
				{
					cycles.add(path);
					break;
				}
			}
			else
			{
				LinkedHashSet<ControlPoint> pathClone = new LinkedHashSet<>(path);
				pathClone.add(neighbour);
				if (pathClone.size() <= maxLength)
				{
					findCycle(pathClone, neighbour, maxLength);
				}
			}
		}
	}
}
