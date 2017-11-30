package editor;

import entity.ControlPoint;
import entity.MutablePoint3D;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.paint.Color;

public class TangentDrawer {
	public static final int DOT_SIZE = 9;
	public static final int HALF_DOT_SIZE = DOT_SIZE / 2;

	private static final Color controlPointColor = Color.RED;
	private static final Color neighbourPointColor = Color.GREEN;
	private static final Color tangentPointColor = Color.MAGENTA;
	private static final Color tangentLineColor = Color.MAGENTA;

	GraphicsContext gc;

	public TangentDrawer(Canvas canvas) {
		gc = canvas.getGraphicsContext2D();
	}

	public void drawTangent(ControlPoint controlPoint, ControlPoint neighbour) {
		gc.setFill(controlPointColor);
		gc.fillRect(controlPoint.getX() - HALF_DOT_SIZE, controlPoint.getY() - HALF_DOT_SIZE, DOT_SIZE, DOT_SIZE);
		if (controlPoint.getNeighbours().contains(neighbour)) {
			MutablePoint3D tangent = controlPoint.getTangent(neighbour);
			gc.setFill(neighbourPointColor);
			gc.fillRect(neighbour.getX() - HALF_DOT_SIZE, neighbour.getY() - HALF_DOT_SIZE, DOT_SIZE, DOT_SIZE);
			gc.setFill(tangentPointColor);
			gc.fillRect(controlPoint.getX() + tangent.getX() - HALF_DOT_SIZE, controlPoint.getY() + tangent.getY() - HALF_DOT_SIZE, DOT_SIZE, DOT_SIZE);
			gc.setStroke(tangentLineColor);
			gc.strokeLine(controlPoint.getX(), controlPoint.getY(), controlPoint.getX() + tangent.getX(), controlPoint.getY() + tangent.getY());
		}
	}
}
