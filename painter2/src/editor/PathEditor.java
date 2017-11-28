package editor;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;

import entity.ControlPoint;
import javafx.scene.input.MouseButton;
import javafx.scene.input.MouseEvent;
import test.MainApp;
import tools.Tools;

public class PathEditor extends AbstractEditor {
	private Consumer<List<ControlPoint>> curveDrawer;
	private ControlPoint controlPoint;
	private List<ControlPoint> controlPoints = new ArrayList<>();

	double clickedX;
	double clickedY;

	public PathEditor(Consumer<List<ControlPoint>> curveDrawer) {
		this.curveDrawer = curveDrawer;
	}

	@Override
	public void handle(MouseEvent mouseEvent) {
		if (mouseEvent.getEventType() == MouseEvent.MOUSE_MOVED) {
			controlPoint = Tools.getControlPointAt(MainApp.canvas, controlPoints, mouseEvent.getX(), mouseEvent.getY(),
					0.0, CurveDrawer.DOT_SIZE / 2);
			if (controlPoint != null) {
				MainApp.actualControlPoint = controlPoint;
			}
			curveDrawer.accept(controlPoints);
		}
		if (mouseEvent.getButton() == MouseButton.PRIMARY) {
			if (mouseEvent.getEventType() == MouseEvent.MOUSE_PRESSED) {
				controlPoint = Tools.getControlPointAt(MainApp.canvas, controlPoints, mouseEvent.getX(),
						mouseEvent.getY(), 0.0, CurveDrawer.DOT_SIZE / 2);
				if (!mouseEvent.isControlDown()) {
					controlPoints.forEach(cp -> cp.setSelected(cp == controlPoint));
				}
				if (mouseEvent.isControlDown() && controlPoint != null) {
					controlPoint.setSelected(!controlPoint.isSelected());
				}
				if (controlPoint == null) {
					controlPoint = new ControlPoint(mouseEvent.getX(), mouseEvent.getY(), mouseEvent.getZ());
					if (!controlPoints.isEmpty()) {
						ControlPoint lastControlPoint = controlPoints.get(controlPoints.size() - 1);
						lastControlPoint.setTangent(controlPoint, controlPoint);
						controlPoint.setTangent(lastControlPoint, lastControlPoint);
					}
					controlPoints.add(controlPoint);
				}
				MainApp.actualControlPoint = controlPoint;
			} else if (mouseEvent.getEventType() == MouseEvent.MOUSE_DRAGGED) {
				controlPoint.setX(mouseEvent.getX());
				controlPoint.setY(mouseEvent.getY());
			}
			curveDrawer.accept(controlPoints);
		} else if (mouseEvent.getButton() == MouseButton.SECONDARY) {
			if (mouseEvent.getEventType() == MouseEvent.MOUSE_PRESSED) {
				clickedX = mouseEvent.getX();
				clickedY = mouseEvent.getY();
				controlPoint = Tools.getControlPointAt(MainApp.canvas, controlPoints, clickedX, clickedY, 0.0,
						CurveDrawer.DOT_SIZE / 2);
				if (controlPoint != null) {
					if (MainApp.actualControlPoint == controlPoint) {
						MainApp.actualControlPoint = null;
					}
					controlPoints.remove(controlPoint);
					controlPoints.forEach(cp -> cp.getNeighbours().remove(controlPoint));
					curveDrawer.accept(controlPoints);
				}
			} else if (mouseEvent.getEventType() == MouseEvent.MOUSE_DRAGGED) {
				if (controlPoint == null) {
					for (ControlPoint cp : controlPoints) {
						if (cp.isSelected()) {
							cp.setX(cp.getX() + mouseEvent.getX() - clickedX);
							cp.setY(cp.getY() + mouseEvent.getY() - clickedY);
						}
					}
					clickedX = mouseEvent.getX();
					clickedY = mouseEvent.getY();
					curveDrawer.accept(controlPoints);
				}
			}
		}
	}

	public List<ControlPoint> getControlPoints() {
		return controlPoints;
	}
}
