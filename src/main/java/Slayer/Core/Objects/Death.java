package Slayer.Core.Objects;

import java.io.Serializable;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;

public class Death implements Serializable
{
	private static final long serialVersionUID = 1869555397495176134L;
	private String player = null;
	private EntityType entity;
	private SerialLocation location;

	public Death(Player player, Entity entity)
	{
		this.player = player.getName();
		this.entity = entity.getType();
		location = new SerialLocation(player.getLocation());
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
	 * Returns the location of the player that died.
	 * 
	 * @return Location
	 */
	public Location getLocation()
	{
		return location.unserialize();
	}
}
