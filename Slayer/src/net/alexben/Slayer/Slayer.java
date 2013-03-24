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
