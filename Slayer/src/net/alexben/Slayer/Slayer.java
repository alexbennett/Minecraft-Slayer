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

import java.io.IOException;
import java.util.ArrayList;
import java.util.Map;

import net.alexben.Slayer.Handlers.SCommands;
import net.alexben.Slayer.Handlers.SFlatFile;
import net.alexben.Slayer.Handlers.SScheduler;
import net.alexben.Slayer.Libraries.ConfigAccessor;
import net.alexben.Slayer.Libraries.Metrics;
import net.alexben.Slayer.Libraries.Objects.SerialItemStack;
import net.alexben.Slayer.Libraries.Objects.Task;
import net.alexben.Slayer.Listeners.SAssignmentListener;
import net.alexben.Slayer.Listeners.SEntityListener;
import net.alexben.Slayer.Listeners.SPlayerListener;
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
		getServer().getPluginManager().registerEvents(new SAssignmentListener(), this);
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
		catch(IOException e)
		{
			// Metrics failed to load, log it
			SUtil.log("warning", "Plugins metrics failed to load.");
		}
	}

	public static void loadTasks()
	{
		// Define variables
		FileConfiguration config = Slayer.taskConfig.getConfig();
		Task newTask = null;
		int count = 0;

		for(Map<?, ?> task : config.getMapList("tasks"))
		{
			// Increase the count for logging
			count++;

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
					Map<String, Object> enchantments = (Map<String, Object>) enchObj;

					for(Map.Entry<String, Object> enchantment : enchantments.entrySet())
					{
						int level = 0;
						Enchantment enchant = Enchantment.getByName(enchantment.getKey().toUpperCase());

						// Determine the appropriate level
						if(enchantment.getValue().equals("max"))
						{
							// Use max level
							level = enchant.getMaxLevel();
						}
						else
						{
							// Use the level given
							level = SObjUtil.toInteger(enchantment.getValue());
						}

						item.addUnsafeEnchantment(enchant, level);
					}
				}

				// Add it to the reward array
				rewards.add(new SerialItemStack(item));
			}

			// Create the actual task
			if(task.get("mob") != null)
			{
				newTask = new Task(task.get("name").toString(), task.get("desc").toString(), SObjUtil.toInteger(task.get("value")), rewards, SObjUtil.toInteger(task.get("amount")), EntityType.fromName((String) task.get("mob")));
			}
			else if(task.get("item") != null)
			{
				newTask = new Task(task.get("name").toString(), task.get("desc").toString(), SObjUtil.toInteger(task.get("value")), rewards, SObjUtil.toInteger(task.get("amount")), new ItemStack(SObjUtil.toInteger(task.get("item"))));
			}

			// Load it into the instance
			STaskUtil.loadTask(newTask);
		}

		// Log the tasks loaded
		SUtil.log("info", count + " task(s) loaded into memory.");
	}
}
