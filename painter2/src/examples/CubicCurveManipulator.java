package examples;

import javafx.application.Application;
import javafx.beans.property.DoubleProperty;
import javafx.scene.Cursor;
import javafx.scene.Group;
import javafx.scene.Scene;
import javafx.scene.input.MouseEvent;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.scene.shape.CubicCurve;
import javafx.scene.shape.Line;
import javafx.scene.shape.StrokeLineCap;
import javafx.scene.shape.StrokeType;
import javafx.stage.Stage;

/** Example of how a cubic curve works, drag the anchors around to change the curve. */
public class CubicCurveManipulator extends Application
{
	public static void main(String[] args) throws Exception
	{
		launch(args);
	}

	@Override
	public void start(final Stage stage) throws Exception
	{
		CubicCurve curve = createStartingCurve();

		Line controlLine1 = new BoundLine(curve.controlX1Property(), curve.controlY1Property(), curve.startXProperty(), curve.startYProperty());
		Line controlLine2 = new BoundLine(curve.controlX2Property(), curve.controlY2Property(), curve.endXProperty(), curve.endYProperty());

		Anchor start = new Anchor(Color.PALEGREEN, curve.startXProperty(), curve.startYProperty());
		Anchor control1 = new Anchor(Color.GOLD, curve.controlX1Property(), curve.controlY1Property());
		Anchor control2 = new Anchor(Color.GOLDENROD, curve.controlX2Property(), curve.controlY2Property());
		Anchor end = new Anchor(Color.TOMATO, curve.endXProperty(), curve.endYProperty());

		stage.setTitle("Cubic Curve Manipulation Sample");
		stage.setScene(new Scene(new Group(controlLine1, controlLine2, curve, start, control1, control2, end), 400, 400, Color.ALICEBLUE));
		stage.show();
	}

	private CubicCurve createStartingCurve()
	{
		CubicCurve curve = new CubicCurve();
		curve.setStartX(100);
		curve.setStartY(100);
		curve.setControlX1(150);
		curve.setControlY1(50);
		curve.setControlX2(250);
		curve.setControlY2(150);
		curve.setEndX(300);
		curve.setEndY(100);
		curve.setStroke(Color.FORESTGREEN);
		curve.setStrokeWidth(2);
		curve.setStrokeLineCap(StrokeLineCap.ROUND);
		curve.setFill(Color.CORNSILK.deriveColor(0, 1.2, 1, 0.6));
		return curve;
	}

	class BoundLine extends Line
	{
		BoundLine(DoubleProperty startX, DoubleProperty startY, DoubleProperty endX, DoubleProperty endY)
		{
			startXProperty().bind(startX);
			startYProperty().bind(startY);
			endXProperty().bind(endX);
			endYProperty().bind(endY);
			setStrokeWidth(2);
			setStroke(Color.GRAY.deriveColor(0, 1, 1, 0.5));
			setStrokeLineCap(StrokeLineCap.BUTT);
			getStrokeDashArray().setAll(10.0, 5.0);
		}
	}

	// a draggable anchor displayed around a point.
	class Anchor extends Circle
	{
		double dragDeltaX = 0.0;
		double dragDeltaY = 0.0;

		Anchor(Color color, DoubleProperty x, DoubleProperty y)
		{
			super(x.get(), y.get(), 10);
			setFill(color.deriveColor(1, 1, 1, 0.5));
			setStroke(color);
			setStrokeWidth(2);
			setStrokeType(StrokeType.OUTSIDE);

			x.bind(centerXProperty());
			y.bind(centerYProperty());

			setOnMousePressed(this::handleMousePressed);
			setOnMouseReleased(this::handleMouseReleased);
			setOnMouseDragged(this::handleMouseDragged);
			setOnMouseEntered(this::handleMouseEntered);
			setOnMouseExited(this::handleMouseExited);
		}

		private void handleMousePressed(MouseEvent mouseEvent)
		{
			dragDeltaX = getCenterX() - mouseEvent.getX();
			dragDeltaY = getCenterY() - mouseEvent.getY();
			getScene().setCursor(Cursor.MOVE);
		}

		private void handleMouseReleased(MouseEvent mouseEvent)
		{
			getScene().setCursor(Cursor.HAND);
		}

		private void handleMouseDragged(MouseEvent mouseEvent)
		{
			double newX = mouseEvent.getX() + dragDeltaX;
			if (newX > 0 && newX < getScene().getWidth())
			{
				setCenterX(newX);
			}
			double newY = mouseEvent.getY() + dragDeltaY;
			if (newY > 0 && newY < getScene().getHeight())
			{
				setCenterY(newY);
			}
		}

		private void handleMouseEntered(MouseEvent mouseEvent)
		{
			if (!mouseEvent.isPrimaryButtonDown())
			{
				getScene().setCursor(Cursor.HAND);
			}
		}

		private void handleMouseExited(MouseEvent mouseEvent)
		{
			if (!mouseEvent.isPrimaryButtonDown())
			{
				getScene().setCursor(Cursor.DEFAULT);
			}
		}
	}
}