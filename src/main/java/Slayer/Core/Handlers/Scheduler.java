package Slayer.Core.Handlers;

import org.bukkit.Bukkit;

import Slayer.Utilities.ConfigUtil;
import Slayer.Utilities.EntityUtil;
import Slayer.Utilities.TaskUtil;

public class Scheduler
{
	@SuppressWarnings("deprecation")
	public static void startThreads()
	{
		// Define variables
		int saveFrequency = ConfigUtil.getSettingInt("data.save_freq") * 20;
		int assignmentRefreshFrequency = ConfigUtil.getSettingInt("data.assignment_refresh_freq") * 20;

		// Save data
		Bukkit.getServer().getScheduler().scheduleAsyncRepeatingTask(Slayer.Core.Slayer.plugin, new Runnable()
		{
			@Override
			public void run()
			{
				FlatFile.save();
			}
		}, saveFrequency, saveFrequency);

		// Update time-restricted assignments
		Bukkit.getServer().getScheduler().scheduleAsyncRepeatingTask(Slayer.Core.Slayer.plugin, new Runnable()
		{
			@Override
			public void run()
			{
				TaskUtil.refreshTimedAssignments();
			}
		}, 0, 20);

		// Cleanup entity tracking
		Bukkit.getServer().getScheduler().scheduleAsyncRepeatingTask(Slayer.Core.Slayer.plugin, new Runnable()
		{
			@Override
			public void run()
			{
				EntityUtil.cleanupMap();
			}
		}, 0, 20);

		// Clear completed/failed/inactive assignments
		Bukkit.getServer().getScheduler().scheduleAsyncRepeatingTask(Slayer.Core.Slayer.plugin, new Runnable()
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
		Bukkit.getServer().getScheduler().cancelTasks(Slayer.Core.Slayer.plugin);
	}
}
