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
import net.alexben.Slayer.Handlers.SFlatFile;
import net.alexben.Slayer.Handlers.SScheduler;
import net.alexben.Slayer.Libraries.ConfigAccessor;
import net.alexben.Slayer.Libraries.Metrics;
import net.alexben.Slayer.Libraries.Objects.SerialItemStack;
import net.alexben.Slayer.Libraries.Objects.Task;
import net.alexben.Slayer.Listeners.SEntityListener;
import net.alexben.Slayer.Listeners.SPlayerListener;
import net.alexben.Slayer.Listeners.STaskListener;
import net.alexben.Slayer.Utilities.SConfigUtil;
import net.alexben.Slayer.Utilities.SObjUtil;
import net.alexben.Slayer.Utilities.STaskUtil;
import net.alexben.Slayer.Utilities.SUtil;
import org.bukkit.command.CommandExecutor;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.EntityType;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Map;

public class Slayer extends JavaPlugin
{
    public static ConfigAccessor taskConfig;

    @Override
    public void onEnable()
    {
        // Initialize the config, scheduler, and utilities
        SConfigUtil.initialize(this);
        SUtil.initialize(this);

        // Load things
        loadConfigs();
        loadListeners();
        loadCommands();
        loadMetrics();
        loadTasks();

        // Start the scheduler
        SScheduler.startThreads();

        // Load data
        SFlatFile.load();

        // Log that JustAFK successfully loaded
        SUtil.log("info", "Slayer has been successfully enabled!");
    }

    @Override
    public void onDisable()
    {
        SFlatFile.save();
        SScheduler.stopThreads();

        SUtil.log("info", "Disabled!");
    }

    private void loadConfigs()
    {
        taskConfig = new ConfigAccessor(this, "tasks.yml");
    }

    private void loadListeners()
    {
        getServer().getPluginManager().registerEvents(new SPlayerListener(), this);
        getServer().getPluginManager().registerEvents(new STaskListener(), this);
        getServer().getPluginManager().registerEvents(new SEntityListener(), this);
    }

    private void loadCommands()
    {
        CommandExecutor executor = new SCommands();

        getCommand("sl").setExecutor(executor);
        getCommand("sladmin").setExecutor(executor);
    }

    private void loadMetrics()
    {
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
    }

    public static void loadTasks()
    {
        FileConfiguration config = Slayer.taskConfig.getConfig();

        for(Map<?, ?> task : config.getMapList("tasks"))
        {
            ArrayList<SerialItemStack> rewards = new ArrayList<SerialItemStack>();

            // All of this to simply handle rewards...
            for(Object object : (ArrayList<?>) task.get("reward"))
            {
                // Cast the object to a map
                Map<String, Object> reward = (Map<String, Object>) object;

                // Create the item
                ItemStack item = new ItemStack(SObjUtil.toInteger(reward.get("itemid")), SObjUtil.toInteger(reward.get("amount")));

                // Add the enchantments
                for(Object enchObj : (ArrayList<?>) reward.get("enchantments"))
                {
                    Map<String, Integer> enchantments = (Map<String, Integer>) enchObj;

                    for(Map.Entry<String, Integer> enchantment : enchantments.entrySet())
                    {
                        Enchantment enchant = Enchantment.getByName(enchantment.getKey().toUpperCase());
                        int level = enchantment.getValue();
                        if(level > enchant.getMaxLevel()) level = enchant.getMaxLevel();

                        item.addEnchantment(enchant, level);
                    }
                }

                // Add it to the reward array
                rewards.add(new SerialItemStack(item));
            }

            // Create the actual task
            Task newTask = new Task(task.get("name").toString(), task.get("desc").toString(), SObjUtil.toInteger(task.get("value")), rewards, SObjUtil.toInteger(task.get("amount")), EntityType.fromName((String) task.get("mob")));

            // Load it into the instance
            STaskUtil.loadTask(newTask);
        }
    }
}
