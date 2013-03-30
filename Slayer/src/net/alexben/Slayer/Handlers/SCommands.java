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
import java.util.Map;

import net.alexben.Slayer.Libraries.Objects.Assignment;
import net.alexben.Slayer.Libraries.Objects.Task;
import net.alexben.Slayer.Utilities.*;

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

		return false;
	}

	/**
	 * Handles all admin-specific commands.
	 */
	public boolean sl_admin(Player player, String[] args)
	{
		if(args.length == 0)
		{
			SUtil.sendMsg(player, "Admin Directory");
			player.sendMessage(ChatColor.GRAY + "" + ChatColor.ITALIC + "/sladmin assign <player>");

			return true;
		}

		return false;
	}

	/**
	 * Handles all basic commands.
	 */
	public boolean slayer(Player player, String[] args)
	{
		if(args.length == 0)
		{
			SUtil.sendMsg(player, "Command Directory");
			player.sendMessage(ChatColor.GRAY + "" + ChatColor.ITALIC + "/sl leaderboard");
			player.sendMessage(ChatColor.GRAY + "" + ChatColor.ITALIC + "/sl new task");
			player.sendMessage(ChatColor.GRAY + "" + ChatColor.ITALIC + "/sl tasks");
			player.sendMessage(ChatColor.GRAY + "" + ChatColor.ITALIC + "/sl my tasks");
			player.sendMessage(ChatColor.GRAY + "" + ChatColor.ITALIC + "/sl my rewards");
			player.sendMessage(ChatColor.GRAY + "" + ChatColor.ITALIC + "/sl claim");
			player.sendMessage(ChatColor.RED + "" + ChatColor.ITALIC + "/sladmin");

			return true;
		}
		else
		{
			String action, category = null, option1 = null, option2 = null;

			action = args[0];
			if(args.length > 1) category = args[1];
			if(args.length > 2) category = args[2];
			if(args.length > 3) category = args[3];

			if(action.equalsIgnoreCase("new"))
			{
				if(category.equalsIgnoreCase("task"))
				{
					STaskUtil.assignRandomTask(player);

					return true;
				}
			}
			else if(action.equalsIgnoreCase("tasks"))
			{
				// If this command is disabled then return
				if(!SConfigUtil.getSettingBoolean("enable_task_list")) return true;

				// Send the player a list of all available tasks
			}
			else if(action.equalsIgnoreCase("leaderboard"))
			{
				SUtil.sendMsg(player, "Leaderboard");
				player.sendMessage(" ");

				int pos = 1;

				for(Map.Entry<Integer, OfflinePlayer> leader : SStatsUtil.getTopPoints().entrySet())
				{
					player.sendMessage(ChatColor.AQUA + "  [" + pos + "] " + ChatColor.WHITE + leader.getValue().getName() + ChatColor.YELLOW + " (Score: " + leader.getKey() + ")");
					pos++;
				}

				player.sendMessage(" ");
				return true;
			}
			else if(action.equalsIgnoreCase("my"))
			{
				if(category.equalsIgnoreCase("tasks"))
				{
					if(STaskUtil.getAssignments(player) == null)
					{
						SUtil.sendMsg(player, "You currently have no Slayer Tasks.");
						return true;
					}

					player.sendMessage(" ");
					SUtil.sendMsg(player, "Your current task(s) are:");
					player.sendMessage(" ");

					// List the tasks
					for(Assignment assignment : STaskUtil.getAssignments(player))
					{
						ChatColor color;
						if(assignment.isActive()) color = ChatColor.GREEN;
						else color = ChatColor.RED;

						if(assignment.getTask().getType().equals(Task.TaskType.MOB))
						{
							player.sendMessage(" > " + color + assignment.getTask().getName() + ChatColor.RESET + " (Mob: " + assignment.getTask().getMob().getName() + ", Kills: " + assignment.getAmountObtained() + "/" + assignment.getAmountNeeded() + ")");
						}
						else if(assignment.getTask().getType().equals(Task.TaskType.ITEM))
						{
							player.sendMessage(" > " + color + assignment.getTask().getName() + ChatColor.RESET + " (Item: " + SObjUtil.capitalize(assignment.getTask().getItem().getType().name().toLowerCase()) + ", Obtained: " + assignment.getAmountObtained() + "/" + assignment.getAmountNeeded() + ")");
						}
					}

					player.sendMessage(" ");
					player.sendMessage(ChatColor.GREEN + "Green" + ChatColor.RESET + " means active, " + ChatColor.RED + "red" + ChatColor.RESET + " means inactive.");

					return true;
				}
				else if(category.equalsIgnoreCase("rewards"))
				{
					ArrayList<ItemStack> rewards = SPlayerUtil.getRewards(player);

					// If they don't have rewards then tell them and return
					if(rewards.isEmpty())
					{
						SUtil.sendMsg(player, "You have no rewards... Go complete some tasks!");
						return true;
					}

					String message;
					if(rewards.size() > 1) message = ChatColor.YELLOW + "" + rewards.size() + ChatColor.RESET + " rewards await you:";
					else message = ChatColor.YELLOW + "" + rewards.size() + ChatColor.RESET + " reward awaits you:";

					// They have rewards, list them
					SUtil.sendMsg(player, message);
					player.sendMessage(" ");

					for(ItemStack reward : rewards)
					{
						// Define variables
						String enchanted = "";

						if(reward.getEnchantments() != null)
						{
							enchanted = ChatColor.GREEN + "(Enchanted)";
						}

						player.sendMessage(" > " + reward.getType().name() + ChatColor.YELLOW + " (Amount: " + reward.getAmount() + ") " + enchanted);
					}

					player.sendMessage(" ");
					player.sendMessage("You can claim your rewards by using " + ChatColor.GREEN + "/sl claim" + ChatColor.RESET + ".");

					return true;
				}
			}

			/*
			 * TODO
			 * The reward claiming system needs to be greatly reworked.
			 * It's largely redundant at the moment and, well... basically useless.
			 */

			else if(action.equalsIgnoreCase("claim"))
			{
				ArrayList<ItemStack> rewards = SPlayerUtil.getRewards(player);

				// If they don't have rewards then tell them and return
				if(rewards.isEmpty())
				{
					SUtil.sendMsg(player, "You have no rewards... Go complete some tasks!");
					return true;
				}

				// Remove the reward from their queue
				for(ItemStack reward : rewards)
				{
					player.getInventory().addItem(reward);
					SPlayerUtil.removeReward(player, reward);
				}

				// Send the appropriate message depending on the amount of rewards available
				if(rewards.size() > 1) SUtil.sendMsg(player, "You have been given all " + ChatColor.YELLOW + rewards.size() + ChatColor.RESET + " of your rewards, enjoy!");
				else SUtil.sendMsg(player, "You have been given your " + ChatColor.YELLOW + "1" + ChatColor.RESET + " available reward, enjoy!");

				return true;
			}
		}

		return false;
	}
}
