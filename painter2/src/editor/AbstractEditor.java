package editor;

import javafx.event.EventHandler;
import javafx.scene.Scene;
import javafx.scene.input.MouseEvent;

public abstract class AbstractEditor implements EventHandler<MouseEvent> {

	public void activate(Scene scene) {
		scene.setOnMouseMoved(this);
		scene.setOnMousePressed(this);
		scene.setOnMouseDragged(this);
		scene.setOnMouseReleased(this);
	}
}
