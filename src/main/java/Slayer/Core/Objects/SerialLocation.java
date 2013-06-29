package Slayer.Core.Objects;

import java.io.Serializable;

import org.bukkit.Bukkit;
import org.bukkit.Location;

public class SerialLocation implements Serializable
{
	private static final long serialVersionUID = 1869297397495176134L;
	private double X, Y, Z;
	private float pitch, yaw;
	private final String world;
	private String name;

	public SerialLocation(String world, double X, double Y, double Z, float pitch, float yaw)
	{
		this.world = world;
		this.X = X;
		this.Y = Y;
		this.Z = Z;
		this.pitch = pitch;
		this.yaw = yaw;
	}

	public SerialLocation(String world, double X, double Y, double Z, float pitch, float yaw, String name)
	{
		this.world = world;
		this.X = X;
		this.Y = Y;
		this.Z = Z;
		this.pitch = pitch;
		this.yaw = yaw;
		this.name = name.toUpperCase();
	}

	public SerialLocation(Location location)
	{
		this.world = location.getWorld().getName();
		this.X = location.getX();
		this.Y = location.getY();
		this.Z = location.getZ();
		this.pitch = location.getPitch();
		this.yaw = location.getYaw();
	}

	public SerialLocation(Location location, String name)
	{
		this.world = location.getWorld().getName();
		this.X = location.getX();
		this.Y = location.getY();
		this.Z = location.getZ();
		this.pitch = location.getPitch();
		this.yaw = location.getYaw();
		this.name = name.toUpperCase();
	}

	public boolean hasName()
	{
		return name != null;
	}

	public String getName()
	{
		return name;
	}

	public synchronized void setName(String name)
	{
		this.name = name.toUpperCase();
	}

	public Location unserialize()
	{
		return new Location(Bukkit.getServer().getWorld(this.world), this.X, this.Y, this.Z, this.yaw, this.pitch);
	}
}
