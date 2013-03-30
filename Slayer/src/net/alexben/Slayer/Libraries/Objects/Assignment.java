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

package net.alexben.Slayer.Libraries.Objects;

import java.io.Serializable;

import net.alexben.Slayer.Utilities.SObjUtil;

import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;

public class Assignment implements Serializable
{
	private static final long serialVersionUID = 1869297753495176134L;
	private String player = null;
	private int id, progress;
	private Task task;
	private boolean active;

	public Assignment(OfflinePlayer player, Task task)
	{
		this.id = SObjUtil.generateInt(5);
		this.player = player.getName();
		this.progress = 0;
		this.active = true;
		this.task = task;
	}

	/**
	 * Sets the active status of the task.
	 * 
	 * @param status the boolean to set the activity to.
	 */
	public void setActive(boolean status)
	{
		active = status;
	}

	/**
	 * Returns the ID of the task.
	 * 
	 * @return int
	 */
	public int getID()
	{
		return id;
	}

	/**
	 * Returns the player associated with this task.
	 * 
	 * @return Player
	 */
	public Player getPlayer()
	{
		return Bukkit.getPlayer(player);
	}

	/**
	 * Returns the actual Task associated with this attachment.
	 */
	public Task getTask()
	{
		return task;
	}

	/**
	 * Returns true/false depending on if the task is active or not.
	 * 
	 * @return boolean
	 */
	public boolean isActive()
	{
		return active;
	}

	/**
	 * Sets the assignment progress to <code>amount</code>.
	 */
	public void setProgress(int amount)
	{
		progress = amount;
	}

	/**
	 * Increases the value of the assignment progress.
	 */
	public void addProgress()
	{
		progress++;
	}

	public void addProgress(int amount)
	{
		progress = progress + amount;
	}

	/**
	 * Decreases the value of the assignment progress.
	 */
	public void subtractProgress()
	{
		progress--;
	}

	public void subtractProgress(int amount)
	{
		progress = progress - amount;
	}

	/**
	 * Returns the amount needed for task completion.
	 * 
	 * @return int
	 */
	public int getAmountNeeded()
	{
		return task.getGoal();
	}

	/**
	 * Returns the current amount obtained for this task.
	 * 
	 * @return int
	 */
	public int getAmountObtained()
	{
		return progress;
	}

	/**
	 * Returns the percent completion of the task.
	 * 
	 * @return double
	 */
	public double getCompletion()
	{
		return(getAmountObtained() / getAmountNeeded());
	}

	/**
	 * Returns the completion boolean for the task.
	 * 
	 * @return boolean
	 */
	public boolean isComplete()
	{
		if(getCompletion() >= 1) return true;
		return false;
	}
}
