package editor;

import java.util.List;
import java.util.function.BiConsumer;
import java.util.function.Consumer;

import entity.ControlPoint;
import entity.MutablePoint3D;
import javafx.geometry.Point3D;
import javafx.scene.input.MouseButton;
import javafx.scene.input.MouseEvent;
import test.MainApp;

public class TangentEditor extends AbstractEditor {
	private Consumer<List<ControlPoint>> curveDrawer;
	private BiConsumer<ControlPoint, ControlPoint> cpAndNeighbour;
	private ControlPoint controlPoint;
	private ControlPoint neighbour;
	private MutablePoint3D tangentPoint;
	double clickedX;
	double clickedY;

	public TangentEditor(Consumer<List<ControlPoint>> curveDrawer,
			BiConsumer<ControlPoint, ControlPoint> cpAndNeighbour) {
		this.curveDrawer = curveDrawer;
		this.cpAndNeighbour = cpAndNeighbour;
	}

	@Override
	public void handle(MouseEvent mouseEvent) {
		if (mouseEvent.getEventType() == MouseEvent.MOUSE_MOVED) {
			ControlPoint controlPoint = MainApp.getControlPointAt(mouseEvent.getX(), mouseEvent.getY(), 0.0);
			MainApp.actualControlPoint = controlPoint;
			curveDrawer.accept(MainApp.controlPoints);
		}
		if (mouseEvent.getButton() == MouseButton.PRIMARY) {
			if (mouseEvent.getEventType() == MouseEvent.MOUSE_PRESSED) {
				ControlPoint point = MainApp.getControlPointAt(mouseEvent.getX(), mouseEvent.getY(), 0.0);
				if (point != null) {
					if (!mouseEvent.isControlDown()) {
						controlPoint = point;
					} else if (controlPoint != null && controlPoint.getNeighbours().contains(point)) {
						neighbour = point;
					}
				}
			}
			curveDrawer.accept(MainApp.controlPoints);
		}
		if (mouseEvent.getButton() == MouseButton.SECONDARY) {
			if (mouseEvent.getEventType() == MouseEvent.MOUSE_PRESSED) {
				if (controlPoint != null && neighbour != null) {
					clickedX = mouseEvent.getX();
					clickedY = mouseEvent.getY();
					tangentPoint = getTangentPointAt(controlPoint, neighbour, clickedX, clickedY, 0.0);
				}
			} else if (mouseEvent.getEventType() == MouseEvent.MOUSE_DRAGGED) {
				if (tangentPoint != null) {
					tangentPoint.setX(mouseEvent.getX() - controlPoint.getX());
					tangentPoint.setY(mouseEvent.getY() - controlPoint.getY());
					tangentPoint.setZ(mouseEvent.getZ() - controlPoint.getZ());
				}
			}
			curveDrawer.accept(MainApp.controlPoints);
		}
		if (controlPoint != null && neighbour != null) {
			cpAndNeighbour.accept(controlPoint, neighbour);
		}
	}

	public static MutablePoint3D getTangentPointAt(ControlPoint controlPoint, ControlPoint neighbour, double x,
			double y, double z) {
		MutablePoint3D tangentPoint = controlPoint.getTangent(neighbour);
		Point3D point = MainApp.canvas.localToParent(controlPoint.getX() + tangentPoint.getX(),
				controlPoint.getY() + tangentPoint.getY(), controlPoint.getZ() + tangentPoint.getZ());
		if (Math.abs(point.getX() - x) <= (TangentDrawer.DOT_SIZE / 2)
				&& Math.abs(point.getY() - y) <= (TangentDrawer.DOT_SIZE / 2)) {
			return tangentPoint;
		}
		return null;
	}
}
