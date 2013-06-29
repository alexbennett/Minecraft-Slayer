package net.alexben.Slayer.Utilities;

import java.util.HashMap;

import org.bukkit.OfflinePlayer;

/**
 * Utility that handles all data-related methods.
 */
public class DataUtil
{
	// Define variables
	private static final HashMap<String, HashMap<String, Object>> save = new HashMap<String, HashMap<String, Object>>();

	/**
	 * Returns true if the <code>player</code> exists.
	 * 
	 * @param player the player to look for.
	 * @return boolean
	 */
	public static boolean playerExists(OfflinePlayer player)
	{
		return save.containsKey(player.getName());
	}

	/**
	 * Returns true if <code>key</code> exists for <code>player</code>.
	 * 
	 * @param player the player to check.
	 * @param key the key to look for.
	 * @return boolean
	 */
	public static boolean hasData(OfflinePlayer player, String key)
	{
		return save.get(player.getName()).containsKey(key);
	}

	/**
	 * Saves <code>data</code> under the name <code>key</code> to <code>player</code>.
	 * 
	 * @param player the player to save data to.
	 * @param key the name of the data.
	 * @param data the data to save.
	 */
	public static void saveData(OfflinePlayer player, String key, Object data)
	{
		// Create new save for the player if one doesn't already exist
		if(!save.containsKey(player.getName()))
		{
			save.put(player.getName(), new HashMap<String, Object>());
		}

		// Prepend the data with the plugin prefix to avoid plugin collisions and save the data
		save.get(player.getName()).put(key.toLowerCase(), data);
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
	 * Returns the data with the key <code>key</code> from <code>player</code>'s HashMap.
	 * 
	 * @param player the player to check.
	 * @param key the key to grab.
	 */
	public static Object getData(OfflinePlayer player, String key)
	{
		if(save.containsKey(player.getName()) && save.get(player.getName()).containsKey(key))
		{
			return save.get(player.getName()).get(key);
		}
		return null;
	}

	/**
	 * Removes the data with the name <code>key</code> from <code>player</code>.
	 * 
	 * @param player the player to remove data from.
	 * @param key the key of the data to remove.
	 */
	public static void removeData(OfflinePlayer player, String key)
	{
		if(save.containsKey(player.getName())) save.get(player.getName()).remove(key.toLowerCase());
	}

	/**
	 * Removes all data for the <code>player</code>.
	 * 
	 * @param player the player whose data to remove.
	 */
	public static void removeAllData(OfflinePlayer player)
	{
		save.remove(player.getName());
	}
}
