package net.alexben.Slayer.Core;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Map;

import net.alexben.Slayer.Core.Events.AssignmentRemoveEvent;
import net.alexben.Slayer.Core.Handlers.FlatFile;
import net.alexben.Slayer.Core.Objects.Assignment;
import net.alexben.Slayer.Core.Objects.ObjectFactory;
import net.alexben.Slayer.Core.Objects.SerialItemStack;
import net.alexben.Slayer.Core.Objects.Task;
import net.alexben.Slayer.Listeners.AssignmentListener;
import net.alexben.Slayer.Listeners.EntityListener;
import net.alexben.Slayer.Listeners.PlayerListener;
import net.alexben.Slayer.Modules.AutoUpdate;
import net.alexben.Slayer.Modules.ConfigAccessor;
import net.alexben.Slayer.SlayerPlugin;
import net.alexben.Slayer.Utilities.*;
import net.milkbowl.vault.Vault;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.Plugin;
import org.mcstats.Metrics;

public class Slayer
{
	// Static access
	public static SlayerPlugin plugin;

	// Modules
	public static ConfigAccessor taskConfig, stringConfig;
	public static AutoUpdate update;

	// Economy
	public static Vault vaultEcon = null;

	public Slayer(SlayerPlugin instance)
	{
		// Define public
		plugin = instance;

		// Initialize the config, scheduler, and utilities
		ConfigUtil.initialize();

		// Load things
		loadConfigs();
		loadListeners();
		loadCommands();
		loadTasks();
		loadMetrics();
		loadEconomy();

		FlatFile.load();

		// Update players
		updatePlayers();

		// Lastly initialize the auto-updater
		update = new AutoUpdate(instance, "http://dev.bukkit.org/server-mods/slayer/files.rss", "/slayer update", "slayer.update");
		update.initialize();
	}

	private void updatePlayers()
	{
		for(Player player : Bukkit.getOnlinePlayers())
		{
			PlayerUtil.createSave(player);
			PlayerUtil.updateScoreboard(player);
		}
	}

	private void loadEconomy()
	{
		// Define and grab the plugins
		Plugin vault = Slayer.plugin.getServer().getPluginManager().getPlugin("Vault");

		if(vault != null)
		{
			vaultEcon = (Vault) vault;
			MiscUtil.log("info", "[Economy] Vault detected.");
		}
	}

	private void loadConfigs()
	{
		// LEGACY: Move the old tasks.yml config if it still exists.
		File taskFile = new File(plugin.getDataFolder() + File.separator + "tasks.yml");

		if(taskFile.exists())
		{
			// Create the folder and move the file
			new File(plugin.getDataFolder() + File.separator + "tasks").mkdir();
			taskFile.renameTo(new File(plugin.getDataFolder() + File.separator + "tasks" + File.separator + "tasks.yml"));

			// Log the update
			MiscUtil.log("info", "\"task.yml\" file moved for new save system.");
		}

		// Define the configs
		taskConfig = new ConfigAccessor(plugin, "tasks/tasks.yml");
		stringConfig = new ConfigAccessor(plugin, "strings.yml");

		// Set the option to copy defaults
		stringConfig.getConfig().options().copyDefaults(true);

		// Save the defaults
		stringConfig.saveConfig();
	}

	private void loadListeners()
	{
		plugin.getServer().getPluginManager().registerEvents(new PlayerListener(), plugin);
		plugin.getServer().getPluginManager().registerEvents(new AssignmentListener(), plugin);
		plugin.getServer().getPluginManager().registerEvents(new EntityListener(), plugin);
	}

	private void loadCommands()
	{
		Commands executor = new Commands();

		plugin.getCommand("sl").setExecutor(executor);
		plugin.getCommand("slayer").setExecutor(executor);
		plugin.getCommand("sladmin").setExecutor(executor);
		plugin.getCommand("tasks").setExecutor(executor);
		plugin.getCommand("accept").setExecutor(executor);
		plugin.getCommand("mytasks").setExecutor(executor);
		plugin.getCommand("process").setExecutor(executor);
		plugin.getCommand("rewards").setExecutor(executor);
		plugin.getCommand("forfeit").setExecutor(executor);
	}

	private void loadTasks()
	{
		// Define variables
		FileConfiguration config = Slayer.taskConfig.getConfig();
		Task newTask = null;
		int count = 0;

		for(Map<?, ?> task : config.getMapList("tasks"))
		{
			// If the task is disabled then ignore it and continue to the next one
			if(!ObjUtil.toBoolean(task.get("enabled"))) continue;

			// Define variables
			int timeLimit = 0;
			int level = 1;
			int points = ObjUtil.toInteger(task.get("value"));

			// Validate variables
			if(task.get("timelimit") != null && !task.get("timelimit").equals("none"))
			{
				timeLimit = ObjUtil.toInteger(task.get("timelimit"));
			}

			if(task.get("level") != null && !task.get("level").equals(0))
			{
				level = ObjUtil.toInteger(task.get("level"));
			}

			// Create the actual task
			if(task.get("mob") != null)
			{
				newTask = ObjectFactory.createMobTask(task.get("name").toString(), task.get("desc").toString(), timeLimit, ObjUtil.toInteger(task.get("amount")), points, level, EntityType.fromName(task.get("mob").toString()));
			}
			else if(task.get("item") != null)
			{
				newTask = ObjectFactory.createItemTask(task.get("name").toString(), task.get("desc").toString(), timeLimit, ObjUtil.toInteger(task.get("amount")), points, level, ObjUtil.toInteger(task.get("item")));
			}

			// Handle rewards...
			if(task.get("reward") != null)
			{
				for(Object object : (ArrayList<?>) task.get("reward"))
				{
					// Cast the object to a map
					Map<String, Object> reward = (Map<String, Object>) object;

					// Handle item reward
					if(reward.get("itemid") != null)
					{
						// Define variables
						ArrayList<SerialItemStack> itemRewards = new ArrayList<SerialItemStack>();
						int itemID, amount = 1;
						byte itemByte = (byte) 0;

						// Update variables
						itemID = ObjUtil.toInteger(reward.get("itemid"));
						if(reward.get("itembyte") != null) itemByte = (byte) ObjUtil.toInteger(reward.get("itembyte"));
						if(reward.get("amount") != null) amount = ObjUtil.toInteger(reward.get("amount"));

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
										enchLevel = ObjUtil.toInteger(enchantment.getValue());
									}

									item.addUnsafeEnchantment(enchant, enchLevel);
								}
							}
						}

						// Add it to the reward array
						itemRewards.add(new SerialItemStack(item));

						// Apply the reward
						newTask.setItemReward(itemRewards);
					}

				}
			}

			// Increase the count for logging and load the task into the instance
			count++;
			TaskUtil.loadTask(newTask);
		}

		// Log the tasks loaded
		MiscUtil.log("info", count + " task(s) loaded into memory.");
	}

	private void loadMetrics()
	{
		try
		{
			// Define the MetricsModule instance
			Metrics metrics = new Metrics(plugin);

			// Define the Graphs
			Metrics.Graph typeGraph = metrics.createGraph("Item vs. Mob Tasks");
			Metrics.Graph timedGraph = metrics.createGraph("Timed vs. Untimed Tasks");
			Metrics.Graph statusGraph = metrics.createGraph("Assignment Status Comparison");

			// Add Graph Data
			typeGraph.addPlotter(new Metrics.Plotter("Item Based")
			{
				@Override
				public int getValue()
				{
					int count = 0;

					for(Assignment assignment : TaskUtil.getAllAssignments())
					{
						if(assignment.getTask().getType().equals(Task.TaskType.ITEM)) count++;
					}

					return count;
				}
			});

			typeGraph.addPlotter(new Metrics.Plotter("Mob Based")
			{
				@Override
				public int getValue()
				{
					int count = 0;

					for(Assignment assignment : TaskUtil.getAllAssignments())
					{
						if(assignment.getTask().getType().equals(Task.TaskType.MOB)) count++;
					}

					return count;
				}
			});

			timedGraph.addPlotter(new Metrics.Plotter("Untimed")
			{
				@Override
				public int getValue()
				{
					return TaskUtil.getAllUntimedAssignments().size();
				}
			});

			timedGraph.addPlotter(new Metrics.Plotter("Timed")
			{
				@Override
				public int getValue()
				{
					return TaskUtil.getAllTimedAssignments().size();
				}
			});

			statusGraph.addPlotter(new Metrics.Plotter("Completed")
			{
				@Override
				public int getValue()
				{
					return TaskUtil.getAllCompleteAssignments().size();
				}
			});

			statusGraph.addPlotter(new Metrics.Plotter("Expired")
			{
				@Override
				public int getValue()
				{
					return TaskUtil.getAllExpiredAssignments().size();
				}
			});

			statusGraph.addPlotter(new Metrics.Plotter("Forfeited")
			{
				@Override
				public int getValue()
				{
					return TaskUtil.getAllForfeitedAssignments().size();
				}
			});

			statusGraph.addPlotter(new Metrics.Plotter("Active")
			{
				@Override
				public int getValue()
				{
					return TaskUtil.getAllActiveAssignments().size();
				}
			});

			metrics.enable();
		}
		catch(IOException e)
		{
			// MetricsModule failed to load, log it
			MiscUtil.log("warning", "Plugins metrics failed to load.");
		}
	}
}

class Commands implements CommandExecutor
{
	@Override
	public boolean onCommand(CommandSender sender, Command command, String label, String[] args)
	{
		Player player = (Player) sender;

		// Register admin/miscellaneous commands
		if(command.getName().equalsIgnoreCase("slayer")) return slayer(player, args);
		if(command.getName().equalsIgnoreCase("sladmin")) return sl_admin(player, args);
		if(command.getName().equalsIgnoreCase("sl")) return sl(player, args);

		// Register main commands
		if(command.getName().equalsIgnoreCase("tasks")) return tasks(player);
		if(command.getName().equalsIgnoreCase("rewards")) return myRewards(player);
		if(command.getName().equalsIgnoreCase("mytasks")) return myTasks(player);
		if(command.getName().equalsIgnoreCase("accept")) return accept(player);
		if(command.getName().equalsIgnoreCase("process")) return process(player);
		if(command.getName().equalsIgnoreCase("forfeit")) return forfeit(player, args);

		return false;
	}

	/**
	 * "/slayer" command handling.
	 */
	public boolean slayer(Player player, String args[])
	{
		if(args.length == 0)
		{
			player.sendMessage(ChatColor.GRAY + "-----------------------------------------------------");
			player.sendMessage(ChatColor.RED + "net/alexben/Slayer" + ChatColor.GRAY + " is a plugin developed on the Bukkit platform for");
			player.sendMessage(ChatColor.GRAY + " Minecraft Survival Multiplayer with the intentions of bringing a");
			player.sendMessage(ChatColor.GRAY + "      full, easily expandable task system to the battlefield.");
			player.sendMessage(" ");
			player.sendMessage(ChatColor.GRAY + "    For support or to suggest new features, visit the " + ChatColor.RED + "net/alexben/Slayer");
			player.sendMessage(ChatColor.GRAY + "     BukkitDev project page located at the link given below.");
			player.sendMessage(" ");
			player.sendMessage(ChatColor.GRAY + " Author: " + ChatColor.AQUA + "_Alex " + ChatColor.GRAY + "(" + ChatColor.AQUA + "http://alexben.net/t" + ChatColor.GRAY + ")");
			player.sendMessage(ChatColor.GRAY + " Source: " + ChatColor.AQUA + "http://github.com/alexbennett/Minecraft-Slayer/");
			player.sendMessage(ChatColor.GRAY + " BukkitDev: " + ChatColor.AQUA + "http://dev.bukkit.org/server-mods/slayer/");
			player.sendMessage(ChatColor.GRAY + "-----------------------------------------------------");

			return true;
		}
		else if(args[0].equalsIgnoreCase("update"))
		{
			// Check Permissions
			if(!MiscUtil.hasPermissionOrOP(player, "slayer.update")) return MiscUtil.noPermission(player);

			if(args.length == 1)
			{
				MiscUtil.sendMsg(player, "Latest Slayer version: " + ChatColor.YELLOW + Slayer.update.getLatestVersion());
				MiscUtil.sendMsg(player, "Server Slayer version: " + ChatColor.YELLOW + Slayer.plugin.getDescription().getVersion());

				if(Slayer.update.check() || !Slayer.update.supported())
				{
					MiscUtil.sendMsg(player, "Please type " + ChatColor.GOLD + "/slayer update confirm" + ChatColor.WHITE + " to update.");
				}
				else
				{
					MiscUtil.sendMsg(player, ChatColor.GREEN + "This server is running the latest version!");
				}

				return true;
			}
			else if(args[1].equalsIgnoreCase("confirm"))
			{
				if(!Slayer.update.check()) return true;

				MiscUtil.sendMsg(player, "Starting download...");

				if(Slayer.update.download())
				{
					MiscUtil.sendMsg(player, ChatColor.GREEN + "Download complete! " + ChatColor.WHITE + "Reload to apply the changes.");
				}
				else
				{
					MiscUtil.sendMsg(player, ChatColor.RED + "Download failed...");
				}

				return true;
			}
		}

		return false;
	}

	/**
	 * "/sl" command handling.
	 */
	public boolean sl(Player player, String[] args)
	{
		// Check permissions
		if(!MiscUtil.hasPermissionOrOP(player, "slayer.basic")) return MiscUtil.noPermission(player);

		// Handle the command
		if(args.length == 0)
		{
			MiscUtil.sendMsg(player, "Command Directory");
			player.sendMessage(ChatColor.GRAY + "" + ChatColor.ITALIC + "/tasks");
			player.sendMessage(ChatColor.GRAY + "" + ChatColor.ITALIC + "/process");
			player.sendMessage(ChatColor.GRAY + "" + ChatColor.ITALIC + "/rewards");
			player.sendMessage(ChatColor.GRAY + "" + ChatColor.ITALIC + "/sl my tasks");
			player.sendMessage(ChatColor.GRAY + "" + ChatColor.ITALIC + "/sl my stats");
			player.sendMessage(ChatColor.GRAY + "" + ChatColor.ITALIC + "/sl leaderboard");
			player.sendMessage(ChatColor.GRAY + "" + ChatColor.ITALIC + "/sl scoreboard");
			player.sendMessage(ChatColor.GRAY + "" + ChatColor.ITALIC + "/forfeit <assignment #>");
			if(MiscUtil.hasPermissionOrOP(player, "slayer.admin")) player.sendMessage(ChatColor.RED + "" + ChatColor.ITALIC + "/sladmin");

			return true;
		}
		else
		{
			String action, category = null;

			action = args[0];
			if(args.length > 1) category = args[1];

			if(action.equalsIgnoreCase("process"))
			{
				return process(player);
			}
			else if(action.equalsIgnoreCase("accept"))
			{
				return accept(player);
			}
			else if(action.equalsIgnoreCase("scoreboard"))
			{
				return scoreboard(player);
			}
			else if(action.equalsIgnoreCase("forfeit"))
			{
				return forfeit(player, args);
			}
			else if(action.equalsIgnoreCase("tasks"))
			{
				return tasks(player);
			}
			else if(action.equalsIgnoreCase("leaderboard"))
			{
				return leaderboard(player);
			}
			else if(action.equalsIgnoreCase("my"))
			{
				if(category.equalsIgnoreCase("stats"))
				{
					return myStats(player);
				}
				else if(category.equalsIgnoreCase("tasks"))
				{
					return myTasks(player);
				}
				else if(category.equalsIgnoreCase("rewards"))
				{
					return myRewards(player);
				}
			}
		}

		MiscUtil.sendMsg(player, ChatColor.GRAY + "Improper use of " + ChatColor.GOLD + "/sl" + ChatColor.GRAY + ". Please try again.");
		return true;
	}

	/**
	 * Accepts the task that the user clicked.
	 * 
	 * @param player the player to accept for.
	 * @return boolean
	 */
	public boolean accept(Player player)
	{
		// Check permissions
		if(!MiscUtil.hasPermissionOrOP(player, "slayer.basic")) return MiscUtil.noPermission(player);

		// Make sure they actually have a selected task to process
		if(!DataUtil.hasData(player, "clicked_task"))
		{
			MiscUtil.sendMsg(player, ChatColor.GRAY + MiscUtil.getString("no_selected_task").replace("{command}", ChatColor.GOLD + "/tasks" + ChatColor.GRAY));
			return true;
		}

		// Make sure they haven't met the limit
		if(TaskUtil.getActiveAssignments(player).size() >= ConfigUtil.getSettingInt("tasks.limit"))
		{
			// Met the limit, deny them!
			MiscUtil.sendMsg(player, ChatColor.GRAY + MiscUtil.getString("has_met_limit"));
			return true;
		}
		else
		{
			// They still have room for more tasks, attempt to give them what they chose
			Task task = (Task) DataUtil.getData(player, "clicked_task");

			// They don't have the task, give it to them
			TaskUtil.assignTask(player, task);

			// Clear the temporary data
			DataUtil.removeData(player, "clicked_task");

			return true;
		}
	}

	/**
	 * Sends the <code>player</code> a list of their available tasks.
	 * 
	 * @param player the player to send to.
	 * @return boolean
	 */
	public boolean tasks(Player player)
	{
		// Check permissions
		if(!MiscUtil.hasPermissionOrOP(player, "slayer.basic")) return MiscUtil.noPermission(player);

		// Open the player's task inventory
		PlayerUtil.openTaskInventory(player);
		return true;
	}

	/**
	 * Forfeits a task for the <code>player</code>.
	 * 
	 * @param player the player to forfeit for.
	 * @return boolean
	 */
	public boolean forfeit(Player player, String[] args)
	{
		// Check permissions
		if(!MiscUtil.hasPermissionOrOP(player, "slayer.basic")) return MiscUtil.noPermission(player);

		// If this command is disabled then return
		if(!ConfigUtil.getSettingBoolean("forfeit.enable"))
		{
			MiscUtil.sendMsg(player, ChatColor.RED + MiscUtil.getString("disabled_functionality"));

			return true;
		}

		// If they're using it wrong tell 'em
		if(args.length != 1)
		{
			MiscUtil.sendMsg(player, "Improper usage. " + ChatColor.GOLD + "/forfeit <assignment #>");
			return true;
		}

		// Define variables
		int assignmentID = ObjUtil.toInteger(args[0]);

		// They want to forfeit, let them have their way!
		if(TaskUtil.hasAssignment(player, assignmentID))
		{
			// They have the assignment matching the given id, remove it
			TaskUtil.forfeitAssignment(player, assignmentID);
		}

		return true;
	}

	/**
	 * Enables or disabled the <code>player</code>'s scoreboard.
	 * 
	 * @param player the player to accept for.
	 * @return boolean
	 */
	public boolean scoreboard(Player player)
	{
		// Check permissions
		if(!MiscUtil.hasPermissionOrOP(player, "slayer.basic")) return MiscUtil.noPermission(player);

		if(PlayerUtil.scoreboardEnabled(player))
		{
			PlayerUtil.toggleScoreboard(player, false);
			MiscUtil.sendMsg(player, ChatColor.GRAY + MiscUtil.getString("scoreboard_disabled"));
		}
		else
		{
			PlayerUtil.toggleScoreboard(player, true);
			MiscUtil.sendMsg(player, ChatColor.GRAY + MiscUtil.getString("scoreboard_enabled"));
		}

		return true;
	}

	/**
	 * Opens an item processing inventory for the <code>player</code>.
	 * 
	 * @param player the player to open the inventory for.
	 * @return boolean
	 */
	public boolean process(Player player)
	{
		// Check permissions
		if(!MiscUtil.hasPermissionOrOP(player, "slayer.basic")) return MiscUtil.noPermission(player);

		// Open their item processing inventory
		PlayerUtil.openProcessingInventory(player);
		return true;
	}

	/**
	 * Shows the <code>player</code> a leaderboard for net.alexben.Slayer.
	 * 
	 * @param player the player to show the leaderboard to.
	 * @return boolean
	 */
	public boolean leaderboard(Player player)
	{
		// Check permissions
		if(!MiscUtil.hasPermissionOrOP(player, "slayer.basic")) return MiscUtil.noPermission(player);

		// TODO: Make this work.
		MiscUtil.sendMsg(player, ChatColor.AQUA + "Coming Soon!");
		return true;
	}

	/**
	 * Sends the <code>player</code> their detailed slayer info.
	 * 
	 * @param player the player to send the info to.
	 * @return boolean
	 */
	public boolean myStats(Player player)
	{
		// Check permissions
		if(!MiscUtil.hasPermissionOrOP(player, "slayer.basic")) return MiscUtil.noPermission(player);

		// TODO: Work on statistics.

		MiscUtil.sendMsg(player, "My Information");
		player.sendMessage(" ");
		player.sendMessage("  > Assignments:");
		player.sendMessage(" ");
		player.sendMessage("     - Active: " + ChatColor.YELLOW + TaskUtil.getActiveAssignments(player).size());
		player.sendMessage("     - Completions: " + ChatColor.GREEN + PlayerUtil.getCompletions(player));
		player.sendMessage("     - Forfeits: " + ChatColor.RED + PlayerUtil.getForfeits(player));
		player.sendMessage("     - Expirations: " + ChatColor.RED + PlayerUtil.getExpirations(player));
		player.sendMessage("     - Total Given: " + ChatColor.GREEN + PlayerUtil.getTotalAssignments(player));
		player.sendMessage(" ");
		player.sendMessage("  > Available Rewards: " + ChatColor.GREEN + PlayerUtil.getRewardAmount(player));
		player.sendMessage(" ");
		player.sendMessage("  > Statistics:");
		player.sendMessage(" ");
		player.sendMessage("     - Level: " + ChatColor.LIGHT_PURPLE + PlayerUtil.getLevel(player));
		player.sendMessage("     - Points: " + ChatColor.YELLOW + PlayerUtil.getPoints(player) + ChatColor.GRAY + " (" + ChatColor.YELLOW + ((int) PlayerUtil.getPointsGoal(player) - PlayerUtil.getPoints(player)) + ChatColor.GRAY + " until level " + ((int) PlayerUtil.getLevel(player) + 1) + ")");
		player.sendMessage("     - Rank: " + ChatColor.AQUA + "Coming Soon!");
		player.sendMessage(" ");

		return true;
	}

	/**
	 * Sends the <code>player</code> a list of their active tasks.
	 * 
	 * @param player the player to send the list to.
	 * @return boolean
	 */
	public boolean myTasks(Player player)
	{
		// Check permissions
		if(!MiscUtil.hasPermissionOrOP(player, "slayer.basic")) return MiscUtil.noPermission(player);

		// Return a message if they have no assignments.
		if(TaskUtil.getActiveAssignments(player) == null || TaskUtil.getActiveAssignments(player).isEmpty())
		{
			MiscUtil.sendMsg(player, ChatColor.GRAY + MiscUtil.getString("no_tasks"));
			return true;
		}

		// Define variables
		ArrayList<Assignment> assignments = TaskUtil.getDisplayedAssignments(player);

		player.sendMessage(" ");
		if(assignments.size() > 1) MiscUtil.sendMsg(player, MiscUtil.getString("current_assignments_are"));
		else MiscUtil.sendMsg(player, MiscUtil.getString("current_assignment_is"));
		player.sendMessage(" ");

		// List the tasks
		for(Assignment assignment : assignments)
		{
			String color = "" + ChatColor.AQUA;
			if(!assignment.isActive()) color = "" + ChatColor.GRAY;
			if(assignment.isComplete()) color = "" + ChatColor.AQUA;
			if(assignment.isExpired()) color = ChatColor.RED + "" + ChatColor.STRIKETHROUGH;
			if(assignment.isForfeited()) color = ChatColor.DARK_GRAY + "" + ChatColor.STRIKETHROUGH;

			String timeLimit = "", miscTag = "";
			if(assignment.getTask().isTimed() && assignment.isActive())
			{
				timeLimit = ChatColor.GRAY + " (Time Remaining: " + ChatColor.RED + assignment.getTimeLeft() + " minutes" + ChatColor.GRAY + ")";
			}
			if(assignment.isForfeited()) miscTag = ChatColor.RED + " [FORFEITED]";
			else if(assignment.isExpired()) miscTag = ChatColor.RED + " [EXPIRED]";
			else if(assignment.isComplete()) miscTag = ChatColor.GREEN + " [COMPLETE]";

			player.sendMessage(ChatColor.GRAY + " > " + color + assignment.getTask().getName() + ChatColor.RESET + timeLimit + miscTag);
			player.sendMessage(ChatColor.GRAY + "     - Level: " + ChatColor.DARK_PURPLE + assignment.getTask().getLevel());
			player.sendMessage(ChatColor.GRAY + "     - Points: " + ChatColor.GREEN + assignment.getTask().getValue());

			if(!assignment.isExpired() && !assignment.isFailed() && !assignment.isComplete() && !assignment.isForfeited())
			{
				if(assignment.getTask().getType().equals(Task.TaskType.MOB))
				{
					player.sendMessage(ChatColor.GRAY + "     - Mob: " + ChatColor.YELLOW + ObjUtil.capitalize(assignment.getTask().getMob().getName().toLowerCase()));
					player.sendMessage(ChatColor.GRAY + "     - Kills: " + ChatColor.YELLOW + assignment.getAmountObtained() + ChatColor.GRAY + "/" + ChatColor.YELLOW + assignment.getAmountNeeded());
				}
				else if(assignment.getTask().getType().equals(Task.TaskType.ITEM))
				{
					player.sendMessage(ChatColor.GRAY + "     - Item: " + ChatColor.YELLOW + ObjUtil.capitalize(assignment.getTask().getItemType().name().toLowerCase().replace("_", " ")));
					player.sendMessage(ChatColor.GRAY + "     - Obtained: " + ChatColor.YELLOW + assignment.getAmountObtained() + ChatColor.GRAY + "/" + ChatColor.YELLOW + assignment.getAmountNeeded());
				}
				player.sendMessage(ChatColor.GRAY + "     - Assignment #: " + ChatColor.YELLOW + assignment.getID());
			}
			player.sendMessage(ChatColor.GRAY + "     - Description: " + ChatColor.YELLOW + assignment.getTask().getDesc());
			player.sendMessage(" ");
		}

		player.sendMessage(" ");

		return true;
	}

	/**
	 * Opens the <code>player</code>'s reward inventory.
	 * 
	 * @param player the player to open the inventory for.
	 * @return boolean
	 */
	public boolean myRewards(Player player)
	{
		// Check permissions
		if(!MiscUtil.hasPermissionOrOP(player, "slayer.basic")) return MiscUtil.noPermission(player);

		ArrayList<ItemStack> rewards = PlayerUtil.getRewards(player);

		// If they don't have rewards then tell them and return
		if(rewards.isEmpty())
		{
			MiscUtil.sendMsg(player, ChatColor.GRAY + MiscUtil.getString("no_rewards"));
			return true;
		}

		PlayerUtil.openRewardBackpack(player);

		return true;
	}

	/**
	 * Handles all admin-specific commands.
	 */
	public boolean sl_admin(Player player, String[] args)
	{
		// Check permissions
		if(!MiscUtil.hasPermissionOrOP(player, "slayer.admin")) return MiscUtil.noPermission(player);

		if(args.length == 0)
		{
			MiscUtil.sendMsg(player, "Admin Directory");
			player.sendMessage(ChatColor.GRAY + "" + ChatColor.ITALIC + "/sladmin save");
			player.sendMessage(ChatColor.GRAY + "" + ChatColor.ITALIC + "/sladmin clear entities");
			player.sendMessage(ChatColor.GRAY + "" + ChatColor.ITALIC + "/sladmin reset player <player>");
			player.sendMessage(ChatColor.GRAY + "" + ChatColor.ITALIC + "/sladmin remove task <player> <id>");
			player.sendMessage(ChatColor.GRAY + "" + ChatColor.ITALIC + "/sladmin set points <player> <points>");
			player.sendMessage(ChatColor.GRAY + "" + ChatColor.ITALIC + "/sladmin set level <player> <level>");

			return true;
		}
		else
		{
			String action, category = null, option1 = null, option2 = null;

			action = args[0];
			if(args.length > 1) category = args[1];
			if(args.length > 2) option1 = args[2];
			if(args.length > 3) option2 = args[3];

			if(action.equalsIgnoreCase("remove"))
			{
				if(category.equalsIgnoreCase("task"))
				{
					if(option1 == null || option2 == null)
					{
						MiscUtil.sendMsg(player, ChatColor.GRAY + "Improper use of " + ChatColor.GOLD + "/sladmin remove task" + ChatColor.GRAY + ". Please try again.");
						return true;
					}

					OfflinePlayer editing = Bukkit.getOfflinePlayer(option1);
					int taskID = ObjUtil.toInteger(option2);

					if(TaskUtil.removeAssignment(editing, taskID, AssignmentRemoveEvent.RemoveReason.ADMIN))
					{
						MiscUtil.sendAdminMsg(player, ChatColor.GREEN + MiscUtil.getString("assignment_removed").replace("{taskid}", "" + taskID));
					}
					else
					{
						MiscUtil.sendAdminMsg(player, ChatColor.RED + MiscUtil.getString("assignment_not_removed"));
					}
					return true;
				}
			}
			else if(action.equalsIgnoreCase("set"))
			{
				if(category == null)
				{
					MiscUtil.sendMsg(player, ChatColor.GRAY + "Improper use of " + ChatColor.GOLD + "/sladmin set" + ChatColor.GRAY + ". Please try again.");
					return true;
				}

				if(category.equalsIgnoreCase("points"))
				{
					OfflinePlayer editing = Bukkit.getOfflinePlayer(option1);
					int points = ObjUtil.toInteger(option2);

					// Set the points and update their scoreboard if they're online
					PlayerUtil.setPoints(editing, points);

					if(editing.isOnline())
					{
						PlayerUtil.updateScoreboard(editing.getPlayer());
					}

					// Message the admin
					MiscUtil.sendAdminMsg(player, ChatColor.GREEN + MiscUtil.getString("points_set_success").replace("{player}", editing.getName()).replace("{points}", "" + points));

					return true;
				}
				else if(category.equalsIgnoreCase("level"))
				{
					OfflinePlayer editing = Bukkit.getOfflinePlayer(option1);
					int level = ObjUtil.toInteger(option2);

					// Set the level and update their scoreboard if they're online
					PlayerUtil.setLevel(editing, level);

					if(editing.isOnline())
					{
						PlayerUtil.updateScoreboard(editing.getPlayer());
					}

					// Message the admin
					MiscUtil.sendAdminMsg(player, ChatColor.GREEN + MiscUtil.getString("level_set_success").replace("{player}", editing.getName()).replace("{level}", "" + level));

					return true;
				}
			}
			else if(action.equalsIgnoreCase("clear"))
			{
				if(category.equalsIgnoreCase("entities"))
				{
					MiscUtil.sendAdminMsg(player, ChatColor.YELLOW + MiscUtil.getString("clearing_entities"));
					EntityUtil.getEntityMap().clear();
					MiscUtil.sendAdminMsg(player, ChatColor.GREEN + MiscUtil.getString("entities_cleared"));

					return true;
				}
			}
			else if(action.equalsIgnoreCase("save"))
			{
				MiscUtil.sendAdminMsg(player, ChatColor.YELLOW + MiscUtil.getString("forcing_save"));

				if(FlatFile.save())
				{
					MiscUtil.sendAdminMsg(player, ChatColor.GREEN + MiscUtil.getString("save_success"));
				}
				else
				{
					MiscUtil.sendAdminMsg(player, ChatColor.RED + MiscUtil.getString("save_failure"));
				}

				return true;
			}
			else if(action.equalsIgnoreCase("reset"))
			{
				if(category == null)
				{
					MiscUtil.sendMsg(player, ChatColor.GRAY + "Improper use of " + ChatColor.GOLD + "/sladmin reset" + ChatColor.GRAY + ". Please try again.");
					return true;
				}

				if(category.equalsIgnoreCase("player"))
				{
					OfflinePlayer editing = Bukkit.getOfflinePlayer(option1);

					// Remove all of the data
					DataUtil.removeAllData(editing);
					PlayerUtil.createSave(editing);

					// Update their scoreboard if they're online
					if(player.isOnline())
					{
						PlayerUtil.updateScoreboard(player.getPlayer());
					}

					// Message the admin
					MiscUtil.sendAdminMsg(player, ChatColor.GREEN + "All Slayer data has been reset for " + editing.getName() + ".");

					return true;
				}
			}
		}

		return false;
	}
}
