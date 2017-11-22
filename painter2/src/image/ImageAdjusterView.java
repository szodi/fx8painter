package image;

import javafx.event.Event;
import javafx.event.EventHandler;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.input.ScrollEvent;

public class ImageAdjusterView extends ImageView implements EventHandler<Event>
{
	Image image;
	double scaleFactor = 1.0;
	double clickedX = 0.0;
	double clickedY = 0.0;

	public ImageAdjusterView(Image image)
	{
		super(image);
	}

	@Override
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
			setScaleX(scaleFactor);
			setScaleY(scaleFactor);
		}
	}
}
