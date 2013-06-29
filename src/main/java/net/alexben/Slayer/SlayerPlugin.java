package net.alexben.Slayer;

import net.alexben.Slayer.Core.Handlers.Database;
import net.alexben.Slayer.Core.Handlers.Scheduler;
import net.alexben.Slayer.Core.Slayer;
import net.alexben.Slayer.Utilities.MiscUtil;

import org.bukkit.plugin.java.JavaPlugin;

public class SlayerPlugin extends JavaPlugin
{
	@Override
	public void onEnable()
	{
		new Slayer(this);

		// Initialize the database
		Database.initializeConnection();

		// Start the scheduler
		Scheduler.startThreads();

		// Log that Slayer successfully loaded
		MiscUtil.log("info", "Slayer has been successfully enabled!");
	}

	@Override
	public void onDisable()
	{
		// Stop the scheduler
		Scheduler.stopThreads();

		// Log that Slayer successfully unloaded
		MiscUtil.log("info", "Slayer has been disabled.");
	}
}
