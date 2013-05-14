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

import net.alexben.Slayer.Handlers.SFlatFile;
import net.alexben.Slayer.Libraries.Objects.Death;
import net.alexben.Slayer.Libraries.Objects.Kill;
import net.alexben.Slayer.Libraries.Objects.SerialItemStack;
import net.alexben.Slayer.Libraries.Objects.Task;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.scoreboard.DisplaySlot;
import org.bukkit.scoreboard.Objective;
import org.bukkit.scoreboard.Score;
import org.bukkit.scoreboard.Scoreboard;

/**
 * Utility for all player-related methods.
 */
public class SPlayerUtil
{
	/**
	 * Creates a new Slayer save for the <code>player</code>.
	 * 
	 * @param player the player for whom to create the save.
	 */
	public static void createSave(OfflinePlayer player)
	{
		// Return if the player already exists
		if(SDataUtil.playerExists(player)) return;

		// This is really simple for the time being and almost unnecessary, but I'm adding it for future expansion.
		SDataUtil.saveData(player, "points", 0);
		SDataUtil.saveData(player, "level", 1);
		SDataUtil.saveData(player, "scoreboard", true);
		SDataUtil.saveData(player, "task_assignments_total", 0);
		SDataUtil.saveData(player, "task_completions", 0);
		SDataUtil.saveData(player, "task_forfeits", 0);
		SDataUtil.saveData(player, "task_expirations", 0);
		SDataUtil.saveData(player, "kills", new ArrayList<Kill>());
		SDataUtil.saveData(player, "deaths", new ArrayList<Death>());
		SDataUtil.saveData(player, "reward_queue", new ArrayList<SerialItemStack>());

		// Save the data
		SFlatFile.savePlayer(player);
	}

	/**
	 * Returns an ArrayList of all players loaded into memory.
	 * 
	 * @return ArrayList
	 */
	public static ArrayList<OfflinePlayer> getPlayers()
	{
		ArrayList<OfflinePlayer> players = new ArrayList<OfflinePlayer>();

		for(Map.Entry<String, HashMap<String, Object>> player : SDataUtil.getAllData().entrySet())
		{
			players.add(Bukkit.getOfflinePlayer(player.getKey()));
		}

		return players;
	}

	/**
	 * Returns true if the <code>player</code> has their scoreboard enabled.
	 * 
	 * @param player the player to check.
	 * @return boolean
	 */
	public static boolean scoreboardEnabled(OfflinePlayer player)
	{
		if(SDataUtil.hasData(player, "scoreboard"))
		{
			return SObjUtil.toBoolean(SDataUtil.getData(player, "scoreboard"));
		}
		else return true;
	}

	/**
	 * Toggles the Slayer scoreboard to <code>option</code> for <code>player</code>.
	 * 
	 * @param player the player to update.
	 * @param option the option to set.
	 */
	public static void toggleScoreboard(Player player, boolean option)
	{
		SDataUtil.saveData(player, "scoreboard", option);
		updateScoreboard(player);
	}

	/**
	 * Updates all data in the scoreboard for the <code>player</code>.
	 * 
	 * @param player the player to update for.
	 */
	public static void updateScoreboard(Player player)
	{
		// First do a version check to make sure we can use scoreboards
		if(!Bukkit.getBukkitVersion().contains("1.5.1-R0.3"))
		{
			// TODO: Eventually remove this.
			return;
		}

		// Define variables
		Scoreboard slayer = Bukkit.getScoreboardManager().getNewScoreboard();

		// Define objective
		Objective info = slayer.registerNewObjective("player_info", "dummy");
		info.setDisplaySlot(DisplaySlot.SIDEBAR);
		info.setDisplayName(ChatColor.AQUA + "Your Slayer Statistics");

		// Add the data if the scoreboard is enabled
		if(SConfigUtil.getSettingBoolean("misc.private_scoreboard") && scoreboardEnabled(player))
		{
			Score level = info.getScore(Bukkit.getOfflinePlayer(ChatColor.GRAY + SMiscUtil.getString("scoreboard_level")));
			level.setScore(getLevel(player));

			Score points = info.getScore(Bukkit.getOfflinePlayer(ChatColor.GRAY + SMiscUtil.getString("scoreboard_points")));
			points.setScore(getPoints(player));

			Score nextLevelPoints = info.getScore(Bukkit.getOfflinePlayer(ChatColor.GRAY + SMiscUtil.getString("scoreboard_points_needed")));
			nextLevelPoints.setScore(getPointsGoal(player) - getPoints(player));

			Score activeTasks = info.getScore(Bukkit.getOfflinePlayer(ChatColor.GRAY + SMiscUtil.getString("scoreboard_active_tasks")));
			activeTasks.setScore(STaskUtil.getActiveAssignments(player).size());

			Score rewards = info.getScore(Bukkit.getOfflinePlayer(ChatColor.GRAY + SMiscUtil.getString("scoreboard_rewards")));
			rewards.setScore(SPlayerUtil.getRewardAmount(player));

			player.setScoreboard(slayer);
		}

		// Update the scoreboard
		player.setScoreboard(slayer);
	}

	/**
	 * Returns the number of tasks assigned all time for <code>player</code>.
	 * 
	 * @return int
	 */
	public static int getTotalAssignments(OfflinePlayer player)
	{
		if(SDataUtil.getData(player, "task_assignments_total") == null) SDataUtil.saveData(player, "task_assignments_total", 0);
		return SObjUtil.toInteger(SDataUtil.getData(player, "task_assignments_total"));
	}

	/**
	 * Returns the number of task completions for <code>player</code>.
	 * 
	 * @return int
	 */
	public static int getCompletions(OfflinePlayer player)
	{
		if(SDataUtil.getData(player, "task_completions") == null) SDataUtil.saveData(player, "task_completions", 0);
		return SObjUtil.toInteger(SDataUtil.getData(player, "task_completions"));
	}

	/**
	 * Gives the <code>player</code> a task completion.
	 * 
	 * @param player the player to give the completion to.
	 */
	public static void addCompletion(OfflinePlayer player)
	{
		if(SDataUtil.hasData(player, "task_completions"))
		{
			SDataUtil.saveData(player, "task_completions", getCompletions(player) + 1);
		}
	}

	/**
	 * Returns the number of task expirations for <code>player</code>.
	 * 
	 * @return int
	 */
	public static int getExpirations(OfflinePlayer player)
	{
		if(SDataUtil.getData(player, "task_expirations") == null) SDataUtil.saveData(player, "task_expirations", 0);
		return SObjUtil.toInteger(SDataUtil.getData(player, "task_expirations"));
	}

	/**
	 * Gives the <code>player</code> a task expiration.
	 * 
	 * @param player the player to give the expiration to.
	 */
	public static void addExpiration(OfflinePlayer player)
	{
		if(SDataUtil.hasData(player, "task_expirations"))
		{
			SDataUtil.saveData(player, "task_expirations", getExpirations(player) + 1);
		}
	}

	/**
	 * Returns the number of task forfeits for <code>player</code>.
	 * 
	 * @return int
	 */
	public static int getForfeits(OfflinePlayer player)
	{
		if(SDataUtil.getData(player, "task_forfeits") == null) SDataUtil.saveData(player, "task_forfeits", 0);
		return SObjUtil.toInteger(SDataUtil.getData(player, "task_forfeits"));
	}

	/**
	 * Gives the <code>player</code> a task forfeit.
	 * 
	 * @param player the player to give the forfeit to.
	 */
	public static void addForfeit(OfflinePlayer player)
	{
		if(SDataUtil.hasData(player, "task_forfeits"))
		{
			SDataUtil.saveData(player, "task_forfeits", getForfeits(player) + 1);
		}
	}

	/**
	 * Adds the <code>item</code> to the <code>player</code>'s reward queue.
	 * 
	 * @param player the player to add the item to.
	 * @param item the item to add.
	 */
	public static void addReward(OfflinePlayer player, SerialItemStack item)
	{
		for(SerialItemStack serialReward : ((ArrayList<SerialItemStack>) SDataUtil.getData(player, "reward_queue")))
		{
			if(serialReward.toItemStack().isSimilar(item.toItemStack()))
			{
				serialReward.setAmount(serialReward.toItemStack().getAmount() + item.toItemStack().getAmount());
				return;
			}
		}

		((ArrayList<SerialItemStack>) SDataUtil.getData(player, "reward_queue")).add(item);
	}

	/**
	 * Removes the reward matching <code>item</code> from the <code>player</code>'s
	 * reward queue and returns true if successful.
	 * 
	 * @param player the player to remove the reward from.
	 * @param item the reward to remove.
	 * @return boolean
	 */
	public static boolean removeReward(OfflinePlayer player, ItemStack item)
	{
		ArrayList<SerialItemStack> rewards = (ArrayList<SerialItemStack>) SDataUtil.getData(player, "reward_queue");

		for(SerialItemStack reward : rewards)
		{
			if(reward.toItemStack().isSimilar(item))
			{
				int amount = reward.toItemStack().getAmount() - item.getAmount();

				if(amount < 1)
				{
					rewards.remove(rewards.indexOf(reward));
				}
				else
				{
					reward.setAmount(amount);
				}

				return true;
			}
		}
		return false;
	}

	/**
	 * Returns an ArrayList of <code>player</code>'s rewards.
	 * 
	 * @param player the player to get rewards for.
	 * @return ArrayList
	 */
	public static ArrayList<ItemStack> getRewards(OfflinePlayer player)
	{
		ArrayList<ItemStack> rewards = new ArrayList<ItemStack>();

		for(SerialItemStack reward : (ArrayList<SerialItemStack>) SDataUtil.getData(player, "reward_queue"))
		{
			rewards.add(reward.toItemStack());
		}

		return rewards;
	}

	/**
	 * Returns the total number of items in the <code>player</code>'s reward
	 * queue.
	 * 
	 * @param player the player whose queue to check.
	 * @return int
	 */
	public static int getRewardAmount(OfflinePlayer player)
	{
		int count = 0;

		for(ItemStack item : getRewards(player))
		{
			count += item.getAmount();
		}

		return count;
	}

	/**
	 * Sets the <code>player</code>'s points to <code>points</code>.
	 * 
	 * @param player the player to edit.
	 * @param points the points to set to.
	 */
	public static void setPoints(OfflinePlayer player, int points)
	{
		if(SDataUtil.hasData(player, "points"))
		{
			SDataUtil.saveData(player, "points", points);
		}
	}

	/**
	 * Gives the <code>player</code> the <code>points</code>.
	 * 
	 * @param player the player to give points to.
	 * @param points the number of points to give.
	 */
	public static void addPoints(OfflinePlayer player, int points)
	{
		if(SDataUtil.hasData(player, "points"))
		{
			setPoints(player, getPoints(player) + points);
		}
	}

	/**
	 * Subtracts <code>points</code> from the <code>player</code>'s total points.
	 * 
	 * @param player the player to edit.
	 * @param points the number of points to subtract.
	 */
	public static void subtractPoints(OfflinePlayer player, int points)
	{
		if(points > getPoints(player)) setPoints(player, 0);
		else setPoints(player, getPoints(player) - points);
	}

	/**
	 * Returns the <code>player</code>'s points.
	 * 
	 * @param player the player to check.
	 * @return Integer
	 */
	public static int getPoints(OfflinePlayer player)
	{
		return SObjUtil.toInteger(SDataUtil.getData(player, "points"));
	}

	/**
	 * Returns the points goal until <code>player</code>'s next level.
	 * 
	 * @param player the player to check.
	 * @return Integer
	 */
	public static int getPointsGoal(OfflinePlayer player)
	{
		return (int) Math.ceil(13.4 * Math.pow(getLevel(player) + 1, 2));
	}

	/**
	 * Returns the points goal until for <code>level</code>.
	 * 
	 * @param level the level to check for.
	 * @return Integer
	 */
	public static int getLevelPointsGoal(int level)
	{
		return (int) Math.ceil(13.4 * Math.pow(level, 2));
	}

	/**
	 * Sets the <code>player</code>'s level to <code>points</code>.
	 * 
	 * @param player the player to edit.
	 * @param levels the points to set to.
	 */
	public static void setLevel(OfflinePlayer player, int levels)
	{
		SDataUtil.saveData(player, "level", levels);
	}

	/**
	 * Gives the <code>player</code> a single level.
	 * 
	 * @param player the player to give points to.
	 */
	public static void addLevel(OfflinePlayer player)
	{
		if(SDataUtil.hasData(player, "level"))
		{
			setLevel(player, getLevel(player) + 1);
		}
		else
		{
			SDataUtil.saveData(player, "level", 2);
		}
	}

	/**
	 * Returns the <code>player</code>'s level.
	 * 
	 * @param player the player to check.
	 * @return Integer
	 */
	public static int getLevel(OfflinePlayer player)
	{
		if(!SDataUtil.hasData(player, "level")) SDataUtil.saveData(player, "level", 1);
		return SObjUtil.toInteger(SDataUtil.getData(player, "level"));
	}

	/**
	 * Adds a kill to the <code>player</code>'s overall kills.
	 * 
	 * @param player the player to give a kill to.
	 */
	public static void addKill(Player player, Entity entity)
	{
		Kill kill = new Kill(player, entity);
		ArrayList<Kill> kills;

		if(SDataUtil.hasData(player, "kills")) kills = (ArrayList<Kill>) SDataUtil.getData(player, "kills");
		else kills = new ArrayList<Kill>();

		kills.add(kill);
	}

	/**
	 * Adds a death to the <code>player</code>'s overall deaths.
	 * 
	 * @param player the player that died.
	 * @param entity the entity that killed the <code>player</code>.
	 */
	public static void addDeath(Player player, Entity entity)
	{
		Death death = new Death(player, entity);
		ArrayList<Death> deaths;

		if(SDataUtil.hasData(player, "deaths")) deaths = (ArrayList<Death>) SDataUtil.getData(player, "deaths");
		else deaths = new ArrayList<Death>();

		deaths.add(death);
	}

	/**
	 * Returns the kill count for <code>player</code>.
	 * 
	 * @param player the player to check.
	 * @return Integer
	 */
	public static int getKillCount(OfflinePlayer player)
	{
		return ((ArrayList<Kill>) SDataUtil.getData(player, "kills")).size();
	}

	/**
	 * Returns the death count for <code>player</code>.
	 * 
	 * @param player the player to check.
	 * @return Integer
	 */
	public static int getDeathCount(OfflinePlayer player)
	{
		return ((ArrayList<Kill>) SDataUtil.getData(player, "deaths")).size();
	}

	/**
	 * Returns an ArrayList of all kills for <code>player</code>.
	 * 
	 * @param player the player to check.
	 * @return ArrayList
	 */
	public static ArrayList<Kill> getKills(OfflinePlayer player)
	{
		return (ArrayList<Kill>) SDataUtil.getData(player, "kills");
	}

	/**
	 * Returns an ArrayList of all deaths for <code>player</code>.
	 * 
	 * @param player the player to check.
	 * @return ArrayList
	 */
	public static ArrayList<Death> getDeaths(OfflinePlayer player)
	{
		return (ArrayList<Death>) SDataUtil.getData(player, "deaths");
	}

	/**
	 * Opens an inventory used to process task items.
	 * 
	 * @param player the player for whom to open the inventory.
	 */
	public static void openProcessingInventory(Player player)
	{
		Inventory inventory = SMiscUtil.getInstance().getServer().createInventory(player, 27, "Item Processing Inventory");
		SDataUtil.saveData(player, "inv_process", true);
		player.openInventory(inventory);
	}

	/**
	 * Generates a reward inventory based off of the <code>player</code>'s current
	 * reward items and opens it.
	 * 
	 * @param player the player for whom to open the inventory.
	 */
	public static void openRewardBackpack(Player player)
	{
		Inventory inventory = SMiscUtil.getInstance().getServer().createInventory(player, 27, player.getName() + "'s Rewards");

		for(ItemStack item : getRewards(player))
		{
			inventory.addItem(item);
		}

		SDataUtil.saveData(player, "inv_rewards", true);

		player.openInventory(inventory);
	}

	/**
	 * Generates an inventory populated with task papers and booklets used
	 * to allow players to view available tasks based on their current level.
	 * 
	 * @param player the player for whom to open the inventory.
	 */
	public static void openTaskInventory(Player player)
	{
		// Define variables
		int level = getLevel(player);
		Inventory inventory = SMiscUtil.getInstance().getServer().createInventory(player, 27, "Slayer Tasks " + ChatColor.DARK_PURPLE + "(Click to Select)");

		// Loop through tasks and determine which ones to display
		for(Task task : STaskUtil.getTasksUpToLevel(level))
		{
			if(!STaskUtil.hasCompleted(player, task) || SConfigUtil.getSettingBoolean("tasks.reusable")) inventory.addItem(task.getTaskSheet());
		}

		// Open the inventory
		player.openInventory(inventory);
	}
}
