package entity;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import javafx.geometry.Point3D;

public class ControlPoint extends MutablePoint3D
{
	private static final long serialVersionUID = -804513685542371926L;

	boolean isSelected = false;
	Map<ControlPoint, MutablePoint3D> tangents = new HashMap<>();

	public ControlPoint(double x, double y, double z)
	{
		super(x, y, z);
	}

	public void setTangent(ControlPoint neighbour, ControlPoint point)
	{
		setTangent(neighbour, new MutablePoint3D(neighbour.getX() - point.getX(), neighbour.getY() - point.getY(), neighbour.getZ() - point.getZ()));
	}

	public void setTangent(ControlPoint neighbour, MutablePoint3D point)
	{
		tangents.put(neighbour, point);
	}

	public MutablePoint3D getTangent(ControlPoint neighbour)
	{
		return tangents.get(neighbour);
	}

	public void deleteTangent(ControlPoint neighbour)
	{
		tangents.remove(neighbour);
	}

	public Set<ControlPoint> getNeighbours()
	{
		return tangents.keySet();
	}

	public Collection<MutablePoint3D> getTangents()
	{
		return tangents.values();
	}

	public boolean isSelected()
	{
		return isSelected;
	}

	public void setSelected(boolean isSelected)
	{
		this.isSelected = isSelected;
	}

	public void translate(Point3D point)
	{
		translate(point.getX(), point.getY(), point.getZ());
	}

	public void translate(MutablePoint3D point)
	{
		translate(point.getX(), point.getY(), point.getZ());
	}

	public void translate(double x, double y, double z)
	{
		add(x, y, z);
		for (Entry<ControlPoint, MutablePoint3D> tangentEntry : tangents.entrySet())
		{
			tangentEntry.getValue().add(x, y, z);
		}
	}
}
