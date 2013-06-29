package net.alexben.Slayer.Utilities;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import net.alexben.Slayer.Core.Handlers.FlatFile;
import net.alexben.Slayer.Core.Objects.Death;
import net.alexben.Slayer.Core.Objects.Kill;
import net.alexben.Slayer.Core.Objects.SerialItemStack;
import net.alexben.Slayer.Core.Objects.Task;

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
public class PlayerUtil
{
	/**
	 * Creates a new net.alexben.Slayer save for the <code>player</code>.
	 * 
	 * @param player the player for whom to create the save.
	 */
	public static void createSave(OfflinePlayer player)
	{
		// Return if the player already exists
		if(DataUtil.playerExists(player)) return;

		// This is really simple for the time being and almost unnecessary, but I'm adding it for future expansion.
		DataUtil.saveData(player, "points", 0);
		DataUtil.saveData(player, "level", 1);
		DataUtil.saveData(player, "scoreboard", true);
		DataUtil.saveData(player, "task_assignments_total", 0);
		DataUtil.saveData(player, "task_completions", 0);
		DataUtil.saveData(player, "task_forfeits", 0);
		DataUtil.saveData(player, "task_expirations", 0);
		DataUtil.saveData(player, "kills", new ArrayList<Kill>());
		DataUtil.saveData(player, "deaths", new ArrayList<Death>());
		DataUtil.saveData(player, "reward_queue", new ArrayList<SerialItemStack>());

		// Save the data
		FlatFile.savePlayer(player);
	}

	/**
	 * Returns an ArrayList of all players loaded into memory.
	 * 
	 * @return ArrayList
	 */
	public static ArrayList<OfflinePlayer> getPlayers()
	{
		ArrayList<OfflinePlayer> players = new ArrayList<OfflinePlayer>();

		for(Map.Entry<String, HashMap<String, Object>> player : DataUtil.getAllData().entrySet())
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
		if(DataUtil.hasData(player, "scoreboard"))
		{
			return ObjUtil.toBoolean(DataUtil.getData(player, "scoreboard"));
		}
		else return true;
	}

	/**
	 * Toggles the net.alexben.Slayer scoreboard to <code>option</code> for <code>player</code>.
	 * 
	 * @param player the player to update.
	 * @param option the option to set.
	 */
	public static void toggleScoreboard(Player player, boolean option)
	{
		DataUtil.saveData(player, "scoreboard", option);
		updateScoreboard(player);
	}

	/**
	 * Updates all data in the scoreboard for the <code>player</code>.
	 * 
	 * @param player the player to update for.
	 */
	public static void updateScoreboard(Player player)
	{
		// Define variables
		Scoreboard slayer = Bukkit.getScoreboardManager().getNewScoreboard();

		// Define objective
		Objective info = slayer.registerNewObjective("player_info", "dummy");
		info.setDisplaySlot(DisplaySlot.SIDEBAR);
		info.setDisplayName(ChatColor.AQUA + "Your Slayer Statistics");

		// Add the data if the scoreboard is enabled
		if(ConfigUtil.getSettingBoolean("misc.private_scoreboard") && scoreboardEnabled(player))
		{
			Score level = info.getScore(Bukkit.getOfflinePlayer(ChatColor.GRAY + MiscUtil.getString("scoreboard_level")));
			level.setScore(getLevel(player));

			Score points = info.getScore(Bukkit.getOfflinePlayer(ChatColor.GRAY + MiscUtil.getString("scoreboard_points")));
			points.setScore(getPoints(player));

			Score nextLevelPoints = info.getScore(Bukkit.getOfflinePlayer(ChatColor.GRAY + MiscUtil.getString("scoreboard_points_needed")));
			nextLevelPoints.setScore(getPointsGoal(player) - getPoints(player));

			Score activeTasks = info.getScore(Bukkit.getOfflinePlayer(ChatColor.GRAY + MiscUtil.getString("scoreboard_active_tasks")));
			activeTasks.setScore(TaskUtil.getActiveAssignments(player).size());

			Score rewards = info.getScore(Bukkit.getOfflinePlayer(ChatColor.GRAY + MiscUtil.getString("scoreboard_rewards")));
			rewards.setScore(PlayerUtil.getRewardAmount(player));

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
		if(DataUtil.getData(player, "task_assignments_total") == null) DataUtil.saveData(player, "task_assignments_total", 0);
		return ObjUtil.toInteger(DataUtil.getData(player, "task_assignments_total"));
	}

	/**
	 * Returns the number of task completions for <code>player</code>.
	 * 
	 * @return int
	 */
	public static int getCompletions(OfflinePlayer player)
	{
		if(DataUtil.getData(player, "task_completions") == null) DataUtil.saveData(player, "task_completions", 0);
		return ObjUtil.toInteger(DataUtil.getData(player, "task_completions"));
	}

	/**
	 * Gives the <code>player</code> a task completion.
	 * 
	 * @param player the player to give the completion to.
	 */
	public static void addCompletion(OfflinePlayer player)
	{
		if(DataUtil.hasData(player, "task_completions"))
		{
			DataUtil.saveData(player, "task_completions", getCompletions(player) + 1);
		}
	}

	/**
	 * Returns the number of task expirations for <code>player</code>.
	 * 
	 * @return int
	 */
	public static int getExpirations(OfflinePlayer player)
	{
		if(DataUtil.getData(player, "task_expirations") == null) DataUtil.saveData(player, "task_expirations", 0);
		return ObjUtil.toInteger(DataUtil.getData(player, "task_expirations"));
	}

	/**
	 * Gives the <code>player</code> a task expiration.
	 * 
	 * @param player the player to give the expiration to.
	 */
	public static void addExpiration(OfflinePlayer player)
	{
		if(DataUtil.hasData(player, "task_expirations"))
		{
			DataUtil.saveData(player, "task_expirations", getExpirations(player) + 1);
		}
	}

	/**
	 * Returns the number of task forfeits for <code>player</code>.
	 * 
	 * @return int
	 */
	public static int getForfeits(OfflinePlayer player)
	{
		if(DataUtil.getData(player, "task_forfeits") == null) DataUtil.saveData(player, "task_forfeits", 0);
		return ObjUtil.toInteger(DataUtil.getData(player, "task_forfeits"));
	}

	/**
	 * Gives the <code>player</code> a task forfeit.
	 * 
	 * @param player the player to give the forfeit to.
	 */
	public static void addForfeit(OfflinePlayer player)
	{
		if(DataUtil.hasData(player, "task_forfeits"))
		{
			DataUtil.saveData(player, "task_forfeits", getForfeits(player) + 1);
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
		for(SerialItemStack serialReward : ((ArrayList<SerialItemStack>) DataUtil.getData(player, "reward_queue")))
		{
			if(serialReward.toItemStack().isSimilar(item.toItemStack()))
			{
				serialReward.setAmount(serialReward.toItemStack().getAmount() + item.toItemStack().getAmount());
				return;
			}
		}

		((ArrayList<SerialItemStack>) DataUtil.getData(player, "reward_queue")).add(item);
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
		ArrayList<SerialItemStack> rewards = (ArrayList<SerialItemStack>) DataUtil.getData(player, "reward_queue");

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

		for(SerialItemStack reward : (ArrayList<SerialItemStack>) DataUtil.getData(player, "reward_queue"))
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
		if(DataUtil.hasData(player, "points"))
		{
			DataUtil.saveData(player, "points", points);
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
		if(DataUtil.hasData(player, "points"))
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
		return ObjUtil.toInteger(DataUtil.getData(player, "points"));
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
		DataUtil.saveData(player, "level", levels);
	}

	/**
	 * Gives the <code>player</code> a single level.
	 * 
	 * @param player the player to give points to.
	 */
	public static void addLevel(OfflinePlayer player)
	{
		if(DataUtil.hasData(player, "level"))
		{
			setLevel(player, getLevel(player) + 1);
		}
		else
		{
			DataUtil.saveData(player, "level", 2);
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
		if(!DataUtil.hasData(player, "level")) DataUtil.saveData(player, "level", 1);
		return ObjUtil.toInteger(DataUtil.getData(player, "level"));
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

		if(DataUtil.hasData(player, "kills")) kills = (ArrayList<Kill>) DataUtil.getData(player, "kills");
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

		if(DataUtil.hasData(player, "deaths")) deaths = (ArrayList<Death>) DataUtil.getData(player, "deaths");
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
		return ((ArrayList<Kill>) DataUtil.getData(player, "kills")).size();
	}

	/**
	 * Returns the death count for <code>player</code>.
	 * 
	 * @param player the player to check.
	 * @return Integer
	 */
	public static int getDeathCount(OfflinePlayer player)
	{
		return ((ArrayList<Kill>) DataUtil.getData(player, "deaths")).size();
	}

	/**
	 * Returns an ArrayList of all kills for <code>player</code>.
	 * 
	 * @param player the player to check.
	 * @return ArrayList
	 */
	public static ArrayList<Kill> getKills(OfflinePlayer player)
	{
		return (ArrayList<Kill>) DataUtil.getData(player, "kills");
	}

	/**
	 * Returns an ArrayList of all deaths for <code>player</code>.
	 * 
	 * @param player the player to check.
	 * @return ArrayList
	 */
	public static ArrayList<Death> getDeaths(OfflinePlayer player)
	{
		return (ArrayList<Death>) DataUtil.getData(player, "deaths");
	}

	/**
	 * Opens an inventory used to process task items.
	 * 
	 * @param player the player for whom to open the inventory.
	 */
	public static void openProcessingInventory(Player player)
	{
		Inventory inventory = net.alexben.Slayer.Core.Slayer.plugin.getServer().createInventory(player, 27, "Item Processing Inventory");
		DataUtil.saveData(player, "inv_process", true);
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
		Inventory inventory = net.alexben.Slayer.Core.Slayer.plugin.getServer().createInventory(player, 27, player.getName() + "'s Rewards");

		for(ItemStack item : getRewards(player))
		{
			inventory.addItem(item);
		}

		DataUtil.saveData(player, "inv_rewards", true);

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
		Inventory inventory = net.alexben.Slayer.Core.Slayer.plugin.getServer().createInventory(player, 27, "Slayer Tasks " + ChatColor.DARK_PURPLE + "(Click to Select)");

		// Loop through tasks and determine which ones to display
		for(Task task : TaskUtil.getTasksUpToLevel(level))
		{
			if(!TaskUtil.hasCompleted(player, task) || ConfigUtil.getSettingBoolean("tasks.reusable")) inventory.addItem(task.getTaskSheet());
		}

		// Open the inventory
		player.openInventory(inventory);
	}
}
