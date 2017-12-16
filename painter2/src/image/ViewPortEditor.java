package image;

import javafx.event.Event;
import javafx.event.EventHandler;
import javafx.geometry.Point2D;
import javafx.geometry.Rectangle2D;
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

public class ViewPortEditor extends ImageView implements EventHandler<Event>
{
	double clickedX = 0.0;
	double clickedY = 0.0;
	Rotate rotate = new Rotate();
	Scale scale = new Scale();
	int mirrorFactor = 1;

	Point2D localPoint;

	Point2D pivot;
	Point2D target;

	double oldScaleFactor = 1.0;
	double oldAngle;
	double oldTranslateX = 0.0;
	double oldTranslateY = 0.0;

	double translateX = 0.0;
	double translateY = 0.0;

	double width;
	double height;

	double lastMouseX;
	double lastMouseY;

	public ViewPortEditor(Image image, double width, double height)
	{
		super(image);
		this.width = width;
		this.height = height;
		getTransforms().add(scale);
		getTransforms().add(rotate);
		resetTransforms();
	}

	public void activate(Node node)
	{
		node.setOnMouseMoved(this);
		node.setOnMouseDragged(this);
		node.setOnMousePressed(this);
		node.setOnScroll(this);
		node.getScene().setOnKeyPressed(this);
		doSetViewport();
	}

	public void handle(Event event)
	{
		if (event instanceof MouseEvent)
		{
			MouseEvent mouseEvent = (MouseEvent)event;
			lastMouseX = mouseEvent.getX();
			lastMouseY = mouseEvent.getY();
			handle(mouseEvent);
		}
		else if (event instanceof ScrollEvent)
		{
			ScrollEvent scrollEvent = (ScrollEvent)event;
			lastMouseX = scrollEvent.getX();
			lastMouseY = scrollEvent.getY();
			handle(scrollEvent);
		}
		else if (event instanceof KeyEvent)
		{
			handle((KeyEvent)event);
		}
		doSetViewport();
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

	public void handle(KeyEvent keyEvent)
	{
		if (keyEvent.getCode() == KeyCode.M)
		{
			mirror();
		}
	}

	protected void doSetViewport()
	{
		Rectangle2D viewPort = new Rectangle2D(mirrorFactor * translateX, translateY, Math.abs(width / scale.getX()), Math.abs(height / scale.getY()));
		setViewport(viewPort);
	}

	public void handle(ScrollEvent scrollEvent)
	{
		double scaleFactor = scale.getX() * Math.pow(1.01, scrollEvent.getDeltaY());
		zoom(scrollEvent.getX(), scrollEvent.getY(), scaleFactor, Math.abs(scaleFactor));
	}

	protected void zoom(double x, double y, double factorX, double factorY)
	{
		Point2D localOld = parentToLocal(x, y);
		scale.setX(factorX);
		scale.setY(factorY);
		Point2D local = parentToLocal(x, y);
		translateX += mirrorFactor * (localOld.getX() - local.getX());
		translateY += localOld.getY() - local.getY();
	}

	protected void mirror()
	{
		mirrorFactor = -mirrorFactor;
		scale.setX(-scale.getX());
		setTranslateX(mirrorFactor < 0 ? width : 0);
		translateX = mirrorFactor * (width - 2 * lastMouseX) / scale.getX() - translateX;
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
			oldScaleFactor = mirrorFactor * scale.getX();
			oldAngle = rotate.getAngle();
		}
	}

	protected void handlePrimaryMouseDragged(MouseEvent event)
	{
		Point2D actualPoint = new Point2D(event.getX(), event.getY());
		if (pivot != null && target != null && event.isControlDown())
		{
			double dSource = pivot.distance(target);
			double dDestination = pivot.distance(actualPoint);
			if (dSource > 0.0)
			{
				double scaleFactor = oldScaleFactor * dDestination / dSource;
				zoom(pivot.getX(), pivot.getY(), mirrorFactor * scaleFactor, scaleFactor);
			}
			// double angle = angle(pivot, target, actualPoint);
			// rotate.setAngle((oldAngle + mirrorFactor * angle) % 360);
		}
		else
		{
			Point2D delta = parentToLocal(actualPoint).subtract(localPoint);
			translateX -= mirrorFactor * delta.getX();
			translateY -= delta.getY();
			localPoint = parentToLocal(event.getX(), event.getY());
		}
	}

	protected void handleSecondaryMousePressed(MouseEvent event)
	{
		clickedX = event.getSceneX();
		oldTranslateX = getTranslateX();
		oldTranslateY = getTranslateX();
		rotate.setPivotX((oldTranslateX + width / 2) / scale.getX());
		rotate.setPivotY((oldTranslateY + height / 2) / scale.getY());
	}

	protected void handleSecondaryMouseDragged(MouseEvent event)
	{
		rotate.setAngle(rotate.getAngle() + (event.getSceneX() - clickedX) / 10);
		clickedX = event.getSceneX();
		// Point2D rotatedTranslate = rotate.transform(oldTranslateX, oldTranslateY);
		// setTranslateX(rotatedTranslate.getX());
		// setTranslateY(rotatedTranslate.getY());
	}

	protected double angle(Point2D pivot, Point2D p1, Point2D p2)
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

	public void resetTransforms()
	{
		scale.setX(1.0);
		scale.setY(1.0);
		scale.setZ(1.0);
		rotate.setAngle(0.0);
		translateX = 0.0;
		translateY = 0.0;
	}

	public Scale getScale()
	{
		return scale;
	}
}
