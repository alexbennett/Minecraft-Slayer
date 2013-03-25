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

import org.bukkit.entity.EntityType;

import java.io.Serializable;
import java.util.ArrayList;

public class Task implements Serializable
{
    private String name = null, desc = null;
    private int value, amountNeeded;
    private EntityType mob;
    private ArrayList<SerialItemStack> reward = new ArrayList<SerialItemStack>();

    public Task(String taskName, String taskDesc, int taskValue, ArrayList<SerialItemStack> rewardItems, int amount, EntityType mobType)
    {
        name = taskName;
        desc = taskDesc;
        amountNeeded = amount;
        mob = mobType;
        value = taskValue;

        if(!rewardItems.isEmpty())
        {
            for(SerialItemStack item : rewardItems)
            {
                reward.add(item);
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
     * Returns all plugin rewards.
     *
     * @return ArrayList
     */
    public ArrayList<SerialItemStack> getReward()
    {
        return reward;
    }

    /**
     * Returns the entity type associated with this task.
     *
     * @return EntityType
     */
    public EntityType getMob()
    {
        return mob;
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
}
