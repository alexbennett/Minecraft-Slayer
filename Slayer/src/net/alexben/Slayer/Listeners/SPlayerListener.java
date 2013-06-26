package net.alexben.Slayer.Listeners;

import java.util.HashMap;
import java.util.Map;

import net.alexben.Slayer.Core.Objects.Assignment;
import net.alexben.Slayer.Core.Objects.Task;
import net.alexben.Slayer.Core.Slayer;
import net.alexben.Slayer.Utilities.*;

import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
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
			player.sendMessage(ChatColor.GRAY + "This server is running " + ChatColor.RED + "Slayer v" + Slayer.plugin.getDescription().getVersion() + ChatColor.GRAY + ".");
		}

		if(SConfigUtil.getSettingBoolean("tasks.join_reminders") && STaskUtil.getActiveAssignments(player) != null)
		{
			SMiscUtil.sendMsg(player, ChatColor.GRAY + SMiscUtil.getString("current_assignments_join").replace("{tasks}", ChatColor.YELLOW + "" + STaskUtil.getActiveAssignments(player).size() + ChatColor.GRAY).replace("{command}", ChatColor.GOLD + "/sl my tasks" + ChatColor.GRAY));
		}

		// Update the scoreboard
		SPlayerUtil.updateScoreboard(player);
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
			// If they don't have the data then return
			if(!SDataUtil.hasData(player, "inv_process")) return;

			// Define variables
			Map<Integer, Integer> items = new HashMap<Integer, Integer>();

			// Pre-process items into a Map
			for(ItemStack item : event.getInventory().getContents())
			{
				if(item == null) continue;

				if(items.containsKey(item.getTypeId()))
				{
					items.put(item.getTypeId(), items.get(item.getTypeId()) + item.getAmount());
				}
				else
				{
					items.put(item.getTypeId(), item.getAmount());
				}
			}

			for(Map.Entry<Integer, Integer> item : items.entrySet())
			{
				STaskUtil.processItem(player, new ItemStack(item.getKey(), item.getValue()));
			}

			// Clear the data
			items.clear();
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

				// Define variables
				Map<Integer, Integer> items = new HashMap<Integer, Integer>();

				// Pre-process items into a Map
				for(ItemStack item : event.getInventory().getContents())
				{
					if(item == null) continue;

					if(items.containsKey(item.getTypeId()))
					{
						items.put(item.getTypeId(), items.get(item.getTypeId()) + item.getAmount());
					}
					else
					{
						items.put(item.getTypeId(), item.getAmount());
					}
				}

				for(Map.Entry<Integer, Integer> item : items.entrySet())
				{
					ItemStack newItem = new ItemStack(item.getKey(), item.getValue());

					if(reward.isSimilar(newItem))
					{
						if(reward.getAmount() > newItem.getAmount())
						{
							int amount = reward.getAmount() - newItem.getAmount();

							newItem.setAmount(amount);

							SPlayerUtil.removeReward(player, newItem);
						}
					}
				}

				// Clear the data
				items.clear();
				SDataUtil.removeData(player, "inv_rewards");
			}
		}

		// Update the scoreboard
		SPlayerUtil.updateScoreboard(player);
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
				if(!assignment.getTask().getType().equals(Task.TaskType.ITEM) || !assignment.getTask().getItemStack().isSimilar(item)) continue;

				// Define count
				int count = item.getAmount();

				// Loop through inventory
				for(ItemStack stack : player.getInventory().getContents())
				{
					// If it's the same item then add to the count
					if(assignment.getTask().getItemStack().isSimilar(stack))
					{
						count += stack.getAmount();
					}
				}

				// Check the count against the progress
				if(count >= assignment.getAmountLeft())
				{
					SMiscUtil.sendMsg(player, ChatColor.GRAY + SMiscUtil.getString("enough_items").replace("{task}", ChatColor.AQUA + assignment.getTask().getName() + ChatColor.GRAY).replace("{command}", ChatColor.GOLD + "/process" + ChatColor.GRAY));
				}
			}
		}
	}

	@EventHandler(priority = EventPriority.HIGHEST)
	private void onInventoryClickEvent(InventoryClickEvent event)
	{
		Player player = (Player) event.getWhoClicked();

		if(event.getInventory().getName().toLowerCase().contains("slayer tasks"))
		{
			// Return if the item is null
			if(event.getCurrentItem() == null || event.getCurrentItem().getItemMeta() == null) return;

			if(STaskUtil.getTaskByName(event.getCurrentItem().getItemMeta().getDisplayName()) != null)
			{
				// Define variables
				Task task = STaskUtil.getTaskByName(event.getCurrentItem().getItemMeta().getDisplayName());

				// Ask them if they would like to accept the task
				player.sendMessage(ChatColor.GRAY + SMiscUtil.getString("task_selected").replace("{task}", ChatColor.AQUA + task.getName() + ChatColor.GRAY));
				player.sendMessage(ChatColor.GRAY + SMiscUtil.getString("task_selected_accept").replace("{command}", ChatColor.GOLD + "/accept" + ChatColor.GRAY));

				if(STaskUtil.hasTask(player, task))
				{
					// They already have the task. Message them and return.
					SMiscUtil.sendMsg(player, ChatColor.GRAY + SMiscUtil.getString("has_task").replace("{task}", ChatColor.AQUA + task.getName() + ChatColor.GRAY));
				}
				else
				{
					// They don't have the task, save it for later use
					SDataUtil.saveData(player, "clicked_task", task);
				}

				// Close the inventory
				player.closeInventory();
			}

			// Finally, we cancel the event
			event.setCancelled(true);
		}
	}
}
