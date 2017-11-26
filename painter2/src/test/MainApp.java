package test;

import java.io.File;
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
import javafx.scene.image.Image;
import javafx.scene.image.PixelReader;
import javafx.scene.image.WritableImage;
import javafx.stage.Stage;

import editor.CurveDrawer;
import editor.GridEditor;
import editor.PointEditor;
import editor.Rotator;
import editor.SelectionEditor;
import editor.TangentDrawer;
import editor.TangentEditor;
import entity.ControlPoint;
import image.ImageAdjusterView;
import mesh.SurfaceMeshView;

public class MainApp extends Application
{
	public static List<ControlPoint> controlPoints = new ArrayList<>();

	public static Canvas canvas = new Canvas();
	public static ControlPoint actualControlPoint = null;

	Group group = new Group();
	CurveDrawer curveDrawer = new CurveDrawer(canvas);
	TangentDrawer tangentDrawer = new TangentDrawer(canvas);
	PointEditor pointEditor = new PointEditor(curveDrawer::drawPoints);
	GridEditor gridEditor = new GridEditor(curveDrawer::drawPoints);
	SelectionEditor selectionEditor = new SelectionEditor(curveDrawer::drawSelection);
	Rotator rotator = new Rotator(curveDrawer::drawPoints);
	TangentEditor tangentEditor = new TangentEditor(curveDrawer::drawPoints, tangentDrawer::drawTangent);
	// ImageAdjusterView imageAdjusterView = new ImageAdjusterView(new Image(new File("").toURI().toString()));
	ImageAdjusterView imageAdjusterView = new ImageAdjusterView(new Image(new File("d:\\imagevenue\\Alisa_I\\84593_hegre-art.com_20171102\\031_alisa-soft-daylight-32-10000px.jpg").toURI().toString()));

	SurfaceMeshView meshView = new SurfaceMeshView();

	private Scene scene;

	@Override
	public void start(Stage primaryStage) throws Exception
	{
		scene = new Scene(group, 1920, 1080, true, SceneAntialiasing.BALANCED);

		canvas.setWidth(scene.getWidth());
		canvas.setHeight(scene.getHeight());

		pointEditor.activate(scene);

		imageAdjusterView.setTranslateZ(800);
		// scene.setOnKeyPressed(event -> {
		// if (event.getCode() == KeyCode.DELETE)
		// {
		// controlPoints = controlPoints.stream().filter(cp -> cp.isSelected()).collect(Collectors.toList());
		// }
		// });

		group.getChildren().add(imageAdjusterView);
		group.getChildren().add(meshView);
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
		tbCurveEditor.setOnAction(event -> {
			pointEditor.activate(scene);
			meshView.setVisible(false);
		});

		Button tbImageAdjuster = new Button("ImageAdjuster");
		tbImageAdjuster.setOnAction(event -> {
			scene.setOnMouseDragged(imageAdjusterView);
			scene.setOnMousePressed(imageAdjusterView);
			scene.setOnScroll(imageAdjusterView);
		});

		Button tbGridDrawer = new Button("GridDrawer");
		tbGridDrawer.setOnAction(event -> gridEditor.activate(scene));

		Button tbSelectionDrawer = new Button("SelectionDrawer");
		tbSelectionDrawer.setOnAction(event -> selectionEditor.activate(scene));

		Button tbRotator = new Button("Rotator");
		tbRotator.setOnAction(event -> rotator.activate(scene));

		Button tbTangentEditor = new Button("TangentEditor");
		tbTangentEditor.setOnAction(event -> {
			tangentEditor.activate(scene);
			meshView.setVisible(false);
		});

		Button tbMeshView = new Button("MeshView");
		tbMeshView.setOnAction(event -> {
			PixelReader reader = imageAdjusterView.getImage().getPixelReader();
			WritableImage cropped = new WritableImage(reader, (int)-imageAdjusterView.getX(), (int)-imageAdjusterView.getY(), (int)scene.getWidth(), (int)scene.getHeight());
			meshView.activate(scene, cropped);
			// imageAdjusterView.setVisible(false);
			meshView.setVisible(true);
		});

		return new ToolBar(tbCurveEditor, tbImageAdjuster, tbGridDrawer, tbSelectionDrawer, tbRotator, tbTangentEditor, tbMeshView);
	}

	public static ControlPoint getControlPointAt(double x, double y, double z)
	{
		for (ControlPoint controlPoint : MainApp.controlPoints)
		{
			Point3D point = MainApp.canvas.localToParent(controlPoint.getX(), controlPoint.getY(), controlPoint.getZ());
			if (Math.abs(point.getX() - x) <= (CurveDrawer.DOT_SIZE / 2) && Math.abs(point.getY() - y) <= (CurveDrawer.DOT_SIZE / 2))
			{
				return controlPoint;
			}
		}
		return null;
	}

	public static void main(String[] args)
	{
		launch(args);
	}
}
