package Slayer.Core.Objects;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.EntityType;
import org.bukkit.inventory.ItemStack;

import Slayer.Utilities.ConfigUtil;
import Slayer.Utilities.ItemUtil;
import Slayer.Utilities.ObjUtil;

import com.google.common.collect.Lists;

public class Task implements Serializable
{
	// Main variables
	private static final long serialVersionUID = 1869297353395176134L;
	private String name = null, desc = null;
	private int itemId, level, points, amountNeeded, timeLimit;
	private TaskType type;
	private EntityType entity;

	// Rewards
	private String commandReward = null;
	private int economyReward;
	private ArrayList<SerialItemStack> itemReward = new ArrayList<SerialItemStack>();

	void setName(String name)
	{
		this.name = name;
	}

	void setDesc(String desc)
	{
		this.desc = desc;
	}

	void setTimeLimit(int timeLimit)
	{
		this.timeLimit = timeLimit;
	}

	void setAmountNeeded(int amountNeeded)
	{
		this.amountNeeded = amountNeeded;
	}

	void setPoints(int points)
	{
		this.points = points;
	}

	void setLevel(int level)
	{
		this.level = level;
	}

	void setType(TaskType type)
	{
		this.type = type;
	}

	void setEntityType(EntityType entity)
	{
		this.entity = entity;
	}

	void setItemType(int itemId)
	{
		this.itemId = itemId;
	}

	public void setItemReward(ArrayList<SerialItemStack> itemReward)
	{
		this.itemReward = Lists.newArrayList(itemReward);
	}

	public void setEconomyReward(int economyReward)
	{
		this.economyReward = economyReward;
	}

	/**
	 * Returns the name of the task.
	 * 
	 * @return String
	 */
	public String getName()
	{
		return name;
	}

	/**
	 * Returns the task description.
	 * 
	 * @return String
	 */
	public String getDesc()
	{
		return desc;
	}

	/**
	 * Returns the time limit for the task.
	 * 
	 * @return int
	 */
	public int getTimeLimit()
	{
		return timeLimit;
	}

	/**
	 * Returns thus task's TaskType.
	 * 
	 * @return TaskType
	 */
	public TaskType getType()
	{
		return type;
	}

	/**
	 * Returns the point value of the task.
	 * 
	 * @return Integer
	 */
	public int getValue()
	{
		return (int) Math.ceil(points * ConfigUtil.getSettingDouble("tasks.point_multiplier"));
	}

	/**
	 * Returns the level of the task.
	 * 
	 * @return Integer
	 */
	public int getLevel()
	{
		return level;
	}

	/**
	 * Returns a piece of paper with information from the task.
	 * 
	 * @return ItemStack
	 */
	public ItemStack getTaskSheet()
	{
		List<String> lore = new ArrayList<String>();

		// Set variables used in the book
		String goal = null;

		if(type.equals(TaskType.ITEM))
		{
			goal = "Obtain " + amountNeeded + " " + ObjUtil.capitalize(Material.getMaterial(itemId).name().toLowerCase().replace("_", " ")) + "(s)";
		}
		else if(type.equals(TaskType.MOB))
		{
			goal = "Kill " + amountNeeded + " " + ObjUtil.capitalize(entity.getName().replace("_", " ")) + "(s)";
		}

		// Create the content
		lore.add(ChatColor.GRAY + "" + ChatColor.ITALIC + "(Level " + ChatColor.DARK_PURPLE + ChatColor.ITALIC + level + ChatColor.GRAY + ChatColor.ITALIC + " Slayer Task)");
		lore.add("");
		lore.add(ChatColor.GREEN + "\"" + desc + "\"");
		lore.add("");
		lore.add(ChatColor.GRAY + "Goal: " + ChatColor.YELLOW + goal);
		lore.add(ChatColor.GRAY + "Rewards: " + ChatColor.YELLOW + itemReward.size() + " item(s)");
		lore.add(ChatColor.GRAY + "Points: " + ChatColor.GREEN + points);
		if(isTimed()) lore.add(ChatColor.GRAY + "Time Limit: " + ChatColor.RED + timeLimit + " minutes");

		// Return the book
		return ItemUtil.createItem(Material.PAPER, ChatColor.AQUA + name, lore, null);
	}

	/**
	 * Returns all plugin rewards.
	 * 
	 * @return ArrayList
	 */
	public ArrayList<ItemStack> getItemReward()
	{
		ArrayList<ItemStack> rewards = new ArrayList<ItemStack>();

		for(SerialItemStack item : itemReward)
		{
			rewards.add(item.toItemStack());
		}

		return rewards;
	}

	/**
	 * Returns the entity type associated with this task.
	 * 
	 * @return EntityType
	 */
	public EntityType getMob()
	{
		return entity;
	}

	/**
	 * Returns the Material associated with this task.
	 */
	public Material getItemType()
	{
		return Material.getMaterial(itemId);
	}

	/**
	 * Returns an ItemStack of the material associated with this task.
	 */
	public ItemStack getItemStack()
	{
		return new ItemStack(Material.getMaterial(itemId));
	}

	/**
	 * Returns the amount needed for task completion.
	 * 
	 * @return int
	 */
	public int getGoal()
	{
		return amountNeeded;
	}

	/**
	 * Returns true if this task is timed.
	 * 
	 * @return boolean
	 */
	public boolean isTimed()
	{
		return timeLimit != 0;
	}

	/**
	 * The different types of tasks.
	 */
	public enum TaskType
	{
		ITEM, MOB
	}
}
