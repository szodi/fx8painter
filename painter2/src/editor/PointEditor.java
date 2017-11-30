package editor;

import java.util.List;
import java.util.function.Consumer;

import entity.ControlPoint;
import javafx.scene.input.MouseEvent;
import test.MainApp;

public class PointEditor extends PathEditor {
	public PointEditor(List<ControlPoint> controlPoints, Consumer<List<ControlPoint>> curveDrawer) {
		super(controlPoints, curveDrawer);
	}

	@Override
	protected void handlePrimaryMousePressed(MouseEvent event) {
		controlPoint = getControlPointAt(event.getX(), event.getY());
		if (controlPoint == null) {
			if (!event.isControlDown()) {
				controlPoint = new ControlPoint(event.getX(), event.getY(), event.getZ());
				controlPoints.add(controlPoint);
			}
		} else {
			if (event.isControlDown()) {
				controlPoint.setSelected(!controlPoint.isSelected());
			} else {
				controlPoints.forEach(cp -> cp.setSelected(false));
			}
		}
		if (event.isShiftDown() && MainApp.actualControlPoint != null) {
			MainApp.actualControlPoint.setTangent(controlPoint, controlPoint);
			controlPoint.setTangent(MainApp.actualControlPoint, MainApp.actualControlPoint);
		}
		MainApp.actualControlPoint = controlPoint;
	}
}
