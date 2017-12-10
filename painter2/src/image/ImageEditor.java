package image;

import javafx.event.Event;
import javafx.event.EventHandler;
import javafx.geometry.Point2D;
import javafx.scene.Node;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.MouseButton;
import javafx.scene.input.MouseEvent;
import javafx.scene.input.ScrollEvent;
import javafx.scene.transform.Rotate;
import javafx.scene.transform.Scale;
import javafx.scene.transform.Translate;

public class ImageEditor extends ImageView implements EventHandler<Event>
{
	Image image;
	double clickedX = 0.0;
	double clickedY = 0.0;
	Scale scale = new Scale(1.0, 1.0);
	Rotate rotate = new Rotate();
	Translate translate = new Translate();
	int mirrorFactor = 1;

	Point2D localPoint;

	Point2D pivot;
	Point2D target;

	double oldScaleFactor = 1.0;
	double oldAngle;

	public ImageEditor(Image image)
	{
		super(image);
		getTransforms().add(scale);
		getTransforms().add(rotate);
		getTransforms().add(translate);
	}

	public void activate(Node node)
	{
		node.setOnMouseMoved(this);
		node.setOnMouseDragged(this);
		node.setOnMousePressed(this);
		node.setOnScroll(this);
		node.getScene().setOnKeyPressed(this);
	}

	public void resetTransforms()
	{
		scale.setX(1.0);
		scale.setY(1.0);
		scale.setZ(1.0);
		rotate.setAngle(0.0);
		translate.setX(0.0);
		translate.setY(0.0);
		translate.setZ(0.0);
	}

	public void handle(Event event)
	{
		if (event instanceof MouseEvent)
		{
			handle((MouseEvent)event);
		}
		else if (event instanceof ScrollEvent)
		{
			handle((ScrollEvent)event);
		}
		else if (event instanceof KeyEvent)
		{
			handle((KeyEvent)event);
		}
	}

	public void handle(MouseEvent mouseEvent)
	{
		if (mouseEvent.getButton() == MouseButton.PRIMARY)
		{
			if (mouseEvent.getEventType() == MouseEvent.MOUSE_PRESSED)
			{
				handlePrimaryMousePressed(mouseEvent);
			}
			else if (mouseEvent.getEventType() == MouseEvent.MOUSE_DRAGGED)
			{
				handlePrimaryMouseDragged(mouseEvent);
			}
		}
		else if (mouseEvent.getButton() == MouseButton.SECONDARY)
		{
			if (mouseEvent.getEventType() == MouseEvent.MOUSE_PRESSED)
			{
				handleSecondaryMousePressed(mouseEvent);
			}
			else if (mouseEvent.getEventType() == MouseEvent.MOUSE_DRAGGED)
			{
				handleSecondaryMouseDragged(mouseEvent);
			}
		}
	}

	public void handle(ScrollEvent scrollEvent)
	{
		scale.setPivotX(scrollEvent.getX());
		scale.setPivotY(scrollEvent.getY());
		if (scrollEvent.getEventType() == ScrollEvent.SCROLL)
		{
			if (mirrorFactor > 0)
			{
				scale.setX(Math.max(0, scale.getX() + scrollEvent.getDeltaY() / 500));
			}
			else
			{
				scale.setX(Math.min(0, scale.getX() - scrollEvent.getDeltaY() / 500));
			}
			scale.setY(Math.max(0, scale.getY() + scrollEvent.getDeltaY() / 500));
		}
	}

	public void handle(KeyEvent keyEvent)
	{
		if (keyEvent.getCode() == KeyCode.M)
		{
			scale.setX(-scale.getX());
			mirrorFactor = -mirrorFactor;
		}
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
				scale.setX(mirrorFactor * scaleFactor);
				scale.setY(scaleFactor);
			}
			double angle = angle(pivot, target, p);
			rotate.setAngle((oldAngle + mirrorFactor * angle) % 360);
		}
		else
		{
			Point2D delta = actualPoint.subtract(localPoint);
			translate.setX(translate.getX() + delta.getX());
			translate.setY(translate.getY() + delta.getY());
			localPoint = parentToLocal(event.getX(), event.getY());
		}
	}

	protected void handleSecondaryMousePressed(MouseEvent event)
	{
		clickedX = event.getSceneX();
	}

	protected void handleSecondaryMouseDragged(MouseEvent event)
	{
		rotate.setAngle(rotate.getAngle() + (event.getSceneX() - clickedX) / 10);
		clickedX = event.getSceneX();
	}

	private static double angle(Point2D pivot, Point2D p1, Point2D p2)
	{
		double dpx1 = (p1.getX() - pivot.getX());
		double dpy1 = (p1.getY() - pivot.getY());
		double dpx2 = (p2.getX() - pivot.getX());
		double dpy2 = (p2.getY() - pivot.getY());

		double dot = dpx1 * dpx2 + dpy1 * dpy2;
		double det = dpx1 * dpy2 - dpy1 * dpx2;
		double angle1 = Math.atan2(det, dot);
		return (Math.toDegrees(angle1) + 360) % 360;
	}

	public Scale getScale()
	{
		return scale;
	}

	public void setScale(Scale scale)
	{
		this.scale = scale;
	}
}
