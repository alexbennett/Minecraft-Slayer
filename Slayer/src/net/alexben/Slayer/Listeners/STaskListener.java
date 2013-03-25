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

package net.alexben.Slayer.Listeners;

import net.alexben.Slayer.Events.TaskAssignEvent;
import net.alexben.Slayer.Events.TaskCompleteEvent;
import org.bukkit.ChatColor;
import org.bukkit.OfflinePlayer;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;

public class STaskListener implements Listener
{
    @EventHandler(priority = EventPriority.MONITOR)
    private void onTaskAssign(TaskAssignEvent event)
    {
        OfflinePlayer player = event.getPlayer();

        if(player.isOnline())
        {
            player.getPlayer().sendMessage(ChatColor.GREEN + "You have been given a new Slayer assignment!");
            player.getPlayer().sendMessage(ChatColor.GRAY + "" + ChatColor.ITALIC + "For details, please type " + ChatColor.ITALIC + "/sl view tasks");
        }
    }

    @EventHandler(priority = EventPriority.MONITOR)
    private void onTaskComplete(TaskCompleteEvent event)
    {
        // TODO
    }
}
