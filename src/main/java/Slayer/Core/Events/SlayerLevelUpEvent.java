package Slayer.Core.Events;

import org.bukkit.entity.Player;
import org.bukkit.event.Cancellable;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

/**
 * Called when a Assignment is assigned to a player.
 */
public class SlayerLevelUpEvent extends Event implements Cancellable
{
	private static final HandlerList handlers = new HandlerList();
	private Player player;
	private int prevLevel, currLevel;
	private boolean cancelled = false;

	public SlayerLevelUpEvent(Player player, int prevLevel, int currLevel)
	{
		this.player = player;
		this.prevLevel = prevLevel;
		this.currLevel = currLevel;
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
	 * Returns the previous level for the player.
	 * 
	 * @return Integer
	 */
	public int getPreviousLevel()
	{
		return this.prevLevel;
	}

	/**
	 * Returns the current level for the player.
	 * 
	 * @return Integer
	 */
	public int getCurrentLevel()
	{
		return this.currLevel;
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
