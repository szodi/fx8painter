package test;

import java.util.ArrayList;
import java.util.List;

import javafx.application.Application;
import javafx.geometry.Point3D;
import javafx.scene.Group;
import javafx.scene.Scene;
import javafx.scene.SceneAntialiasing;
import javafx.scene.canvas.Canvas;
import javafx.scene.control.Button;
import javafx.scene.control.ToolBar;
import javafx.stage.Stage;

import editor.CurveDrawer;
import editor.GridEditor;
import editor.PointEditor;
import editor.Rotator;
import editor.SelectionEditor;
import entity.ControlPoint;

public class MainApp extends Application
{
	public static List<ControlPoint> controlPoints = new ArrayList<>();

	Group group = new Group();
	public static Canvas canvas = new Canvas();
	CurveDrawer curveDrawer = new CurveDrawer(canvas);
	PointEditor pointEditor = new PointEditor(curveDrawer::drawPoints);
	GridEditor gridEditor = new GridEditor(curveDrawer::drawPoints);
	SelectionEditor selectionEditor = new SelectionEditor(curveDrawer::drawSelection);
	Rotator rotator = new Rotator(curveDrawer::drawPoints);

	private Scene scene;

	@Override
	public void start(Stage primaryStage) throws Exception
	{
		scene = new Scene(group, 1920, 1080, true, SceneAntialiasing.BALANCED);

		canvas.setWidth(scene.getWidth());
		canvas.setHeight(scene.getHeight());

		pointEditor.activate(scene);

		group.getChildren().add(canvas);
		group.getChildren().add(initToolbar());
		primaryStage.setScene(scene);
		// primaryStage.setFullScreen(true);
		// primaryStage.setFullScreenExitKeyCombination(KeyCombination.NO_MATCH);
		primaryStage.show();

	}

	private ToolBar initToolbar()
	{
		Button tbCurveEditor = new Button("CurveEditor");
		tbCurveEditor.setOnAction(event -> pointEditor.activate(scene));

		Button tbGridDrawer = new Button("GridDrawer");
		tbGridDrawer.setOnAction(event -> gridEditor.activate(scene));

		Button tbSelectionDrawer = new Button("SelectionDrawer");
		tbSelectionDrawer.setOnAction(event -> selectionEditor.activate(scene));

		Button tbRotator = new Button("Rotator");
		tbRotator.setOnAction(event -> rotator.activate(scene));

		return new ToolBar(tbCurveEditor, tbGridDrawer, tbSelectionDrawer, tbRotator);
	}

	public static void rotateCurve(Point3D rotationAxis, double angle)
	{
		Canvas canvas = new Canvas();
		canvas.setRotationAxis(rotationAxis);
		canvas.setRotate(angle);
		for (ControlPoint controlPoint : controlPoints)
		{
			Point3D rotated = canvas.localToParent(controlPoint.getX(), controlPoint.getY(), controlPoint.getZ());
			controlPoint.setX(rotated.getX());
			controlPoint.setY(rotated.getY());
			controlPoint.setZ(rotated.getZ());
		}
	}

	public static void main(String[] args)
	{
		launch(args);
	}
}
