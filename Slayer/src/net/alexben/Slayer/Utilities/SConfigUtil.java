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

import java.util.ArrayList;

import net.alexben.Slayer.Slayer;

import org.bukkit.configuration.Configuration;
import org.bukkit.configuration.file.FileConfiguration;

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
