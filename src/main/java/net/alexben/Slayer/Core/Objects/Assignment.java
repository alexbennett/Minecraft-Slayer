package net.alexben.Slayer.Core.Objects;

import java.io.Serializable;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import net.alexben.Slayer.Core.Handlers.Database;
import net.alexben.Slayer.Utilities.MiscUtil;
import net.alexben.Slayer.Utilities.ObjUtil;

import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;

public class Assignment implements Serializable
{
	private static final long serialVersionUID = 1869297753495176134L;
	private String player = null;
	private int id, progress;
	private long expiration;
	private Task task;
	private boolean display, active, failed, expired, forfeited;

	public Assignment(OfflinePlayer player, Task task)
	{
		this.id = ObjUtil.generateInt(5);
		this.player = player.getName();
		this.progress = 0;
		this.active = true;
		this.failed = false;
		this.expired = false;
		this.display = true;
		this.task = task;
		this.expiration = System.currentTimeMillis() + (task.getTimeLimit() * 60000); // Converts to milliseconds
	}

	/**
	 * Saves the Assignment via SQL and returns it's id.
	 * 
	 * @return Long
	 */
	public long saveSql()
	{
		// Define the variables
		PreparedStatement ps = Database.toPreparedStatement("INSERT INTO assignments (player_id, task, progress, expiration, display, active, failed, expired, forfeited) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?)");
		int sqlId = -1;

		try
		{
			// Set the values
			ps.setInt(1, Database.getPlayerId(Bukkit.getOfflinePlayer(player)));
			ps.setObject(2, task);
			ps.setInt(3, progress);
			ps.setLong(4, expiration);
			ps.setBoolean(5, display);
			ps.setBoolean(6, active);
			ps.setBoolean(7, failed);
			ps.setBoolean(8, expired);
			ps.setBoolean(9, forfeited);

			// Execute
			ps.executeUpdate();

			// Find the id and return it
			ResultSet keys = ps.getGeneratedKeys();

			if(keys.next())
			{
				sqlId = keys.getInt(1);
			}
		}
		catch(SQLException e)
		{
			MiscUtil.log("severe", "There was a problem while saving an assignment to the database.");
			e.printStackTrace(); // TODO
		}

		return sqlId;
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
	 * Sets the failure status of the task.
	 * 
	 * @param status the boolean to set the task to.
	 */
	public void setFailed(boolean status)
	{
		failed = status;
	}

	/**
	 * Sets the forfeit status of the task.
	 * 
	 * @param status the boolean to set the task to.
	 */
	public void setForfeited(boolean status)
	{
		forfeited = status;
	}

	/**
	 * Sets the expiration status of the task.
	 * 
	 * @param status the boolean to set the task to.
	 */
	public void setExpired(boolean status)
	{
		expired = status;
	}

	/**
	 * Sets the display status of the task.
	 * 
	 * @param status the boolean to set the task to.
	 */
	public void setDisplay(boolean status)
	{
		display = status;
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
	 * @return OfflinePlayer
	 */
	public OfflinePlayer getOfflinePlayer()
	{
		return Bukkit.getOfflinePlayer(player);
	}

	/**
	 * Returns the actual Task associated with this assignment.
	 */
	public Task getTask()
	{
		return task;
	}

	/**
	 * Returns the amount of time in minutes before expiration.
	 * 
	 * @return double
	 */
	public long getTimeLeft()
	{
		if(!task.isTimed()) return -1;
		if(expired) return 0L;
		return((getExpiration() - System.currentTimeMillis()) / 60000);
	}

	/**
	 * Returns the expiration time.
	 * 
	 * @return long
	 */
	public Long getExpiration()
	{
		return expiration;
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
	 * Returns true/false depending on if the task is failed or not.
	 * 
	 * @return boolean
	 */
	public boolean isFailed()
	{
		return failed;
	}

	/**
	 * Returns true/false depending on if the task is active or not.
	 * 
	 * @return boolean
	 */
	public boolean isExpired()
	{
		return expired;
	}

	/**
	 * Returns true/false depending on if the task is forfeited or not.
	 * 
	 * @return boolean
	 */
	public boolean isForfeited()
	{
		return forfeited;
	}

	/**
	 * Returns true/false depending on if the task is displayed or not.
	 * 
	 * @return boolean
	 */
	public boolean isDisplayed()
	{
		return display;
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
	 * Returns the amount left for task completion.
	 * 
	 * @return int
	 */
	public int getAmountLeft()
	{
		return getAmountNeeded() - getAmountObtained();
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
