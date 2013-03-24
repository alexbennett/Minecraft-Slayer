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

import net.alexben.Slayer.Libraries.Objects.Task;
import org.bukkit.OfflinePlayer;

import java.util.ArrayList;

/**
 * Handles all Slayer task-related methods.
 */
public class STaskUtil
{
    /**
     * Returns an ArrayList of active tasks for <code>player</code>.
     *
     * @param player the player to check.
     * @return an ArrayList of Tasks.
     */
    public static ArrayList<Task> getTasks(OfflinePlayer player)
    {
        if(SUtil.getData(player, "tasks") != null)
        {
            return (ArrayList<Task>) SUtil.getData(player, "tasks");
        }
        return null;
    }

    /**
     * Assigns the <code>task</code> to the <code>player</code>.
     *
     * @param player the player to assign to.
     * @param task the task to assign.
     */
    public static void assignTask(OfflinePlayer player, Task task)
    {
        if(SUtil.getData(player, "tasks") != null)
        {
            ((ArrayList<Task>) SUtil.getData(player, "tasks")).add(task);
        }
        else
        {
            ArrayList<Task> tasks = new ArrayList<Task>();
            tasks.add(task);

            SUtil.saveData(player, "tasks", tasks);
        }
    }
}
