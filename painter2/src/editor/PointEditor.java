package editor;

import java.util.Iterator;
import java.util.List;
import java.util.function.Consumer;

import javafx.scene.canvas.Canvas;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.MouseEvent;

import entity.ControlPoint;
import test.MainApp;

public class PointEditor extends PathEditor
{
	public PointEditor(List<ControlPoint> controlPoints, Consumer<List<ControlPoint>> curveDrawer)
	{
		super(controlPoints, curveDrawer);
	}

	@Override
	public void activate(Canvas canvas)
	{
		super.activate(canvas);
		canvas.setOnKeyPressed(this::handleKeyPressed);
	}

	private void handleKeyPressed(KeyEvent keyEvent)
	{
		if (keyEvent.getCode() == KeyCode.DELETE)
		{
			for (Iterator<ControlPoint> iterator = controlPoints.iterator(); iterator.hasNext();)
			{
				ControlPoint controlPoint = (ControlPoint)iterator.next();
				if (controlPoint.isSelected())
				{
					if (MainApp.actualControlPoint == controlPoint)
					{
						MainApp.actualControlPoint = null;
					}
					controlPoint.deleteTangentsRecursively();
					iterator.remove();
				}
			}
			curveDrawer.accept(controlPoints);
		}
	}

	@Override
	protected void handlePrimaryMousePressed(MouseEvent event)
	{
		controlPoint = getControlPointAt(event.getX(), event.getY());
		if (controlPoint == null)
		{
			if (!event.isControlDown())
			{
				controlPoint = new ControlPoint(event.getX(), event.getY(), event.getZ());
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
		if (event.isShiftDown() && MainApp.actualControlPoint != null)
		{
			MainApp.actualControlPoint.setTangent(controlPoint, controlPoint);
			controlPoint.setTangent(MainApp.actualControlPoint, MainApp.actualControlPoint);
		}
		MainApp.actualControlPoint = controlPoint;
	}
}
