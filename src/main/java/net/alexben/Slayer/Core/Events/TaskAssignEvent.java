package net.alexben.Slayer.Core.Events;

import net.alexben.Slayer.Core.Objects.Task;

import org.bukkit.entity.Player;
import org.bukkit.event.Cancellable;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

/**
 * Called when a Assignment is assigned to a player.
 */
public class TaskAssignEvent extends Event implements Cancellable
{
	private static final HandlerList handlers = new HandlerList();
	private Player player;
	private Task task;
	private boolean cancelled = false;

	public TaskAssignEvent(Player player, Task task)
	{
		this.player = player;
		this.task = task;
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
	 * Returns the task associated with the event.
	 * 
	 * @return Task
	 */
	public Task getTask()
	{
		return this.task;
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
