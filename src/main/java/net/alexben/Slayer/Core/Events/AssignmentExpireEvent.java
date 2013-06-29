package net.alexben.Slayer.Core.Events;

import net.alexben.Slayer.Core.Objects.Assignment;

import org.bukkit.OfflinePlayer;
import org.bukkit.event.Cancellable;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

/**
 * Called when a player's assignment expires.
 */
public class AssignmentExpireEvent extends Event implements Cancellable
{
	private static final HandlerList handlers = new HandlerList();
	private OfflinePlayer player;
	private Assignment assignment;
	private boolean cancelled = false;

	public AssignmentExpireEvent(OfflinePlayer player, Assignment assignment)
	{
		this.player = player;
		this.assignment = assignment;
	}

	/**
	 * Returns the player associated with the event.
	 * 
	 * @return OfflinePlayer
	 */
	public OfflinePlayer getOfflinePlayer()
	{
		return this.player;
	}

	/**
	 * Returns the Assignment associated with the event.
	 * 
	 * @return Assignment
	 */
	public Assignment getAssignment()
	{
		return this.assignment;
	}

	@Override
	public HandlerList getHandlers()
	{
		return handlers;
	}

	public static HandlerList getHandlerList()
	{
		return handlers;
	}

	@Override
	public boolean isCancelled()
	{
		return this.cancelled;
	}

	@Override
	public synchronized void setCancelled(boolean cancelled)
	{
		this.cancelled = cancelled;
	}
}
