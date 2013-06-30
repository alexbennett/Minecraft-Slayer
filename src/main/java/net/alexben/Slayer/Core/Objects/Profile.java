package net.alexben.Slayer.Core.Objects;

import java.io.Serializable;
import java.util.ArrayList;

import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.inventory.ItemStack;

public class Profile implements Serializable
{
	// Define main variables
	private static final long serialVersionUID = 1869682453395176134L;
	private String player;
	private long id;
	private int level, points, kills, deaths;

	// Rewards
	private ArrayList<SerialItemStack> itemRewards = new ArrayList<SerialItemStack>();

	void setPlayer(OfflinePlayer player)
	{
		this.player = player.getName();
	}

	void setId(Long id)
	{
		this.id = id;
	}

	void setLevel(Integer level)
	{
		this.level = level;
	}

	void setPoints(Integer points)
	{
		this.points = points;
	}

	void setKills(Integer kills)
	{
		this.kills = kills;
	}

	void setDeaths(Integer deaths)
	{
		this.deaths = deaths;
	}

	public void addItemReward(ItemStack item)
	{

	}

	public OfflinePlayer getOfflinePlayer()
	{
		return Bukkit.getOfflinePlayer(player);
	}
}
