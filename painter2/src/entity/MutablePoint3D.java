package entity;

import java.io.Serializable;

import javafx.beans.NamedArg;

public class MutablePoint3D implements Serializable
{
	private static final long serialVersionUID = -1577538620233665110L;

	public static final MutablePoint3D ZERO = new MutablePoint3D(0.0, 0.0, 0.0);

	protected double x;

	public final double getX()
	{
		return x;
	}

	public final void setX(double x)
	{
		this.x = x;
	}

	protected double y;

	public final double getY()
	{
		return y;
	}

	public final void setY(double y)
	{
		this.y = y;
	}

	protected double z;

	public final double getZ()
	{
		return z;
	}

	public final void setZ(double z)
	{
		this.z = z;
	}

	private int hash = 0;

	public MutablePoint3D(@NamedArg("x") double x, @NamedArg("y") double y, @NamedArg("z") double z)
	{
		this.x = x;
		this.y = y;
		this.z = z;
	}

	public MutablePoint3D clone()
	{
		return new MutablePoint3D(x, y, z);
	}

	public double distance(double x1, double y1, double z1)
	{
		double a = getX() - x1;
		double b = getY() - y1;
		double c = getZ() - z1;
		return Math.sqrt(a * a + b * b + c * c);
	}

	public double distance(MutablePoint3D point)
	{
		return distance(point.getX(), point.getY(), point.getZ());
	}

	public MutablePoint3D add(double x, double y, double z)
	{
		this.x += x;
		this.y += y;
		this.z += z;
		return this;
	}

	public MutablePoint3D add(MutablePoint3D point)
	{
		return add(point.getX(), point.getY(), point.getZ());
	}

	public MutablePoint3D subtract(double x, double y, double z)
	{
		this.x -= x;
		this.y -= y;
		this.z -= z;
		return this;
	}

	public MutablePoint3D subtract(MutablePoint3D point)
	{
		return subtract(point.getX(), point.getY(), point.getZ());
	}

	public MutablePoint3D multiply(double factor)
	{
		this.x *= factor;
		this.y *= factor;
		this.z *= factor;
		return this;
	}

	public MutablePoint3D normalize()
	{
		final double mag = magnitude();

		if (mag == 0.0)
		{
			return new MutablePoint3D(0.0, 0.0, 0.0);
		}

		return new MutablePoint3D(getX() / mag, getY() / mag, getZ() / mag);
	}

	public MutablePoint3D midpoint(double x, double y, double z)
	{
		return new MutablePoint3D(x + (getX() - x) / 2.0, y + (getY() - y) / 2.0, z + (getZ() - z) / 2.0);
	}

	public MutablePoint3D midpoint(MutablePoint3D point)
	{
		return midpoint(point.getX(), point.getY(), point.getZ());
	}

	public double angle(double x, double y, double z)
	{
		final double ax = getX();
		final double ay = getY();
		final double az = getZ();

		final double delta = (ax * x + ay * y + az * z) / Math.sqrt((ax * ax + ay * ay + az * az) * (x * x + y * y + z * z));

		if (delta > 1.0)
		{
			return 0.0;
		}
		if (delta < -1.0)
		{
			return 180.0;
		}

		return Math.toDegrees(Math.acos(delta));
	}

	public double angle(MutablePoint3D point)
	{
		return angle(point.getX(), point.getY(), point.getZ());
	}

	public double angle(MutablePoint3D p1, MutablePoint3D p2)
	{
		final double x = getX();
		final double y = getY();
		final double z = getZ();

		final double ax = p1.getX() - x;
		final double ay = p1.getY() - y;
		final double az = p1.getZ() - z;
		final double bx = p2.getX() - x;
		final double by = p2.getY() - y;
		final double bz = p2.getZ() - z;

		final double delta = (ax * bx + ay * by + az * bz) / Math.sqrt((ax * ax + ay * ay + az * az) * (bx * bx + by * by + bz * bz));

		if (delta > 1.0)
		{
			return 0.0;
		}
		if (delta < -1.0)
		{
			return 180.0;
		}

		return Math.toDegrees(Math.acos(delta));
	}

	public double magnitude()
	{
		final double x = getX();
		final double y = getY();
		final double z = getZ();

		return Math.sqrt(x * x + y * y + z * z);
	}

	public double dotProduct(double x, double y, double z)
	{
		return getX() * x + getY() * y + getZ() * z;
	}

	public double dotProduct(MutablePoint3D vector)
	{
		return dotProduct(vector.getX(), vector.getY(), vector.getZ());
	}

	public MutablePoint3D crossProduct(double x, double y, double z)
	{
		final double ax = getX();
		final double ay = getY();
		final double az = getZ();

		return new MutablePoint3D(ay * z - az * y, az * x - ax * z, ax * y - ay * x);
	}

	public MutablePoint3D crossProduct(MutablePoint3D vector)
	{
		return crossProduct(vector.getX(), vector.getY(), vector.getZ());
	}

	@Override
	public boolean equals(Object obj)
	{
		return obj == this;
	}

	@Override
	public int hashCode()
	{
		if (hash == 0)
		{
			long bits = 7L;
			bits = 31L * bits + Double.doubleToLongBits(getX());
			bits = 31L * bits + Double.doubleToLongBits(getY());
			bits = 31L * bits + Double.doubleToLongBits(getZ());
			hash = (int)(bits ^ (bits >> 32));
		}
		return hash;
	}

	@Override
	public String toString()
	{
		return "MutablePoint3D [x = " + getX() + ", y = " + getY() + ", z = " + getZ() + "]";
	}
}
