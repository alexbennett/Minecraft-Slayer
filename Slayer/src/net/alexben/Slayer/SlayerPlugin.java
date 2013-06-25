package net.alexben.Slayer;

import net.alexben.Slayer.Core.Handlers.SFlatFile;
import net.alexben.Slayer.Core.Handlers.SScheduler;
import net.alexben.Slayer.Utilities.SMiscUtil;
import org.bukkit.plugin.java.JavaPlugin;

public class SlayerPlugin extends JavaPlugin
{
    @Override
    public void onEnable()
    {
        new Slayer(this);
        SScheduler.startThreads();

        // Log that Slayer successfully loaded
        SMiscUtil.log("info", "Slayer has been successfully enabled!");
    }

    @Override
    public void onDisable()
    {
        SFlatFile.save();
        SScheduler.stopThreads();

        SMiscUtil.log("info", "Slayer has been disabled.");
    }
}
