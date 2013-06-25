package net.alexben.Slayer.Core.Handlers;

import net.alexben.Slayer.Core.Slayer;
import net.alexben.Slayer.Utilities.SConfigUtil;
import net.alexben.Slayer.Utilities.SEntityUtil;
import net.alexben.Slayer.Utilities.STaskUtil;

import org.bukkit.Bukkit;

public class SScheduler
{
	@SuppressWarnings("deprecation")
	public static void startThreads()
	{
		// Define variables
		int saveFrequency = SConfigUtil.getSettingInt("data.save_freq") * 20;
		int assignmentRefreshFrequency = SConfigUtil.getSettingInt("data.assignment_refresh_freq") * 20;

		// Save data
		Bukkit.getServer().getScheduler().scheduleAsyncRepeatingTask(Slayer.plugin, new Runnable()
		{
			@Override
			public void run()
			{
				SFlatFile.save();
			}
		}, saveFrequency, saveFrequency);

		// Update time-restricted assignments
		Bukkit.getServer().getScheduler().scheduleAsyncRepeatingTask(Slayer.plugin, new Runnable()
		{
			@Override
			public void run()
			{
				STaskUtil.refreshTimedAssignments();
			}
		}, 0, 20);

		// Cleanup entity tracking
		Bukkit.getServer().getScheduler().scheduleAsyncRepeatingTask(Slayer.plugin, new Runnable()
		{
			@Override
			public void run()
			{
				SEntityUtil.cleanupMap();
			}
		}, 0, 20);

		// Clear completed/failed/inactive assignments
		Bukkit.getServer().getScheduler().scheduleAsyncRepeatingTask(Slayer.plugin, new Runnable()
		{
			@Override
			public void run()
			{
				// Remove the assignment
				STaskUtil.refreshAssignments();
			}
		}, assignmentRefreshFrequency, assignmentRefreshFrequency);
	}

	public static void stopThreads()
	{
		Bukkit.getServer().getScheduler().cancelTasks(Slayer.plugin);
	}
}
