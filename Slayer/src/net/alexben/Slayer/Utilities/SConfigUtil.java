package net.alexben.Slayer.Utilities;

import java.util.ArrayList;

import net.alexben.Slayer.Core.Slayer;

import org.bukkit.configuration.Configuration;

/**
 * Utility for all data-related methods.
 */
public class SConfigUtil
{
	public static void initialize()
	{
		Configuration mainConfig = Slayer.plugin.getConfig().getRoot();
		mainConfig.options().copyDefaults(true);
        Slayer.plugin.saveConfig();
	}

	public static int getSettingInt(String id)
	{
		if(Slayer.plugin.getConfig().isInt(id))
		{
			return Slayer.plugin.getConfig().getInt(id);
		}
		else return -1;
	}

	public static String getSettingString(String id)
	{
		if(Slayer.plugin.getConfig().isString(id))
		{
			return Slayer.plugin.getConfig().getString(id);
		}
		else return null;
	}

	public static boolean getSettingBoolean(String id)
	{
		return !Slayer.plugin.getConfig().isBoolean(id) || Slayer.plugin.getConfig().getBoolean(id);
	}

	public static double getSettingDouble(String id)
	{
		if(Slayer.plugin.getConfig().isDouble(id))
		{
			return Slayer.plugin.getConfig().getDouble(id);
		}
		else return -1;
	}

	public static ArrayList<String> getSettingArrayListString(String id)
	{
		ArrayList<String> strings = new ArrayList<String>();
		if(Slayer.plugin.getConfig().isList(id))
		{
			for(String s : Slayer.plugin.getConfig().getStringList(id))
				strings.add(s);
			return strings;
		}
		else return null;
	}
}
