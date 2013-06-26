package net.alexben.Slayer.Core.Objects;

import org.bukkit.entity.EntityType;

/**
 * Allows for the creation of objects used in Slayer.
 */
public class ObjectFactory
{
	public static Task createMobTask(String name, String desc, int timeLimit, int amountNeeded, int points, int level, EntityType entity)
	{
		Task task = new Task();
		task.setType(Task.TaskType.MOB);
		task.setName(name);
		task.setDesc(desc);
		task.setTimeLimit(timeLimit);
		task.setAmountNeeded(amountNeeded);
		task.setPoints(points);
		task.setLevel(level);
		task.setEntityType(entity);
		return task;
	}

	public static Task createItemTask(String name, String desc, int timeLimit, int amountNeeded, int points, int level, int itemId)
	{
		Task task = new Task();
		task.setType(Task.TaskType.ITEM);
		task.setName(name);
		task.setDesc(desc);
		task.setTimeLimit(timeLimit);
		task.setAmountNeeded(amountNeeded);
		task.setPoints(points);
		task.setLevel(level);
		task.setItemType(itemId);
		return task;
	}
}
