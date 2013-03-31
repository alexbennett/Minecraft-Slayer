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