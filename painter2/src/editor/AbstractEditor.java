package editor;

import java.util.Collection;

import javafx.event.EventHandler;
import javafx.scene.Scene;
import javafx.scene.input.MouseButton;
import javafx.scene.input.MouseEvent;

import entity.ControlPoint;
import test.MainApp;
import tools.Tools;

public abstract class AbstractEditor implements EventHandler<MouseEvent>
{
	protected double clickedX;
	protected double clickedY;

	protected ControlPoint controlPoint;

	public void activate(Scene scene)
	{
		scene.setOnMouseMoved(this);
		scene.setOnMousePressed(this);
		scene.setOnMouseDragged(this);
		scene.setOnMouseReleased(this);
	}

	public ControlPoint getControlPointAt(double x, double y)
	{
		return getControlPointAt(MainApp.controlPoints, x, y);
	}

	public ControlPoint getControlPointAt(Collection<ControlPoint> controlPoints, double x, double y)
	{
		return Tools.getControlPointAt(MainApp.canvas, controlPoints, x, y, 0.0, CurveDrawer.DOT_SIZE / 2);
	}

	@Override
	public void handle(MouseEvent mouseEvent)
	{
		if (mouseEvent.getEventType() == MouseEvent.MOUSE_MOVED)
		{
			handleMouseMoved(mouseEvent);
		}
		if (mouseEvent.getButton() == MouseButton.PRIMARY)
		{
			if (mouseEvent.getEventType() == MouseEvent.MOUSE_PRESSED)
			{
				handlePrimaryMousePressed(mouseEvent);
			}
			else if (mouseEvent.getEventType() == MouseEvent.MOUSE_DRAGGED)
			{
				handlePrimaryMouseDragged(mouseEvent);
			}
			else if (mouseEvent.getEventType() == MouseEvent.MOUSE_RELEASED)
			{
				handlePrimaryMouseReleased(mouseEvent);
			}
		}
		else if (mouseEvent.getButton() == MouseButton.SECONDARY)
		{
			if (mouseEvent.getEventType() == MouseEvent.MOUSE_PRESSED)
			{
				handleSecondaryMousePressed(mouseEvent);
			}
			else if (mouseEvent.getEventType() == MouseEvent.MOUSE_DRAGGED)
			{
				handleSecondaryMouseDragged(mouseEvent);
			}
			else if (mouseEvent.getEventType() == MouseEvent.MOUSE_RELEASED)
			{
				handleSecondaryMouseReleased(mouseEvent);
			}

		}
	}

	protected void handleMouseMoved(MouseEvent event)
	{
	}

	protected void handlePrimaryMousePressed(MouseEvent event)
	{
	}

	protected void handleSecondaryMousePressed(MouseEvent event)
	{
		handlePrimaryMousePressed(event);
	}

	protected void handlePrimaryMouseDragged(MouseEvent event)
	{
	}

	protected void handleSecondaryMouseDragged(MouseEvent event)
	{
		handlePrimaryMouseDragged(event);
	}

	protected void handlePrimaryMouseReleased(MouseEvent event)
	{
	}

	protected void handleSecondaryMouseReleased(MouseEvent event)
	{
		handlePrimaryMouseReleased(event);
	}
}
