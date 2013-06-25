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
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;

public class Kill implements Serializable
{
	private static final long serialVersionUID = 1869297397495176734L;
	private String player = null;
	private EntityType entity;
	private SerialLocation location;

	public Kill(Player player, Entity entity)
	{
		this.player = player.getName();
		this.entity = entity.getType();
		this.location = new SerialLocation(entity.getLocation());
	}

	/**
	 * Returns the player associated with this kill.
	 * 
	 * @return Player
	 */
	public Player getPlayer()
	{
		return Bukkit.getPlayer(player);
	}

	/**
	 * Returns the EntityType associated with this kill.
	 * 
	 * @return EntityType
	 */
	public EntityType getEntity()
	{
		return entity;
	}

	/**
	 * Returns the entity that was killed.
	 * 
	 * @return Location
	 */
	public Location getLocation()
	{
		return location.unserialize();
	}
}
