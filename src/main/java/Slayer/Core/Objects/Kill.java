package Slayer.Core.Objects;

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
