package net.alexben.Slayer.Core.Objects;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import net.alexben.Slayer.Utilities.SConfigUtil;
import net.alexben.Slayer.Utilities.SItemUtil;
import net.alexben.Slayer.Utilities.SObjUtil;

import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.EntityType;
import org.bukkit.inventory.ItemStack;

public class Task implements Serializable
{
	private static final long serialVersionUID = 1869297353395176134L;
	private String name = null, desc = null, commandReward = null;
	private int level, value, amountNeeded, timeLimit, economyReward;
	private SerialItemStack item;
	private TaskType type;
	private EntityType entity;
	private ArrayList<SerialItemStack> itemReward = new ArrayList<SerialItemStack>();

	public Task(String task, String desc, int timeLimit, int value, int level, ArrayList<SerialItemStack> rewards, int amount, EntityType entity)
	{
		this.name = task;
		this.desc = desc;
		this.timeLimit = timeLimit;
		this.amountNeeded = amount;
		this.value = value;
		this.level = level;
		this.entity = entity;
		this.type = TaskType.MOB;

		if(!rewards.isEmpty())
		{
			for(SerialItemStack reward : rewards)
			{
				this.itemReward.add(reward);
			}
		}
	}

	public Task(String task, String desc, int timeLimit, int value, int level, ArrayList<SerialItemStack> rewards, int amount, ItemStack item)
	{
		this.name = task;
		this.desc = desc;
		this.timeLimit = timeLimit;
		this.amountNeeded = amount;
		this.value = value;
		this.level = level;
		this.item = new SerialItemStack(item);
		this.type = TaskType.ITEM;

		if(!rewards.isEmpty())
		{
			for(SerialItemStack reward : rewards)
			{
				this.itemReward.add(reward);
			}
		}
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
	 * Returns the value of the task.
	 * 
	 * @return Integer
	 */
	public int getValue()
	{
		return (int) Math.ceil(value * SConfigUtil.getSettingDouble("tasks.point_multiplier"));
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
			goal = "Obtain " + amountNeeded + " " + SObjUtil.capitalize(item.toItemStack().getType().name().toLowerCase().replace("_", " ")) + "(s)";
		}
		else if(type.equals(TaskType.MOB))
		{
			goal = "Kill " + amountNeeded + " " + SObjUtil.capitalize(entity.getName().replace("_", " ")) + "(s)";
		}

		// Create the content
		lore.add(ChatColor.GRAY + "" + ChatColor.ITALIC + "(Level " + ChatColor.DARK_PURPLE + ChatColor.ITALIC + level + ChatColor.GRAY + ChatColor.ITALIC + " Slayer Task)");
		lore.add("");
		lore.add(ChatColor.GREEN + "\"" + desc + "\"");
		lore.add("");
		lore.add(ChatColor.GRAY + "Goal: " + ChatColor.YELLOW + goal);
		lore.add(ChatColor.GRAY + "Rewards: " + ChatColor.YELLOW + itemReward.size() + " item(s)");
		lore.add(ChatColor.GRAY + "Points: " + ChatColor.GREEN + value);
		if(isTimed()) lore.add(ChatColor.GRAY + "Time Limit: " + ChatColor.RED + timeLimit + " minutes");

		// Return the book
		return SItemUtil.createItem(Material.PAPER, ChatColor.AQUA + name, lore, null);
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
	 * Returns the ItemStack associated with this task.
	 */
	public ItemStack getItem()
	{
		return item.toItemStack();
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
