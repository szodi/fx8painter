package mesh;

import javafx.collections.ObservableFloatArray;
import javafx.event.EventHandler;
import javafx.geometry.Bounds;
import javafx.scene.Node;
import javafx.scene.image.Image;
import javafx.scene.image.PixelReader;
import javafx.scene.image.WritableImage;
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

	public void activate(Node node, Image image)
	{
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
		node.setOnMouseMoved(this);
		node.setOnMousePressed(this);
		node.setOnMouseDragged(this);
		node.setOnMouseReleased(this);

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
		if (mouseEvent.getEventType() == MouseEvent.MOUSE_DRAGGED)
		{
			rotate1.setAngle(-mouseEvent.getSceneX() + clickedX);
			rotate2.setAngle(mouseEvent.getSceneY() - clickedY);
		}
	}
}
