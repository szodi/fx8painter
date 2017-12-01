package editor;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;

import javafx.scene.Scene;
import javafx.scene.input.MouseEvent;
import javafx.scene.shape.Rectangle;

import entity.ControlPoint;

public class GridEditor extends AbstractEditor
{
	public static int horizontalPointsCount = 3;
	public static int verticalPointsCount = 7;

	private List<ControlPoint> points;
	private Rectangle rectangle = new Rectangle();
	private Consumer<List<ControlPoint>> curveDrawer;

	public GridEditor(List<ControlPoint> controlPoints, Consumer<List<ControlPoint>> curveDrawer)
	{
		this.controlPoints = controlPoints;
		this.curveDrawer = curveDrawer;
	}

	@Override
	public void activate(Scene scene)
	{
		super.activate(scene);
		curveDrawer.accept(controlPoints);
	}

	@Override
	public void handle(MouseEvent mouseEvent)
	{
		super.handle(mouseEvent);
		curveDrawer.accept(controlPoints);
	}

	@Override
	protected void handlePrimaryMousePressed(MouseEvent event)
	{
		points = createDefaultControlPoints();
		clickedX = event.getX();
		clickedY = event.getY();
		controlPoints.addAll(points);
	}

	@Override
	protected void handlePrimaryMouseDragged(MouseEvent event)
	{
		rectangle.setX(Math.min(clickedX, event.getX()));
		rectangle.setY(Math.min(clickedY, event.getY()));
		rectangle.setWidth(Math.abs(event.getX() - clickedX));
		rectangle.setHeight(Math.abs(event.getY() - clickedY));
		updateControlPoints();
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
					cp0.setTangent(cpRight, cpRight.clone().subtract(cp0));
					cpRight.setTangent(cp0, cp0.clone().subtract(cpRight));
				}
				if (j < verticalPointsCount - 1)
				{
					ControlPoint cpDown = points.get((j + 1) * horizontalPointsCount + i);
					cp0.setTangent(cpDown, cpDown.clone().subtract(cp0));
					cpDown.setTangent(cp0, cp0.clone().subtract(cpDown));
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
}
