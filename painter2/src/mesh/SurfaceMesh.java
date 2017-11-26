package mesh;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Function;

import javafx.application.Application;
import javafx.geometry.Point2D;
import javafx.geometry.Point3D;
import javafx.scene.Group;
import javafx.scene.PerspectiveCamera;
import javafx.scene.Scene;
import javafx.scene.SceneAntialiasing;
import javafx.scene.input.KeyCombination;
import javafx.scene.shape.Mesh;
import javafx.scene.shape.TriangleMesh;
import javafx.stage.Stage;

public class SurfaceMesh extends Application
{
	Group group = new Group();
	Scene scene;
	Mesh mesh;
	SurfaceMeshView surfaceMeshView;

	private PerspectiveCamera camera;

	private void initCamera()
	{
		this.camera = new PerspectiveCamera(true);
		this.camera.setNearClip(0.1);
		this.camera.setFarClip(10000.0);
		this.camera.setTranslateZ(-1000);
	}

	@Override
	public void start(Stage primaryStage) throws Exception
	{
		initCamera();
		mesh = createMesh();
		// surfaceMeshView = new SurfaceMeshView(mesh);
		group.getChildren().add(surfaceMeshView);
		scene = new Scene(group, 1280, 720, true, SceneAntialiasing.BALANCED);

		scene.setOnMouseDragged(surfaceMeshView);
		scene.setOnMousePressed(surfaceMeshView);

		scene.setCamera(camera);

		primaryStage.setScene(scene);
		primaryStage.setFullScreen(true);
		primaryStage.setFullScreenExitKeyCombination(KeyCombination.NO_MATCH);
		primaryStage.show();

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
	private Point3D getCoonsPoint(Function<Double, Point3D> c0, Function<Double, Point3D> c1, Function<Double, Point3D> d0, Function<Double, Point3D> d1, double s, double t)
	{
		double d_Lc_x = (1 - t) * c0.apply(s).getX() + t * c1.apply(s).getX();
		double d_Ld_x = (1 - s) * d0.apply(t).getX() + s * d1.apply(t).getX();
		double d_Lc_y = (1 - t) * c0.apply(s).getY() + t * c1.apply(s).getY();
		double d_Ld_y = (1 - s) * d0.apply(t).getY() + s * d1.apply(t).getY();
		double d_Lc_z = (1 - t) * c0.apply(s).getZ() + t * c1.apply(s).getZ();
		double d_Ld_z = (1 - s) * d0.apply(t).getZ() + s * d1.apply(t).getZ();
		// double d_B_x = c0.apply(0.0).getX() * (1 - s) * (1 - t) + c0.apply(1.0).getX() * s * (1 - t) + c1.apply(0.0).getX() * (1 - s) * t + c1.apply(1.0).getX() * s * t;
		// double d_B_y = c0.apply(0.0).getY() * (1 - s) * (1 - t) + c0.apply(1.0).getY() * s * (1 - t) + c1.apply(0.0).getY() * (1 - s) * t + c1.apply(1.0).getY() * s * t;
		// double d_B_z = c0.apply(0.0).getZ() * (1 - s) * (1 - t) + c0.apply(1.0).getZ() * s * (1 - t) + c1.apply(0.0).getZ() * (1 - s) * t + c1.apply(1.0).getZ() * s * t;
		return new Point3D(d_Lc_x + d_Ld_x, d_Lc_y + d_Ld_y, d_Lc_z + d_Ld_z);
	}

	private Point3D c0(double s)
	{
		return new Point3D(0, 50 - 60 * Math.sin(s * Math.PI), 0);
	}

	private Point3D c1(double s)
	{
		return new Point3D(0, 0, 200 - 60 * Math.sin(s * Math.PI));
	}

	private Point3D d0(double s)
	{
		return new Point3D(0, 50 - 60 * Math.sin(s * Math.PI), 0);
	}

	private Point3D d1(double s)
	{
		return new Point3D(150, 50 - 60 * Math.sin(s * Math.PI), 0);
	}

	private Point3D getSurfacePoint(double s, double t)
	{
		return getCoonsPoint(s0 -> c0(s), s1 -> c1(s), t0 -> d0(t), t1 -> d1(t), s, t);
	}

	private Mesh createMesh()
	{
		double smoothnessHorizontal = 0.01;
		double smoothnessVertical = 0.01;
		List<Point3D> lPoints = new ArrayList<Point3D>();
		List<Point2D> lTexCoords = new ArrayList<Point2D>();
		List<Integer> lFaces = new ArrayList<Integer>();
		int j = 0;

		for (double u = 0.0; u < 1.0 - smoothnessHorizontal; u += smoothnessHorizontal)
		{
			for (double v = 0.0; v < 1.0 - smoothnessVertical; v += smoothnessVertical)
			{
				Point3D p00 = getSurfacePoint(u, v);
				Point3D p10 = getSurfacePoint(u + smoothnessHorizontal, v);
				Point3D p01 = getSurfacePoint(u, v + smoothnessVertical);
				Point3D p11 = getSurfacePoint(u + smoothnessHorizontal, v + smoothnessVertical);
				Point3D[] points = {p00, p10, p01, p11};
				int[] faces = {0, 1, 2, 1, 3, 2};
				for (int i = 0; i < points.length; i++)
				{
					lPoints.add(points[i]);
					lTexCoords.add(toPoint2D(points[i]));
				}
				for (int i = 0; i < faces.length; i++)
				{
					lFaces.add(j * 4 + faces[i]);
					lFaces.add(j * 4 + faces[i]);
				}
				j++;
			}
		}
		TriangleMesh mesh = new TriangleMesh();
		float[] points = getPoints(lPoints);
		float[] texCoords = getTexCoords(lTexCoords);
		int[] faces = getFaces(lFaces);
		mesh.getPoints().setAll(points);
		mesh.getTexCoords().setAll(texCoords);
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

	private static float[] getPoints(List<Point3D> points)
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

	private static float[] getTexCoords(List<Point2D> texCoords)
	{
		double imgWidth = 100;
		double imgHeight = 100;
		float[] result = new float[texCoords.size() * 2];
		for (int i = 0; i < texCoords.size(); i++)
		{
			Point2D pointOnImage = texCoords.get(i);
			result[i * 2 + 0] = (float)(pointOnImage.getX() / imgWidth);
			result[i * 2 + 1] = (float)(pointOnImage.getY() / imgHeight);
		}
		return result;
	}

	public static Point2D toPoint2D(Point3D point)
	{
		return new Point2D(point.getX(), point.getY());
	}

	public static void main(String[] args)
	{
		launch(args);
	}
}
