package editor;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;

import javafx.scene.Node;
import javafx.scene.input.MouseEvent;

import entity.ControlPoint;
import entity.Path;
import test.MainApp;

public class PathEditor extends AbstractEditor
{
	protected Consumer<List<ControlPoint>> curveDrawer;
	private List<Path> paths = new ArrayList<>();
	private Path activePath;

	public PathEditor(List<ControlPoint> controlPoints, Consumer<List<ControlPoint>> curveDrawer)
	{
		this.controlPoints = controlPoints;
		this.curveDrawer = curveDrawer;
	}

	@Override
	public void activate(Node node)
	{
		super.activate(node);
		curveDrawer.accept(controlPoints);
		node.getScene().setOnKeyPressed(event -> System.out.println(activePath));
	}

	@Override
	public void handle(MouseEvent mouseEvent)
	{
		super.handle(mouseEvent);
		curveDrawer.accept(controlPoints);
	}

	@Override
	protected void handlePrimaryMousePressed(MouseEvent event)
	{
		controlPoint = getControlPointAt(controlPoints, event.getX(), event.getY());
		if (controlPoint == null)
		{
			if (event.isShiftDown() || activePath == null)
			{
				activePath = new Path();
				paths.add(activePath);
			}
			if (!event.isControlDown())
			{
				controlPoint = new ControlPoint(event.getX(), event.getY(), event.getZ());
				if (!controlPoints.isEmpty() && !event.isShiftDown())
				{
					ControlPoint lastControlPoint = controlPoints.get(controlPoints.size() - 1);
					lastControlPoint.setTangent(controlPoint, controlPoint);
					controlPoint.setTangent(lastControlPoint, lastControlPoint);
				}
				controlPoints.add(controlPoint);
				activePath.getControlPoints().add(controlPoint);
			}
		}
		else
		{
			activePath = getPath(controlPoint);
			if (event.isControlDown())
			{
				controlPoint.setSelected(!controlPoint.isSelected());
			}
			else
			{
				controlPoints.forEach(cp -> cp.setSelected(false));
			}
		}
		MainApp.actualControlPoint = controlPoint;
	}

	private Path getPath(ControlPoint controlPoint)
	{
		for (Path path : paths)
		{
			if (path.getControlPoints().indexOf(controlPoint) > -1)
			{
				return path;
			}
		}
		return null;
	}

	@Override
	protected void handlePrimaryMouseDragged(MouseEvent event)
	{
		if (controlPoint != null)
		{
			controlPoint.setX(event.getX());
			controlPoint.setY(event.getY());
		}
	}

	@Override
	protected void handleSecondaryMousePressed(MouseEvent event)
	{
		clickedX = event.getX();
		clickedY = event.getY();
		controlPoint = getControlPointAt(controlPoints, event.getX(), event.getY());
		if (controlPoint != null)
		{
			if (MainApp.actualControlPoint == controlPoint)
			{
				MainApp.actualControlPoint = null;
			}
			controlPoint.deleteTangentsRecursively();
			controlPoints.remove(controlPoint);
			getPath(controlPoint).removeControlPoint(controlPoint);
		}
	}

	@Override
	protected void handleSecondaryMouseDragged(MouseEvent event)
	{
		controlPoints.stream().filter(ControlPoint::isSelected).forEach(cp -> cp.add(event.getX() - clickedX, event.getY() - clickedY, 0.0));
		clickedX = event.getX();
		clickedY = event.getY();
	}
}
