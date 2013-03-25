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

import net.alexben.Slayer.Events.TaskAssignEvent;
import net.alexben.Slayer.Libraries.Objects.Assignment;
import net.alexben.Slayer.Libraries.Objects.Task;
import org.bukkit.ChatColor;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.Random;

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
     * Returns an ArrayList of active tasks for <code>player</code>.
     *
     * @param player the player to check.
     * @return an ArrayList of Tasks.
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
}
