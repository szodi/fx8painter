package image;

import javafx.geometry.Point2D;
import javafx.scene.image.Image;
import javafx.scene.input.MouseEvent;

public class MultiPointImageEditor extends ImageEditor
{
	Point2D pivot;
	Point2D target;

	double clickedY = 0.0;
	double oldScaleFactor = 1.0;
	double oldAngle;

	public MultiPointImageEditor(Image image)
	{
		super(image);
	}

	protected void handlePrimaryMousePressed(MouseEvent event)
	{
		localPoint = parentToLocal(event.getX(), event.getY());
		if (event.isControlDown())
		{
			target = new Point2D(event.getX(), event.getY());
		}
		else
		{
			pivot = new Point2D(event.getX(), event.getY());
			oldScaleFactor = scale.getX();
			oldAngle = rotate.getAngle();
		}
	}

	protected void handlePrimaryMouseDragged(MouseEvent event)
	{
		Point2D actualPoint = parentToLocal(event.getX(), event.getY());
		if (pivot != null && target != null && event.isControlDown())
		{
			Point2D p = new Point2D(event.getX(), event.getY());
			double dSource = pivot.distance(target);
			double dDestination = pivot.distance(p);
			if (dSource > 0.0)
			{
				double scaleFactor = oldScaleFactor * dDestination / dSource;
				scale.setX(scaleFactor);
				scale.setY(scaleFactor);
			}
			double angle = angle(pivot, target, p);
			rotate.setAngle(oldAngle + angle);
		}
		else
		{
			Point2D delta = actualPoint.subtract(localPoint);
			translate.setX(translate.getX() + delta.getX());
			translate.setY(translate.getY() + delta.getY());
			localPoint = parentToLocal(event.getX(), event.getY());
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
