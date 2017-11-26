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
import javafx.scene.shape.MeshView;
import javafx.scene.shape.Rectangle;
import javafx.scene.shape.TriangleMesh;
import javafx.scene.transform.Affine;
import javafx.scene.transform.Rotate;

public class SurfaceMeshView extends MeshView implements EventHandler<MouseEvent>
{
	double clickedX = 0.0;
	double clickedY = 0.0;
	double rx = 0.0;
	double ry = 0.0;

	double mouseOldX;
	double mouseOldY;

	PhongMaterial material = new PhongMaterial();
	private TriangleMesh mesh;

	public SurfaceMeshView()
	{
		setCullFace(CullFace.NONE);
		// setDrawMode(DrawMode.LINE);
		getTransforms().add(new Affine());

	}

	public void activate(Scene scene, Image image)
	{
		MeshBuilder meshBuilder = new MeshBuilder();
		if (mesh == null)
		{
			mesh = (TriangleMesh)meshBuilder.buildMesh(true);
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
		Rectangle textureBounds = MeshBuilder.getControlPointBounds(mesh.getPoints());
		PixelReader reader = image.getPixelReader();
		WritableImage cropped = new WritableImage(reader, (int)textureBounds.getX(), (int)textureBounds.getY(), (int)textureBounds.getWidth(), (int)textureBounds.getHeight());
		return cropped;
	}

	@Override
	public void handle(MouseEvent mouseEvent)
	{
		if (mouseEvent.getEventType() == MouseEvent.MOUSE_PRESSED)
		{
			mouseOldX = mouseEvent.getSceneX();
			mouseOldY = mouseEvent.getSceneY();
		}
		else if (mouseEvent.getEventType() == MouseEvent.MOUSE_DRAGGED)
		{
			if (mouseEvent.isPrimaryButtonDown())
			{
				Rotate r1 = new Rotate(-mouseEvent.getSceneX() + mouseOldX, Rotate.Y_AXIS);
				r1.setPivotX(960);
				r1.setPivotY(540);
				r1.setPivotZ(0);
				getTransforms().set(0, r1.createConcatenation(getTransforms().get(0)));

				Rotate r2 = new Rotate(mouseEvent.getSceneY() - mouseOldY, Rotate.X_AXIS);
				r2.setPivotX(960);
				r2.setPivotY(540);
				r2.setPivotZ(0);
				getTransforms().set(0, r2.createConcatenation(getTransforms().get(0)));
			}
			mouseOldX = mouseEvent.getSceneX();
			mouseOldY = mouseEvent.getSceneY();
		}
	}
}
