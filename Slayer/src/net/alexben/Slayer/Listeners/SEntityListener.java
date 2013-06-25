package net.alexben.Slayer.Listeners;

import net.alexben.Slayer.Core.Objects.Assignment;
import net.alexben.Slayer.Utilities.*;

import org.bukkit.ChatColor;
import org.bukkit.entity.Arrow;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.CreatureSpawnEvent;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDeathEvent;

public class SEntityListener implements Listener
{
	@EventHandler(priority = EventPriority.MONITOR)
	private void onEntityDeathEvent(EntityDeathEvent event)
	{
		// Define variables
		Entity entity = event.getEntity();
		Player player = null;

		// Make sure the damage was caused by a player. If not, return.
		if(!(entity.getLastDamageCause() instanceof EntityDamageByEntityEvent)) return;

		// Define variables
		EntityDamageByEntityEvent damageEvent = (EntityDamageByEntityEvent) entity.getLastDamageCause();

		// Handle for a player and arrow kills only
		if(damageEvent.getDamager() instanceof Player)
		{
			player = (Player) damageEvent.getDamager();
		}
		else if(damageEvent.getDamager() instanceof Arrow)
		{
			Arrow arrow = (Arrow) damageEvent.getDamager();

			if(arrow.getShooter() instanceof Player)
			{
				player = (Player) arrow.getShooter();
			}
		}
		else return;

		// If it's a spawner entity and blocking is enabled then return
		if(SConfigUtil.getSettingBoolean("block.spawner_kills") && SEntityUtil.isSpawnerEntity(entity))
		{
			// Remove the entity from the tracking
			SEntityUtil.removeEntity(entity);

			if(SDataUtil.hasData(player, "spawner_kill_tracking"))
			{
				int kills = SObjUtil.toInteger(SDataUtil.getData(player, "spawner_kill_tracking"));

				if(kills >= 5)
				{
					for(Assignment assignment : STaskUtil.getAssignments(player))
					{
						// Continue to next assignment if this one is inactive
						if(!assignment.isActive()) continue;

						// Message them if they have a task involving this mob
						if(assignment.getTask().getType().equals(entity.getType()))
						{
							SMiscUtil.sendMsg(player, ChatColor.GRAY + SMiscUtil.getString("camping_spawners"));
							break;
						}
					}

					SDataUtil.removeData(player, "spawner_kill_tracking");
				}

				SDataUtil.saveData(player, "spawner_kill_tracking", kills + 1);

				return;
			}
			else
			{
				SDataUtil.saveData(player, "spawner_kill_tracking", 1);

				return;
			}
		}
		else
		{
			// Process the kill across all of the player's assignments
			STaskUtil.processKill(player, entity);
		}
	}

	@EventHandler(priority = EventPriority.MONITOR)
	private void onCreatureSpawnEvent(CreatureSpawnEvent event)
	{
		// Add the entity to the list
		if(SConfigUtil.getSettingBoolean("block.spawner_kills") && event.getSpawnReason().equals(CreatureSpawnEvent.SpawnReason.SPAWNER))
		{
			SEntityUtil.addEntity(event.getEntity(), event.getSpawnReason());
		}
	}
}
