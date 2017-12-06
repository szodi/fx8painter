package mesh;

import javafx.collections.ObservableFloatArray;
import javafx.event.EventHandler;
import javafx.geometry.Bounds;
import javafx.scene.Node;
import javafx.scene.image.Image;
import javafx.scene.image.PixelReader;
import javafx.scene.image.WritableImage;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.MouseEvent;
import javafx.scene.paint.PhongMaterial;
import javafx.scene.shape.CullFace;
import javafx.scene.shape.MeshView;
import javafx.scene.shape.Rectangle;
import javafx.scene.shape.TriangleMesh;
import javafx.scene.transform.Rotate;

import tools.Tools;

public class SurfaceMeshView extends MeshView implements EventHandler<MouseEvent>
{
	double clickedX;
	double clickedY;

	PhongMaterial material = new PhongMaterial();
	private TriangleMesh mesh;

	private Rotate rotate1 = new Rotate(0, 960, 540, 100.0, Rotate.Y_AXIS);
	private Rotate rotate2 = new Rotate(0, 960, 540, 100.0, Rotate.X_AXIS);

	public SurfaceMeshView()
	{
		setCullFace(CullFace.NONE);
		// setDrawMode(DrawMode.LINE);
		getTransforms().add(rotate1);
		getTransforms().add(rotate2);

	}

	public void activate(Node node, MeshBuilder meshBuilder, Image image)
	{
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
		node.setOnMouseMoved(this);
		node.setOnMousePressed(this);
		node.setOnMouseDragged(this);
		node.setOnMouseReleased(this);
		node.getScene().setOnKeyPressed(this::handleKeyPressed);

		Bounds bounds = getLayoutBounds();
		rotate1.setPivotX(bounds.getMinX() + bounds.getWidth() / 2);
		rotate1.setPivotY(bounds.getMinY() + bounds.getHeight() / 2);
		rotate2.setPivotX(bounds.getMinX() + bounds.getWidth() / 2);
		rotate2.setPivotY(bounds.getMinY() + bounds.getHeight() / 2);
		setTranslateZ(-500);
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
		if (mouseEvent.getEventType() == MouseEvent.MOUSE_PRESSED)
		{
			handlePrimaryMousePressed(mouseEvent);
		}
		if (mouseEvent.getEventType() == MouseEvent.MOUSE_DRAGGED)
		{
			handlePrimaryMouseDragged(mouseEvent);
		}
	}

	public void handlePrimaryMousePressed(MouseEvent event)
	{
		clickedX = event.getSceneX();
		clickedY = event.getSceneY();
	}

	public void handlePrimaryMouseDragged(MouseEvent event)
	{
		rotate1.setAngle(rotate1.getAngle() + clickedX - event.getSceneX());
		rotate2.setAngle(rotate2.getAngle() + event.getSceneY() - clickedY);
		clickedX = event.getSceneX();
		clickedY = event.getSceneY();
	}

	protected void handleKeyPressed(KeyEvent event)
	{
		System.out.println("SurfaceMeshView.handleKeyPressed()");
		if (event.getCode() == KeyCode.X)
		{
			rotate2.setAxis(Rotate.X_AXIS);
			getTransforms().clear();
			getTransforms().add(rotate2);
		}
		else if (event.getCode() == KeyCode.Y)
		{
			rotate1.setAxis(Rotate.Y_AXIS);
			getTransforms().clear();
			getTransforms().add(rotate1);
		}
		else if (event.getCode() == KeyCode.Z)
		{
			rotate1.setAxis(Rotate.Z_AXIS);
			getTransforms().clear();
			getTransforms().add(rotate1);
		}
		else if (event.getCode() == KeyCode.ESCAPE)
		{
			rotate1.setAxis(Rotate.Y_AXIS);
			rotate2.setAxis(Rotate.X_AXIS);
			getTransforms().clear();
			getTransforms().add(rotate1);
			getTransforms().add(rotate2);
		}
	}
}
