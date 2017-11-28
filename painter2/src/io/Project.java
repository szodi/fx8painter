package io;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.util.List;

import entity.ControlPoint;

public class Project implements Serializable
{
	private static final long serialVersionUID = -3500377907635190594L;

	String image;
	List<ControlPoint> controlPoints;
	double imageX;
	double imageY;
	double imageScaleX;
	double imageScaleY;

	public void save(File file)
	{
		try (ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(file)))
		{
			writeObject(oos);
		}
		catch (Exception e)
		{
			e.printStackTrace();
		}
	}

	public void load(File file)
	{
		try (ObjectInputStream ois = new ObjectInputStream(new FileInputStream(file)))
		{
			readObject(ois);
		}
		catch (Exception e)
		{
			e.printStackTrace();
		}
	}

	private void writeObject(ObjectOutputStream out) throws IOException
	{
		out.writeObject(image);
		out.writeObject(controlPoints);
		out.writeDouble(imageX);
		out.writeDouble(imageY);
		out.writeDouble(imageScaleX);
		out.writeDouble(imageScaleY);
	}

	@SuppressWarnings("unchecked")
	private void readObject(ObjectInputStream in) throws IOException, ClassNotFoundException
	{
		image = (String)in.readObject();
		controlPoints = (List<ControlPoint>)in.readObject();
		imageX = in.readDouble();
		imageY = in.readDouble();
		imageScaleX = in.readDouble();
		imageScaleY = in.readDouble();
	}

	public String getImage()
	{
		return image;
	}

	public void setImage(String image)
	{
		this.image = image;
	}

	public List<ControlPoint> getControlPoints()
	{
		return controlPoints;
	}

	public void setControlPoints(List<ControlPoint> controlPoints)
	{
		this.controlPoints = controlPoints;
	}

	public double getImageX()
	{
		return imageX;
	}

	public void setImageX(double imageX)
	{
		this.imageX = imageX;
	}

	public double getImageY()
	{
		return imageY;
	}

	public void setImageY(double imageY)
	{
		this.imageY = imageY;
	}

	public double getImageScaleX()
	{
		return imageScaleX;
	}

	public void setImageScaleX(double imageScaleX)
	{
		this.imageScaleX = imageScaleX;
	}

	public double getImageScaleY()
	{
		return imageScaleY;
	}

	public void setImageScaleY(double imageScaleY)
	{
		this.imageScaleY = imageScaleY;
	}
}
