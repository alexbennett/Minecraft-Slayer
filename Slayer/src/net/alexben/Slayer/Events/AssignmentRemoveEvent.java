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

package net.alexben.Slayer.Events;

import net.alexben.Slayer.Libraries.Objects.Assignment;

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
