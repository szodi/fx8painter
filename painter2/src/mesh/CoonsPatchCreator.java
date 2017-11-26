package mesh;

import java.util.ArrayList;
import java.util.List;

import javafx.geometry.Point2D;
import javafx.scene.shape.TriangleMesh;

import editor.CurveDrawer;
import entity.ControlPoint;
import entity.MutablePoint3D;

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
		return getBezierPoint(leftTop, leftTop.getTangent(rightTop), rightTop.getTangent(leftTop), rightTop, s);
	}

	private MutablePoint3D c1(double s)
	{
		return getBezierPoint(leftBottom, leftBottom.getTangent(rightBottom), rightBottom.getTangent(leftBottom), rightBottom, s);
	}

	private MutablePoint3D d0(double t)
	{
		return getBezierPoint(leftTop, leftTop.getTangent(leftBottom), leftBottom.getTangent(leftTop), leftBottom, t);
	}

	private MutablePoint3D d1(double t)
	{
		return getBezierPoint(rightTop, rightTop.getTangent(rightBottom), rightBottom.getTangent(rightTop), rightBottom, t);
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

	public static MutablePoint3D getBezierPoint(MutablePoint3D point1, MutablePoint3D point2, MutablePoint3D point3, MutablePoint3D point4, double t)
	{
		double x = (1 - t) * (1 - t) * (1 - t) * point1.getX() + 3 * t * (1 - t) * (1 - t) * (point1.getX() + point2.getX()) + 3 * t * t * (1 - t) * (point4.getX() + point3.getX()) + t * t * t * point4.getX();
		double y = (1 - t) * (1 - t) * (1 - t) * point1.getY() + 3 * t * (1 - t) * (1 - t) * (point1.getY() + point2.getY()) + 3 * t * t * (1 - t) * (point4.getY() + point3.getY()) + t * t * t * point4.getY();
		double z = (1 - t) * (1 - t) * (1 - t) * point1.getZ() + 3 * t * (1 - t) * (1 - t) * (point1.getZ() + point2.getZ()) + 3 * t * t * (1 - t) * (point4.getZ() + point3.getZ()) + t * t * t * point4.getZ();
		return new MutablePoint3D(x, y, z);
	}

	public TriangleMesh createMesh()
	{
		List<MutablePoint3D> lPoints = new ArrayList<>();
		List<Integer> lFaces = new ArrayList<>();
		int horizontalSteps = (int)(1.0 / CurveDrawer.smoothness);
		int verticalSteps = (int)(1.0 / CurveDrawer.smoothness);
		for (int y = 0; y <= verticalSteps; y++)
		{
			for (int x = 0; x <= horizontalSteps; x++)
			{
				double u = (double)x / (double)horizontalSteps;
				double v = (double)y / (double)verticalSteps;
				lPoints.add(getCoonsPoint(u, v));
				int[] faces = {0, horizontalSteps + 1, horizontalSteps + 2, 0, horizontalSteps + 2, 1};
				if (x < horizontalSteps && y < verticalSteps)
				{
					int faceIndex = y * (horizontalSteps + 1) + x;
					for (int k = 0; k < faces.length; k++)
					{
						lFaces.add(faceIndex + faces[k]);
						lFaces.add(faceIndex + faces[k]);
					}
				}
			}
		}
		TriangleMesh mesh = new TriangleMesh();
		float[] points = getPoints(lPoints);
		int[] faces = getFaces(lFaces);
		mesh.getPoints().setAll(points);
		mesh.getFaces().setAll(faces);
		return mesh;
	}

	private static int[] getFaces(List<Integer> faces)
	{
		int[] result = new int[faces.size()];
		for (int i = 0; i < faces.size(); i++)
		{
			result[i] = faces.get(i);
		}
		return result;
	}

	private static float[] getPoints(List<MutablePoint3D> points)
	{
		float[] result = new float[points.size() * 3];
		for (int i = 0; i < points.size(); i++)
		{
			result[i * 3 + 0] = (float)points.get(i).getX();
			result[i * 3 + 1] = (float)points.get(i).getY();
			result[i * 3 + 2] = (float)points.get(i).getZ();
		}
		return result;
	}

	private float[] getTexCoords(List<Point2D> texCoords)
	{
		float[] result = new float[texCoords.size() * 2];
		for (int i = 0; i < texCoords.size(); i++)
		{
			Point2D pointOnImage = texCoords.get(i);
			result[i * 2 + 0] = (float)pointOnImage.getX();
			result[i * 2 + 1] = (float)pointOnImage.getY();
		}
		return result;
	}

	public static Point2D toPoint2D(MutablePoint3D point)
	{
		return new Point2D(point.getX(), point.getY());
	}
}
