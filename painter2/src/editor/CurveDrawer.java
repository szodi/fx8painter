package editor;

import java.util.List;

import entity.ControlPoint;
import entity.MutablePoint3D;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import test.MainApp;
import tools.Tools;

public class CurveDrawer {
	public static final int DOT_SIZE = 11;
	public static final int HALF_DOT_SIZE = DOT_SIZE / 2;

	public static final double smoothness = 0.02;

	private static final Color SELECTION_COLOR = new Color(0, 0, 0.5, 0.5);
	private static final Color ACTUAL_CONTROLPOINT_COLOR = Color.GREEN;
	private static final Color SELECTED_CONTROLPOINT_COLOR = Color.DARKGOLDENROD;
	private static final Color UNSELECTED_CONTROLPOINT_COLOR = Color.LIGHTCYAN;

	Canvas canvas;
	GraphicsContext gc;

	public CurveDrawer(Canvas canvas) {
		this.canvas = canvas;
		gc = canvas.getGraphicsContext2D();
	}

	public void drawPoints(List<ControlPoint> points) {
		gc.setLineWidth(2);
		gc.clearRect(0, 0, canvas.getWidth(), canvas.getHeight());
		for (ControlPoint controlPoint : points) {
			drawControlPoint(controlPoint);
			gc.setStroke(Color.BLUE);
			for (ControlPoint neighbour : controlPoint.getNeighbours()) {
				drawTangent(controlPoint, neighbour);
			}
		}
	}

	private void drawControlPoint(ControlPoint controlPoint) {
		gc.setFill(controlPoint == MainApp.actualControlPoint ? ACTUAL_CONTROLPOINT_COLOR : (controlPoint.isSelected() ? SELECTED_CONTROLPOINT_COLOR : UNSELECTED_CONTROLPOINT_COLOR));
		gc.fillRect(controlPoint.getX() - HALF_DOT_SIZE, controlPoint.getY() - HALF_DOT_SIZE, DOT_SIZE, DOT_SIZE);
	}

	private void drawTangent(ControlPoint controlPoint, ControlPoint neighbour) {
		for (double t = 0.0; t < 1.0; t += smoothness) {
			MutablePoint3D point = Tools.getBezierPoint(controlPoint, neighbour, t);
			gc.strokeLine(point.getX(), point.getY(), point.getX(), point.getY());
		}
	}

	public void drawSelection(Rectangle rectangle) {
		drawPoints(MainApp.controlPoints);
		if (rectangle == null) {
			return;
		}
		gc.setFill(SELECTION_COLOR);
		gc.fillRect(rectangle.getX(), rectangle.getY(), rectangle.getWidth(), rectangle.getHeight());
	}
}
