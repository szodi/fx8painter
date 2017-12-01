package mesh;

import javafx.geometry.Point2D;

import drawer.CurveDrawer;
import entity.ControlPoint;
import entity.MutablePoint3D;
import tools.Tools;

public class CoonsPatchCreator
{
	ControlPoint leftTop;
	ControlPoint rightTop;
	ControlPoint leftBottom;
	ControlPoint rightBottom;

	public CoonsPatchCreator(ControlPoint leftTop, ControlPoint rightTop, ControlPoint leftBottom, ControlPoint rightBottom)
	{
		this.leftTop = leftTop;
		this.rightTop = rightTop;
		this.leftBottom = leftBottom;
		this.rightBottom = rightBottom;
	}

	private MutablePoint3D c0(double s)
	{
		return Tools.getBezierPoint(leftTop, leftTop.getTangent(rightTop), rightTop.getTangent(leftTop), rightTop, s);
	}

	private MutablePoint3D c1(double s)
	{
		return Tools.getBezierPoint(leftBottom, leftBottom.getTangent(rightBottom), rightBottom.getTangent(leftBottom), rightBottom, s);
	}

	private MutablePoint3D d0(double t)
	{
		return Tools.getBezierPoint(leftTop, leftTop.getTangent(leftBottom), leftBottom.getTangent(leftTop), leftBottom, t);
	}

	private MutablePoint3D d1(double t)
	{
		return Tools.getBezierPoint(rightTop, rightTop.getTangent(rightBottom), rightBottom.getTangent(rightTop), rightBottom, t);
	}

	/**
	 * Coons patch
	 * 
	 * Bilinear blending[edit] Given four space curves c0(s), c1(s), d0(t), d1(t) which meet at four corners c0(0) = d0(0), c0(1) = d1(0), c1(0) = d0(1), c1(1) = d1(1); linear interpolation can be used to interpolate between c0 and c1, that is
	 *
	 * Lc(s,t) = (1-t)*c0(s) + t*c1(s)
	 *
	 * and between d0, d1
	 *
	 * Ld(s,t) = (1-s)*d0(t) + s*d1(t)
	 * 
	 * producing two ruled surfaces defined on the unit square. The bilinear interpolation on the four corner points is another surface
	 * 
	 * B(s,t) = c0(0)*(1-s)*(1-t) + c0(1)*s*(1-t) + c1(0)*(1-s)*t + c1(1)*s*t.
	 * 
	 * A bilinearly blended Coons patch is the surface
	 * 
	 * Lc(s,t) + Ld(s,t) - B(s,t)
	 * 
	 * @return
	 */
	private MutablePoint3D getCoonsPoint(double s, double t)
	{
		double d_Lc_x = (1 - t) * c0(s).getX() + t * c1(s).getX();
		double d_Lc_y = (1 - t) * c0(s).getY() + t * c1(s).getY();
		double d_Lc_z = (1 - t) * c0(s).getZ() + t * c1(s).getZ();

		double d_Ld_x = (1 - s) * d0(t).getX() + s * d1(t).getX();
		double d_Ld_y = (1 - s) * d0(t).getY() + s * d1(t).getY();
		double d_Ld_z = (1 - s) * d0(t).getZ() + s * d1(t).getZ();

		double d_B_x = c0(0.0).getX() * (1 - s) * (1 - t) + c0(1.0).getX() * s * (1 - t) + c1(0.0).getX() * (1 - s) * t + c1(1.0).getX() * s * t;
		double d_B_y = c0(0.0).getY() * (1 - s) * (1 - t) + c0(1.0).getY() * s * (1 - t) + c1(0.0).getY() * (1 - s) * t + c1(1.0).getY() * s * t;
		double d_B_z = c0(0.0).getZ() * (1 - s) * (1 - t) + c0(1.0).getZ() * s * (1 - t) + c1(0.0).getZ() * (1 - s) * t + c1(1.0).getZ() * s * t;
		return new MutablePoint3D(d_Lc_x + d_Ld_x - d_B_x, d_Lc_y + d_Ld_y - d_B_y, d_Lc_z + d_Ld_z - d_B_z);
	}

	public float[] createPoints()
	{
		int horizontalSteps = (int)(1.0 / CurveDrawer.smoothness);
		int verticalSteps = (int)(1.0 / CurveDrawer.smoothness);
		float[] points = new float[(horizontalSteps + 1) * (verticalSteps + 1) * 3];
		int n = 0;
		for (int y = 0; y <= verticalSteps; y++)
		{
			for (int x = 0; x <= horizontalSteps; x++)
			{
				double u = (double)x / (double)horizontalSteps;
				double v = (double)y / (double)verticalSteps;
				MutablePoint3D point = getCoonsPoint(u, v);
				points[n++] = (float)point.getX();
				points[n++] = (float)point.getY();
				points[n++] = (float)point.getZ();
			}
		}
		return points;
	}

	public int[] createFaces()
	{
		int horizontalSteps = (int)(1.0 / CurveDrawer.smoothness);
		int verticalSteps = (int)(1.0 / CurveDrawer.smoothness);
		int[] faces = new int[horizontalSteps * verticalSteps * 12];
		int n = 0;
		for (int y = 0; y < verticalSteps; y++)
		{
			for (int x = 0; x < horizontalSteps; x++)
			{
				int[] faceIndices = {0, horizontalSteps + 1, horizontalSteps + 2, 0, horizontalSteps + 2, 1};
				int faceIndex = y * (horizontalSteps + 1) + x;
				for (int k = 0; k < faceIndices.length; k++)
				{
					faces[n++] = faceIndex + faceIndices[k];
					faces[n++] = faceIndex + faceIndices[k];
				}
			}
		}
		return faces;
	}

	public static Point2D toPoint2D(MutablePoint3D point)
	{
		return new Point2D(point.getX(), point.getY());
	}
}
