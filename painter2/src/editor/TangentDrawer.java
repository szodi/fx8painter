package editor;

import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.paint.Color;

import entity.ControlPoint;
import entity.MutablePoint3D;

public class TangentDrawer
{
	public static final int DOT_SIZE = 5;

	private static final Color controlPointColor = new Color(1.0, 0.0, 0.0, 1.0);
	private static final Color neighbourPointColor = new Color(0.0, 1.0, 0.0, 1.0);
	private static final Color tangentPointColor = new Color(1.0, 0.0, 1.0, 1.0);
	private static final Color tangentLineColor = new Color(1.0, 0.0, 1.0, 1.0);

	Canvas canvas;
	GraphicsContext gc;

	public TangentDrawer(Canvas canvas)
	{
		this.canvas = canvas;
		gc = canvas.getGraphicsContext2D();
	}

	public void drawTangent(ControlPoint controlPoint, ControlPoint neighbour)
	{
		if (controlPoint.getNeighbours().contains(neighbour))
		{
			MutablePoint3D tangent = controlPoint.getTangent(neighbour);
			gc.setFill(controlPointColor);
			gc.fillRect(controlPoint.getX() - DOT_SIZE / 2, controlPoint.getY() - DOT_SIZE / 2, DOT_SIZE, DOT_SIZE);
			gc.setFill(neighbourPointColor);
			gc.fillRect(neighbour.getX() - DOT_SIZE / 2, neighbour.getY() - DOT_SIZE / 2, DOT_SIZE, DOT_SIZE);
			gc.setFill(tangentPointColor);
			gc.fillRect(controlPoint.getX() + tangent.getX() - DOT_SIZE / 2, controlPoint.getY() + tangent.getY() - DOT_SIZE / 2, DOT_SIZE, DOT_SIZE);
			gc.setStroke(tangentLineColor);
			gc.beginPath();
			gc.lineTo(controlPoint.getX(), controlPoint.getY());
			gc.lineTo(controlPoint.getX() + tangent.getX(), controlPoint.getY() + tangent.getY());
			gc.stroke();
		}
	}
}
