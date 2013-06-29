package net.alexben.Slayer.Core.Handlers;

import net.alexben.Slayer.Core.Slayer;
import net.alexben.Slayer.Utilities.ConfigUtil;
import net.alexben.Slayer.Utilities.EntityUtil;
import net.alexben.Slayer.Utilities.TaskUtil;

import org.bukkit.Bukkit;

public class Scheduler
{
	@SuppressWarnings("deprecation")
	public static void startThreads()
	{
		// Define variables
		int saveFrequency = ConfigUtil.getSettingInt("data.save_freq") * 20;
		int assignmentRefreshFrequency = ConfigUtil.getSettingInt("data.assignment_refresh_freq") * 20;

		// Save data
		Bukkit.getServer().getScheduler().scheduleAsyncRepeatingTask(Slayer.plugin, new Runnable()
		{
			@Override
			public void run()
			{
				// TODO: SQL saving.
				// FlatFile.save();
			}
		}, saveFrequency, saveFrequency);

		// Update time-restricted assignments
		Bukkit.getServer().getScheduler().scheduleAsyncRepeatingTask(Slayer.plugin, new Runnable()
		{
			@Override
			public void run()
			{
				TaskUtil.refreshTimedAssignments();
			}
		}, 0, 20);

		// Cleanup entity tracking
		Bukkit.getServer().getScheduler().scheduleAsyncRepeatingTask(Slayer.plugin, new Runnable()
		{
			@Override
			public void run()
			{
				EntityUtil.cleanupMap();
			}
		}, 0, 20);

		// Clear completed/failed/inactive assignments
		Bukkit.getServer().getScheduler().scheduleAsyncRepeatingTask(Slayer.plugin, new Runnable()
		{
			@Override
			public void run()
			{
				// Remove the assignment
				TaskUtil.refreshAssignments();
			}
		}, assignmentRefreshFrequency, assignmentRefreshFrequency);
	}

	public static void stopThreads()
	{
		Bukkit.getServer().getScheduler().cancelTasks(Slayer.plugin);
	}
}
