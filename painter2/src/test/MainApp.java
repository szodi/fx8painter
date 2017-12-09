package test;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javafx.application.Application;
import javafx.application.Platform;
import javafx.geometry.Rectangle2D;
import javafx.scene.Scene;
import javafx.scene.SceneAntialiasing;
import javafx.scene.SnapshotParameters;
import javafx.scene.SubScene;
import javafx.scene.canvas.Canvas;
import javafx.scene.control.Accordion;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.control.TitledPane;
import javafx.scene.image.Image;
import javafx.scene.image.WritableImage;
import javafx.scene.input.DragEvent;
import javafx.scene.input.Dragboard;
import javafx.scene.input.TransferMode;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.transform.Scale;
import javafx.stage.FileChooser;
import javafx.stage.Stage;

import drawer.CurveDrawer;
import drawer.TangentDrawer;
import editor.CurvePathAttacher;
import editor.GlobalEditor;
import editor.GridEditor;
import editor.MultipointAdjuster;
import editor.PathEditor;
import editor.PointEditor;
import editor.Rotator;
import editor.SelectionEditor;
import editor.TangentEditor;
import entity.ControlPoint;
import entity.Path;
import image.ImageEditor;
import image.MultiPointImageEditor;
import io.Project;
import mesh.CoonsPatchBuilder;
import mesh.SurfaceMeshView;
import mesh.TunnelBuilder;

public class MainApp extends Application
{
	private static String IMAGE_FILE = "d:\\imagevenue\\Alisa_I\\84593_hegre-art.com_20171102\\031_alisa-soft-daylight-32-10000px.jpg";
	public static List<ControlPoint> controlPoints = new ArrayList<>();
	public static List<ControlPoint> pathControlPoints = new ArrayList<>();
	public static List<Path> paths = new ArrayList<>();
	public static Map<ControlPoint, Path> pathOfControlPoint = new HashMap<>();

	public static Canvas canvas = new Canvas();
	public static ControlPoint actualControlPoint = null;
	public static ControlPoint actualPathControlPoint = null;

	CurveDrawer curveDrawer = new CurveDrawer(canvas);
	TangentDrawer tangentDrawer = new TangentDrawer(canvas, curveDrawer);
	PointEditor pointEditor = new PointEditor(controlPoints, curveDrawer::drawPoints);
	PathEditor pathEditor = new PathEditor(pathControlPoints, curveDrawer::drawPoints);
	MultipointAdjuster multipointAdjuster = new MultipointAdjuster(pathControlPoints, curveDrawer::drawPoints);
	CurvePathAttacher curvePathAttacher = new CurvePathAttacher(controlPoints, pathControlPoints, curveDrawer::drawPoints);
	GridEditor gridEditor = new GridEditor(controlPoints, curveDrawer::drawPoints);
	SelectionEditor selectionEditor = new SelectionEditor(controlPoints, curveDrawer::drawSelectorRectangle);
	Rotator rotator = new Rotator(controlPoints, curveDrawer::drawPoints);
	TangentEditor tangentEditor = new TangentEditor(controlPoints, curveDrawer::drawPoints, tangentDrawer::drawTangent);
	ImageEditor imageEditor = new ImageEditor(new Image(new File(IMAGE_FILE).toURI().toString()));
	MultiPointImageEditor multiPointImageEditor = new MultiPointImageEditor(new Image(new File(IMAGE_FILE).toURI().toString()));

	SurfaceMeshView meshView = new SurfaceMeshView();

	private Stage stage;
	private Scene scene;
	private AnchorPane anchorPane = new AnchorPane();
	GlobalEditor globalEditor = new GlobalEditor(anchorPane);

	@Override
	public void start(Stage primaryStage) throws Exception
	{
		this.stage = primaryStage;

		anchorPane.getChildren().add(multiPointImageEditor);
		anchorPane.getChildren().add(meshView);
		anchorPane.getChildren().add(canvas);

		VBox vbox = new VBox();
		vbox.setMinWidth(200);
		vbox.setMaxHeight(500);
		vbox.getChildren().add(initAccordion());

		SubScene editorScene = new SubScene(anchorPane, 1920, 1080, true, SceneAntialiasing.BALANCED);

		HBox hBox = new HBox(vbox, editorScene);

		scene = new Scene(hBox, 1920, 1080, true, SceneAntialiasing.BALANCED);

		canvas.setOnDragOver(this::mouseDragOver);
		canvas.setOnDragDropped(this::mouseDragDropped);

		canvas.setWidth(5000);
		canvas.setHeight(5000);

		pointEditor.activate(anchorPane);

		primaryStage.setScene(scene);
		// primaryStage.setFullScreen(true);
		// primaryStage.setFullScreenExitKeyCombination(KeyCombination.NO_MATCH);
		primaryStage.show();

	}

	private void mouseDragDropped(final DragEvent event)
	{
		final Dragboard db = event.getDragboard();
		boolean success = false;
		if (db.hasFiles())
		{
			success = true;
			final File file = db.getFiles().get(0);
			Platform.runLater(() -> {
				imageEditor.resetTransforms();
				imageEditor.setImage(new Image(file.toURI().toString()));
			});
		}
		event.setDropCompleted(success);
		event.consume();

	}

	private void mouseDragOver(final DragEvent e)
	{
		final Dragboard db = e.getDragboard();
		final boolean isAccepted = db.getFiles().get(0).getName().toLowerCase().endsWith(".png") || db.getFiles().get(0).getName().toLowerCase().endsWith(".jpeg") || db.getFiles().get(0).getName().toLowerCase().endsWith(".jpg");
		if (db.hasFiles() && isAccepted)
		{
			e.acceptTransferModes(TransferMode.COPY);
		}
		else
		{
			e.consume();
		}
	}

	private Accordion initAccordion()
	{
		Button tbCurveEditor = new Button("Curve");
		tbCurveEditor.setOnAction(event -> {
			pointEditor.activate(anchorPane);
			meshView.setVisible(false);
		});

		Button tbPathEditor = new Button("Path");
		tbPathEditor.setOnAction(event -> {
			pathEditor.activate(anchorPane);
			meshView.setVisible(false);
		});

		Button tbCurvePathEditor = new Button("Attacher");
		tbCurvePathEditor.setOnAction(event -> {
			curvePathAttacher.activate(anchorPane);
			meshView.setVisible(false);
		});

		Button tbCurveTangentEditor = new Button("Curve");
		tbCurveTangentEditor.setOnAction(event -> {
			tangentEditor.setControlPoints(controlPoints);
			tangentEditor.activate(anchorPane);
			meshView.setVisible(false);
		});

		Button tbPathTangentEditor = new Button("Path");
		tbPathTangentEditor.setOnAction(event -> {
			tangentEditor.setControlPoints(pathControlPoints);
			tangentEditor.activate(anchorPane);
			meshView.setVisible(false);
		});

		Button tbMultipointAdjuster = new Button("MultipointAdjuster");
		tbMultipointAdjuster.setOnAction(event -> {
			multipointAdjuster.setControlPoints(pathControlPoints);
			multipointAdjuster.activate(anchorPane);
			meshView.setVisible(false);
		});

		Button tbCurveRotator = new Button("Curve");
		tbCurveRotator.setOnAction(event -> {
			rotator.setControlPoints(controlPoints);
			rotator.setCurveDrawer(curveDrawer::drawPoints);
			rotator.activate(anchorPane);
			meshView.setVisible(false);
		});

		Button tbPathRotator = new Button("Path");
		tbPathRotator.setOnAction(event -> {
			rotator.setControlPoints(pathControlPoints);
			rotator.setCurveDrawer(curveDrawer::drawPoints);
			rotator.activate(anchorPane);
			meshView.setVisible(false);
		});

		Button tbCurveSelector = new Button("Curve");
		tbCurveSelector.setOnAction(event -> {
			selectionEditor.setControlPoints(controlPoints);
			selectionEditor.setRectangleProcessor(curveDrawer::drawSelectorRectangle);
			selectionEditor.activate(anchorPane);
			meshView.setVisible(false);
		});

		Button tbPathSelector = new Button("Path");
		tbPathSelector.setOnAction(event -> {
			selectionEditor.setControlPoints(pathControlPoints);
			selectionEditor.setRectangleProcessor(curveDrawer::drawSelectorRectangle);
			selectionEditor.activate(anchorPane);
			meshView.setVisible(false);
		});

		Button tbImageEditor = new Button("ImageEditor");
		tbImageEditor.setOnAction(event -> imageEditor.activate(anchorPane));

		Button tbMultiPointImageEditor = new Button("MultiPointImageEditor");
		tbMultiPointImageEditor.setOnAction(event -> multiPointImageEditor.activate(anchorPane));

		Button tbSelectionDrawer = new Button("Rectangle");
		tbSelectionDrawer.setOnAction(event -> selectionEditor.activate(anchorPane));

		Button tbGlobalEditor = new Button("GlobalEditor");
		tbGlobalEditor.setOnAction(event -> {
			globalEditor.activate(anchorPane);
			meshView.setVisible(false);
		});

		Button tbCoonsPatch = new Button("Coon's patch");
		tbCoonsPatch.setOnAction(event -> {
			SnapshotParameters sp = new SnapshotParameters();
			sp.setViewport(new Rectangle2D(0, 0, scene.getWidth(), scene.getHeight()));

			WritableImage cropped = imageEditor.snapshot(sp, null);
			meshView.activate(anchorPane, new CoonsPatchBuilder(), cropped);
			meshView.setVisible(true);
		});

		Button tbTunnel = new Button("Tunnel");
		tbTunnel.setOnAction(event -> {
			SnapshotParameters sp = new SnapshotParameters();
			sp.setViewport(new Rectangle2D(0, 0, scene.getWidth(), scene.getHeight()));

			WritableImage cropped = imageEditor.snapshot(sp, null);

			meshView.activate(anchorPane, new TunnelBuilder(), cropped);
			meshView.setVisible(true);
		});

		Button tbLoad = new Button("Load");
		tbLoad.setOnAction(event -> load(stage));

		Button tbSave = new Button("Save");
		tbSave.setOnAction(event -> save(stage));

		GridPane pointEditorPane = new GridPane();
		pointEditorPane.addRow(0, tbCurveEditor);
		pointEditorPane.addRow(1, tbPathEditor);
		pointEditorPane.addRow(2, tbCurvePathEditor);

		GridPane tangentEditorPane = new GridPane();
		tangentEditorPane.addRow(0, tbCurveTangentEditor);
		tangentEditorPane.addRow(1, tbPathTangentEditor);
		tangentEditorPane.addRow(2, tbMultipointAdjuster);

		TextField tfHorizontal = new TextField(String.valueOf(GridEditor.horizontalPointsCount));
		tfHorizontal.setMaxWidth(50);
		TextField tfVertical = new TextField(String.valueOf(GridEditor.verticalPointsCount));
		tfVertical.setMaxWidth(50);
		GridPane gridEditorPane = new GridPane();
		gridEditorPane.add(new Label("Horizontal:"), 0, 0);
		gridEditorPane.add(tfHorizontal, 1, 0);
		gridEditorPane.add(new Label("Vertical:"), 0, 1);
		gridEditorPane.add(tfVertical, 1, 1);

		tfHorizontal.textProperty().addListener((obs, oldText, newText) -> {
			int newHorizontal = Integer.parseInt(newText);
			if (newHorizontal > 1)
			{
				GridEditor.horizontalPointsCount = newHorizontal;
			}
		});
		tfVertical.textProperty().addListener((obs, oldText, newText) -> {
			int newVertical = Integer.parseInt(newText);
			if (newVertical > 1)
			{
				GridEditor.verticalPointsCount = newVertical;
			}
		});

		GridPane rotatorPane = new GridPane();
		rotatorPane.addRow(0, tbCurveRotator);
		rotatorPane.addRow(1, tbPathRotator);

		GridPane selectorPane = new GridPane();
		selectorPane.addRow(0, tbCurveSelector);
		selectorPane.addRow(1, tbPathSelector);

		GridPane meshViewPane = new GridPane();
		meshViewPane.addRow(0, tbCoonsPatch);
		meshViewPane.addRow(1, tbTunnel);

		GridPane loadAndSavePane = new GridPane();
		loadAndSavePane.addRow(0, tbLoad);
		loadAndSavePane.addRow(1, tbSave);

		TitledPane tpPointEditor = new TitledPane("Point", pointEditorPane);

		TitledPane tpTangentEditor = new TitledPane("Tangent", tangentEditorPane);

		TitledPane tpGridEditor = new TitledPane("Grid", gridEditorPane);

		tpGridEditor.expandedProperty().addListener((obs, oldValue, newValue) -> {
			if (newValue)
			{
				gridEditor.activate(anchorPane);
			}
		});

		TitledPane tpSelector = new TitledPane("Selector", selectorPane);

		TitledPane tpRotator = new TitledPane("Rotator", rotatorPane);

		TitledPane tpImageAdjuster = new TitledPane("Image", tbMultiPointImageEditor);

		TitledPane tpMesh = new TitledPane("Mesh", meshViewPane);

		TitledPane tpGlobalEditor = new TitledPane("Global", tbGlobalEditor);

		TitledPane tpLoadAndSave = new TitledPane("Load/Save", loadAndSavePane);

		return new Accordion(tpPointEditor, tpTangentEditor, tpGridEditor, tpSelector, tpRotator, tpImageAdjuster, tpMesh, tpGlobalEditor, tpLoadAndSave);
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
		project.setImageScaleX(imageEditor.getScale().getX());
		project.setImageScaleY(imageEditor.getScale().getY());
		project.setImageX(imageEditor.getX());
		project.setImageY(imageEditor.getY());
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
		Scale scale = imageEditor.getScale();
		scale.setPivotX(scene.getWidth() / 2);
		scale.setPivotY(scene.getHeight() / 2);
		scale.setX(project.getImageScaleX());
		scale.setY(project.getImageScaleY());
		imageEditor.setX(project.getImageX());
		imageEditor.setY(project.getImageY());
		controlPoints.clear();
		controlPoints.addAll(project.getControlPoints());
		curveDrawer.drawPoints(controlPoints);
	}

	public static void main(String[] args)
	{
		launch(args);
	}
}
