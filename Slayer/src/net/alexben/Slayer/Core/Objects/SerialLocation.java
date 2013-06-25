/*
 * Copyright (c) 2013 Alex Bennett
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy of this software
 * and associated documentation files (the "Software"), to deal in the Software without restriction,
 * including without limitation the rights to use, copy, modify, merge, publish, distribute, sublicense,
 * and/or sell copies of the Software, and to permit persons to whom the Software is furnished to do
 * so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all copies or substantial
 * portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR IMPLIED, INCLUDING BUT
 * NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT.
 * IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER LIABILITY,
 * WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM, OUT OF OR IN CONNECTION WITH THE
 * SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.
 */

package net.alexben.Slayer.Core.Objects;

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
