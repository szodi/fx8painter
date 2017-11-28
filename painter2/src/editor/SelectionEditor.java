package editor;

import java.util.function.Consumer;

import entity.ControlPoint;
import javafx.scene.input.MouseEvent;
import javafx.scene.shape.Rectangle;
import test.MainApp;
import tools.Tools;

public class SelectionEditor extends AbstractEditor {
	private Consumer<Rectangle> rectangleProcessor;
	double clickedX;
	double clickedY;
	ControlPoint controlPoint;
	Rectangle rectangle = new Rectangle();

	public SelectionEditor(Consumer<Rectangle> rectangleProcessor) {
		this.rectangleProcessor = rectangleProcessor;
	}

	@Override
	public void handle(MouseEvent mouseEvent) {
		if (mouseEvent.getEventType() == MouseEvent.MOUSE_PRESSED) {
			clickedX = mouseEvent.getX();
			clickedY = mouseEvent.getY();
			controlPoint = Tools.getControlPointAt(MainApp.canvas, MainApp.controlPoints, mouseEvent.getX(),
					mouseEvent.getY(), 0.0, CurveDrawer.DOT_SIZE / 2);
			if (!mouseEvent.isControlDown()) {
				MainApp.controlPoints.forEach(cp -> cp.setSelected(cp == controlPoint));
			}
			if (mouseEvent.isControlDown() && controlPoint != null) {
				controlPoint.setSelected(!controlPoint.isSelected());
			}
		} else if (mouseEvent.getEventType() == MouseEvent.MOUSE_DRAGGED && controlPoint == null) {
			rectangle.setX(Math.min(clickedX, mouseEvent.getX()));
			rectangle.setY(Math.min(clickedY, mouseEvent.getY()));
			rectangle.setWidth(Math.abs(mouseEvent.getX() - clickedX));
			rectangle.setHeight(Math.abs(mouseEvent.getY() - clickedY));
			MainApp.controlPoints.forEach(cp -> cp.setSelected(rectangle.contains(cp.getX(), cp.getY())));
			rectangleProcessor.accept(rectangle);
		} else if (mouseEvent.getEventType() == MouseEvent.MOUSE_RELEASED) {
			rectangleProcessor.accept(null);
			controlPoint = null;
		}
	}
}