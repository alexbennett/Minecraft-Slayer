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

import net.alexben.Slayer.Libraries.Objects.Assignment;
import net.alexben.Slayer.Utilities.STaskUtil;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDeathEvent;

public class SEntityListener implements Listener
{
    @EventHandler(priority = EventPriority.MONITOR)
    private void onEntityDeathEvent(EntityDeathEvent event)
    {
        // Define entity
        Entity entity = event.getEntity();

        // Make sure the damage was caused by a player. If not, return.
        if(!(entity.getLastDamageCause() instanceof EntityDamageByEntityEvent)) return;
        if(!(((EntityDamageByEntityEvent) entity.getLastDamageCause()).getDamager() instanceof Player)) return;

        // It's a player, let's go ahead and check their active assignments
        EntityDamageByEntityEvent damageEvent = (EntityDamageByEntityEvent) entity.getLastDamageCause();
        Player player = (Player) damageEvent.getDamager();

        // It's a player, now look for active tasks
        for(Assignment assignment : STaskUtil.getAssignments(player))
        {
            if(assignment.getTask().getMob().equals(entity.getType()))
            {
                // TODO Expand this, currently just a place-holder for kill tracking
                player.sendMessage("You killed a " + entity.getType().getName() + ", which is part of the \"" + assignment.getTask().getName() + "\" task.");
            }
        }
    }
}
