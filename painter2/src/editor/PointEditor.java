package editor;

import java.util.List;
import java.util.function.Consumer;

import entity.ControlPoint;
import javafx.scene.input.MouseButton;
import javafx.scene.input.MouseEvent;
import test.MainApp;
import tools.Tools;

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
			controlPoint = Tools.getControlPointAt(MainApp.canvas, MainApp.controlPoints, mouseEvent.getX(),
					mouseEvent.getY(), 0.0, CurveDrawer.DOT_SIZE / 2);
			if (controlPoint != null) {
				MainApp.actualControlPoint = controlPoint;
			}
			curveDrawer.accept(MainApp.controlPoints);
		}
		if (mouseEvent.getButton() == MouseButton.PRIMARY) {
			if (mouseEvent.getEventType() == MouseEvent.MOUSE_PRESSED) {
				controlPoint = Tools.getControlPointAt(MainApp.canvas, MainApp.controlPoints, mouseEvent.getX(),
						mouseEvent.getY(), 0.0, CurveDrawer.DOT_SIZE / 2);
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
				if (!mouseEvent.isControlDown() && mouseEvent.isShiftDown() && MainApp.actualControlPoint != null) {
					MainApp.actualControlPoint.setTangent(controlPoint, controlPoint);
					controlPoint.setTangent(MainApp.actualControlPoint, MainApp.actualControlPoint);
				}
				MainApp.actualControlPoint = controlPoint;
			} else if (mouseEvent.getEventType() == MouseEvent.MOUSE_DRAGGED) {
				controlPoint.setX(mouseEvent.getX());
				controlPoint.setY(mouseEvent.getY());
			}
			curveDrawer.accept(MainApp.controlPoints);
		} else if (mouseEvent.getButton() == MouseButton.SECONDARY) {
			if (mouseEvent.getEventType() == MouseEvent.MOUSE_PRESSED) {
				clickedX = mouseEvent.getX();
				clickedY = mouseEvent.getY();
				controlPoint = Tools.getControlPointAt(MainApp.canvas, MainApp.controlPoints, clickedX, clickedY, 0.0,
						CurveDrawer.DOT_SIZE / 2);
				if (controlPoint != null) {
					if (MainApp.actualControlPoint == controlPoint) {
						MainApp.actualControlPoint = null;
					}
					MainApp.controlPoints.remove(controlPoint);
					MainApp.controlPoints.forEach(cp -> cp.getNeighbours().remove(controlPoint));
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
