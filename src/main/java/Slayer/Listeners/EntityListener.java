package Slayer.Listeners;

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

import Slayer.Core.Objects.Assignment;
import Slayer.Utilities.*;

public class EntityListener implements Listener
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
		if(ConfigUtil.getSettingBoolean("block.spawner_kills") && EntityUtil.isSpawnerEntity(entity))
		{
			// Remove the entity from the tracking
			EntityUtil.removeEntity(entity);

			if(DataUtil.hasData(player, "spawner_kill_tracking"))
			{
				int kills = ObjUtil.toInteger(DataUtil.getData(player, "spawner_kill_tracking"));

				if(kills >= 5)
				{
					for(Assignment assignment : TaskUtil.getAssignments(player))
					{
						// Continue to next assignment if this one is inactive
						if(!assignment.isActive()) continue;

						// Message them if they have a task involving this mob
						if(assignment.getTask().getType().equals(entity.getType()))
						{
							MiscUtil.sendMsg(player, ChatColor.GRAY + MiscUtil.getString("camping_spawners"));
							break;
						}
					}

					DataUtil.removeData(player, "spawner_kill_tracking");
				}

				DataUtil.saveData(player, "spawner_kill_tracking", kills + 1);

				return;
			}
			else
			{
				DataUtil.saveData(player, "spawner_kill_tracking", 1);

				return;
			}
		}
		else
		{
			// Process the kill across all of the player's assignments
			TaskUtil.processKill(player, entity);
		}
	}

	@EventHandler(priority = EventPriority.MONITOR)
	private void onCreatureSpawnEvent(CreatureSpawnEvent event)
	{
		// Add the entity to the list
		if(ConfigUtil.getSettingBoolean("block.spawner_kills") && event.getSpawnReason().equals(CreatureSpawnEvent.SpawnReason.SPAWNER))
		{
			EntityUtil.addEntity(event.getEntity(), event.getSpawnReason());
		}
	}
}
