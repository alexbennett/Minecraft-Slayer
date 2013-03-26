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

package net.alexben.Slayer.Handlers;

import net.alexben.Slayer.Libraries.Objects.Assignment;
import net.alexben.Slayer.Utilities.STaskUtil;
import net.alexben.Slayer.Utilities.SUtil;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class SCommands implements CommandExecutor
{
    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args)
    {
        Player player = (Player) sender;

        if(command.getName().equalsIgnoreCase("sladmin")) return sl_admin(player, args);
        if(command.getName().equalsIgnoreCase("sl")) return slayer(player, args);

        return false;
    }

    /**
     * Handles all admin-specific commands.
     */
    public boolean sl_admin(Player player, String[] args)
    {
        if(args.length == 0)
        {
            SUtil.taggedMessage(player, "Admin Directory");
            player.sendMessage(ChatColor.GRAY + "" + ChatColor.ITALIC + "/sladmin assign <player>");

            return true;
        }

        return false;
    }

    /**
     * Handles all basic commands.
     */
    public boolean slayer(Player player, String[] args)
    {
        if(args.length == 0)
        {
            SUtil.taggedMessage(player, "Command Directory");
            player.sendMessage(ChatColor.GRAY + "" + ChatColor.ITALIC + "/sl leaderboard");
            player.sendMessage(ChatColor.GRAY + "" + ChatColor.ITALIC + "/sl new task");
            player.sendMessage(ChatColor.GRAY + "" + ChatColor.ITALIC + "/sl view tasks");
            player.sendMessage(ChatColor.RED + "" + ChatColor.ITALIC + "/sladmin");

            return true;
        }
        else
        {
            String action, category = null, option1 = null, option2 = null;

            action = args[0];
            if(args.length > 1) category = args[1];
            if(args.length > 2) category = args[2];
            if(args.length > 3) category = args[3];


            if(action.equalsIgnoreCase("new"))
            {
                if(category.equalsIgnoreCase("task"))
                {
                    STaskUtil.assignRandomTask(player);

                    return true;
                }
            }
            else if(action.equalsIgnoreCase("view"))
            {
                if(category.contains("task"))
                {
                    if(STaskUtil.getAssignments(player) == null)
                    {
                        SUtil.sendMessage(player, ChatColor.GRAY + "You currently have no Slayer Tasks.");
                        return true;
                    }

                    player.sendMessage(" ");
                    SUtil.sendMessage(player, ChatColor.GRAY + "Your current task(s) are:");
                    player.sendMessage(" ");

                    // List the tasks
                    for(Assignment assignment : STaskUtil.getAssignments(player))
                    {
                        ChatColor color;
                        if(assignment.isActive()) color = ChatColor.GREEN;
                        else color = ChatColor.RED;

                        player.sendMessage(ChatColor.GRAY + " > " + color + assignment.getTask().getName() + ChatColor.GRAY + " (Mob: " + assignment.getTask().getMob().getName() + ", Kills: " + assignment.getAmountObtained() + "/" + assignment.getAmountNeeded() + ")");
                    }

                    player.sendMessage(" ");
                    player.sendMessage(ChatColor.GREEN + "Green" + ChatColor.GRAY + " means active, " + ChatColor.RED + "red" + ChatColor.GRAY + " means inactive.");

                    return true;
                }
            }
            else if(action.equalsIgnoreCase("leaderboard"))
            {
                SUtil.taggedMessage(player, "This functionality is coming soon.");
                return true;
            }
        }

        return false;
    }
}
