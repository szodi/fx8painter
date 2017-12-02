package editor;

import java.util.List;
import java.util.function.Consumer;

import javafx.scene.Node;
import javafx.scene.input.MouseEvent;
import javafx.scene.shape.Rectangle;

import entity.ControlPoint;
import test.MainApp;

public class SelectionEditor extends AbstractEditor
{
	private Consumer<Rectangle> rectangleProcessor;
	Rectangle rectangle = new Rectangle();

	public SelectionEditor(List<ControlPoint> controlPoints, Consumer<Rectangle> rectangleProcessor)
	{
		this.controlPoints = controlPoints;
		this.rectangleProcessor = rectangleProcessor;
	}

	@Override
	public void activate(Node node)
	{
		super.activate(node);
		rectangleProcessor.accept(rectangle);
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
			controlPoints.forEach(cp -> cp.setSelected(cp == controlPoint));
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
			controlPoints.forEach(cp -> cp.setSelected(rectangle.contains(cp.getX(), cp.getY())));
		}
	}

	@Override
	protected void handlePrimaryMouseReleased(MouseEvent event)
	{
		rectangle.setX(0.0);
		rectangle.setY(0.0);
		rectangle.setWidth(0.0);
		rectangle.setHeight(0.0);
		controlPoint = null;
	}
}