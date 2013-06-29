package net.alexben.Slayer.Utilities;

import java.util.ArrayList;
import java.util.Random;

import net.alexben.Slayer.Core.Events.*;
import net.alexben.Slayer.Core.Objects.Assignment;
import net.alexben.Slayer.Core.Objects.Task;
import net.alexben.Slayer.Core.Slayer;

import org.bukkit.ChatColor;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

/**
 * Handles all net.alexben.Slayer task-related methods.
 */
public class TaskUtil
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
	 * Returns an ArrayList of all tasks with a level matching <code>level</code>.
	 * 
	 * @param level the level to look for.
	 * @return ArrayList
	 */
	public static ArrayList<Task> getTasksWithLevel(int level)
	{
		ArrayList<Task> temp = new ArrayList<Task>();

		for(Task task : tasks)
		{
			if(task.getLevel() == level) temp.add(task);
		}

		return temp;
	}

	/**
	 * Returns an ArrayList of all tasks with a level less than or equal to <code>level</code>.
	 * 
	 * @param level the level to max at.
	 * @return ArrayList
	 */
	public static ArrayList<Task> getTasksUpToLevel(int level)
	{
		ArrayList<Task> temp = new ArrayList<Task>();

		for(Task task : tasks)
		{
			if(task.getLevel() <= level) temp.add(task);
		}

		return temp;
	}

	/**
	 * Returns the Task with the name <code>name</code>.
	 * 
	 * @param name the name of the task to return.
	 * @return Task
	 */
	public static Task getTaskByName(String name)
	{
		for(Task task : tasks)
		{
			if(task.getName().equalsIgnoreCase(ChatColor.stripColor(name))) return task;
		}

		return null;
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
				MiscUtil.sendMsg(player, ChatColor.GRAY + MiscUtil.getString("has_all_tasks"));

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

		for(OfflinePlayer player : PlayerUtil.getPlayers())
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

		for(OfflinePlayer player : PlayerUtil.getPlayers())
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
	 * Returns an ArrayList of all timed server-wide assignments.
	 * 
	 * @return ArrayList
	 */
	public static ArrayList<Assignment> getAllTimedAssignments()
	{
		ArrayList<Assignment> assignments = new ArrayList<Assignment>();

		for(Assignment assignment : getAllAssignments())
		{
			if(assignment.getTask().isTimed()) assignments.add(assignment);
		}

		return assignments;
	}

	/**
	 * Returns an ArrayList of all untimed server-wide assignments.
	 * 
	 * @return ArrayList
	 */
	public static ArrayList<Assignment> getAllUntimedAssignments()
	{
		ArrayList<Assignment> assignments = new ArrayList<Assignment>();

		for(Assignment assignment : getAllAssignments())
		{
			if(!assignment.getTask().isTimed()) assignments.add(assignment);
		}

		return assignments;
	}

	/**
	 * Returns an ArrayList of all completed assignments.
	 * 
	 * @return ArrayList
	 */
	public static ArrayList<Assignment> getAllCompleteAssignments()
	{
		ArrayList<Assignment> assignments = new ArrayList<Assignment>();

		for(Assignment assignment : getAllAssignments())
		{
			if(assignment.isComplete()) assignments.add(assignment);
		}

		return assignments;
	}

	/**
	 * Returns an ArrayList of all expired assignments.
	 * 
	 * @return ArrayList
	 */
	public static ArrayList<Assignment> getAllExpiredAssignments()
	{
		ArrayList<Assignment> assignments = new ArrayList<Assignment>();

		for(Assignment assignment : getAllAssignments())
		{
			if(assignment.isExpired()) assignments.add(assignment);
		}

		return assignments;
	}

	/**
	 * Returns an ArrayList of all failed assignments.
	 * 
	 * @return ArrayList
	 */
	public static ArrayList<Assignment> getAllFailedAssignments()
	{
		ArrayList<Assignment> assignments = new ArrayList<Assignment>();

		for(Assignment assignment : getAllAssignments())
		{
			if(assignment.isFailed()) assignments.add(assignment);
		}

		return assignments;
	}

	/**
	 * Returns an ArrayList of all forfeited assignments.
	 * 
	 * @return ArrayList
	 */
	public static ArrayList<Assignment> getAllForfeitedAssignments()
	{
		ArrayList<Assignment> assignments = new ArrayList<Assignment>();

		for(Assignment assignment : getAllAssignments())
		{
			if(assignment.isForfeited()) assignments.add(assignment);
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
		if(DataUtil.getData(player, "assignments") != null)
		{
			return (ArrayList<Assignment>) DataUtil.getData(player, "assignments");
		}
		return null;
	}

	/**
	 * Returns an ArrayList of active assignments for <code>player</code>.
	 * 
	 * @param player the player to check.
	 * @return ArrayList
	 */
	public static ArrayList<Assignment> getDisplayedAssignments(OfflinePlayer player)
	{
		ArrayList<Assignment> temp = new ArrayList<Assignment>();

		if(getAssignments(player) == null) return temp;

		for(Assignment assignment : getAssignments(player))
		{
			if(assignment.isDisplayed()) temp.add(assignment);
		}

		return temp;
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
			Slayer.plugin.getServer().getPluginManager().callEvent(assignmentRemoveEvent);
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
			Slayer.plugin.getServer().getPluginManager().callEvent(assignmentForfeitEvent);
			if(assignmentForfeitEvent.isCancelled()) return false;

			assignment.setForfeited(true);
			assignment.setActive(false);
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
			if(assignment.getTask().equals(task) && assignment.isActive()) return true;
		}
		return false;
	}

	/**
	 * Returns true if the <code>player</code> has completed the <code>task</code>.
	 * 
	 * @param player the player to check.
	 * @param task the task to check for.
	 * @return boolean
	 */
	public static boolean hasCompleted(OfflinePlayer player, Task task)
	{
		if(getAssignments(player) == null || getAssignments(player).isEmpty()) return false;

		for(Assignment assignment : getAssignments(player))
		{
			if(assignment.getTask().equals(task) && (!assignment.isActive() || assignment.isComplete())) return true;
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
		Slayer.plugin.getServer().getPluginManager().callEvent(taskAssignEvent);

		if(!taskAssignEvent.isCancelled())
		{
			Assignment assignment = new Assignment(player, task);

			if(DataUtil.getData(player, "assignments") != null)
			{
				((ArrayList<Assignment>) DataUtil.getData(player, "assignments")).add(assignment);
			}
			else
			{
				ArrayList<Assignment> assignments = new ArrayList<Assignment>();
				assignments.add(assignment);

				DataUtil.saveData(player, "assignments", assignments);
			}

			// Tracking
			DataUtil.saveData(player, "task_assignments_total", PlayerUtil.getTotalAssignments(player) + 1);

			// Update scoreboard
			PlayerUtil.updateScoreboard(player);

			return assignment;
		}
		else
		{
			player.sendMessage(ChatColor.RED + MiscUtil.getString("give_assignment_fail"));
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

		// Hide old assignments and stoof
		for(Assignment assignment : getAllAssignments())
		{
			if(!assignment.isActive() || assignment.isExpired() || assignment.isFailed() || assignment.isComplete() || assignment.isForfeited())
			{
				// Continue if the assignment isn't displayable
				if(!assignment.isDisplayed()) continue;

				// Remove the assignment, it's no longer needed
				assignment.setDisplay(false);
				assignment.setActive(false);

				count++;
			}
		}

		if(count > 0) MiscUtil.log("info", count + " inactive assignments have been updated.");
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
				Slayer.plugin.getServer().getPluginManager().callEvent(assignmentExpireEvent);
				if(assignmentExpireEvent.isCancelled()) return;

				// Log the expiration
				MiscUtil.log("info", "An assignment (#: " + assignment.getID() + ") for " + assignment.getOfflinePlayer().getName() + " has expired.");

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
					MiscUtil.sendMsg(player, ChatColor.GRAY + MiscUtil.getString("mob_task_update").replace("{obtained}", ChatColor.YELLOW + "" + assignment.getAmountObtained() + ChatColor.GRAY).replace("{needed}", ChatColor.YELLOW + "" + assignment.getAmountNeeded() + ChatColor.GRAY).replace("{task}", ChatColor.AQUA + assignment.getTask().getName() + ChatColor.GRAY));
				}

				// Now handle completed assignments
				if(assignment.isComplete())
				{
					// The assignment is complete, call the event
					AssignmentCompleteEvent assignmentCompleteEvent = new AssignmentCompleteEvent(player, assignment);
					Slayer.plugin.getServer().getPluginManager().callEvent(assignmentCompleteEvent);
					if(assignmentCompleteEvent.isCancelled()) return;

					// Set the assignment to inactive
					assignment.setActive(false);
				}
			}
		}

		// Add overall kill for tracking purposes
		PlayerUtil.addKill(player, entity);
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

			if(assignment.getTask().getItemStack().isSimilar(item))
			{
				int amount = item.getAmount();

				// Check the count to make sure it doesn't go over the requirement. If it does, set it to the amount needed.
				if(amount > assignment.getAmountLeft()) amount = assignment.getAmountLeft();

				// The item matches the requirement, add to the count
				assignment.addProgress(amount);

				// For items, always message the player (makes up for the possibility of picking up multiple items and not receiving the message
				if(!assignment.isComplete())
				{
					MiscUtil.sendMsg(player, ChatColor.GRAY + MiscUtil.getString("item_task_update").replace("{obtained}", ChatColor.YELLOW + "" + assignment.getAmountObtained() + ChatColor.GRAY).replace("{needed}", ChatColor.YELLOW + "" + assignment.getAmountNeeded() + ChatColor.GRAY).replace("{task}", ChatColor.AQUA + assignment.getTask().getName() + ChatColor.GRAY));
				}

				// Now handle completed assignments
				if(assignment.isComplete())
				{
					// The assignment is complete, call the event
					AssignmentCompleteEvent assignmentCompleteEvent = new AssignmentCompleteEvent(player, assignment);
					Slayer.plugin.getServer().getPluginManager().callEvent(assignmentCompleteEvent);
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
		// Return if they have no assignments
		if(getAssignments(player) == null) return;

		// First get their assignments
		for(Assignment assignment : getAssignments(player))
		{
			// Continue to next assignment if this one is inactive
			if(!assignment.isActive()) continue;

			// Continue to next assignment if it doesn't match the correct TaskType
			if(!assignment.getTask().getType().equals(Task.TaskType.ITEM)) continue;

			if(assignment.getTask().getItemStack().isSimilar(item))
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
