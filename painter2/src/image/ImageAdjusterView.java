package image;

import javafx.event.Event;
import javafx.event.EventHandler;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.input.ScrollEvent;
import javafx.scene.transform.Scale;

public class ImageAdjusterView extends ImageView implements EventHandler<Event>
{
	Image image;
	double scaleFactor = 1.0;
	double clickedX = 0.0;
	double clickedY = 0.0;
	Scale scale = new Scale(scaleFactor, scaleFactor);

	public ImageAdjusterView(Image image)
	{
		super(image);
		getTransforms().add(scale);
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
		if (mouseEvent.getEventType() == MouseEvent.MOUSE_PRESSED)
		{
			clickedX = mouseEvent.getX() - getX();
			clickedY = mouseEvent.getY() - getY();
		}
		else if (mouseEvent.getEventType() == MouseEvent.MOUSE_DRAGGED)
		{
			setX(mouseEvent.getX() - clickedX);
			setY(mouseEvent.getY() - clickedY);
		}
	}

	public void handle(ScrollEvent scrollEvent)
	{
		if (scrollEvent.getEventType() == ScrollEvent.SCROLL)
		{
			scaleFactor = Math.max(0, scaleFactor + scrollEvent.getDeltaY() / 500);
			scale.setX(scaleFactor);
			scale.setY(scaleFactor);
		}
	}

	public Scale getScale()
	{
		return scale;
	}
}
