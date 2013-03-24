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

import net.alexben.Slayer.Slayer;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;

import java.util.HashMap;
import java.util.logging.Logger;

public class SUtil
{
    // Define variables
    private static Slayer plugin = null;
    private static String pluginName = null;
    private static String pluginNameNoColor = null;
    public static String pluginPrefix = null;
    private static final Logger log = Logger.getLogger("Minecraft");
    private static final HashMap<String, HashMap<String, Object>> save = new HashMap<String, HashMap<String, Object>>();

    public static void initialize(Slayer instance)
    {
        plugin = instance;
        pluginName = ChatColor.GREEN + plugin.getDescription().getName() + ChatColor.RESET;
        pluginNameNoColor = plugin.getDescription().getName();
        pluginPrefix = pluginNameNoColor.toLowerCase() + "_";
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
        if(type.equalsIgnoreCase("info")) log.info("[" + pluginNameNoColor + "] " + msg);
        else if(type.equalsIgnoreCase("warning")) log.warning("[" + pluginNameNoColor + "] " + msg);
        else if(type.equalsIgnoreCase("severe")) log.severe("[" + pluginNameNoColor + "] " + msg);
    }

    /**
     * Sends a server-wide message.
     *
     * @param msg the message to send.
     */
    public static void serverMsg(String msg)
    {
        if(SConfigUtil.getSettingBoolean("tagmessages"))
        {
            Bukkit.getServer().broadcastMessage("[" + pluginName + "] " + msg);
        }
        else Bukkit.getServer().broadcastMessage(msg);

    }

    /**
     * Sends a message to a player prepended with the plugin name.
     *
     * @param player the player to message.
     * @param msg the message to send.
     */
    public static void sendMessage(Player player, String msg)
    {
        if(SConfigUtil.getSettingBoolean("tagmessages"))
        {
            player.sendMessage("[" + pluginName + "] " + msg);
        }
        else
        {
            player.sendMessage(msg);
        }
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
     * Returns true if <code>player</code> has the permission called <code>permission</code>
     * or is an OP.
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
     * Saves <code>data</code> under the key <code>name</code> to <code>player</code>.
     *
     * @param player the player to save data to.
     * @param name the name of the data.
     * @param data the data to save.
     */
    public static void saveData(OfflinePlayer player, String name, Object data)
    {
        // Create new save for the player if one doesn't already exist
        if(!save.containsKey(pluginPrefix + player.getName()))
        {
            save.put(pluginPrefix + player.getName(), new HashMap<String, Object>());
        }

        // Prepend the data with the plugin prefix to avoid plugin collisions and save the data
        save.get(pluginPrefix + player.getName()).put(name.toLowerCase(), data);
    }

    /**
     * Returns all saved data.
     *
     * @return HashMap<String, HashMap<String, Object>>
     */
    public static HashMap<String, HashMap<String, Object>> getAllData()
    {
        return save;
    }

    /**
     * Returns the data with the key <code>name</code> from <code>player</code>'s HashMap.
     *
     * @param player the player to check.
     * @param name the key to grab.
     */
    public static Object getData(OfflinePlayer player, String name)
    {
        if(save.containsKey(pluginPrefix + player.getName()) && save.get(pluginPrefix + player.getName()).containsKey(name))
        {
            return save.get(pluginPrefix + player.getName()).get(name);
        }
        return null;
    }

    /**
     * Removes the data with the key <code>name</code> from <code>player</code>.
     *
     * @param player the player to remove data from.
     * @param name the key of the data to remove.
     */
    public static void removeData(OfflinePlayer player, String name)
    {
        if(save.containsKey(pluginPrefix + player.getName())) save.get(pluginPrefix + player.getName()).remove(name.toLowerCase());
    }

    /**
     * Removes all data for the <code>player</code>.
     *
     * @param player the player whose data to remove.
     */
    public static void removeAllData(OfflinePlayer player)
    {
        save.remove(pluginPrefix + player.getName());
    }
}
