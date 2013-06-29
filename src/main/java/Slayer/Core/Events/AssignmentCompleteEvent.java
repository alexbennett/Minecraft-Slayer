package Slayer.Core.Events;

import org.bukkit.entity.Player;
import org.bukkit.event.Cancellable;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

import Slayer.Core.Objects.Assignment;

/**
 * Called when a player completes a Task.
 */
public class AssignmentCompleteEvent extends Event implements Cancellable
{
	private static final HandlerList handlers = new HandlerList();
	private Player player;
	private Assignment assignment;
	private boolean cancelled = false;

	public AssignmentCompleteEvent(Player player, Assignment assignment)
	{
		this.player = player;
		this.assignment = assignment;
	}

	/**
	 * Returns the player associated with the event.
	 * 
	 * @return Player
	 */
	public Player getPlayer()
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
