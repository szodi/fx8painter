package image;

import javafx.event.EventHandler;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.input.ScrollEvent;

public class ImageAdjusterView extends ImageView
{
	Image image;
	double scaleFactor = 1.0;

	public ImageAdjusterView(Image image)
	{
		super(image);
		setOnMouseDragged(mouseHandler);
		setOnMousePressed(mouseHandler);
		setOnScroll(scrollHandler);
	}

	EventHandler<MouseEvent> mouseHandler = new EventHandler<MouseEvent>()
	{
		double clickedX = 0.0;
		double clickedY = 0.0;

		@Override
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
	};

	EventHandler<ScrollEvent> scrollHandler = new EventHandler<ScrollEvent>()
	{
		@Override
		public void handle(ScrollEvent scrollEvent)
		{
			if (scrollEvent.getEventType() == ScrollEvent.SCROLL)
			{
				scaleFactor = Math.max(0, scaleFactor + scrollEvent.getDeltaY() / 500);
				setScaleX(scaleFactor);
				setScaleY(scaleFactor);
			}
		}
	};
}
