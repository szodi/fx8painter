package drawer;

import javafx.scene.canvas.Canvas;
import javafx.scene.paint.Color;

public class PathDrawer extends CurveDrawer
{
	public PathDrawer(Canvas canvas)
	{
		super(canvas);
		curveSegmentColor = Color.RED;
	}
}
