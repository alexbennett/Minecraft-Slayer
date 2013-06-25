package net.alexben.Slayer.Listeners;

import net.alexben.Slayer.Core.Events.*;
import net.alexben.Slayer.Core.Objects.Assignment;
import net.alexben.Slayer.Core.Objects.SerialItemStack;
import net.alexben.Slayer.Core.Slayer;
import net.alexben.Slayer.Utilities.SConfigUtil;
import net.alexben.Slayer.Utilities.SMiscUtil;
import net.alexben.Slayer.Utilities.SPlayerUtil;

import org.bukkit.ChatColor;
import org.bukkit.Color;
import org.bukkit.FireworkEffect;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Firework;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.FireworkMeta;

public class SAssignmentListener implements Listener
{
	@EventHandler(priority = EventPriority.MONITOR)
	private void onTaskAssign(TaskAssignEvent event)
	{
		Player player = event.getPlayer();

		if(player.isOnline())
		{
			SMiscUtil.sendMsg(player.getPlayer(), ChatColor.GREEN + SMiscUtil.getString("new_assignment"));
			SMiscUtil.sendMsg(player.getPlayer(), ChatColor.GRAY + SMiscUtil.getString("new_assignment_details").replace("{command}", ChatColor.GOLD + "/sl my tasks" + ChatColor.GRAY));
		}
	}

	@EventHandler(priority = EventPriority.MONITOR)
	private void onAssignmentComplete(AssignmentCompleteEvent event)
	{
		// Define variables
		final Player player = event.getPlayer();
		final Assignment assignment = event.getAssignment();

		// Add the rewards to their reward queue
		for(ItemStack item : assignment.getTask().getReward())
		{
			SPlayerUtil.addReward(player, new SerialItemStack(item));
		}

		// From this point on, return if the player is offline
		if(!player.isOnline()) return;

		SMiscUtil.sendMsg(player, ChatColor.GRAY + SMiscUtil.getString("assignment_complete").replace("{task}", ChatColor.AQUA + assignment.getTask().getName() + ChatColor.GRAY));
		SMiscUtil.sendMsg(player, ChatColor.GRAY + SMiscUtil.getString("rewards_awaiting").replace("{rewards}", ChatColor.YELLOW + "" + SPlayerUtil.getRewardAmount(player) + ChatColor.GRAY).replace("{command}", ChatColor.GOLD + "/rewards" + ChatColor.GRAY));

		// Shoot a firework, woohoo!
		if(SConfigUtil.getSettingBoolean("tasks.completion_firework"))
		{
			Firework firework = (Firework) player.getPlayer().getLocation().getWorld().spawnEntity(player.getPlayer().getLocation(), EntityType.FIREWORK);
			FireworkMeta fireworkmeta = firework.getFireworkMeta();
			FireworkEffect.Type type = FireworkEffect.Type.BALL_LARGE;
			FireworkEffect effect = FireworkEffect.builder().flicker(false).withColor(Color.AQUA).withFade(Color.FUCHSIA).with(type).trail(true).build();
			fireworkmeta.addEffect(effect);
			fireworkmeta.setPower(1);
			firework.setFireworkMeta(fireworkmeta);
		}

		// Tracking
		SPlayerUtil.addCompletion(player);

		// Handle points and leveling
		SPlayerUtil.addPoints(player, assignment.getTask().getValue());

		if(SPlayerUtil.getPoints(player) >= SPlayerUtil.getPointsGoal(player))
		{
			// Loop and make sure they didn't get a crap-ton of points and if so update accordingly
			while(SPlayerUtil.getPoints(player) >= SPlayerUtil.getPointsGoal(player))
			{
				SlayerLevelUpEvent levelUpEvent = new SlayerLevelUpEvent(player, SPlayerUtil.getLevel(player), SPlayerUtil.getLevel(player) + 1);
                Slayer.plugin.getServer().getPluginManager().callEvent(levelUpEvent);

				if(!levelUpEvent.isCancelled())
				{
					// Get rid of the points
					SPlayerUtil.subtractPoints(player, SPlayerUtil.getPointsGoal(player));

					// Add the level
					SPlayerUtil.addLevel(player);

					// Message the player
					SMiscUtil.sendMsg(player, ChatColor.GRAY + SMiscUtil.getString("level_up_msg1").replace("{level}", ChatColor.LIGHT_PURPLE + "" + SPlayerUtil.getLevel(player) + ChatColor.GRAY));
					SMiscUtil.sendMsg(player, ChatColor.GRAY + SMiscUtil.getString("level_up_msg2").replace("{points}", "" + ChatColor.YELLOW + ((int) SPlayerUtil.getPointsGoal(player) - SPlayerUtil.getPoints(player)) + ChatColor.GRAY));

					if(SConfigUtil.getSettingBoolean("misc.level_up_firework"))
					{
						// Shoot a random firework!
						SMiscUtil.shootRandomFirework(player.getLocation());
					}
				}
			}
		}

		// Update the scoreboard
		SPlayerUtil.updateScoreboard(player);
	}

	@EventHandler(priority = EventPriority.MONITOR)
	private void onAssignmentExpire(AssignmentExpireEvent event)
	{
		// Do nothing if they're offline
		if(!event.getOfflinePlayer().isOnline()) return;

		Player player = event.getOfflinePlayer().getPlayer();
		Assignment assignment = event.getAssignment();

		SMiscUtil.sendMsg(player, ChatColor.GRAY + SMiscUtil.getString("assignment_expired").replace("{task}", ChatColor.AQUA + assignment.getTask().getName() + ChatColor.GRAY));

		// Handle expiration punishment if enabled
		if(SConfigUtil.getSettingBoolean("expiration.punish"))
		{
			// TODO: Update punishments.
		}

		// Tracking
		SPlayerUtil.addExpiration(player);

		// Update the scoreboard
		SPlayerUtil.updateScoreboard(player);
	}

	@EventHandler(priority = EventPriority.MONITOR)
	private void onAssignmentForfeit(AssignmentForfeitEvent event)
	{
		// Define the variables
		OfflinePlayer player = event.getOfflinePlayer();
		Assignment assignment = event.getAssignment();

		SMiscUtil.sendMsg(player, ChatColor.GRAY + SMiscUtil.getString("assignment_forfeit").replace("{task}", ChatColor.AQUA + assignment.getTask().getName() + ChatColor.GRAY));

		// Handle punishments if enabled
		if(SConfigUtil.getSettingBoolean("forfeit.punish"))
		{
			// TODO: Update punishments.
		}

		// Tracking
		SPlayerUtil.addForfeit(player);

		if(player.isOnline())
		{
			// Update scoreboard
			SPlayerUtil.updateScoreboard(player.getPlayer());
		}
	}
}
