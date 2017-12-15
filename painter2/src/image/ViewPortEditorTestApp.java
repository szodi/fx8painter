package image;

import java.io.File;

import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.SceneAntialiasing;
import javafx.scene.image.Image;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Stage;

public class ViewPortEditorTestApp extends Application
{
	private static String IMAGE_FILE = "d:\\grid.png";
	// private static String IMAGE_FILE = "d:\\imagevenue\\Lana_I\\72405_met-art.com_20161021\\118_MetArt_Presenting-Lana_Lana-I_high_0118.jpg";

	// ViewPortEditor viewPortEditor = new ViewPortEditor(new Image("http://www.imgion.com/images/01/Voilet-Flower-.jpg"));
	ViewPortEditor viewPortEditor = new ViewPortEditor(new Image(new File(IMAGE_FILE).toURI().toString()));

	@Override
	public void start(Stage primaryStage) throws Exception
	{
		AnchorPane anchorPane = new AnchorPane(viewPortEditor);
		Scene scene = new Scene(anchorPane, 1920, 1080, true, SceneAntialiasing.BALANCED);

		viewPortEditor.activate(anchorPane);

		primaryStage.setScene(scene);
		primaryStage.show();
	}

	public static void main(String[] args)
	{
		launch(args);
	}
}
