package editor;

import java.util.List;
import java.util.function.Consumer;

import entity.ControlPoint;
import entity.MutablePoint3D;
import javafx.geometry.Point3D;
import javafx.scene.Scene;
import javafx.scene.canvas.Canvas;
import javafx.scene.input.MouseEvent;
import javafx.scene.transform.Rotate;
import test.MainApp;

public class Rotator extends AbstractEditor {
	private Consumer<List<ControlPoint>> curveDrawer;

	public Rotator(Consumer<List<ControlPoint>> curveDrawer) {
		this.curveDrawer = curveDrawer;
	}

	@Override
	public void activate(Scene scene) {
		super.activate(scene);
		curveDrawer.accept(MainApp.controlPoints);
	}

	@Override
	public void handle(MouseEvent mouseEvent) {
		super.handle(mouseEvent);
		clickedX = mouseEvent.getX();
		clickedY = mouseEvent.getY();
		curveDrawer.accept(MainApp.controlPoints);
	}

	@Override
	protected void handlePrimaryMouseDragged(MouseEvent event) {
		rotateCurve(Rotate.Y_AXIS, (clickedX - event.getX()) / 8, false);
		rotateCurve(Rotate.X_AXIS, (event.getY() - clickedY) / 8, false);
	}

	@Override
	protected void handleSecondaryMouseDragged(MouseEvent event) {
		rotateCurve(Rotate.Y_AXIS, (clickedX - event.getX()) / 8, true);
		rotateCurve(Rotate.X_AXIS, (event.getY() - clickedY) / 8, true);
	}

	public static void rotateCurve(Point3D rotationAxis, double angle, boolean onlySelected) {
		Canvas canvas = new Canvas(MainApp.canvas.getWidth(), MainApp.canvas.getHeight());
		Rotate rotate = new Rotate(angle, canvas.getWidth() / 2, canvas.getHeight() / 2, 0.0, rotationAxis);
		canvas.getTransforms().add(rotate);
		for (ControlPoint controlPoint : MainApp.controlPoints) {
			if (!onlySelected || (onlySelected && controlPoint.isSelected())) {
				Point3D rotated = canvas.localToParent(controlPoint.getX(), controlPoint.getY(), controlPoint.getZ());
				for (MutablePoint3D tangent : controlPoint.getTangents()) {
					Point3D rotatedTangent = canvas.localToParent(tangent.getX() + controlPoint.getX(), tangent.getY() + controlPoint.getY(), tangent.getZ() + controlPoint.getZ());
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
