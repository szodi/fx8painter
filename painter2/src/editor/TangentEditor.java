package editor;

import java.util.List;
import java.util.function.BiConsumer;
import java.util.function.Consumer;

import javafx.scene.input.MouseEvent;

import entity.ControlPoint;
import test.MainApp;

public class TangentEditor extends AbstractEditor
{
	private Consumer<List<ControlPoint>> curveDrawer;
	private BiConsumer<ControlPoint, ControlPoint> cpAndNeighbour;
	private ControlPoint neighbour;

	public TangentEditor(Consumer<List<ControlPoint>> curveDrawer, BiConsumer<ControlPoint, ControlPoint> cpAndNeighbour)
	{
		this.curveDrawer = curveDrawer;
		this.cpAndNeighbour = cpAndNeighbour;
	}

	@Override
	public void handle(MouseEvent mouseEvent)
	{
		super.handle(mouseEvent);
		curveDrawer.accept(MainApp.controlPoints);
		if (controlPoint != null && neighbour != null)
		{
			cpAndNeighbour.accept(controlPoint, neighbour);
		}
	}

	@Override
	protected void handleMouseMoved(MouseEvent event)
	{
		MainApp.actualControlPoint = getControlPointAt(event.getX(), event.getY());
	}

	@Override
	protected void handlePrimaryMousePressed(MouseEvent event)
	{
		ControlPoint point = getControlPointAt(event.getX(), event.getY());
		if (point != null)
		{
			if (!event.isControlDown())
			{
				controlPoint = point;
			}
			else if (controlPoint != null && controlPoint.getNeighbours().contains(point))
			{
				neighbour = point;
			}
		}
	}

	@Override
	protected void handleSecondaryMousePressed(MouseEvent event)
	{
		clickedX = event.getX();
		clickedY = event.getY();
	}

	protected void handleSecondaryMouseDragged(MouseEvent event)
	{
		if (controlPoint != null && neighbour != null)
		{
			controlPoint.getTangent(neighbour).add(event.getX() - clickedX, event.getY() - clickedY, 0.0);
			clickedX = event.getX();
			clickedY = event.getY();
		}
	}

}
