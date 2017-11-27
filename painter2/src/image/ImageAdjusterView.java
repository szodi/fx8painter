package image;

import javafx.event.Event;
import javafx.event.EventHandler;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.input.ScrollEvent;
import javafx.scene.transform.Scale;

public class ImageAdjusterView extends ImageView implements EventHandler<Event> {
	Image image;
	double scaleFactor = 1.0;
	double clickedX = 0.0;
	double clickedY = 0.0;
	Scale scale = new Scale(scaleFactor, scaleFactor);

	public ImageAdjusterView(Image image) {
		super(image);
		getTransforms().add(scale);
	}

	@Override
	public void handle(Event event) {
		if (event instanceof MouseEvent) {
			handle((MouseEvent) event);
		} else if (event instanceof ScrollEvent) {
			handle((ScrollEvent) event);
		}
	}

	public void handle(MouseEvent mouseEvent) {
		if (mouseEvent.getEventType() == MouseEvent.MOUSE_PRESSED) {
			clickedX = mouseEvent.getX() - getX();
			clickedY = mouseEvent.getY() - getY();
		} else if (mouseEvent.getEventType() == MouseEvent.MOUSE_DRAGGED) {
			scale.setPivotX(Math.max(scale.getPivotX() + (clickedX - mouseEvent.getX()) / scaleFactor, 0));
			scale.setPivotY(Math.max(scale.getPivotY() + (clickedY - mouseEvent.getY()) / scaleFactor, 0));
			clickedX = mouseEvent.getX();
			clickedY = mouseEvent.getY();
		}
	}

	public void handle(ScrollEvent scrollEvent) {
		if (scrollEvent.getEventType() == ScrollEvent.SCROLL) {
			scaleFactor = Math.max(0, scaleFactor + scrollEvent.getDeltaY() / 500);
			scale.setX(scaleFactor);
			scale.setY(scaleFactor);
			// System.out.println((scale.getTx() / scaleFactor) + "\t" + (scale.getTy() /
			// scaleFactor));
			// System.out.println(scale.getPivotX() + "\t" + scale.getPivotY());
			// System.out.println((scale.getTx() / scaleFactor) + "\t" + (scale.getTy() /
			// scaleFactor));
		}
	}

	public Scale getScale() {
		return scale;
	}
}
