package editor;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;

import javafx.scene.input.MouseEvent;

import entity.ControlPoint;
import test.MainApp;

public class PathEditor extends AbstractEditor
{
	protected Consumer<List<ControlPoint>> curveDrawer;
	protected ControlPoint controlPoint;
	protected List<ControlPoint> controlPoints = new ArrayList<>();

	public PathEditor(Consumer<List<ControlPoint>> curveDrawer)
	{
		this.curveDrawer = curveDrawer;
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
			if (!event.isControlDown())
			{
				controlPoint = new ControlPoint(event.getX(), event.getY(), event.getZ());
				if (!controlPoints.isEmpty())
				{
					ControlPoint lastControlPoint = controlPoints.get(controlPoints.size() - 1);
					lastControlPoint.setTangent(controlPoint, controlPoint);
					controlPoint.setTangent(lastControlPoint, lastControlPoint);
				}
				controlPoints.add(controlPoint);
			}
		}
		else
		{
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

	protected void handlePrimaryMouseDragged(MouseEvent event)
	{
		if (controlPoint != null)
		{
			controlPoint.setX(event.getX());
			controlPoint.setY(event.getY());
		}
	}

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
			controlPoints.remove(controlPoint);
			controlPoints.forEach(cp -> cp.getNeighbours().remove(controlPoint));
		}
	}

	protected void handleSecondaryMouseDragged(MouseEvent event)
	{
		controlPoints.stream().filter(ControlPoint::isSelected).forEach(cp -> cp.add(event.getX() - clickedX, event.getY() - clickedY, 0.0));
		clickedX = event.getX();
		clickedY = event.getY();
	}

	public List<ControlPoint> getControlPoints()
	{
		return controlPoints;
	}
}
