package image;

import java.io.File;

import javafx.application.Application;
import javafx.scene.Group;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.scene.input.KeyCombination;
import javafx.stage.Stage;

public class ImageAdjuster extends Application
{
	private static final String IMAGEPATH = new File("d:\\pic\\Ausztria\\DSCN7823.JPG").toURI().toString();

	Group group = new Group();
	Scene scene;
	Image image = new Image(IMAGEPATH);
	ImageAdjusterView imageAdjusterView = new ImageAdjusterView(image);

	@Override
	public void start(Stage primaryStage) throws Exception
	{
		group.getChildren().add(imageAdjusterView);
		scene = new Scene(group);

		primaryStage.setScene(scene);
		primaryStage.setFullScreen(true);
		primaryStage.setFullScreenExitKeyCombination(KeyCombination.NO_MATCH);
		primaryStage.show();
	}

	public static void main(String[] args)
	{
		launch(args);
	}
}
