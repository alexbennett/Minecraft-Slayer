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

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Map;

import net.alexben.Slayer.Handlers.SCommands;
import net.alexben.Slayer.Handlers.SFlatFile;
import net.alexben.Slayer.Handlers.SScheduler;
import net.alexben.Slayer.Libraries.BukkitUpdate;
import net.alexben.Slayer.Libraries.ConfigAccessor;
import net.alexben.Slayer.Libraries.Metrics;
import net.alexben.Slayer.Libraries.Objects.Assignment;
import net.alexben.Slayer.Libraries.Objects.SerialItemStack;
import net.alexben.Slayer.Libraries.Objects.Task;
import net.alexben.Slayer.Listeners.SAssignmentListener;
import net.alexben.Slayer.Listeners.SEntityListener;
import net.alexben.Slayer.Listeners.SPlayerListener;
import net.alexben.Slayer.Utilities.*;

import org.bukkit.Bukkit;
import org.bukkit.command.CommandExecutor;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.java.JavaPlugin;

public class Slayer extends JavaPlugin
{
	public static ConfigAccessor taskConfig, stringConfig;
	public static BukkitUpdate update;

	@Override
	public void onEnable()
	{
		// Initialize the config, scheduler, and utilities
		SConfigUtil.initialize(this);
		SMiscUtil.initialize(this);

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

		// Update players
		updatePlayers();

		// Lastly initialize the auto-updater
		update = new BukkitUpdate(this, "http://dev.bukkit.org/server-mods/slayer/files.rss", "/slayer update", "slayer.update");
		update.initialize();

		// Log that JustAFK successfully loaded
		SMiscUtil.log("info", "Slayer has been successfully enabled!");
	}

	@Override
	public void onDisable()
	{
		SFlatFile.save();
		SScheduler.stopThreads();

		SMiscUtil.log("info", "Disabled!");
	}

	private void updatePlayers()
	{
		for(Player player : Bukkit.getOnlinePlayers())
		{
			SPlayerUtil.createSave(player);
			SPlayerUtil.updateScoreboard(player);
		}
	}

	private void loadConfigs()
	{
		// LEGACY: Move the old tasks.yml config if it still exists.
		File taskFile = new File(this.getDataFolder() + File.separator + "tasks.yml");

		if(taskFile.exists())
		{
			// Create the folder and move the file
			new File(this.getDataFolder() + File.separator + "tasks").mkdir();
			taskFile.renameTo(new File(this.getDataFolder() + File.separator + "tasks" + File.separator + "tasks.yml"));

			// Log the update
			SMiscUtil.log("info", "\"task.yml\" file moved for new save system.");
		}

		// Define the configs
		taskConfig = new ConfigAccessor(this, "tasks/tasks.yml");
		stringConfig = new ConfigAccessor(this, "strings.yml");

		// Set the option to copy defaults
		stringConfig.getConfig().options().copyDefaults(true);

		// Save the defaults
		stringConfig.saveConfig();
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
		getCommand("slayer").setExecutor(executor);
		getCommand("sladmin").setExecutor(executor);
	}

	private void loadMetrics()
	{
		try
		{
			// Define the Metrics instance
			Metrics metrics = new Metrics(this);

			// Define the Graphs
			Metrics.Graph typeGraph = metrics.createGraph("Item vs. Mob Tasks");
			Metrics.Graph timedGraph = metrics.createGraph("Timed vs. Untimed Tasks");
			Metrics.Graph statusGraph = metrics.createGraph("Assignment Status Comparison");

			// Add Graph Data
			typeGraph.addPlotter(new Metrics.Plotter("Total ~=~ Item Based")
			{
				@Override
				public int getValue()
				{
					int count = 0;

					for(Assignment assignment : STaskUtil.getAllAssignments())
					{
						if(assignment.getTask().getType().equals(Task.TaskType.ITEM)) count++;
					}

					return count;
				}
			});

			typeGraph.addPlotter(new Metrics.Plotter("Total ~=~ Mob Based")
			{
				@Override
				public int getValue()
				{
					int count = 0;

					for(Assignment assignment : STaskUtil.getAllAssignments())
					{
						if(assignment.getTask().getType().equals(Task.TaskType.MOB)) count++;
					}

					return count;
				}
			});

			timedGraph.addPlotter(new Metrics.Plotter("Total ~=~ Untimed")
			{
				@Override
				public int getValue()
				{
					return STaskUtil.getAllUntimedAssignments().size();
				}
			});

			timedGraph.addPlotter(new Metrics.Plotter("Total ~=~ Timed")
			{
				@Override
				public int getValue()
				{
					return STaskUtil.getAllTimedAssignments().size();
				}
			});

			statusGraph.addPlotter(new Metrics.Plotter("Total ~=~ Completed")
			{
				@Override
				public int getValue()
				{
					return STaskUtil.getAllCompleteAssignments().size();
				}
			});

			statusGraph.addPlotter(new Metrics.Plotter("Total ~=~ Expired")
			{
				@Override
				public int getValue()
				{
					return STaskUtil.getAllExpiredAssignments().size();
				}
			});

			statusGraph.addPlotter(new Metrics.Plotter("Total ~=~ Forfeited")
			{
				@Override
				public int getValue()
				{
					return STaskUtil.getAllForfeitedAssignments().size();
				}
			});

			statusGraph.addPlotter(new Metrics.Plotter("Total ~=~ Active")
			{
				@Override
				public int getValue()
				{
					return STaskUtil.getAllActiveAssignments().size();
				}
			});

			metrics.enable();
		}
		catch(IOException e)
		{
			// Metrics failed to load, log it
			SMiscUtil.log("warning", "Plugins metrics failed to load.");
		}
	}

	private static void loadTasks()
	{
		// Define variables
		FileConfiguration config = Slayer.taskConfig.getConfig();
		Task newTask = null;
		int count = 0;

		for(Map<?, ?> task : config.getMapList("tasks"))
		{
			// If the task is disabled then ignore it and continue to the next one
			if(!SObjUtil.toBoolean(task.get("enabled"))) continue;

			// Define variables
			ArrayList<SerialItemStack> rewards = new ArrayList<SerialItemStack>();
			int timeLimit = 0;
			int level = 1;
			int value = SObjUtil.toInteger(task.get("value"));

			// Validate variables
			if(task.get("timelimit") != null && !task.get("timelimit").equals("none"))
			{
				timeLimit = SObjUtil.toInteger(task.get("timelimit"));
			}

			if(task.get("level") != null && !task.get("level").equals(0))
			{
				level = SObjUtil.toInteger(task.get("level"));
			}

			// All of this to simply handle rewards...
			if(task.get("reward") != null)
			{
				for(Object object : (ArrayList<?>) task.get("reward"))
				{
					// Define variables
					int itemID, amount = 1;
					byte itemByte = (byte) 0;

					// Cast the object to a map
					Map<String, Object> reward = (Map<String, Object>) object;

					// Update variables
					itemID = SObjUtil.toInteger(reward.get("itemid"));
					if(reward.get("itembyte") != null) itemByte = (byte) SObjUtil.toInteger(reward.get("itembyte"));
					if(reward.get("amount") != null) amount = SObjUtil.toInteger(reward.get("amount"));

					// Create the item
					ItemStack item = new ItemStack(itemID, amount, itemByte);

					// Add the enchantments if they are wanted
					if(reward.get("enchantments") != null)
					{
						for(Object enchObj : (ArrayList<?>) reward.get("enchantments"))
						{
							Map<String, Object> enchantments = (Map<String, Object>) enchObj;

							for(Map.Entry<String, Object> enchantment : enchantments.entrySet())
							{
								int enchLevel = 0;
								Enchantment enchant = Enchantment.getByName(enchantment.getKey().toUpperCase());

								// Determine the appropriate level
								if(enchantment.getValue().equals("max"))
								{
									// Use max level
									enchLevel = enchant.getMaxLevel();
								}
								else
								{
									// Use the level given
									enchLevel = SObjUtil.toInteger(enchantment.getValue());
								}

								item.addUnsafeEnchantment(enchant, enchLevel);
							}
						}
					}

					// Add it to the reward array
					rewards.add(new SerialItemStack(item));
				}
			}

			// Create the actual task
			if(task.get("mob") != null)
			{
				newTask = new Task(task.get("name").toString(), task.get("desc").toString(), timeLimit, value, level, rewards, SObjUtil.toInteger(task.get("amount")), EntityType.fromName((String) task.get("mob")));
			}
			else if(task.get("item") != null)
			{
				newTask = new Task(task.get("name").toString(), task.get("desc").toString(), timeLimit, value, level, rewards, SObjUtil.toInteger(task.get("amount")), new ItemStack(SObjUtil.toInteger(task.get("item"))));
			}

			// Increase the count for logging and load the task into the instance
			count++;
			STaskUtil.loadTask(newTask);
		}

		// Log the tasks loaded
		SMiscUtil.log("info", count + " task(s) loaded into memory.");
	}
}
