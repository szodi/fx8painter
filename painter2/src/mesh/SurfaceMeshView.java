package mesh;

import javafx.collections.ObservableFloatArray;
import javafx.event.EventHandler;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.scene.image.PixelReader;
import javafx.scene.image.WritableImage;
import javafx.scene.input.MouseEvent;
import javafx.scene.paint.PhongMaterial;
import javafx.scene.shape.CullFace;
import javafx.scene.shape.DrawMode;
import javafx.scene.shape.MeshView;
import javafx.scene.shape.Rectangle;
import javafx.scene.shape.TriangleMesh;
import javafx.scene.transform.Affine;
import javafx.scene.transform.Rotate;

import tools.Tools;

public class SurfaceMeshView extends MeshView implements EventHandler<MouseEvent>
{
	double clickedX;
	double clickedY;

	PhongMaterial material = new PhongMaterial();
	private TriangleMesh mesh;

	double width;
	double height;

	public SurfaceMeshView()
	{
		setCullFace(CullFace.NONE);
		setDrawMode(DrawMode.LINE);
		getTransforms().add(new Affine());

	}

	public void activate(Scene scene, Image image)
	{
		width = scene.getWidth();
		height = scene.getHeight();
		MeshBuilder meshBuilder = new MeshBuilder();
		if (mesh == null)
		{
			mesh = (TriangleMesh)meshBuilder.buildMesh(true);
			// material.setDiffuseColor(Color.AQUAMARINE);
			Image texture = getTextureImageClip(mesh, image);
			material.setDiffuseMap(texture);
			setMaterial(material);
		}
		else
		{
			ObservableFloatArray texCoords = mesh.getTexCoords();
			mesh = (TriangleMesh)meshBuilder.buildMesh(false);
			mesh.getTexCoords().addAll(texCoords);
		}
		super.setMesh(mesh);
		scene.setOnMouseMoved(this);
		scene.setOnMousePressed(this);
		scene.setOnMouseDragged(this);
		scene.setOnMouseReleased(this);

	}

	public Image getTextureImageClip(TriangleMesh mesh, Image image)
	{
		Rectangle textureBounds = Tools.getControlPointBounds(mesh.getPoints());
		PixelReader reader = image.getPixelReader();
		WritableImage cropped = new WritableImage(reader, (int)textureBounds.getX(), (int)textureBounds.getY(), (int)textureBounds.getWidth(), (int)textureBounds.getHeight());
		return cropped;
	}

	@Override
	public void handle(MouseEvent mouseEvent)
	{
		if (mouseEvent.getEventType() == MouseEvent.MOUSE_DRAGGED)
		{
			Rotate r1 = new Rotate(-mouseEvent.getSceneX() + clickedX, width / 2, height / 2, 0.0, Rotate.Y_AXIS);
			getTransforms().set(0, r1.createConcatenation(getTransforms().get(0)));

			Rotate r2 = new Rotate(mouseEvent.getSceneY() - clickedY, width / 2, height / 2, 0.0, Rotate.X_AXIS);
			getTransforms().set(0, r2.createConcatenation(getTransforms().get(0)));
		}
		clickedX = mouseEvent.getSceneX();
		clickedY = mouseEvent.getSceneY();
	}
}
