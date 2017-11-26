package mesh;

import javafx.event.EventHandler;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.scene.input.MouseEvent;
import javafx.scene.paint.PhongMaterial;
import javafx.scene.shape.CullFace;
import javafx.scene.shape.MeshView;
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

	public SurfaceMeshView()
	{
		setCullFace(CullFace.NONE);
		// setDrawMode(DrawMode.LINE);
		getTransforms().add(new Affine());

	}

	public void activate(Scene scene, Image image)
	{
		MeshBuilder meshBuilder = new MeshBuilder();
		TriangleMesh mesh = (TriangleMesh)meshBuilder.buildMesh();
		super.setMesh(mesh);
		scene.setOnMouseMoved(this);
		scene.setOnMousePressed(this);
		scene.setOnMouseDragged(this);
		scene.setOnMouseReleased(this);
		Image texture = meshBuilder.getTextureImageClip(mesh, image);

		material.setDiffuseMap(texture);
		setMaterial(material);
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
