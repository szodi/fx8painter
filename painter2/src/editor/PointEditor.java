package editor;

import java.util.List;
import java.util.function.Consumer;

import javafx.scene.input.MouseButton;
import javafx.scene.input.MouseEvent;

import entity.ControlPoint;
import test.MainApp;

public class PointEditor extends AbstractEditor
{
	private Consumer<List<ControlPoint>> curveDrawer;
	private ControlPoint controlPoint;

	public PointEditor(Consumer<List<ControlPoint>> curveDrawer)
	{
		this.curveDrawer = curveDrawer;
	}

	@Override
	public void handle(MouseEvent mouseEvent)
	{
		if (mouseEvent.getEventType() == MouseEvent.MOUSE_MOVED)
		{
			ControlPoint controlPoint = MainApp.getControlPointAt(mouseEvent.getX(), mouseEvent.getY(), 0.0);
			MainApp.actualControlPoint = controlPoint;
			curveDrawer.accept(MainApp.controlPoints);
		}
		if (mouseEvent.getButton() == MouseButton.PRIMARY)
		{
			if (mouseEvent.getEventType() == MouseEvent.MOUSE_PRESSED)
			{
				controlPoint = MainApp.getControlPointAt(mouseEvent.getX(), mouseEvent.getY(), 0.0);
				if (controlPoint == null)
				{
					controlPoint = new ControlPoint(mouseEvent.getX(), mouseEvent.getY(), mouseEvent.getZ());
					MainApp.controlPoints.add(controlPoint);
				}
			}
			else if (mouseEvent.getEventType() == MouseEvent.MOUSE_DRAGGED)
			{
				controlPoint.setX(mouseEvent.getX());
				controlPoint.setY(mouseEvent.getY());
			}
			curveDrawer.accept(MainApp.controlPoints);
		}
		else if (mouseEvent.getButton() == MouseButton.SECONDARY)
		{
			if (mouseEvent.getEventType() == MouseEvent.MOUSE_PRESSED)
			{
				ControlPoint controlPoint = MainApp.getControlPointAt(mouseEvent.getX(), mouseEvent.getY(), 0.0);
				if (controlPoint != null)
				{
					MainApp.controlPoints.remove(controlPoint);
					curveDrawer.accept(MainApp.controlPoints);
				}
			}
		}
	}
}
