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
import java.util.Random;

import net.alexben.Slayer.Events.AssignmentCompleteEvent;
import net.alexben.Slayer.Events.TaskAssignEvent;
import net.alexben.Slayer.Libraries.Objects.Assignment;
import net.alexben.Slayer.Libraries.Objects.Task;

import org.bukkit.ChatColor;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

/**
 * Handles all Slayer task-related methods.
 */
public class STaskUtil
{
	// Define variables
	private static final ArrayList<Task> tasks = new ArrayList<Task>();

	/**
	 * Loads the task into the task ArrayList for future reference.
	 */
	public static void loadTask(Task task)
	{
		tasks.add(task);
	}

	/**
	 * Returns an ArrayList of all loaded tasks.
	 * 
	 * @return ArrayList
	 */
	public static ArrayList<Task> getTasks()
	{
		return tasks;
	}

	/**
	 * Returns a random slayer task created from the task configuration.
	 * 
	 * @return Task
	 */
	public static Task getRandomTask()
	{
		Random rand = new Random();
		return tasks.get(rand.nextInt(tasks.size()));
	}

	/**
	 * Assigns a random Task to the <code>player</code>.
	 * 
	 * @param player the player to assign to.
	 */
	public static void assignRandomTask(Player player)
	{
		Task task = getRandomTask();

		TaskAssignEvent taskAssignEvent = new TaskAssignEvent(player, task);
		SUtil.getInstance().getServer().getPluginManager().callEvent(taskAssignEvent);
		if(taskAssignEvent.isCancelled())
		{
			player.sendMessage(ChatColor.RED + "A task could not be assigned to you.");
			return;
		}

		assignTask(player, task);

		return;
	}

	/**
	 * Returns an ArrayList of all assignments for <code>player</code>.
	 * 
	 * @param player the player to check.
	 * @return ArrayList
	 */
	public static ArrayList<Assignment> getAssignments(OfflinePlayer player)
	{
		if(SDataUtil.getData(player, "assignments") != null)
		{
			return (ArrayList<Assignment>) SDataUtil.getData(player, "assignments");
		}
		return null;
	}

	/**
	 * Returns an ArrayList of active assignments for <code>player</code>.
	 * 
	 * @param player the player to check.
	 * @return ArrayList
	 */
	public static ArrayList<Assignment> getActiveAssignments(OfflinePlayer player)
	{
		ArrayList<Assignment> active = new ArrayList<Assignment>();

		if(getAssignments(player) == null) return active;

		for(Assignment assignment : getAssignments(player))
		{
			if(assignment.isActive()) active.add(assignment);
		}

		return active;
	}

	/**
	 * Assigns the <code>task</code> to the <code>player</code> and returns the new
	 * Assignment.
	 * 
	 * @param player the player to assign to.
	 * @param task the task to assign.
	 * @return Assignment
	 */
	public static Assignment assignTask(OfflinePlayer player, Task task)
	{
		Assignment assignment = new Assignment(player, task);

		if(SDataUtil.getData(player, "assignments") != null)
		{
			((ArrayList<Assignment>) SDataUtil.getData(player, "assignments")).add(assignment);
		}
		else
		{
			ArrayList<Assignment> assignments = new ArrayList<Assignment>();
			assignments.add(assignment);

			SDataUtil.saveData(player, "assignments", assignments);
		}

		return assignment;
	}

	/**
	 * Process the <code>entity</code> passed in as a kill and distribute it
	 * across all active assignments.
	 * 
	 * @param player the player to process for.
	 * @param entity the EntityType of the kill to process.
	 */
	public static void processKill(Player player, Entity entity)
	{
		// First get their assignments
		for(Assignment assignment : getAssignments(player))
		{
			// Continue to next assignment if this one is inactive
			if(!assignment.isActive()) continue;

			// Continue to next assignment if it doesn't match the correct TaskType
			if(!assignment.getTask().getType().equals(Task.TaskType.MOB)) continue;

			if(assignment.getTask().getMob().equals(entity.getType()))
			{
				// The mob matches the requirement, add to the count
				assignment.addProgress();

				// Message the player on even kills
				if(assignment.getAmountObtained() % 2 == 0 && !assignment.isComplete())
				{
					SUtil.sendMsg(player, "You have " + ChatColor.YELLOW + assignment.getAmountObtained() + ChatColor.RESET + " of " + ChatColor.YELLOW + assignment.getAmountNeeded() + ChatColor.RESET + " kills needed for \"" + ChatColor.YELLOW + assignment.getTask().getName() + ChatColor.RESET + "\".");
				}

				// Now handle completed assignments
				if(assignment.isComplete())
				{
					// The assignment is complete, call the event
					AssignmentCompleteEvent assignmentCompleteEvent = new AssignmentCompleteEvent(player, assignment);
					SUtil.getInstance().getServer().getPluginManager().callEvent(assignmentCompleteEvent);
					if(assignmentCompleteEvent.isCancelled()) return;

					// Set the assignment to inactive
					assignment.setActive(false);
				}
			}
		}

		// Add overall kill for tracking purposes
		SPlayerUtil.addKill(player, entity);
	}

	/**
	 * Process the <code>item</code> passed in and distribute it
	 * across all active assignments.
	 * 
	 * @param player the player to process for.
	 * @param item the item to process.
	 */
	public static void processItem(Player player, ItemStack item)
	{
		// First get their assignments
		for(Assignment assignment : getAssignments(player))
		{
			// Continue to next assignment if this one is inactive
			if(!assignment.isActive()) continue;

			// Continue to next assignment if it doesn't match the correct TaskType
			if(!assignment.getTask().getType().equals(Task.TaskType.ITEM)) continue;

			if(assignment.getTask().getItem().isSimilar(item))
			{
				// The item matches the requirement, add to the count
				assignment.addProgress(item.getAmount());

				// For items, always message the player (makes up for the possibility of picking up multiple items and not receiving the message
				if(!assignment.isComplete())
				{
					SUtil.sendMsg(player, "You have " + ChatColor.YELLOW + assignment.getAmountObtained() + ChatColor.RESET + " of " + ChatColor.YELLOW + assignment.getAmountNeeded() + ChatColor.RESET + " items needed for \"" + ChatColor.YELLOW + assignment.getTask().getName() + ChatColor.RESET + "\".");
				}

				// Now handle completed assignments
				if(assignment.isComplete())
				{
					// The assignment is complete, call the event
					AssignmentCompleteEvent assignmentCompleteEvent = new AssignmentCompleteEvent(player, assignment);
					SUtil.getInstance().getServer().getPluginManager().callEvent(assignmentCompleteEvent);
					if(assignmentCompleteEvent.isCancelled()) return;

					// Set the assignment to inactive
					assignment.setActive(false);
				}
			}
		}
	}

	/**
	 * Unprocess the <code>item</code> to prevent the <code>player</code> from
	 * dropping and picking up the same item to complete the task.
	 * 
	 * @param player the player to unprocess for.
	 * @param item the item to unprocess.
	 */
	public static void unprocessItem(Player player, ItemStack item)
	{
		// TODO: Validate this to make sure it's fool-proof.

		// First get their assignments
		for(Assignment assignment : getAssignments(player))
		{
			// Continue to next assignment if this one is inactive
			if(!assignment.isActive()) continue;

			// Continue to next assignment if it doesn't match the correct TaskType
			if(!assignment.getTask().getType().equals(Task.TaskType.ITEM)) continue;

			if(assignment.getTask().getItem().isSimilar(item))
			{
				if(item.getAmount() <= assignment.getAmountObtained())
				{
					assignment.subtractProgress(item.getAmount());
				}
				else if(item.getAmount() > assignment.getAmountObtained())
				{
					assignment.setProgress(0);
				}
			}
		}
	}
}
