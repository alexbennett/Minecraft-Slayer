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

import net.alexben.Slayer.Events.*;
import net.alexben.Slayer.Libraries.Objects.Assignment;
import net.alexben.Slayer.Libraries.Objects.Task;

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
		// Define the initial random task and other variables
		Task task = getRandomTask();
		int i = 0;

		while(hasTask(player, task))
		{
			if(i >= Math.pow(tasks.size(), 2))
			{
				// They have all available tasks, let 'em know
				SMiscUtil.sendMsg(player, SMiscUtil.getString("has_all_tasks"));

				return;
			}

			task = getRandomTask();
			i++;
		}

		assignTask(player, task);
	}

	/**
	 * Returns the assignment for <code>player</code> with an id of <code>id</code>.
	 * 
	 * @param player the player to check.
	 * @param id the id to check for.
	 * @return Assignment
	 */
	public static Assignment getAssignment(OfflinePlayer player, int id)
	{
		for(Assignment assignment : getAssignments(player))
		{
			if(assignment.getID() == id) return assignment;
		}
		return null;
	}

	/**
	 * Returns an ArrayList of all server-wide assignments.
	 * 
	 * @return ArrayList
	 */
	public static ArrayList<Assignment> getAllAssignments()
	{
		ArrayList<Assignment> assignments = new ArrayList<Assignment>();

		for(OfflinePlayer player : SPlayerUtil.getPlayers())
		{
			if(getAssignments(player) == null || getAssignments(player).isEmpty()) continue;

			for(Assignment assignment : getAssignments(player))
			{
				assignments.add(assignment);
			}
		}

		return assignments;
	}

	/**
	 * Returns an ArrayList of all active server-wide assignments.
	 * 
	 * @return ArrayList
	 */
	public static ArrayList<Assignment> getAllActiveAssignments()
	{
		ArrayList<Assignment> assignments = new ArrayList<Assignment>();

		for(OfflinePlayer player : SPlayerUtil.getPlayers())
		{
			if(getAssignments(player) == null || getAssignments(player).isEmpty()) continue;

			for(Assignment assignment : getAssignments(player))
			{
				if(assignment.isActive()) assignments.add(assignment);
			}
		}

		return assignments;
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
	 * Removes the assignment with an id matching <code>id</code> from the <code>player</code>.
	 * Returns true upon successful removal.
	 * 
	 * @param player the player to remove the assignment from.
	 * @param id the assignment to remove.
	 * @return boolean
	 */
	public static boolean removeAssignment(OfflinePlayer player, int id, AssignmentRemoveEvent.RemoveReason reason)
	{
		if(hasAssignment(player, id))
		{
			Assignment assignment = getAssignment(player, id);

			// Call the event
			AssignmentRemoveEvent assignmentRemoveEvent = new AssignmentRemoveEvent(assignment.getOfflinePlayer(), assignment, reason);
			SMiscUtil.getInstance().getServer().getPluginManager().callEvent(assignmentRemoveEvent);
			if(assignmentRemoveEvent.isCancelled()) return false;

			getAssignments(player).remove(assignment);
			return true;
		}
		return false;
	}

	/**
	 * Forfeits the assignment with an id matching <code>id</code> for the <code>player</code>.
	 * Returns true upon successful forfeit.
	 * 
	 * @param player the player to forfeit the assignment for.
	 * @param id the assignment to forfeit.
	 */
	public static boolean forfeitAssignment(OfflinePlayer player, int id)
	{
		if(hasAssignment(player, id))
		{
			Assignment assignment = getAssignment(player, id);

			// Call the event
			AssignmentForfeitEvent assignmentForfeitEvent = new AssignmentForfeitEvent(assignment.getOfflinePlayer(), assignment);
			SMiscUtil.getInstance().getServer().getPluginManager().callEvent(assignmentForfeitEvent);
			if(assignmentForfeitEvent.isCancelled()) return false;

			getAssignments(player).remove(assignment);
			return true;
		}
		return false;
	}

	/**
	 * Returns true if the <code>player</code> already has the <code>task</code>.
	 * 
	 * @param player the player to check.
	 * @param task the task to check for.
	 * @return boolean
	 */
	public static boolean hasTask(OfflinePlayer player, Task task)
	{
		if(getAssignments(player) == null || getAssignments(player).isEmpty()) return false;

		for(Assignment assignment : getAssignments(player))
		{
			if(assignment.getTask().equals(task)) return true;
		}
		return false;
	}

	/**
	 * Returns true if the <code>player</code> already has the assignment
	 * with an id of <code>id</code>.
	 * 
	 * @param player the player to check.
	 * @param id the task to check for.
	 * @return boolean
	 */
	public static boolean hasAssignment(OfflinePlayer player, int id)
	{
		if(getAssignments(player) == null || getAssignments(player).isEmpty()) return false;

		for(Assignment assignment : getAssignments(player))
		{
			if(assignment.getID() == id) return true;
		}
		return false;
	}

	/**
	 * Assigns the <code>task</code> to the <code>player</code> and returns the new
	 * Assignment.
	 * 
	 * @param player the player to assign to.
	 * @param task the task to assign.
	 * @return Assignment
	 */
	public static Assignment assignTask(Player player, Task task)
	{
		TaskAssignEvent taskAssignEvent = new TaskAssignEvent(player, task);
		SMiscUtil.getInstance().getServer().getPluginManager().callEvent(taskAssignEvent);

		if(!taskAssignEvent.isCancelled())
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

			// Tracking
			SDataUtil.saveData(player, "task_completions", SPlayerUtil.getTotalAssignments(player) + 1);

			return assignment;
		}
		else
		{
			player.sendMessage(SMiscUtil.getString("assignment_fail"));
			return null;
		}
	}

	/**
	 * Cycles through all assignments and cleans up house.
	 */
	public static void refreshAssignments()
	{
		// Return if there are no assignments
		if(getAllAssignments() == null || getAllAssignments().isEmpty()) return;

		// Define variables
		int count = 0;

		// Clear old assignments and stoof
		for(Assignment assignment : getAllAssignments())
		{
			if(assignment.isExpired() || assignment.isFailed() || assignment.isComplete())
			{
				// Remove the assignment, it's no longer needed
				removeAssignment(assignment.getOfflinePlayer(), assignment.getID(), AssignmentRemoveEvent.RemoveReason.AUTO);

				count++;
			}
		}

		if(count > 0) SMiscUtil.log("info", count + " inactive assignments have been cleared.");
	}

	/**
	 * Cycles through all active assignments and fails those that are expired.
	 */
	public static void refreshTimedAssignments()
	{
		// Return if there are no assignments
		if(getAllActiveAssignments() == null || getAllActiveAssignments().isEmpty()) return;

		for(Assignment assignment : getAllActiveAssignments())
		{
			if(assignment.getTask().isTimed() && assignment.getTimeLeft() <= 0)
			{
				// Call the event
				AssignmentExpireEvent assignmentExpireEvent = new AssignmentExpireEvent(assignment.getOfflinePlayer(), assignment);
				SMiscUtil.getInstance().getServer().getPluginManager().callEvent(assignmentExpireEvent);
				if(assignmentExpireEvent.isCancelled()) return;

				// Log the expiration
				SMiscUtil.log("info", "An assignment (#: " + assignment.getID() + ") for " + assignment.getOfflinePlayer().getName() + " has expired.");

				// It's expired. Set it to failed and inactive.
				assignment.setFailed(true);
				assignment.setExpired(true);
				assignment.setActive(false);
			}
		}
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
		// Return if they have no assignments
		if(getAssignments(player) == null) return;

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
					SMiscUtil.sendMsg(player, SMiscUtil.getString("mob_task_update").replace("{obtained}", "" + assignment.getAmountObtained()).replace("{needed}", "" + assignment.getAmountNeeded()).replace("{task}", assignment.getTask().getName()));
				}

				// Now handle completed assignments
				if(assignment.isComplete())
				{
					// The assignment is complete, call the event
					AssignmentCompleteEvent assignmentCompleteEvent = new AssignmentCompleteEvent(player, assignment);
					SMiscUtil.getInstance().getServer().getPluginManager().callEvent(assignmentCompleteEvent);
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
		// Return if they have no assignments
		if(getAssignments(player) == null) return;

		// First get their assignments
		for(Assignment assignment : getAssignments(player))
		{
			// Continue to next assignment if this one is inactive
			if(!assignment.isActive()) continue;

			// Continue to next assignment if it doesn't match the correct TaskType
			if(!assignment.getTask().getType().equals(Task.TaskType.ITEM)) continue;

			if(assignment.getTask().getItem().isSimilar(item))
			{
				int amount = item.getAmount();

				// Check the count to make sure it doesn't go over the requirement. If it does, set it to the amount needed.
				if(amount > assignment.getAmountLeft()) amount = assignment.getAmountLeft();

				// The item matches the requirement, add to the count
				assignment.addProgress(amount);

				// For items, always message the player (makes up for the possibility of picking up multiple items and not receiving the message
				if(!assignment.isComplete())
				{
					SMiscUtil.sendMsg(player, SMiscUtil.getString("item_task_update").replace("{obtained}", "" + assignment.getAmountObtained()).replace("{needed}", "" + assignment.getAmountNeeded()).replace("{task}", assignment.getTask().getName()));
				}

				// Now handle completed assignments
				if(assignment.isComplete())
				{
					// The assignment is complete, call the event
					AssignmentCompleteEvent assignmentCompleteEvent = new AssignmentCompleteEvent(player, assignment);
					SMiscUtil.getInstance().getServer().getPluginManager().callEvent(assignmentCompleteEvent);
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
