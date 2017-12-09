package image;

import javafx.geometry.Point2D;
import javafx.scene.image.Image;
import javafx.scene.input.MouseEvent;

public class MultiPointImageEditor extends ImageEditor
{
	Point2D pivot;
	Point2D target;

	double clickedY = 0.0;
	double oldScaleFactor;

	public MultiPointImageEditor(Image image)
	{
		super(image);
	}

	protected void handlePrimaryMousePressed(MouseEvent event)
	{
		clickedX = event.getSceneX();
		clickedY = event.getSceneY();
		if (event.isControlDown())
		{
			target = new Point2D(clickedX, clickedY);
		}
		else
		{
			pivot = new Point2D(clickedX, clickedY);
			rotate.setPivotX(pivot.getX());
			rotate.setPivotY(pivot.getY());
			oldScaleFactor = scale.getX();
		}
	}

	protected void handlePrimaryMouseDragged(MouseEvent event)
	{
		if (pivot != null && target != null && event.isControlDown())
		{
			Point2D actualPoint = new Point2D(event.getSceneX(), event.getSceneY());
			double dSource = pivot.distance(target);
			double dDestination = pivot.distance(actualPoint);
			if (dSource > 0.0)
			{
				double scaleFactor = oldScaleFactor * dDestination / dSource;
				scale.setX(scaleFactor);
				scale.setY(scaleFactor);
			}
			double angle = angle(pivot, target, actualPoint);
			rotate.setAngle(angle);
		}
		else
		{
			translate.setX(translate.getX() + event.getSceneX() - clickedX);
			translate.setY(translate.getY() + event.getSceneY() - clickedY);
			clickedX = event.getSceneX();
			clickedY = event.getSceneY();
		}
	}

	private static double angle(Point2D pivot, Point2D p1, Point2D p2)
	{
		double r1 = pivot.distance(p1);
		double r2 = pivot.distance(p2);
		double angle1 = Math.acos((p1.getX() - pivot.getX()) / r1);
		double angle2 = Math.acos((p2.getX() - pivot.getX()) / r2);
		return Math.toDegrees(angle2 - angle1);
	}
}
