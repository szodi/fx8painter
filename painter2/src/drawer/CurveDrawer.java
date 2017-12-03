package drawer;

import java.util.List;

import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;

import entity.ControlPoint;
import entity.MutablePoint3D;
import test.MainApp;
import tools.Tools;

public class CurveDrawer
{
	public static final int DOT_SIZE = 11;
	public static final int HALF_DOT_SIZE = DOT_SIZE / 2;

	public static final double smoothness = 0.02;

	protected Color selectionColor = new Color(0, 0, 0.5, 0.5);
	protected Color actualControlPointColor = Color.GREEN;
	protected Color selectedControlPointColor = Color.DARKGOLDENROD;
	protected Color unselectedControlPointColor = Color.LIGHTCYAN;

	protected Color controlPointBorder = Color.BLACK;
	protected Color segmentColor = Color.BLUE;

	protected GraphicsContext gc;

	public CurveDrawer(Canvas canvas)
	{
		gc = canvas.getGraphicsContext2D();
	}

	public void drawPoints(List<ControlPoint> points)
	{
		gc.setLineWidth(2);
		gc.clearRect(0, 0, gc.getCanvas().getWidth(), gc.getCanvas().getHeight());
		for (ControlPoint controlPoint : points)
		{
			gc.setFill(controlPoint == MainApp.actualControlPoint ? actualControlPointColor : (controlPoint.isSelected() ? selectedControlPointColor : unselectedControlPointColor));
			drawControlPoint(controlPoint);
			gc.setStroke(segmentColor);
			for (ControlPoint neighbour : controlPoint.getNeighbours())
			{
				drawSegment(controlPoint, neighbour);
			}
		}
	}

	void drawControlPoint(ControlPoint controlPoint)
	{
		gc.fillRect(controlPoint.getX() - HALF_DOT_SIZE, controlPoint.getY() - HALF_DOT_SIZE, DOT_SIZE, DOT_SIZE);
		gc.setStroke(controlPointBorder);
		gc.strokeRect(controlPoint.getX() - HALF_DOT_SIZE, controlPoint.getY() - HALF_DOT_SIZE, DOT_SIZE, DOT_SIZE);
	}

	protected void drawSegment(ControlPoint controlPoint, ControlPoint neighbour)
	{
		for (double t = 0.0; t < 1.0; t += smoothness)
		{
			MutablePoint3D point = Tools.getBezierPoint(controlPoint, neighbour, t);
			gc.strokeLine(point.getX(), point.getY(), point.getX(), point.getY());
		}
	}

	public void drawSelectorRectangle(Rectangle rectangle, List<ControlPoint> controlPoints)
	{
		drawPoints(controlPoints);
		if (rectangle == null)
		{
			return;
		}
		gc.setFill(selectionColor);
		gc.fillRect(rectangle.getX(), rectangle.getY(), rectangle.getWidth(), rectangle.getHeight());
	}
}
