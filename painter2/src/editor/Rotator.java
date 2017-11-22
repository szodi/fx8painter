package editor;

import java.util.List;
import java.util.function.Consumer;

import entity.ControlPoint;
import entity.MutablePoint3D;
import javafx.geometry.Point3D;
import javafx.scene.canvas.Canvas;
import javafx.scene.input.MouseButton;
import javafx.scene.input.MouseEvent;
import javafx.scene.transform.Rotate;
import test.MainApp;

public class Rotator extends AbstractEditor {
	double clickedX;
	double clickedY;
	private Consumer<List<ControlPoint>> curveDrawer;

	public Rotator(Consumer<List<ControlPoint>> curveDrawer) {
		this.curveDrawer = curveDrawer;
	}

	@Override
	public void handle(MouseEvent mouseEvent) {
		if (mouseEvent.getButton() == MouseButton.PRIMARY) {
			if (mouseEvent.getEventType() == MouseEvent.MOUSE_PRESSED) {
				clickedX = mouseEvent.getX();
				clickedY = mouseEvent.getY();
			} else if (mouseEvent.getEventType() == MouseEvent.MOUSE_DRAGGED) {
				rotateCurve(Rotate.Y_AXIS, (clickedX - mouseEvent.getX()) / 10, false);
				rotateCurve(Rotate.X_AXIS, (mouseEvent.getY() - clickedY) / 10, false);
				clickedX = mouseEvent.getX();
				clickedY = mouseEvent.getY();
				curveDrawer.accept(MainApp.controlPoints);
			}
		} else if (mouseEvent.getButton() == MouseButton.SECONDARY) {
			if (mouseEvent.getEventType() == MouseEvent.MOUSE_PRESSED) {
				clickedX = mouseEvent.getX();
				clickedY = mouseEvent.getY();
			} else if (mouseEvent.getEventType() == MouseEvent.MOUSE_DRAGGED) {
				rotateCurve(Rotate.Y_AXIS, (clickedX - mouseEvent.getX()) / 10, true);
				rotateCurve(Rotate.X_AXIS, (mouseEvent.getY() - clickedY) / 10, true);
				clickedX = mouseEvent.getX();
				clickedY = mouseEvent.getY();
				curveDrawer.accept(MainApp.controlPoints);
			}
		}
	}

	public static void rotateCurve(Point3D rotationAxis, double angle, boolean onlySelected) {
		Canvas canvas = new Canvas();
		canvas.setRotationAxis(rotationAxis);
		canvas.setRotate(angle);
		for (ControlPoint controlPoint : MainApp.controlPoints) {
			if (!onlySelected || (onlySelected && controlPoint.isSelected())) {
				Point3D rotated = canvas.localToParent(controlPoint.getX(), controlPoint.getY(), controlPoint.getZ());
				for (MutablePoint3D tangent : controlPoint.getTangents()) {
					Point3D rotatedTangent = canvas.localToParent(tangent.getX() + controlPoint.getX(),
							tangent.getY() + controlPoint.getY(), tangent.getZ() + controlPoint.getZ());
					tangent.setX(rotatedTangent.getX() - rotated.getX());
					tangent.setY(rotatedTangent.getY() - rotated.getY());
					tangent.setZ(rotatedTangent.getZ() - rotated.getZ());
				}
				controlPoint.setX(rotated.getX());
				controlPoint.setY(rotated.getY());
				controlPoint.setZ(rotated.getZ());
			}
		}
	}
}
