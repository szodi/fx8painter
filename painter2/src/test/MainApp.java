package test;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

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
import javafx.scene.control.CheckBox;
import javafx.scene.control.TitledPane;
import javafx.scene.control.ToolBar;
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
import editor.GlobalEditor;
import editor.GridEditor;
import editor.PathEditor;
import editor.PointEditor;
import editor.Rotator;
import editor.SelectionEditor;
import editor.TangentEditor;
import entity.ControlPoint;
import image.ImageEditor;
import io.Project;
import mesh.SurfaceMeshView;

public class MainApp extends Application
{
	private static String IMAGE_FILE = "d:\\imagevenue\\Alisa_I\\84593_hegre-art.com_20171102\\031_alisa-soft-daylight-32-10000px.jpg";
	public static List<ControlPoint> controlPoints = new ArrayList<>();
	public static List<ControlPoint> pathControlPoints = new ArrayList<>();

	public static Canvas canvas = new Canvas();
	public static ControlPoint actualControlPoint = null;

	CurveDrawer curveDrawer = new CurveDrawer(canvas);
	TangentDrawer tangentDrawer = new TangentDrawer(canvas, curveDrawer);
	PointEditor pointEditor = new PointEditor(controlPoints, curveDrawer::drawPoints);
	PathEditor pathEditor = new PathEditor(pathControlPoints, curveDrawer::drawPoints);
	GridEditor gridEditor = new GridEditor(controlPoints, curveDrawer::drawPoints);
	SelectionEditor selectionEditor = new SelectionEditor(controlPoints, curveDrawer::drawSelectorRectangle);
	Rotator rotator = new Rotator(controlPoints, curveDrawer::drawPoints);
	TangentEditor tangentEditor = new TangentEditor(controlPoints, curveDrawer::drawPoints, tangentDrawer::drawTangent);
	ImageEditor imageEditor = new ImageEditor(new Image(new File(IMAGE_FILE).toURI().toString()));

	SurfaceMeshView meshView = new SurfaceMeshView();

	private Stage stage;
	private Scene scene;
	private AnchorPane anchorPane = new AnchorPane();
	GlobalEditor globalEditor = new GlobalEditor(anchorPane);

	@Override
	public void start(Stage primaryStage) throws Exception
	{
		this.stage = primaryStage;

		anchorPane.getChildren().add(imageEditor);
		anchorPane.getChildren().add(meshView);
		anchorPane.getChildren().add(canvas);

		VBox vbox = new VBox();
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
		Button tbCurveEditor = new Button("CurveEditor");
		tbCurveEditor.setOnAction(event -> {
			pointEditor.activate(anchorPane);
			meshView.setVisible(false);
		});

		Button tbImageAdjuster = new Button("ImageAdjuster");
		tbImageAdjuster.setOnAction(event -> imageEditor.activate(anchorPane));

		Button tbGridDrawer = new Button("GridDrawer");
		tbGridDrawer.setOnAction(event -> gridEditor.activate(anchorPane));

		Button tbSelectionDrawer = new Button("SelectionDrawer");
		tbSelectionDrawer.setOnAction(event -> selectionEditor.activate(anchorPane));

		Button tbRotator = new Button("Rotator");
		tbRotator.setOnAction(event -> rotator.activate(anchorPane));

		Button tbTangentEditor = new Button("TangentEditor");
		tbTangentEditor.setOnAction(event -> {
			tangentEditor.activate(anchorPane);
			meshView.setVisible(false);
		});

		Button tbPathEditor = new Button("PathEditor");
		tbPathEditor.setOnAction(event -> {
			pathEditor.activate(anchorPane);
			meshView.setVisible(false);
		});

		Button tbGlobalEditor = new Button("GlobalEditor");
		tbGlobalEditor.setOnAction(event -> {
			globalEditor.activate(anchorPane);
			meshView.setVisible(false);
		});

		Button tbMeshView = new Button("MeshView");
		tbMeshView.setOnAction(event -> {
			SnapshotParameters sp = new SnapshotParameters();
			sp.setViewport(new Rectangle2D(0, 0, scene.getWidth(), scene.getHeight()));

			WritableImage cropped = imageEditor.snapshot(sp, null);
			meshView.activate(anchorPane, cropped);
			meshView.setVisible(true);
		});

		Button tbLoad = new Button("Load");
		tbLoad.setOnAction(event -> load(stage));

		Button tbSave = new Button("Save");
		tbSave.setOnAction(event -> save(stage));

		CheckBox checkBox = new CheckBox();
		checkBox.setText("Edit Path");
		checkBox.setOnMouseClicked(event -> tangentEditor.setControlPoints(checkBox.isSelected() ? pathControlPoints : controlPoints));
		tbSave.setOnAction(event -> save(stage));

		GridPane gridPane = new GridPane();
		gridPane.addRow(0, tbCurveEditor);
		gridPane.addRow(1, tbGridDrawer);
		gridPane.addRow(2, tbSelectionDrawer);
		gridPane.addRow(3, tbRotator);
		gridPane.addRow(4, tbTangentEditor);
		gridPane.addRow(5, tbPathEditor);
		gridPane.addRow(6, tbGlobalEditor);

		TitledPane tpEditor = new TitledPane("Editor", gridPane);

		TitledPane tpImageAdjuster = new TitledPane("ImageAdjuster", tbImageAdjuster);

		TitledPane tpMeshView = new TitledPane("MeshView", tbMeshView);
		TitledPane tpLoad = new TitledPane("Load", tbLoad);
		TitledPane tpSave = new TitledPane("Save", tbSave);
		TitledPane tpCheckBox = new TitledPane("checkBox", checkBox);
		return new Accordion(tpEditor, tpImageAdjuster, tpMeshView, tpLoad, tpSave, tpCheckBox);
	}

	private ToolBar initToolbar()
	{
		Button tbCurveEditor = new Button("CurveEditor");
		tbCurveEditor.setOnAction(event -> {
			pointEditor.activate(anchorPane);
			meshView.setVisible(false);
		});

		Button tbImageAdjuster = new Button("ImageAdjuster");
		tbImageAdjuster.setOnAction(event -> imageEditor.activate(anchorPane));

		Button tbGridDrawer = new Button("GridDrawer");
		tbGridDrawer.setOnAction(event -> gridEditor.activate(anchorPane));

		Button tbSelectionDrawer = new Button("SelectionDrawer");
		tbSelectionDrawer.setOnAction(event -> selectionEditor.activate(anchorPane));

		Button tbRotator = new Button("Rotator");
		tbRotator.setOnAction(event -> rotator.activate(anchorPane));

		Button tbTangentEditor = new Button("TangentEditor");
		tbTangentEditor.setOnAction(event -> {
			tangentEditor.activate(anchorPane);
			meshView.setVisible(false);
		});

		Button tbPathEditor = new Button("PathEditor");
		tbPathEditor.setOnAction(event -> {
			pathEditor.activate(anchorPane);
			meshView.setVisible(false);
		});

		Button tbMeshView = new Button("MeshView");
		tbMeshView.setOnAction(event -> {
			SnapshotParameters sp = new SnapshotParameters();
			sp.setViewport(new Rectangle2D(0, 0, scene.getWidth(), scene.getHeight()));

			WritableImage cropped = imageEditor.snapshot(sp, null);
			meshView.activate(anchorPane, cropped);
			meshView.setVisible(true);
		});

		Button tbLoad = new Button("Load");
		tbLoad.setOnAction(event -> load(stage));

		Button tbSave = new Button("Save");
		tbSave.setOnAction(event -> save(stage));

		CheckBox checkBox = new CheckBox();
		checkBox.setText("Edit Path");
		checkBox.setOnMouseClicked(event -> tangentEditor.setControlPoints(checkBox.isSelected() ? pathControlPoints : controlPoints));
		tbSave.setOnAction(event -> save(stage));

		return new ToolBar(tbCurveEditor, tbImageAdjuster, tbGridDrawer, tbSelectionDrawer, tbRotator, tbTangentEditor, tbPathEditor, tbMeshView, tbLoad, tbSave, checkBox);
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
