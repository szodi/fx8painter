package editor;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;

import javafx.scene.input.MouseEvent;
import javafx.scene.shape.Rectangle;

import entity.ControlPoint;
import entity.MutablePoint3D;
import test.MainApp;

public class GridEditor extends AbstractEditor
{
	List<ControlPoint> points;
	double clickedX;
	double clickedY;
	Rectangle rectangle = new Rectangle();
	int horizontalPointsCount = 5;
	int verticalPointsCount = 5;
	private Consumer<List<ControlPoint>> curveDrawer;

	public GridEditor(Consumer<List<ControlPoint>> curveDrawer)
	{
		this.curveDrawer = curveDrawer;
	}

	@Override
	public void handle(MouseEvent mouseEvent)
	{
		if (mouseEvent.getEventType() == MouseEvent.MOUSE_PRESSED)
		{
			points = createDefaultControlPoints();
			clickedX = mouseEvent.getX();
			clickedY = mouseEvent.getY();
			MainApp.controlPoints.addAll(points);
			curveDrawer.accept(MainApp.controlPoints);
		}
		else if (mouseEvent.getEventType() == MouseEvent.MOUSE_DRAGGED)
		{
			rectangle.setX(Math.min(clickedX, mouseEvent.getX()));
			rectangle.setY(Math.min(clickedY, mouseEvent.getY()));
			rectangle.setWidth(Math.abs(mouseEvent.getX() - clickedX));
			rectangle.setHeight(Math.abs(mouseEvent.getY() - clickedY));
			updateControlPoints();
			curveDrawer.accept(MainApp.controlPoints);
		}
	}

	private List<ControlPoint> createDefaultControlPoints()
	{
		List<ControlPoint> points = new ArrayList<>();
		for (int i = 0; i < horizontalPointsCount * verticalPointsCount; i++)
		{
			points.add(new ControlPoint(0.0, 0.0, 0.0));
		}
		for (int j = 0; j < verticalPointsCount; j++)
		{
			for (int i = 0; i < horizontalPointsCount; i++)
			{
				ControlPoint cp0 = points.get(j * horizontalPointsCount + i);
				if (i < horizontalPointsCount - 1)
				{
					ControlPoint cpRight = points.get(j * horizontalPointsCount + i + 1);
					cp0.setTangent(cpRight, new MutablePoint3D(cpRight.getX() - cp0.getX(), cpRight.getY() - cp0.getY(), cpRight.getZ() - cp0.getZ()));
					cpRight.setTangent(cp0, new MutablePoint3D(cp0.getX() - cpRight.getX(), cp0.getY() - cpRight.getY(), cp0.getZ() - cpRight.getZ()));
				}
				if (j < verticalPointsCount - 1)
				{
					ControlPoint cpDown = points.get((j + 1) * horizontalPointsCount + i);
					cp0.setTangent(cpDown, new MutablePoint3D(cpDown.getX() - cp0.getX(), cpDown.getY() - cp0.getY(), cpDown.getZ() - cp0.getZ()));
					cpDown.setTangent(cp0, new MutablePoint3D(cp0.getX() - cpDown.getX(), cp0.getY() - cpDown.getY(), cp0.getZ() - cpDown.getZ()));
				}
			}
		}
		return points;
	}

	private void updateControlPoints()
	{
		double scaleHorizontal = rectangle.getWidth() / (horizontalPointsCount - 1);
		double scaleVertical = rectangle.getHeight() / (verticalPointsCount - 1);
		int k = 0;
		for (int j = 0; j < verticalPointsCount; j++)
		{
			for (int i = 0; i < horizontalPointsCount; i++)
			{
				double px = i * scaleHorizontal + rectangle.getX();
				double py = j * scaleVertical + rectangle.getY();
				ControlPoint controlPoint = points.get(k++);
				controlPoint.setX(px);
				controlPoint.setY(py);
			}
		}
	}

	public int getHorizontalPointsCount()
	{
		return horizontalPointsCount;
	}

	public void setHorizontalPointsCount(int horizontalPointsCount)
	{
		this.horizontalPointsCount = horizontalPointsCount;
	}

	public int getVerticalPointsCount()
	{
		return verticalPointsCount;
	}

	public void setVerticalPointsCount(int verticalPointsCount)
	{
		this.verticalPointsCount = verticalPointsCount;
	}

	public static void main(String[] args)
	{
		MutablePoint3D p1 = new MutablePoint3D(0, 0, 0);
		MutablePoint3D p2 = new MutablePoint3D(0, 0, 0);
		System.out.println(p1.equals(p2));
	}
}
