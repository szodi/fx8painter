package editor;

import java.util.List;
import java.util.function.Consumer;

import javafx.geometry.Point3D;
import javafx.scene.Node;
import javafx.scene.canvas.Canvas;
import javafx.scene.input.MouseEvent;
import javafx.scene.shape.Rectangle;
import javafx.scene.transform.Rotate;

import entity.ControlPoint;
import entity.MutablePoint3D;
import tools.Tools;

public class Rotator extends AbstractEditor
{
	private Consumer<List<ControlPoint>> curveDrawer;
	Rotate rotate1 = new Rotate(0, Rotate.Y_AXIS);
	Rotate rotate2 = new Rotate(0, Rotate.X_AXIS);
	private Canvas canvas = new Canvas();

	public Rotator(List<ControlPoint> controlPoints, Consumer<List<ControlPoint>> curveDrawer)
	{
		this.controlPoints = controlPoints;
		this.curveDrawer = curveDrawer;
		canvas.getTransforms().add(rotate1);
		canvas.getTransforms().add(rotate2);
	}

	@Override
	public void activate(Node node)
	{
		super.activate(node);
		Rectangle controlPointsBounds = Tools.getControlPointBounds(controlPoints);
		canvas.setWidth(controlPointsBounds.getWidth());
		canvas.setHeight(controlPointsBounds.getWidth());
		rotate1.setPivotX(controlPointsBounds.getX() + controlPointsBounds.getWidth() / 2);
		rotate1.setPivotY(controlPointsBounds.getY() + controlPointsBounds.getHeight() / 2);
		rotate2.setPivotX(controlPointsBounds.getX() + controlPointsBounds.getWidth() / 2);
		rotate2.setPivotY(controlPointsBounds.getY() + controlPointsBounds.getHeight() / 2);
		curveDrawer.accept(controlPoints);
	}

	@Override
	public void handle(MouseEvent mouseEvent)
	{
		super.handle(mouseEvent);
		clickedX = mouseEvent.getX();
		clickedY = mouseEvent.getY();
		curveDrawer.accept(controlPoints);
	}

	@Override
	protected void handlePrimaryMouseDragged(MouseEvent event)
	{
		rotate1.setAngle((clickedX - event.getX()) / 8);
		rotate2.setAngle((event.getY() - clickedY) / 8);
		rotateCurve(false);
	}

	@Override
	protected void handleSecondaryMouseDragged(MouseEvent event)
	{
		rotate1.setAngle((clickedX - event.getX()) / 8);
		rotate2.setAngle((event.getY() - clickedY) / 8);
		rotateCurve(true);
	}

	public void rotateCurve(boolean onlySelected)
	{
		for (ControlPoint controlPoint : controlPoints)
		{
			if (!onlySelected || (onlySelected && controlPoint.isSelected()))
			{
				Point3D rotated = canvas.localToParent(controlPoint.getX(), controlPoint.getY(), controlPoint.getZ());
				for (MutablePoint3D tangent : controlPoint.getTangents())
				{
					Point3D rotatedTangent = canvas.localToParent(tangent.getX() + controlPoint.getX(), tangent.getY() + controlPoint.getY(), tangent.getZ() + controlPoint.getZ());
					tangent.setX(rotatedTangent.getX() - rotated.getX());
					tangent.setY(rotatedTangent.getY() - rotated.getY());
					tangent.setZ(rotatedTangent.getZ() - rotated.getZ());
				}
				controlPoint.setX(rotated.getX());
				controlPoint.setY(rotated.getY());
				controlPoint.setZ(rotated.getZ());
			}
		}
	}
}
