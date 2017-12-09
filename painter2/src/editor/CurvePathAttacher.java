package editor;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;

import javafx.scene.Node;
import javafx.scene.input.MouseEvent;

import entity.ControlPoint;
import entity.Path;
import test.MainApp;

public class CurvePathAttacher extends PathEditor
{
	List<ControlPoint> curveControlPoints;
	List<ControlPoint> pathControlPoints;
	ControlPoint pathControlPoint;

	public CurvePathAttacher(List<ControlPoint> controlPoints, List<ControlPoint> pathControlPoints, Consumer<List<ControlPoint>> curveDrawer)
	{
		super(new ArrayList<>(), curveDrawer);
		curveControlPoints = controlPoints;
		this.pathControlPoints = pathControlPoints;
	}

	@Override
	public void activate(Node node)
	{
		super.activate(node);
		controlPoints.clear();
		controlPoints.addAll(curveControlPoints);
		controlPoints.addAll(pathControlPoints);
		curveDrawer.accept(controlPoints);
	}

	@Override
	protected void handlePrimaryMousePressed(MouseEvent event)
	{
		controlPoint = getControlPointAt(curveControlPoints, event.getX(), event.getY());
		pathControlPoint = getControlPointAt(pathControlPoints, event.getX(), event.getY());
		if (event.getClickCount() == 2)
		{
			if (controlPoint != null)
			{
				MainApp.actualControlPoint = controlPoint;
			}
			if (pathControlPoint != null)
			{
				MainApp.actualPathControlPoint = pathControlPoint;
			}
			if (MainApp.actualControlPoint != null && MainApp.actualPathControlPoint != null)
			{
				Path path = getPath(pathControlPoint);
				MainApp.pathOfControlPoint.put(MainApp.actualControlPoint, path);
			}
		}
	}

	@Override
	protected void handleSecondaryMousePressed(MouseEvent event)
	{
		pathControlPoint = getControlPointAt(pathControlPoints, event.getX(), event.getY());
		if (event.getClickCount() == 2)
		{
			if (pathControlPoint != null)
			{
				Path path = getPath(pathControlPoint);
				path.reverse();
				MainApp.actualPathControlPoint = pathControlPoint;
			}
		}
	}

	private Path getPath(ControlPoint controlPoint)
	{
		for (Path path : MainApp.paths)
		{
			if (path.getControlPoints().indexOf(controlPoint) > -1)
			{
				return path;
			}
		}
		return null;
	}
}
