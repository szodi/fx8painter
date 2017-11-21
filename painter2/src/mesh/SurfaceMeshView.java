package mesh;

import javafx.event.EventHandler;
import javafx.scene.input.MouseEvent;
import javafx.scene.shape.CullFace;
import javafx.scene.shape.Mesh;
import javafx.scene.shape.MeshView;
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

	public SurfaceMeshView(Mesh mesh)
	{
		super(mesh);
		setCullFace(CullFace.NONE);
		getTransforms().add(new Affine());
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
				getTransforms().set(0, r1.createConcatenation(getTransforms().get(0)));

				Rotate r2 = new Rotate(mouseEvent.getSceneY() - mouseOldY, Rotate.X_AXIS);
				getTransforms().set(0, r2.createConcatenation(getTransforms().get(0)));
			}
			mouseOldX = mouseEvent.getSceneX();
			mouseOldY = mouseEvent.getSceneY();
		}
	}
}
