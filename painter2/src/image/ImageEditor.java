package image;

import javafx.event.Event;
import javafx.event.EventHandler;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.MouseButton;
import javafx.scene.input.MouseEvent;
import javafx.scene.input.ScrollEvent;
import javafx.scene.transform.Rotate;
import javafx.scene.transform.Scale;

public class ImageEditor extends ImageView implements EventHandler<Event>
{
	Image image;
	double clickedX = 0.0;
	double clickedY = 0.0;
	Scale scale = new Scale(1.0, 1.0);
	int mirrorFactor = 1;

	public ImageEditor(Image image)
	{
		super(image);
		getTransforms().add(scale);
		setRotationAxis(Rotate.Z_AXIS);
	}

	public void handle(Event event)
	{
		scale.setPivotX(getScene().getWidth() / 2);
		scale.setPivotY(getScene().getHeight() / 2);
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
		clickedX = event.getX() - mirrorFactor * getX();
		clickedY = event.getY() - getY();
	}

	protected void handlePrimaryMouseDragged(MouseEvent event)
	{
		setX(mirrorFactor * (event.getX() - clickedX));
		setY(event.getY() - clickedY);
	}

	protected void handleSecondaryMousePressed(MouseEvent event)
	{
		clickedX = event.getX();
		clickedY = event.getY();
	}

	protected void handleSecondaryMouseDragged(MouseEvent event)
	{
		setRotate(getRotate() + (event.getX() - clickedX) / 10);
		clickedX = event.getX();
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
