package mesh;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

import javafx.scene.shape.TriangleMesh;

import entity.ControlPoint;
import test.MainApp;

public class TriangularCoonsPatchBuilder extends MeshBuilder
{
	@Override
	public TriangleMesh createMesh()
	{
		List<ControlPoint> controlPoints = MainApp.controlPoints;
		Set<LinkedHashSet<ControlPoint>> faces = new GraphCycleFinder(controlPoints).findCycles(3);
		faces = triangularizeRectangleFaces(faces);
		List<TriangleMesh> meshes = new ArrayList<>();

		for (LinkedHashSet<ControlPoint> face : faces)
		{
			Iterator<ControlPoint> cpIterator = face.iterator();
			ControlPoint p1 = cpIterator.next();
			ControlPoint p2 = cpIterator.next();
			ControlPoint p3 = cpIterator.next();

			TriangleMesh coonsMesh = new TriangleMesh();
			TriangularCoonsPatch coonsPatchCreator = new TriangularCoonsPatch(p1, p2, p3);
			coonsMesh.getPoints().addAll(coonsPatchCreator.createPoints());
			coonsMesh.getFaces().addAll(coonsPatchCreator.createFaces());
			meshes.add(coonsMesh);
		}

		return mergeMeshes(meshes);
	}

	public static Set<LinkedHashSet<ControlPoint>> triangularizeRectangleFaces(Set<LinkedHashSet<ControlPoint>> faces)
	{
		Set<LinkedHashSet<ControlPoint>> triangularized = new HashSet<>();
		for (LinkedHashSet<ControlPoint> face : faces)
		{
			if (face.size() == 4)
			{
				Iterator<ControlPoint> cpIterator = face.iterator();
				ControlPoint p1 = cpIterator.next();
				ControlPoint p2 = cpIterator.next();
				ControlPoint p3 = cpIterator.next();
				ControlPoint p4 = cpIterator.next();

				LinkedHashSet<ControlPoint> newFace1 = new LinkedHashSet<>();
				newFace1.add(p1);
				newFace1.add(p2);
				newFace1.add(p3);

				LinkedHashSet<ControlPoint> newFace2 = new LinkedHashSet<>();
				newFace2.add(p1);
				newFace2.add(p3);
				newFace2.add(p4);

				triangularized.add(newFace1);
				triangularized.add(newFace2);
			}
			else
			{
				triangularized.add(face);
			}
		}
		return triangularized;
	}
}
