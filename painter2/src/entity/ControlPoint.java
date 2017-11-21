package entity;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

public class ControlPoint extends MutablePoint3D
{
	boolean isSelected = false;
	Map<ControlPoint, MutablePoint3D> tangents = new HashMap<>();

	public ControlPoint(double x, double y, double z)
	{
		super(x, y, z);
	}

	public void setTangent(ControlPoint neighbour, MutablePoint3D point)
	{
		tangents.put(neighbour, point);
	}

	public MutablePoint3D getTangent(ControlPoint neighbour)
	{
		return tangents.get(neighbour);
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
}
