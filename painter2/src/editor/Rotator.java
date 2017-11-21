package editor;

import java.util.List;
import java.util.function.Consumer;

import javafx.scene.input.MouseEvent;
import javafx.scene.transform.Rotate;

import entity.ControlPoint;
import test.MainApp;

public class Rotator extends AbstractEditor
{
	double clickedX;
	double clickedY;
	private Consumer<List<ControlPoint>> curveDrawer;

	public Rotator(Consumer<List<ControlPoint>> curveDrawer)
	{
		this.curveDrawer = curveDrawer;
	}

	@Override
	public void handle(MouseEvent mouseEvent)
	{
		if (mouseEvent.getEventType() == MouseEvent.MOUSE_PRESSED)
		{
			clickedX = mouseEvent.getX();
			clickedY = mouseEvent.getY();
		}
		else if (mouseEvent.getEventType() == MouseEvent.MOUSE_DRAGGED)
		{
			MainApp.rotateCurve(Rotate.Y_AXIS, (clickedX - mouseEvent.getX()) / 10);
			MainApp.rotateCurve(Rotate.X_AXIS, (mouseEvent.getY() - clickedY) / 10);
			clickedX = mouseEvent.getX();
			clickedY = mouseEvent.getY();
			curveDrawer.accept(MainApp.controlPoints);
		}
	}
}
