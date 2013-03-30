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

package net.alexben.Slayer.Libraries.Objects;

import java.io.Serializable;
import java.util.ArrayList;

import org.bukkit.entity.EntityType;
import org.bukkit.inventory.ItemStack;

public class Task implements Serializable
{
	private static final long serialVersionUID = 1869297353395176134L;
	private String name = null, desc = null;
	private int value, amountNeeded;
	private SerialItemStack item;
	private TaskType type;
	private EntityType entity;
	private ArrayList<SerialItemStack> reward = new ArrayList<SerialItemStack>();

	public Task(String task, String desc, int value, ArrayList<SerialItemStack> rewards, int amount, EntityType entity)
	{
		this.name = task;
		this.desc = desc;
		this.amountNeeded = amount;
		this.value = value;
		this.entity = entity;
		this.type = TaskType.MOB;

		if(!rewards.isEmpty())
		{
			for(SerialItemStack reward : rewards)
			{
				this.reward.add(reward);
			}
		}
	}

	public Task(String task, String desc, int value, ArrayList<SerialItemStack> rewards, int amount, ItemStack item)
	{
		this.name = task;
		this.desc = desc;
		this.amountNeeded = amount;
		this.value = value;
		this.item = new SerialItemStack(item);
		this.type = TaskType.ITEM;

		if(!rewards.isEmpty())
		{
			for(SerialItemStack reward : rewards)
			{
				this.reward.add(reward);
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
		return value;
	}

	/**
	 * Returns all plugin rewards.
	 * 
	 * @return ArrayList
	 */
	public ArrayList<ItemStack> getReward()
	{
		ArrayList<ItemStack> rewards = new ArrayList<ItemStack>();

		for(SerialItemStack item : reward)
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
	 * The different types of tasks.
	 */
	public enum TaskType
	{
		ITEM, MOB
	}
}
