package editor;

import java.util.List;

import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;

import entity.ControlPoint;
import entity.MutablePoint3D;
import test.MainApp;

public class CurveDrawer
{
	public static final int DOT_SIZE = 9;
	public static final double smoothness = 0.05;

	Canvas canvas;
	GraphicsContext gc;
	Color selectionColor = new Color(0, 0, 0.5, 0.5);

	public CurveDrawer(Canvas canvas)
	{
		this.canvas = canvas;
		gc = canvas.getGraphicsContext2D();
	}

	public void drawPoints(List<ControlPoint> points)
	{
		gc.clearRect(0, 0, canvas.getWidth(), canvas.getHeight());
		for (ControlPoint controlPoint : points)
		{
			gc.setFill(controlPoint.isSelected() ? Color.LIGHTBLUE : Color.BLACK);
			gc.fillRect(controlPoint.getX() - DOT_SIZE / 2, controlPoint.getY() - DOT_SIZE / 2, DOT_SIZE, DOT_SIZE);
			gc.setStroke(Color.BLACK);
			gc.beginPath();
			for (ControlPoint neighbour : controlPoint.getNeighbours())
			{
				for (double t = 0.0; t < 1.0; t += smoothness)
				{
					MutablePoint3D point = getBezierPoint(controlPoint, controlPoint.getTangent(neighbour), neighbour.getTangent(controlPoint), neighbour, t);
					gc.beginPath();
					gc.lineTo(point.getX(), point.getY());
					gc.stroke();
				}
			}
		}
	}

	public void drawSelection(Rectangle rectangle)
	{
		drawPoints(MainApp.controlPoints);
		if (rectangle == null)
		{
			return;
		}
		gc.setFill(selectionColor);
		gc.fillRect(rectangle.getX(), rectangle.getY(), rectangle.getWidth(), rectangle.getHeight());
	}

	public static MutablePoint3D getBezierPoint(MutablePoint3D point1, MutablePoint3D point2, MutablePoint3D point3, MutablePoint3D point4, double t)
	{
		double x = (1 - t) * (1 - t) * (1 - t) * point1.getX() + 3 * t * (1 - t) * (1 - t) * (point1.getX() + point2.getX()) + 3 * t * t * (1 - t) * (point4.getX() + point3.getX()) + t * t * t * point4.getX();
		double y = (1 - t) * (1 - t) * (1 - t) * point1.getY() + 3 * t * (1 - t) * (1 - t) * (point1.getY() + point2.getY()) + 3 * t * t * (1 - t) * (point4.getY() + point3.getY()) + t * t * t * point4.getY();
		double z = (1 - t) * (1 - t) * (1 - t) * point1.getZ() + 3 * t * (1 - t) * (1 - t) * (point1.getZ() + point2.getZ()) + 3 * t * t * (1 - t) * (point4.getZ() + point3.getZ()) + t * t * t * point4.getZ();
		return new MutablePoint3D(x, y, z);
	}
}
