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

import java.io.*;
import java.util.HashMap;

import net.alexben.Slayer.Utilities.SDataUtil;
import net.alexben.Slayer.Utilities.SUtil;

import org.bukkit.OfflinePlayer;

public class SFlatFile
{
	private static final String path = "plugins/Slayer/";
	private static File PlayerDir;

	public static void start()
	{
		PlayerDir = new File(path + "players");
		if(!PlayerDir.exists())
		{
			PlayerDir.mkdirs();
			SUtil.log("info", "New player data save directory created.");
		}
	}

	/**
	 * Saves all data in the plugin and returns a boolean based on success or failure.
	 * 
	 * @return boolean
	 */
	public synchronized static boolean save()
	{
		start();

		try
		{
			// Clear files first
			for(File file : PlayerDir.listFiles())
				file.delete();

			// Start the timer
			long startTimer = System.currentTimeMillis();

			int playerCount = savePlayers();

			// Stop the timer
			long stopTimer = System.currentTimeMillis();
			double totalTime = (double) (stopTimer - startTimer);

			SUtil.log("info", playerCount + " players saved in " + (totalTime / 1000) + " seconds.");

			return true;
		}
		catch(Exception e)
		{
			SUtil.log("severe", "Something went wrong while saving.");
			e.printStackTrace();

			return false;
		}
	}

	public synchronized static int savePlayers()
	{
		start();
		int count = 0;

		try
		{
			for(String key : SDataUtil.getAllData().keySet())
			{
				count++;

				ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(PlayerDir.getPath() + File.separator + key + ".slay"));
				oos.writeObject(SDataUtil.getAllData().get(key));
				oos.flush();
				oos.close();
			}
		}
		catch(Exception e)
		{
			SUtil.log("severe", "Something went wrong while saving players.");
			e.printStackTrace();
		}

		return count;
	}

	public synchronized static void savePlayer(OfflinePlayer player)
	{
		try
		{
			ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(PlayerDir.getPath() + File.separator + player.getName() + ".slay"));
			oos.writeObject(SDataUtil.getAllData().get(player.getName()));
			oos.flush();
			oos.close();
		}
		catch(Exception e)
		{
			SUtil.log("severe", "Something went wrong while saving the player: " + player.getName());
			e.printStackTrace();
		}
	}

	/*
	 * load() : Loads all Flat File data to HashMaps.
	 */
	public synchronized static void load()
	{
		start();

		try
		{
			SUtil.log("info", "Loading all data...");

			// Start the timer
			long startTimer = System.currentTimeMillis();

			// Load the data
			int playerCount = loadPlayers();

			// Stop the timer
			long stopTimer = System.currentTimeMillis();
			double totalTime = (double) (stopTimer - startTimer);

			SUtil.log("info", playerCount + " players loaded in " + (totalTime / 1000) + " seconds.");
		}
		catch(Exception e)
		{
			SUtil.log("severe", "Something went wrong while loading data.");
			e.printStackTrace();
		}
	}

	@SuppressWarnings("unchecked")
	public synchronized static int loadPlayers()
	{
		start();
		int count = 0;

		File[] fileList = PlayerDir.listFiles();
		if(fileList != null)
		{
			for(File element : fileList)
			{
				count++;

				String name = element.getName();
				if(name.endsWith(".slay"))
				{
					name = name.substring(0, name.length() - 5);

					try
					{
						ObjectInputStream ois = new ObjectInputStream(new FileInputStream(element));
						Object data = ois.readObject();
						SDataUtil.getAllData().put(name, (HashMap<String, Object>) data);
						ois.close();
					}
					catch(Exception e)
					{
						SUtil.log("severe", "Could not load player: " + name);
						e.printStackTrace();
					}
				}
			}
		}

		return count;
	}
}
