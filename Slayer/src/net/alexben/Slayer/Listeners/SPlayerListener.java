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

import net.alexben.Slayer.Utilities.SConfigUtil;
import net.alexben.Slayer.Utilities.SMiscUtil;
import net.alexben.Slayer.Utilities.SPlayerUtil;
import net.alexben.Slayer.Utilities.STaskUtil;

import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerDropItemEvent;
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

		if(SConfigUtil.getSettingBoolean("join_message"))
		{
			player.sendMessage(ChatColor.GRAY + "This server is running " + ChatColor.RED + "Slayer v" + SMiscUtil.getInstance().getDescription().getVersion() + ChatColor.GRAY + ".");
		}

		if(SConfigUtil.getSettingBoolean("join_reminders") && STaskUtil.getActiveAssignments(player) != null)
		{
			SMiscUtil.sendMsg(player, ChatColor.GRAY + "You currently have " + ChatColor.YELLOW + STaskUtil.getActiveAssignments(player).size() + ChatColor.GRAY + " active task(s).");
		}
	}

	@EventHandler(priority = EventPriority.MONITOR)
	private void onPlayerPickupItemEvent(PlayerPickupItemEvent event)
	{
		Player player = event.getPlayer();
		ItemStack item = event.getItem().getItemStack();

		STaskUtil.processItem(player, item);
	}

	@EventHandler(priority = EventPriority.MONITOR)
	private void onPlayerDropItemEvent(PlayerDropItemEvent event)
	{
		Player player = event.getPlayer();
		ItemStack item = event.getItemDrop().getItemStack();

		STaskUtil.unprocessItem(player, item);
	}
}
