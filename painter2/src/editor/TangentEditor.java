package editor;

import java.util.List;
import java.util.function.Consumer;

import javafx.geometry.Point3D;
import javafx.scene.input.MouseButton;
import javafx.scene.input.MouseEvent;

import entity.ControlPoint;
import test.MainApp;

public class TangentEditor extends AbstractEditor
{
	private Consumer<List<ControlPoint>> curveDrawer;
	private ControlPoint controlPoint;

	public TangentEditor(Consumer<List<ControlPoint>> curveDrawer)
	{
		this.curveDrawer = curveDrawer;
	}

	@Override
	public void handle(MouseEvent mouseEvent)
	{
		if (mouseEvent.getEventType() == MouseEvent.MOUSE_MOVED)
		{
			ControlPoint controlPoint = getControlPointAt(mouseEvent.getX(), mouseEvent.getY(), 0.0);
			if (controlPoint != null)
			{
				curveDrawer.accept(MainApp.controlPoints);
			}
		}
		if (mouseEvent.getButton() == MouseButton.PRIMARY)
		{
			if (mouseEvent.getEventType() == MouseEvent.MOUSE_PRESSED)
			{
				controlPoint = getControlPointAt(mouseEvent.getX(), mouseEvent.getY(), 0.0);
				if (controlPoint == null)
				{
					controlPoint = new ControlPoint(mouseEvent.getX(), mouseEvent.getY(), mouseEvent.getZ());
					MainApp.controlPoints.add(controlPoint);
				}
				for (ControlPoint cp : MainApp.controlPoints)
				{
					cp.setSelected(controlPoint == cp);
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
				ControlPoint controlPoint = getControlPointAt(mouseEvent.getX(), mouseEvent.getY(), 0.0);
				if (controlPoint != null)
				{
					MainApp.controlPoints.remove(controlPoint);
					curveDrawer.accept(MainApp.controlPoints);
				}
			}
		}
	}

	public ControlPoint getControlPointAt(Point3D point)
	{
		return getControlPointAt(point.getX(), point.getY(), point.getZ());
	}

	private ControlPoint getControlPointAt(double x, double y, double z)
	{
		for (ControlPoint controlPoint : MainApp.controlPoints)
		{
			Point3D point = MainApp.canvas.localToParent(controlPoint.getX(), controlPoint.getY(), controlPoint.getZ());
			if (Math.abs(point.getX() - x) <= (CurveDrawer.DOT_SIZE / 2) && Math.abs(point.getY() - y) <= (CurveDrawer.DOT_SIZE / 2))
			{
				return controlPoint;
			}
		}
		return null;
	}
}
