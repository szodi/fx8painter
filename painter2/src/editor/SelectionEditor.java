package editor;

import java.util.function.Consumer;

import entity.ControlPoint;
import javafx.scene.input.MouseEvent;
import javafx.scene.shape.Rectangle;
import test.MainApp;

public class SelectionEditor extends AbstractEditor {
	private Consumer<Rectangle> rectangleProcessor;
	double clickedX;
	double clickedY;
	ControlPoint clickedControlPoint;
	Rectangle rectangle = new Rectangle();

	public SelectionEditor(Consumer<Rectangle> rectangleProcessor) {
		this.rectangleProcessor = rectangleProcessor;
	}

	@Override
	public void handle(MouseEvent mouseEvent) {
		if (mouseEvent.getEventType() == MouseEvent.MOUSE_PRESSED) {
			clickedX = mouseEvent.getX();
			clickedY = mouseEvent.getY();
			clickedControlPoint = MainApp.getControlPointAt(mouseEvent.getX(), mouseEvent.getY(), 0.0);
			if (!mouseEvent.isControlDown()) {
				MainApp.controlPoints.forEach(cp -> cp.setSelected(cp == clickedControlPoint));
			}
			if (mouseEvent.isControlDown() && clickedControlPoint != null) {
				clickedControlPoint.setSelected(!clickedControlPoint.isSelected());
			}
		} else if (mouseEvent.getEventType() == MouseEvent.MOUSE_DRAGGED && clickedControlPoint == null) {
			rectangle.setX(Math.min(clickedX, mouseEvent.getX()));
			rectangle.setY(Math.min(clickedY, mouseEvent.getY()));
			rectangle.setWidth(Math.abs(mouseEvent.getX() - clickedX));
			rectangle.setHeight(Math.abs(mouseEvent.getY() - clickedY));
			MainApp.controlPoints.forEach(cp -> cp.setSelected(rectangle.contains(cp.getX(), cp.getY())));
			rectangleProcessor.accept(rectangle);
		} else if (mouseEvent.getEventType() == MouseEvent.MOUSE_RELEASED) {
			rectangleProcessor.accept(null);
			clickedControlPoint = null;
		}
	}
}