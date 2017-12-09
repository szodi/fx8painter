package editor;

import java.util.List;
import java.util.function.Consumer;

import javafx.geometry.Point3D;
import javafx.scene.Node;
import javafx.scene.canvas.Canvas;
import javafx.scene.input.MouseEvent;
import javafx.scene.shape.Rectangle;
import javafx.scene.transform.Rotate;
import javafx.scene.transform.Scale;

import entity.ControlPoint;
import entity.MutablePoint3D;
import entity.Path;
import tools.Tools;

public class MultipointAdjuster extends AbstractEditor
{
	private Consumer<List<ControlPoint>> curveDrawer;
	private Path path;
	private Path pathClone;
	private MutablePoint3D pivot;
	private MutablePoint3D target;

	private Canvas canvas = new Canvas();

	private Rotate rotate = new Rotate(0, Rotate.Z_AXIS);
	private Scale scale = new Scale(1.0, 1.0);
	private MutablePoint3D clicked;

	public MultipointAdjuster(List<ControlPoint> controlPoints, Consumer<List<ControlPoint>> curveDrawer)
	{
		this.curveDrawer = curveDrawer;
		canvas.getTransforms().add(rotate);
		canvas.getTransforms().add(scale);
	}

	// public MultipointAdjuster(List<MutablePoint3D> points, Consumer<List<ControlPoint>> curveDrawer)
	// {
	// this.points = points;
	// this.curveDrawer = curveDrawer;
	// }

	@Override
	public void activate(Node node)
	{
		super.activate(node);
		Rectangle controlPointsBounds = Tools.getControlPointBounds(controlPoints);
		path = Path.create(controlPoints);
		pathClone = path.clone();
		canvas.setWidth(controlPointsBounds.getWidth());
		canvas.setHeight(controlPointsBounds.getWidth());
		curveDrawer.accept(path.getControlPoints());
	}

	public void rotateCurve(Path path, boolean onlySelected)
	{
		for (ControlPoint controlPoint : path.getControlPoints())
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

	@Override
	public void handle(MouseEvent mouseEvent)
	{
		super.handle(mouseEvent);
		curveDrawer.accept(pathClone.getControlPoints());
	}

	@Override
	protected void handlePrimaryMousePressed(MouseEvent event)
	{
		clickedX = event.getSceneX();
		clickedY = event.getSceneY();
		clicked = new MutablePoint3D(clickedX, clickedY, 0);
		if (event.isControlDown())
		{
			target = clicked;
		}
		else
		{
			pivot = clicked;
			scale.setPivotX(pivot.getX());
			scale.setPivotY(pivot.getY());
			rotate.setPivotX(pivot.getX());
			rotate.setPivotY(pivot.getY());
		}
	}

	@Override
	protected void handlePrimaryMouseDragged(MouseEvent event)
	{
		System.out.println(pivot + "\t" + target);
		if (pivot != null && target != null)
		{
			MutablePoint3D actualPoint = new MutablePoint3D(event.getSceneX(), event.getSceneY(), 0);
			double dx = target.getX() - pivot.getX();
			double dy = target.getY() - pivot.getY();
			if (dx != 0.0)
			{
				scale.setX((event.getSceneX() - pivot.getX()) / dx);
			}
			if (dy != 0.0)
			{
				scale.setY((event.getSceneY() - pivot.getY()) / dy);
			}
			// double angle = pivot.angle(clicked, actualPoint);
			// rotate.setAngle(-angle);
		}
		pathClone = path.clone();
		rotateCurve(pathClone, false);
	}

	// @Override
	// protected void handleSecondaryMousePressed(MouseEvent event)
	// {
	// clickedX = event.getX();
	// clickedY = event.getY();
	// }
	//
	// @Override
	// protected void handleSecondaryMouseDragged(MouseEvent event)
	// {
	// if (controlPoint != null && neighbour != null && controlPoint.getTangent(neighbour) != null)
	// {
	// controlPoint.getTangent(neighbour).add(event.getX() - clickedX, event.getY() - clickedY, 0.0);
	// clickedX = event.getX();
	// clickedY = event.getY();
	// }
	// }
}
