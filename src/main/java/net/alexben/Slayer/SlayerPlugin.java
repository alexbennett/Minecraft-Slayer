package net.alexben.Slayer;

import net.alexben.Slayer.Core.Handlers.Scheduler;
import net.alexben.Slayer.Core.Slayer;
import net.alexben.Slayer.Utilities.MiscUtil;

import org.bukkit.plugin.java.JavaPlugin;

public class SlayerPlugin extends JavaPlugin
{
	@Override
	public void onEnable()
	{
		// FlatFile.load();
		new Slayer(this);
		Scheduler.startThreads();

		// Log that net.alexben.Slayer successfully loaded
		MiscUtil.log("info", "Slayer has been successfully enabled!");
	}

	@Override
	public void onDisable()
	{
		Scheduler.stopThreads();
		// FlatFile.save();

		MiscUtil.log("info", "Slayer has been disabled.");
	}
}
