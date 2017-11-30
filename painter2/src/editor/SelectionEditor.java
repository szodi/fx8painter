package editor;

import java.util.function.Consumer;

import javafx.scene.input.MouseEvent;
import javafx.scene.shape.Rectangle;

import test.MainApp;

public class SelectionEditor extends AbstractEditor
{
	private Consumer<Rectangle> rectangleProcessor;
	Rectangle rectangle = new Rectangle();

	public SelectionEditor(Consumer<Rectangle> rectangleProcessor)
	{
		this.rectangleProcessor = rectangleProcessor;
	}

	@Override
	public void handle(MouseEvent mouseEvent)
	{
		MainApp.actualControlPoint = null;
		super.handle(mouseEvent);
		rectangleProcessor.accept(rectangle);
	}

	@Override
	protected void handlePrimaryMousePressed(MouseEvent event)
	{
		clickedX = event.getX();
		clickedY = event.getY();
		controlPoint = getControlPointAt(event.getX(), event.getY());
		if (!event.isControlDown())
		{
			MainApp.controlPoints.forEach(cp -> cp.setSelected(cp == controlPoint));
		}
		if (event.isControlDown() && controlPoint != null)
		{
			controlPoint.setSelected(!controlPoint.isSelected());
		}
	}

	@Override
	protected void handlePrimaryMouseDragged(MouseEvent event)
	{
		if (controlPoint == null)
		{
			rectangle.setX(Math.min(clickedX, event.getX()));
			rectangle.setY(Math.min(clickedY, event.getY()));
			rectangle.setWidth(Math.abs(event.getX() - clickedX));
			rectangle.setHeight(Math.abs(event.getY() - clickedY));
			MainApp.controlPoints.forEach(cp -> cp.setSelected(rectangle.contains(cp.getX(), cp.getY())));
		}
	}

	protected void handlePrimaryMouseReleased(MouseEvent event)
	{
		rectangle = null;
		controlPoint = null;
	}
}