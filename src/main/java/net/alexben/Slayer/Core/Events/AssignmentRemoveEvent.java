package net.alexben.Slayer.Core.Events;

import net.alexben.Slayer.Core.Objects.Assignment;

import org.bukkit.OfflinePlayer;
import org.bukkit.event.Cancellable;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

/**
 * Called when a player's assignment expires.
 */
public class AssignmentRemoveEvent extends Event implements Cancellable
{
	private static final HandlerList handlers = new HandlerList();
	private OfflinePlayer player;
	private Assignment assignment;
	private RemoveReason reason;
	private boolean cancelled = false;

	public AssignmentRemoveEvent(OfflinePlayer player, Assignment assignment, RemoveReason reason)
	{
		this.player = player;
		this.assignment = assignment;
		this.reason = reason;
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

	/**
	 * Returns the reason for removal.
	 * 
	 * @return RemoveReason
	 */
	public RemoveReason getReason()
	{
		return reason;
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

	public enum RemoveReason
	{
		AUTO, ADMIN
	}
}
