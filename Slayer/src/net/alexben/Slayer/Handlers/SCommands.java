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

package net.alexben.Slayer.Handlers;

import java.util.ArrayList;

import net.alexben.Slayer.Events.AssignmentRemoveEvent;
import net.alexben.Slayer.Libraries.Objects.Assignment;
import net.alexben.Slayer.Libraries.Objects.Task;
import net.alexben.Slayer.Utilities.*;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

public class SCommands implements CommandExecutor
{
	@Override
	public boolean onCommand(CommandSender sender, Command command, String label, String[] args)
	{
		Player player = (Player) sender;

		if(command.getName().equalsIgnoreCase("sladmin")) return sl_admin(player, args);
		if(command.getName().equalsIgnoreCase("sl")) return slayer(player, args);
		if(command.getName().equalsIgnoreCase("slayer"))
		{
			if(args.length == 0)
			{
				player.sendMessage(ChatColor.GRAY + "-----------------------------------------------------");
				player.sendMessage(ChatColor.RED + "     Slayer" + ChatColor.GRAY + " is a plugin developed on the Bukkit platform for");
				player.sendMessage(ChatColor.GRAY + " Minecraft Survival Multiplayer with the intentions of bringing a");
				player.sendMessage(ChatColor.GRAY + "      full, easily expandable task system to the battlefield.");
				player.sendMessage(" ");
				player.sendMessage(ChatColor.GRAY + "    For support or to suggest new features, visit the " + ChatColor.RED + "Slayer");
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
				if(!SMiscUtil.hasPermissionOrOP(player, "slayer.update"))
				{
					SMiscUtil.sendMsg(player, ChatColor.RED + "You don't have permission to do that.");
				}

				if(args.length == 1)
				{
					SMiscUtil.sendMsg(player, "Latest Slayer version: " + ChatColor.GREEN + SUpdateUtil.getLatestVersion());
					SMiscUtil.sendMsg(player, "Current Slayer version: " + ChatColor.RED + SMiscUtil.getInstance().getDescription().getVersion());

					if(SUpdateUtil.check())
					{
						SMiscUtil.sendMsg(player, "Please type " + ChatColor.GOLD + "/slayer update confirm" + ChatColor.WHITE + " to update.");
					}
					else
					{
						SMiscUtil.sendMsg(player, ChatColor.GREEN + "This server is running the latest version!");
					}

					return true;
				}
				else if(args[1].equalsIgnoreCase("confirm"))
				{
					if(!SUpdateUtil.check()) return true;

					SMiscUtil.sendMsg(player, "Starting download...");

					if(SUpdateUtil.execute())
					{
						SMiscUtil.sendMsg(player, ChatColor.GREEN + "Download complete! " + ChatColor.WHITE + "Reload to apply the changes.");
						return true;
					}
					else
					{
						SMiscUtil.sendMsg(player, ChatColor.RED + "Download failed...");
					}
				}
			}
		}

		return false;
	}

	/**
	 * Handles all basic commands.
	 */
	public boolean slayer(Player player, String[] args)
	{
		// Check permissions
		if(!SMiscUtil.hasPermissionOrOP(player, "slayer.basic")) return SMiscUtil.noPermission(player);

		if(args.length == 0)
		{
			SMiscUtil.sendMsg(player, "Command Directory");
			player.sendMessage(ChatColor.GRAY + "" + ChatColor.ITALIC + "/sl leaderboard");
			player.sendMessage(ChatColor.GRAY + "" + ChatColor.ITALIC + "/sl tasks");
			player.sendMessage(ChatColor.GRAY + "" + ChatColor.ITALIC + "/sl get task");
			player.sendMessage(ChatColor.GRAY + "" + ChatColor.ITALIC + "/sl process");
			player.sendMessage(ChatColor.GRAY + "" + ChatColor.ITALIC + "/sl my tasks");
			player.sendMessage(ChatColor.GRAY + "" + ChatColor.ITALIC + "/sl my rewards");
			player.sendMessage(ChatColor.GRAY + "" + ChatColor.ITALIC + "/sl my info");
			player.sendMessage(ChatColor.GRAY + "" + ChatColor.ITALIC + "/sl forfeit task <assignment #>");
			if(SMiscUtil.hasPermissionOrOP(player, "slayer.admin")) player.sendMessage(ChatColor.RED + "" + ChatColor.ITALIC + "/sladmin");

			return true;
		}
		else
		{
			String action, category = null, option1 = null, option2 = null;

			action = args[0];
			if(args.length > 1) category = args[1];
			if(args.length > 2) option1 = args[2];
			if(args.length > 3) option2 = args[3];

			if(action.equalsIgnoreCase("process"))
			{
				SPlayerUtil.openProcessingInventory(player);

				return true;
			}
			if(action.equalsIgnoreCase("get"))
			{
				if(category == null)
				{
					SMiscUtil.sendMsg(player, "Improper usage. " + ChatColor.GOLD + "/sl get task");

					return true;
				}

				if(category.equalsIgnoreCase("task"))
				{
					if(STaskUtil.getActiveAssignments(player).size() >= SConfigUtil.getSettingInt("tasks.limit"))
					{
						// They've already met the task limit, tell 'em
						SMiscUtil.sendMsg(player, SMiscUtil.getString("has_met_limit"));

						return true;
					}
					else
					{
						STaskUtil.assignRandomTask(player);

						return true;
					}
				}
			}
			else if(action.equalsIgnoreCase("forfeit"))
			{
				// If this command is disabled then return
				if(!SConfigUtil.getSettingBoolean("forfeit.enable"))
				{
					SMiscUtil.sendMsg(player, "That functionality is disabled.");

					return true;
				}

				// If they're using it wrong tell 'em
				if(category == null || option1 == null)
				{
					SMiscUtil.sendMsg(player, "Improper usage. " + ChatColor.GOLD + "/sl forfeit task <assignment #>");

					return true;
				}

				// Define variables
				int assignmentID = SObjUtil.toInteger(option1);
				Assignment assignment = STaskUtil.getAssignment(player, assignmentID);

				if(category.equalsIgnoreCase("task"))
				{
					// They want to forfeit, let them have their way!
					if(STaskUtil.hasAssignment(player, assignmentID))
					{
						// They have the assignment matching the given id, remove it
						STaskUtil.forfeitAssignment(player, assignmentID);
					}

					return true;
				}
			}
			else if(action.equalsIgnoreCase("tasks"))
			{
				// If this command is disabled then return
				if(!SConfigUtil.getSettingBoolean("tasks.full_list"))
				{
					SMiscUtil.sendMsg(player, "That functionality is disabled.");

					return true;
				}

				// Send the player a list of all available tasks
				SMiscUtil.sendMsg(player, "Available tasks:");
				player.sendMessage(" ");

				for(Task task : STaskUtil.getTasks())
				{
					if(task.getType().equals(Task.TaskType.MOB))
					{
						player.sendMessage(" > " + ChatColor.YELLOW + task.getName() + ChatColor.AQUA + " (Kill " + task.getGoal() + " " + SObjUtil.capitalize(task.getMob().getName().toLowerCase()) + "s)");
					}
					else if(task.getType().equals(Task.TaskType.ITEM))
					{
						player.sendMessage(" > " + ChatColor.YELLOW + task.getName() + ChatColor.AQUA + " (Obtain " + task.getGoal() + " " + SObjUtil.capitalize(task.getItem().getType().name().toLowerCase()) + "s)");
					}
				}

				player.sendMessage(" ");

				return true;
			}
			else if(action.equalsIgnoreCase("leaderboard"))
			{
				// TODO: Make this work and stuff.
				SMiscUtil.sendMsg(player, ChatColor.AQUA + "Coming Soon!");
				return true;
			}
			else if(action.equalsIgnoreCase("my"))
			{
				if(category.equalsIgnoreCase("info"))
				{
					// TODO: Work on statistics.

					SMiscUtil.sendMsg(player, "My Information");
					player.sendMessage(" ");
					player.sendMessage("  > Assignments:");
					player.sendMessage(" ");
					player.sendMessage("     - Active: " + ChatColor.YELLOW + STaskUtil.getActiveAssignments(player).size());
					player.sendMessage("     - Completions: " + ChatColor.GREEN + SPlayerUtil.getCompletions(player));
					player.sendMessage("     - Forfeits: " + ChatColor.RED + SPlayerUtil.getForfeits(player));
					player.sendMessage("     - Expirations: " + ChatColor.RED + SPlayerUtil.getExpirations(player));
					player.sendMessage("     - Total Given: " + ChatColor.GREEN + SPlayerUtil.getTotalAssignments(player));
					player.sendMessage(" ");
					player.sendMessage("  > Available Rewards: " + ChatColor.GREEN + SPlayerUtil.getRewardAmount(player));
					player.sendMessage(" ");
					player.sendMessage("  > Statistics: " + ChatColor.AQUA + "Coming Soon!");
					player.sendMessage(" ");
					return true;
				}
				else if(category.equalsIgnoreCase("tasks"))
				{
					// Return a message if they have no assignments.
					if(STaskUtil.getAssignments(player) == null || STaskUtil.getAssignments(player).isEmpty())
					{
						SMiscUtil.sendMsg(player, SMiscUtil.getString("no_tasks"));

						return true;
					}

					// Define variables
					ArrayList<Assignment> assignments = STaskUtil.getDisplayedAssignments(player);

					player.sendMessage(" ");
					if(assignments.size() > 1) SMiscUtil.sendMsg(player, "Your current assignments are:");
					else SMiscUtil.sendMsg(player, "Your current assignment is:");
					player.sendMessage(" ");

					// List the tasks
					for(Assignment assignment : assignments)
					{
						// Continue to the next assignment if it isn't on display
						if(!assignment.isDisplayed()) continue;

						String color = "" + ChatColor.AQUA;
						if(!assignment.isActive()) color = "" + ChatColor.GRAY;
						if(assignment.isComplete()) color = "" + ChatColor.AQUA;
						if(assignment.isExpired()) color = ChatColor.RED + "" + ChatColor.STRIKETHROUGH;
						if(assignment.isForfeited()) color = ChatColor.DARK_GRAY + "" + ChatColor.STRIKETHROUGH;

						String timeLimit = "", miscTag = "";
						if(assignment.getTask().isTimed() && assignment.isActive())
						{
							timeLimit = ChatColor.GRAY + " (Time Remaining: " + ChatColor.YELLOW + assignment.getTimeLeft() + " minutes" + ChatColor.GRAY + ")";
						}
						if(assignment.isForfeited()) miscTag = ChatColor.RED + " [FORFEITED]";
						else if(assignment.isExpired()) miscTag = ChatColor.RED + " [EXPIRED]";
						else if(assignment.isComplete()) miscTag = ChatColor.GREEN + " [COMPLETE]";

						player.sendMessage(ChatColor.GRAY + " > " + color + assignment.getTask().getName() + ChatColor.RESET + timeLimit + miscTag);

						if(!assignment.isExpired() && !assignment.isFailed() && !assignment.isComplete() && !assignment.isForfeited())
						{
							if(assignment.getTask().getType().equals(Task.TaskType.MOB))
							{
								player.sendMessage(ChatColor.GRAY + "     - Mob: " + ChatColor.YELLOW + SObjUtil.capitalize(assignment.getTask().getMob().getName().toLowerCase()));
								player.sendMessage(ChatColor.GRAY + "     - Kills: " + ChatColor.YELLOW + assignment.getAmountObtained() + ChatColor.GRAY + "/" + ChatColor.YELLOW + assignment.getAmountNeeded());
							}
							else if(assignment.getTask().getType().equals(Task.TaskType.ITEM))
							{
								player.sendMessage(ChatColor.GRAY + "     - Item: " + ChatColor.YELLOW + SObjUtil.capitalize(assignment.getTask().getItem().getType().name().toLowerCase()));
								player.sendMessage(ChatColor.GRAY + "     - Obtained: " + ChatColor.YELLOW + assignment.getAmountObtained() + ChatColor.GRAY + "/" + ChatColor.YELLOW + assignment.getAmountNeeded());
							}
							player.sendMessage(ChatColor.GRAY + "     - Assignment #: " + ChatColor.YELLOW + assignment.getID());
							player.sendMessage(ChatColor.GRAY + "     - Description: " + ChatColor.YELLOW + assignment.getTask().getDesc());
						}
						player.sendMessage(" ");
					}

					player.sendMessage(" ");

					return true;
				}
				else if(category.equalsIgnoreCase("rewards"))
				{
					ArrayList<ItemStack> rewards = SPlayerUtil.getRewards(player);

					// If they don't have rewards then tell them and return
					if(rewards.isEmpty())
					{
						SMiscUtil.sendMsg(player, SMiscUtil.getString("no_rewards"));

						return true;
					}

					SPlayerUtil.openRewardBackpack(player);

					return true;
				}
			}
		}

		SMiscUtil.sendMsg(player, "Error");
		return false;
	}

	/**
	 * Handles all admin-specific commands.
	 */
	public boolean sl_admin(Player player, String[] args)
	{
		// Check permissions
		if(!SMiscUtil.hasPermissionOrOP(player, "slayer.admin")) return SMiscUtil.noPermission(player);

		if(args.length == 0)
		{
			SMiscUtil.sendMsg(player, "Admin Directory");
			player.sendMessage(ChatColor.GRAY + "" + ChatColor.ITALIC + "/sladmin save");
			player.sendMessage(ChatColor.GRAY + "" + ChatColor.ITALIC + "/sladmin clear entities");
			player.sendMessage(ChatColor.GRAY + "" + ChatColor.ITALIC + "/sladmin remove task <player> <id>");
			player.sendMessage(ChatColor.GRAY + "" + ChatColor.ITALIC + "/sladmin set points <player> <points>");

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
					OfflinePlayer editing = Bukkit.getOfflinePlayer(option1);
					int taskID = SObjUtil.toInteger(option2);

					if(STaskUtil.removeAssignment(editing, taskID, AssignmentRemoveEvent.RemoveReason.ADMIN))
					{
						SMiscUtil.sendAdminMsg(player, ChatColor.GREEN + "The assignment (#: " + taskID + ") has been removed.");
					}
					else
					{
						SMiscUtil.sendAdminMsg(player, ChatColor.RED + "The assignment could not be removed.");
					}
					return true;
				}
			}
			else if(action.equalsIgnoreCase("set"))
			{
				if(category.equalsIgnoreCase("points"))
				{
					OfflinePlayer editing = Bukkit.getOfflinePlayer(option1);
					int points = SObjUtil.toInteger(option2);

					SPlayerUtil.setPoints(editing, points);

					SMiscUtil.sendAdminMsg(player, ChatColor.GREEN + editing.getName() + "'s points set successfully to " + points + ".");

					return true;
				}
			}
			else if(action.equalsIgnoreCase("clear"))
			{
				if(category.equalsIgnoreCase("entities"))
				{
					SMiscUtil.sendAdminMsg(player, ChatColor.YELLOW + "Clearing all saved entities...");
					SEntityUtil.getEntityMap().clear();
					SMiscUtil.sendAdminMsg(player, ChatColor.GREEN + "Entities cleared!");

					return true;
				}
			}
			else if(action.equalsIgnoreCase("save"))
			{
				SMiscUtil.sendAdminMsg(player, ChatColor.YELLOW + "Forcing save...");

				if(SFlatFile.save())
				{
					SMiscUtil.sendAdminMsg(player, ChatColor.GREEN + "Saved!");
				}
				else
				{
					SMiscUtil.sendAdminMsg(player, ChatColor.RED + "Save failed... check the console.");
				}

				return true;
			}
		}

		return false;
	}
}
