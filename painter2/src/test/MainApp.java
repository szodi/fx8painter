package test;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import javafx.application.Application;
import javafx.geometry.Rectangle2D;
import javafx.scene.Group;
import javafx.scene.Scene;
import javafx.scene.SceneAntialiasing;
import javafx.scene.SnapshotParameters;
import javafx.scene.canvas.Canvas;
import javafx.scene.control.Button;
import javafx.scene.control.ToolBar;
import javafx.scene.image.Image;
import javafx.scene.image.WritableImage;
import javafx.scene.transform.Scale;
import javafx.stage.FileChooser;
import javafx.stage.Stage;

import editor.CurveDrawer;
import editor.GridEditor;
import editor.PathEditor;
import editor.PointEditor;
import editor.Rotator;
import editor.SelectionEditor;
import editor.TangentDrawer;
import editor.TangentEditor;
import entity.ControlPoint;
import image.ImageAdjusterView;
import io.Project;
import mesh.SurfaceMeshView;

public class MainApp extends Application
{
	private static String IMAGE_FILE = "d:\\imagevenue\\Alisa_I\\84593_hegre-art.com_20171102\\031_alisa-soft-daylight-32-10000px.jpg";
	public static List<ControlPoint> controlPoints = new ArrayList<>();

	public static Canvas canvas = new Canvas();
	public static ControlPoint actualControlPoint = null;

	Group group = new Group();
	CurveDrawer curveDrawer = new CurveDrawer(canvas);
	TangentDrawer tangentDrawer = new TangentDrawer(canvas);
	PointEditor pointEditor = new PointEditor(curveDrawer::drawPoints);
	PathEditor pathEditor = new PathEditor(curveDrawer::drawPoints);
	GridEditor gridEditor = new GridEditor(curveDrawer::drawPoints);
	SelectionEditor selectionEditor = new SelectionEditor(curveDrawer::drawSelection);
	Rotator rotator = new Rotator(curveDrawer::drawPoints);
	TangentEditor tangentEditor = new TangentEditor(curveDrawer::drawPoints, tangentDrawer::drawTangent);
	ImageAdjusterView imageAdjusterView = new ImageAdjusterView(new Image(new File(IMAGE_FILE).toURI().toString()));

	SurfaceMeshView meshView = new SurfaceMeshView();

	private Stage stage;
	private Scene scene;

	@Override
	public void start(Stage primaryStage) throws Exception
	{
		this.stage = primaryStage;
		scene = new Scene(group, 1920, 1080, true, SceneAntialiasing.BALANCED);

		canvas.setWidth(scene.getWidth());
		canvas.setHeight(scene.getHeight());

		pointEditor.activate(scene);

		imageAdjusterView.setTranslateZ(800);

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
			scene.setOnMouseMoved(imageAdjusterView);
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

		Button tbPathEditor = new Button("PathEditor");
		tbPathEditor.setOnAction(event -> {
			pathEditor.activate(scene);
			meshView.setVisible(false);
		});

		Button tbMeshView = new Button("MeshView");
		tbMeshView.setOnAction(event -> {
			SnapshotParameters sp = new SnapshotParameters();
			sp.setViewport(new Rectangle2D(0, 0, scene.getWidth(), scene.getHeight()));

			WritableImage cropped = imageAdjusterView.snapshot(sp, null);
			meshView.activate(scene, cropped);
			meshView.setVisible(true);
		});

		Button tbLoad = new Button("Load");
		tbLoad.setOnAction(event -> load(stage));

		Button tbSave = new Button("Save");
		tbSave.setOnAction(event -> save(stage));

		return new ToolBar(tbCurveEditor, tbImageAdjuster, tbGridDrawer, tbSelectionDrawer, tbRotator, tbTangentEditor, tbPathEditor, tbMeshView, tbLoad, tbSave);
	}

	private void save(Stage stage)
	{
		FileChooser fileChooser = new FileChooser();
		fileChooser.setTitle("Load Project");

		fileChooser.setInitialDirectory(new File("D:\\temp3"));
		// fileChooser.setInitialDirectory(new File(System.getProperty("user.home")));
		fileChooser.getExtensionFilters().addAll(new FileChooser.ExtensionFilter("Painter Project (.prj)", "*.prj"));
		File file = fileChooser.showSaveDialog(stage);
		if (file == null)
		{
			return;
		}
		Project project = new Project();
		project.setImage(IMAGE_FILE);
		project.setControlPoints(controlPoints);
		project.setImageScaleX(imageAdjusterView.getScale().getX());
		project.setImageScaleY(imageAdjusterView.getScale().getY());
		project.setImageX(imageAdjusterView.getX());
		project.setImageY(imageAdjusterView.getY());
		project.save(file);
	}

	private void load(Stage stage)
	{
		FileChooser fileChooser = new FileChooser();
		fileChooser.setTitle("Load Project");

		fileChooser.setInitialDirectory(new File("D:\\temp3"));
		// fileChooser.setInitialDirectory(new File(System.getProperty("user.home")));
		fileChooser.getExtensionFilters().addAll(new FileChooser.ExtensionFilter("Painter Project (.prj)", "*.prj"));
		File file = fileChooser.showOpenDialog(stage);
		if (file == null)
		{
			return;
		}
		Project project = new Project();
		project.load(file);
		IMAGE_FILE = project.getImage();
		Scale scale = imageAdjusterView.getScale();
		scale.setPivotX(scene.getWidth() / 2);
		scale.setPivotY(scene.getHeight() / 2);
		scale.setX(project.getImageScaleX());
		scale.setY(project.getImageScaleY());
		imageAdjusterView.setX(project.getImageX());
		imageAdjusterView.setY(project.getImageY());
		controlPoints.clear();
		controlPoints.addAll(project.getControlPoints());
		curveDrawer.drawPoints(controlPoints);
	}

	public static void main(String[] args)
	{
		launch(args);
	}
}
