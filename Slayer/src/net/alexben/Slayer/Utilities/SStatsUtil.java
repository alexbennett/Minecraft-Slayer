package net.alexben.Slayer.Utilities;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.TreeMap;

import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;

/**
 * Handles all methods used in stats.
 */
public class SStatsUtil
{
	/**
	 * Returns a sorted Map of the top point holders by name.
	 * 
	 * @return Map
	 */
	public static Map<Integer, OfflinePlayer> getTopPoints()
	{
		// Define variables
		Map<Integer, OfflinePlayer> leaderboardUnsorted = new HashMap<Integer, OfflinePlayer>();

		for(Map.Entry<String, HashMap<String, Object>> player : SDataUtil.getAllData().entrySet())
		{
			leaderboardUnsorted.put(SObjUtil.toInteger(player.getValue().get("points")), Bukkit.getOfflinePlayer(player.getKey()));
		}

		TreeMap<Integer, OfflinePlayer> leaderboardSorted = new TreeMap<Integer, OfflinePlayer>(Collections.reverseOrder());
		leaderboardSorted.putAll(leaderboardUnsorted);

		return leaderboardSorted;
	}
}
