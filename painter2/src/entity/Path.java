package entity;

import java.util.Collection;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.Objects;

import tools.Tools;

public class Path {

	List<ControlPoint> controlPoints;

	public Path(List<ControlPoint> controlPoints) {
		this.controlPoints = controlPoints;
	}

	public static Path create(List<ControlPoint> controlPoints) {
		if (Objects.isNull(controlPoints) || controlPoints.size() < 2) {
			throw new RuntimeException("Minimum 2 control point is neccessary");
		}
		int countEnds = 0;
		for (ControlPoint controlPoint : controlPoints) {
			Collection<MutablePoint3D> tangents = controlPoint.getTangents();
			if (tangents.isEmpty() || countEnds > 2) {
				throw new RuntimeException("Curve cannot be separated");
			}
			if (tangents.size() > 2) {
				throw new RuntimeException("Control point cannot have more than 2 connections, " + controlPoint
						+ " has " + tangents.size());
			}
			if (tangents.size() == 1) {
				countEnds++;
			}
		}
		return new Path(controlPoints);
	}

	public MutablePoint3D getPathPoint(double t) {
		double pt = t * (controlPoints.size() - 1);
		int beforeControlPointIndex = (int) Math.floor(pt);
		int afterControlPointIndex = (int) Math.ceil(pt);
		ControlPoint before = controlPoints.get(beforeControlPointIndex);
		ControlPoint after = controlPoints.get(afterControlPointIndex);
		return Tools.getBezierPoint(before, after, pt - beforeControlPointIndex);
	}

	public void removeControlPoint(ControlPoint controlPoint) {
		if (Objects.isNull(controlPoints) || controlPoints.isEmpty()) {
			return;
		}
		ControlPoint before = null;
		ControlPoint after = null;
		for (Iterator<ControlPoint> controlPointIterator = controlPoints.iterator(); controlPointIterator.hasNext();) {
			ControlPoint actual = controlPointIterator.next();
			if (actual != controlPoint) {
				before = actual;
			} else {
				controlPointIterator.remove();
				if (controlPointIterator.hasNext()) {
					after = controlPointIterator.next();
					after.deleteTangent(controlPoint);
				}
				if (before != null) {
					before.deleteTangent(controlPoint);
					if (after != null) {
						before.setTangent(after, after);
						after.setTangent(before, before);
					}
				}
				break;
			}
		}
	}

	public ControlPoint getHead() {
		if (Objects.isNull(controlPoints) || controlPoints.isEmpty()) {
			return null;
		}
		return controlPoints.get(0);
	}

	public ControlPoint getTail() {
		if (Objects.isNull(controlPoints) || controlPoints.isEmpty()) {
			return null;
		}
		return controlPoints.get(controlPoints.size() - 1);
	}

	public void swapHeadAndTail() {
		if (Objects.isNull(controlPoints) || controlPoints.isEmpty()) {
			return;
		}
		Collections.reverse(controlPoints);
	}
}
