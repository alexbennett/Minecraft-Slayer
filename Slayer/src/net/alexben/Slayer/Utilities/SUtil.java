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

package net.alexben.Slayer.Utilities;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.logging.Logger;

import net.alexben.Slayer.Slayer;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.OfflinePlayer;

/**
 * Utility that handles miscellaneous methods.
 */
public class SUtil
{
	// Define variables
	private static Slayer plugin = null;
	private static String pluginName = null;
	private static ChatColor pluginColor = null;
	private static final Logger log = Logger.getLogger("Minecraft");

	public static void initialize(Slayer instance)
	{
		plugin = instance;
		pluginName = plugin.getDescription().getName();
		pluginColor = ChatColor.RED;
	}

	/**
	 * Returns the logger for the current plugin instance.
	 * 
	 * @return the logger instance.
	 */
	public static Logger getLog()
	{
		return log;
	}

	/**
	 * Returns the instance of the plugin.
	 * 
	 * @return Slayer
	 */
	public static Slayer getInstance()
	{
		return plugin;
	}

	/**
	 * Sends <code>msg</code> to the console with type <code>type</code>.
	 * 
	 * @param type the type of message.
	 * @param msg the message to send.
	 */
	public static void log(String type, String msg)
	{
		if(type.equalsIgnoreCase("info")) log.info("[" + pluginName + "] " + msg);
		else if(type.equalsIgnoreCase("warning")) log.warning("[" + pluginName + "] " + msg);
		else if(type.equalsIgnoreCase("severe")) log.severe("[" + pluginName + "] " + msg);
	}

	/**
	 * Sends a server-wide message prepended with the plugin name if <code>tag</code> is true.
	 * 
	 * @param tag if true, the message is prepended with "[Slayer]"
	 * @param msg the message to send.
	 */
	public static void serverMsg(boolean tag, String msg)
	{
		if(tag)
		{
			Bukkit.getServer().broadcastMessage(pluginColor + "[" + pluginName + "] " + ChatColor.RESET + msg);
		}
		else Bukkit.getServer().broadcastMessage(msg);

	}

	/**
	 * Sends a message to a player prepended with the plugin name.
	 * 
	 * @param player the player to message.
	 * @param msg the message to send.
	 */
	public static void sendMsg(OfflinePlayer player, String msg)
	{
		player.getPlayer().sendMessage(pluginColor + "[" + pluginName + "] " + ChatColor.RESET + msg);
	}

	/**
	 * Returns true if <code>player</code> has the permission called <code>permission</code>.
	 * 
	 * @param player the player to check.
	 * @param permission the permission to check for.
	 * @return boolean
	 */
	public static boolean hasPermission(OfflinePlayer player, String permission)
	{
		return player == null || player.getPlayer().hasPermission(permission);
	}

	/**
	 * Returns true if <code>player</code> has the permission called <code>permission</code> or is an OP.
	 * 
	 * @param player the player to check.
	 * @param permission the permission to check for.
	 * @return boolean
	 */
	public static boolean hasPermissionOrOP(OfflinePlayer player, String permission)
	{
		return player == null || player.isOp() || player.getPlayer().hasPermission(permission);
	}

	/**
	 * Returns an ArrayList of all Slayer participants.
	 * 
	 * @return ArrayList
	 */
	public static ArrayList<OfflinePlayer> getAllParticipants()
	{
		ArrayList<OfflinePlayer> players = new ArrayList<OfflinePlayer>();

		for(Map.Entry<String, HashMap<String, Object>> player : SDataUtil.getAllData().entrySet())
		{
			players.add(Bukkit.getPlayer(player.getKey()));
		}

		return players;
	}
}
