package Slayer;

import org.bukkit.plugin.java.JavaPlugin;

import Slayer.Core.Handlers.FlatFile;
import Slayer.Core.Handlers.Scheduler;
import Slayer.Core.Slayer;
import Slayer.Utilities.MiscUtil;

public class SlayerPlugin extends JavaPlugin
{
	@Override
	public void onEnable()
	{
		new Slayer(this);
		FlatFile.load();
		Scheduler.startThreads();

		// Log that Slayer successfully loaded
		MiscUtil.log("info", "Slayer has been successfully enabled!");
	}

	@Override
	public void onDisable()
	{
		Scheduler.stopThreads();
		FlatFile.save();

		MiscUtil.log("info", "Slayer has been disabled.");
	}
}
