package editor;

import java.util.List;
import java.util.function.Consumer;

import entity.ControlPoint;
import javafx.scene.input.MouseButton;
import javafx.scene.input.MouseEvent;
import test.MainApp;

public class PointEditor extends AbstractEditor {
	private Consumer<List<ControlPoint>> curveDrawer;
	private ControlPoint controlPoint;

	double clickedX;
	double clickedY;

	public PointEditor(Consumer<List<ControlPoint>> curveDrawer) {
		this.curveDrawer = curveDrawer;
	}

	@Override
	public void handle(MouseEvent mouseEvent) {
		if (mouseEvent.getEventType() == MouseEvent.MOUSE_MOVED) {
			controlPoint = MainApp.getControlPointAt(mouseEvent.getX(), mouseEvent.getY(), 0.0);
			MainApp.actualControlPoint = controlPoint;
			curveDrawer.accept(MainApp.controlPoints);
		}
		if (mouseEvent.getButton() == MouseButton.PRIMARY) {
			if (mouseEvent.getEventType() == MouseEvent.MOUSE_PRESSED) {
				controlPoint = MainApp.getControlPointAt(mouseEvent.getX(), mouseEvent.getY(), 0.0);
				if (!mouseEvent.isControlDown()) {
					MainApp.controlPoints.forEach(cp -> cp.setSelected(cp == controlPoint));
				}
				if (mouseEvent.isControlDown() && controlPoint != null) {
					controlPoint.setSelected(!controlPoint.isSelected());
				}
				if (controlPoint == null) {
					controlPoint = new ControlPoint(mouseEvent.getX(), mouseEvent.getY(), mouseEvent.getZ());
					MainApp.controlPoints.add(controlPoint);
				}
			} else if (mouseEvent.getEventType() == MouseEvent.MOUSE_DRAGGED) {
				controlPoint.setX(mouseEvent.getX());
				controlPoint.setY(mouseEvent.getY());
			}
			curveDrawer.accept(MainApp.controlPoints);
		} else if (mouseEvent.getButton() == MouseButton.SECONDARY) {
			if (mouseEvent.getEventType() == MouseEvent.MOUSE_PRESSED) {
				clickedX = mouseEvent.getX();
				clickedY = mouseEvent.getY();
				controlPoint = MainApp.getControlPointAt(clickedX, clickedY, 0.0);
				if (controlPoint != null) {
					MainApp.controlPoints.remove(controlPoint);
					curveDrawer.accept(MainApp.controlPoints);
				}
			} else if (mouseEvent.getEventType() == MouseEvent.MOUSE_DRAGGED) {
				if (controlPoint == null) {
					for (ControlPoint cp : MainApp.controlPoints) {
						if (cp.isSelected()) {
							cp.setX(cp.getX() + mouseEvent.getX() - clickedX);
							cp.setY(cp.getY() + mouseEvent.getY() - clickedY);
						}
					}
					clickedX = mouseEvent.getX();
					clickedY = mouseEvent.getY();
					curveDrawer.accept(MainApp.controlPoints);
				}
			}
		}
	}
}
