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

package net.alexben.Slayer.Listeners;

import net.alexben.Slayer.Libraries.Objects.Assignment;
import net.alexben.Slayer.Libraries.Objects.Task;
import net.alexben.Slayer.Utilities.*;

import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerLoginEvent;
import org.bukkit.event.player.PlayerPickupItemEvent;
import org.bukkit.inventory.ItemStack;

public class SPlayerListener implements Listener
{
	@EventHandler(priority = EventPriority.MONITOR)
	private void onPlayerLogin(PlayerLoginEvent event)
	{
		Player player = event.getPlayer();

		// Create a save for the player
		SPlayerUtil.createSave(player);
	}

	@EventHandler(priority = EventPriority.MONITOR)
	private void onPlayerJoin(PlayerJoinEvent event)
	{
		Player player = event.getPlayer();

		if(SConfigUtil.getSettingBoolean("misc.join_message"))
		{
			player.sendMessage(ChatColor.GRAY + "This server is running " + ChatColor.RED + "Slayer v" + SMiscUtil.getInstance().getDescription().getVersion() + ChatColor.GRAY + ".");
		}

		if(SConfigUtil.getSettingBoolean("tasks.join_reminders") && STaskUtil.getActiveAssignments(player) != null)
		{
			SMiscUtil.sendMsg(player, ChatColor.GRAY + "You currently have " + ChatColor.YELLOW + STaskUtil.getActiveAssignments(player).size() + ChatColor.GRAY + " active task(s).");
		}
	}

	@EventHandler(priority = EventPriority.MONITOR)
	private void onInventoryCloseEvent(InventoryCloseEvent event)
	{
		// Return if it isn't a player inventory
		if(!(event.getPlayer() instanceof Player)) return;

		// Define variables
		Player player = (Player) event.getPlayer();

		if(event.getInventory().getName().toLowerCase().contains("processing"))
		{
			// TODO: Pre-process items into stacks so multiple messages don't get sent.

			// If they don't have the data then return
			if(!SDataUtil.hasData(player, "inv_process")) return;

			for(ItemStack item : event.getInventory().getContents())
			{
				STaskUtil.processItem(player, item);
			}

			SDataUtil.removeData(player, "inv_process");
		}
		else if(event.getInventory().getName().toLowerCase().contains("rewards"))
		{
			for(ItemStack reward : SPlayerUtil.getRewards(player))
			{
				if(!event.getInventory().containsAtLeast(reward, 1))
				{
					SPlayerUtil.removeReward(player, reward);
				}

				for(ItemStack item : event.getInventory().getContents())
				{
					if(item != null)
					{
						if(reward.isSimilar(item))
						{
							if(reward.getAmount() > item.getAmount())
							{
								int amount = reward.getAmount() - item.getAmount();
								ItemStack newItem = reward.clone();
								newItem.setAmount(amount);

								SPlayerUtil.removeReward(player, newItem);
							}
						}
					}
				}
			}

			SDataUtil.removeData(player, "inv_rewards");
		}
	}

	@EventHandler(priority = EventPriority.MONITOR)
	private void onPlayerPickupItemEvent(PlayerPickupItemEvent event)
	{
		Player player = event.getPlayer();
		ItemStack item = event.getItem().getItemStack();

		if(STaskUtil.getActiveAssignments(player) != null)
		{
			for(Assignment assignment : STaskUtil.getActiveAssignments(player))
			{
				// Continue if it isn't an item task or if it isn't the correct item
				if(!assignment.getTask().getType().equals(Task.TaskType.ITEM) || !assignment.getTask().getItem().isSimilar(item)) continue;

				// Define count
				int count = item.getAmount();

				// Loop through inventory
				for(ItemStack stack : player.getInventory().getContents())
				{
					// If it's the same item then add to the count
					if(assignment.getTask().getItem().isSimilar(stack))
					{
						count += stack.getAmount();
					}
				}

				// Check the count against the progress
				if(count >= assignment.getAmountLeft())
				{
					SMiscUtil.sendMsg(player, SMiscUtil.getString("enough_items").replace("{task}", assignment.getTask().getName()));
				}
			}
		}
	}
}
