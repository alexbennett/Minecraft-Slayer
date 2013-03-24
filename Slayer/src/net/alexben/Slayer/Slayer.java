package net.alexben.Slayer;

import net.alexben.Slayer.Handlers.SCommands;
import net.alexben.Slayer.Handlers.SScheduler;
import net.alexben.Slayer.Listeners.SPlayerListener;
import net.alexben.Slayer.Utilities.Metrics;
import net.alexben.Slayer.Utilities.SConfigUtil;
import net.alexben.Slayer.Utilities.SUtil;
import org.bukkit.command.CommandExecutor;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.IOException;

public class Slayer extends JavaPlugin
{
    @Override
    public void onEnable()
    {
        // Initialize the config, scheduler, and utilities
        SConfigUtil.initialize(this);
        SUtil.initialize(this);
        SScheduler.initialize(this);

        // Load listeners and commands
        loadListeners();
        loadCommands();

        // Start metrics
        try
        {
            Metrics metrics = new Metrics(this);
            metrics.enable();
        }
        catch (IOException e)
        {
            // Metrics failed to load, log it
            SUtil.log("warning", "Plugins metrics failed to load.");
        }


        // Log that JustAFK successfully loaded
        SUtil.log("info", "Slayer has been successfully enabled!");
    }

    @Override
    public void onDisable()
    {
        SScheduler.stopThreads();

        SUtil.log("info", "Disabled!");
    }

    private void loadListeners()
    {
        getServer().getPluginManager().registerEvents(new SPlayerListener(), this);
    }

    private void loadCommands()
    {
        CommandExecutor executor = new SCommands();

        getCommand("slayer").setExecutor(executor);
    }
}
