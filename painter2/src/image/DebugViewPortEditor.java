package image;

import javafx.event.Event;
import javafx.geometry.Point2D;
import javafx.scene.Node;
import javafx.scene.image.Image;
import javafx.scene.input.Clipboard;
import javafx.scene.input.ClipboardContent;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.AnchorPane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import javafx.scene.text.Text;

public class DebugViewPortEditor extends ViewPortEditor
{
	Text text = new Text();
	Rectangle rectangle = new Rectangle();

	public DebugViewPortEditor(Image image, double width, double height)
	{
		super(image, width, height);
	}

	@Override
	public void activate(Node node)
	{
		super.activate(node);
		rectangle.setFill(Color.LIGHTBLUE);
		((AnchorPane)node).getChildren().add(rectangle);
		((AnchorPane)node).getChildren().add(text);
	}

	@Override
	public void handle(Event event)
	{
		super.handle(event);
		updateTooltip(lastMouseX, lastMouseY);
	}

	@Override
	public void handle(KeyEvent keyEvent)
	{
		super.handle(keyEvent);
		if (keyEvent.getCode() == KeyCode.ESCAPE)
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
		final Clipboard clipboard = Clipboard.getSystemClipboard();
		final ClipboardContent content = new ClipboardContent();
		content.putString(sbText.toString());
		clipboard.setContent(content);
	}
}
