package image;

import javafx.event.Event;
import javafx.event.EventHandler;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseButton;
import javafx.scene.input.MouseEvent;
import javafx.scene.input.ScrollEvent;
import javafx.scene.transform.Rotate;
import javafx.scene.transform.Scale;

public class ImageAdjusterView extends ImageView implements EventHandler<Event>
{
	Image image;
	double clickedX = 0.0;
	double clickedY = 0.0;
	Scale scale = new Scale(1.0, 1.0);

	public ImageAdjusterView(Image image)
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
			scale.setX(Math.max(0, scale.getX() + scrollEvent.getDeltaY() / 500));
			scale.setY(Math.max(0, scale.getY() + scrollEvent.getDeltaY() / 500));
		}
	}

	protected void handlePrimaryMousePressed(MouseEvent event)
	{
		clickedX = event.getX() - getX();
		clickedY = event.getY() - getY();
	}

	protected void handlePrimaryMouseDragged(MouseEvent event)
	{
		setX(event.getX() - clickedX);
		setY(event.getY() - clickedY);
	}

	protected void handleSecondaryMousePressed(MouseEvent event)
	{
		clickedX = event.getX();
		clickedY = event.getY();
	}

	protected void handleSecondaryMouseDragged(MouseEvent event)
	{
		setRotate((event.getX() - clickedX) / 10);
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
