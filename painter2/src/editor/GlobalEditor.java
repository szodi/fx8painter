package editor;

import javafx.scene.Node;
import javafx.scene.image.Image;
import javafx.scene.input.MouseEvent;
import javafx.scene.input.ScrollEvent;
import javafx.scene.transform.Rotate;
import javafx.scene.transform.Scale;
import javafx.scene.transform.Translate;

public class GlobalEditor extends AbstractEditor
{
	Image image;
	Scale scale = new Scale(1.0, 1.0, 960, 540);
	Rotate rotate = new Rotate();
	Translate translate = new Translate();

	Node node;

	public GlobalEditor(Node node)
	{
		super();
		this.node = node;
		node.getTransforms().add(scale);
		node.getTransforms().add(rotate);
		node.getTransforms().add(translate);
	}

	@Override
	public void activate(Node node)
	{
		super.activate(node);
		node.setOnScroll(this::handleScrollEvent);
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

	public void handleScrollEvent(ScrollEvent scrollEvent)
	{
		// scale.setPivotX(scrollEvent.getX());
		// scale.setPivotY(scrollEvent.getY());
		if (scrollEvent.getEventType() == ScrollEvent.SCROLL)
		{
			scale.setX(Math.max(0, scale.getX() + scrollEvent.getDeltaY() / 500));
			scale.setY(Math.max(0, scale.getY() + scrollEvent.getDeltaY() / 500));
		}
	}

	protected void handlePrimaryMousePressed(MouseEvent event)
	{
		clickedX = event.getX();
		clickedY = event.getY();
	}

	protected void handlePrimaryMouseDragged(MouseEvent event)
	{
		translate.setX(translate.getX() + event.getX() - clickedX);
		translate.setY(translate.getY() + event.getY() - clickedY);
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

	public Scale getScale()
	{
		return scale;
	}

	public void setScale(Scale scale)
	{
		this.scale = scale;
	}
}
