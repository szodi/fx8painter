package image;

import java.awt.AWTException;
import java.awt.Robot;

import javafx.event.Event;
import javafx.event.EventHandler;
import javafx.geometry.Bounds;
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
import javafx.scene.layout.AnchorPane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import javafx.scene.text.Text;
import javafx.scene.transform.Rotate;
import javafx.scene.transform.Scale;

public class ViewPortEditor extends ImageView implements EventHandler<Event>
{
	Image image;
	double clickedX = 0.0;
	double clickedY = 0.0;
	Rotate rotate = new Rotate();
	Scale scale = new Scale(1.0, 1.0, 0.0, 0.0);
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

	double parentWidth = 1280;
	double parentHeight = 720;
	private double value;
	private Bounds bip;

	Text text = new Text();
	Rectangle rectangle = new Rectangle(85, 20);

	public ViewPortEditor(Image image)
	{
		super(image);
		getTransforms().add(scale);
		getTransforms().add(rotate);
		setViewport(new Rectangle2D(0, 0, parentWidth, parentHeight));
	}

	public void activate(Node node)
	{
		value = Math.sqrt(parentWidth * parentWidth + parentHeight * parentHeight);
		node.setOnMouseMoved(this);
		node.setOnMouseDragged(this);
		node.setOnMousePressed(this);
		node.setOnScroll(this);
		node.getScene().setOnKeyPressed(this);
		rectangle.setFill(Color.LIGHTBLUE);
		((AnchorPane)node).getChildren().add(rectangle);
		((AnchorPane)node).getChildren().add(text);
		doSetViewport();
		try
		{
			Robot robot = new Robot();
			robot.mouseMove(850, 394);
		}
		catch (AWTException e)
		{
			e.printStackTrace();
		}
	}

	public void handle(Event event)
	{
		if (event instanceof MouseEvent)
		{
			MouseEvent mouseEvent = (MouseEvent)event;
			handle(mouseEvent);
			updateTooltip(mouseEvent.getX(), mouseEvent.getY());
		}
		else if (event instanceof ScrollEvent)
		{
			ScrollEvent scrollEvent = (ScrollEvent)event;
			handle(scrollEvent);
			updateTooltip(scrollEvent.getX(), scrollEvent.getY());
		}
		else if (event instanceof KeyEvent)
		{
			handle((KeyEvent)event);
			doSetViewport();
		}
	}

	private void updateTooltip(double x, double y)
	{
		Point2D p = parentToLocal(x, y);
		StringBuilder sbText = new StringBuilder();
		sbText.append(String.format("parent: %.2f, %.2f", x, y));
		sbText.append('\n');
		sbText.append(String.format("local: %.2f, %.2f", p.getX(), p.getY()));
		sbText.append('\n');
		sbText.append(String.format("translate: %.2f, %.2f", translateX, translateY));
		sbText.append('\n');
		sbText.append(String.format("scale: %.2f", scale.getX()));
		sbText.append('\n');
		sbText.append(String.format("local+translate: %.2f, %.2f", p.getX() + translateX, p.getY() + translateY));
		text.setText(sbText.toString());
		text.setX(x + 5);
		text.setY(y + 30);
		double textWidth = text.getLayoutBounds().getWidth();
		double textHeight = text.getLayoutBounds().getHeight();
		rectangle.setX(text.getX() - 5);
		rectangle.setY(text.getY() - 15);
		rectangle.setWidth(textWidth + 10);
		rectangle.setHeight(textHeight + 10);
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

	private void doSetViewport()
	{
		setViewport(new Rectangle2D(translateX, translateY, Math.abs(value / scale.getX()), Math.abs(value / scale.getY())));
	}

	public void handle(ScrollEvent scrollEvent)
	{
		zoom(scrollEvent.getX(), scrollEvent.getY(), scale.getX() * Math.pow(1.01, scrollEvent.getDeltaY()));
		doSetViewport();
	}

	private void zoom(double x, double y, double factor)
	{
		Point2D localOld = parentToLocal(x, y);
		scale.setX(factor);
		scale.setY(factor);
		Point2D local = parentToLocal(x, y);
		translateX += localOld.getX() - local.getX();
		translateY += localOld.getY() - local.getY();
	}

	public void handle(KeyEvent keyEvent)
	{
		if (keyEvent.getCode() == KeyCode.M)
		{
			scale.setX(-scale.getX());
			mirrorFactor = -mirrorFactor;
		}
		else if (keyEvent.getCode() == KeyCode.ESCAPE)
		{
			translateX = 0.0;
			translateY = 0.0;
		}
		else if (keyEvent.getCode() == KeyCode.LEFT)
		{
			translateX += 1.0;
		}
		else if (keyEvent.getCode() == KeyCode.RIGHT)
		{
			translateX -= 1.0;
		}
		else if (keyEvent.getCode() == KeyCode.UP)
		{
			translateY += 1.0;
		}
		else if (keyEvent.getCode() == KeyCode.DOWN)
		{
			translateY -= 1.0;
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
			oldScaleFactor = mirrorFactor * scale.getX();
			oldAngle = rotate.getAngle();
		}
		// zoomTo(event.getX(), event.getY(), 2.0);
		doSetViewport();
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
				zoom(pivot.getX(), pivot.getY(), oldScaleFactor * dDestination / dSource);
			}
			double angle = angle(pivot, target, p);
			rotate.setAngle((oldAngle + mirrorFactor * angle) % 360);
		}
		else
		{
			Point2D delta = actualPoint.subtract(localPoint);
			translateX -= delta.getX();
			translateY -= delta.getY();
			localPoint = parentToLocal(event.getX(), event.getY());
		}
		doSetViewport();
	}

	protected void handleSecondaryMousePressed(MouseEvent event)
	{
		clickedX = event.getSceneX();
		oldTranslateX = getTranslateX();
		oldTranslateY = getTranslateX();
		rotate.setPivotX((oldTranslateX + parentWidth / 2) / scale.getX());
		rotate.setPivotY((oldTranslateY + parentHeight / 2) / scale.getY());
	}

	protected void handleSecondaryMouseDragged(MouseEvent event)
	{
		rotate.setAngle(rotate.getAngle() + (event.getSceneX() - clickedX) / 10);
		clickedX = event.getSceneX();
		// Point2D rotatedTranslate = rotate.transform(oldTranslateX, oldTranslateY);
		// setTranslateX(rotatedTranslate.getX());
		// setTranslateY(rotatedTranslate.getY());
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
}
