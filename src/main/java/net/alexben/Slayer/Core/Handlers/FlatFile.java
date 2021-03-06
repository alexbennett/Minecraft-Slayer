package net.alexben.Slayer.Core.Handlers;

import java.io.*;
import java.util.HashMap;

import net.alexben.Slayer.Core.Slayer;
import net.alexben.Slayer.Utilities.DataUtil;
import net.alexben.Slayer.Utilities.EntityUtil;
import net.alexben.Slayer.Utilities.MiscUtil;

import org.bukkit.OfflinePlayer;

public class FlatFile
{
	private static File saveDir;

	public static void start()
	{
		// Rename old folder to new name, if it exists
		File oldDir = new File(Slayer.plugin.getDataFolder() + "players");
		if(oldDir.exists())
		{
			oldDir.renameTo(new File(Slayer.plugin.getDataFolder() + "saves"));
			MiscUtil.log("info", "Old player save directory renamed for new save system.");
		}

		saveDir = new File(Slayer.plugin.getDataFolder().getPath() + File.separator + "saves");
		if(!saveDir.exists())
		{
			saveDir.mkdirs();
			MiscUtil.log("info", "New save directory created.");
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
			for(File file : saveDir.listFiles())
			{
				file.delete();
			}

			// Start the timer
			long startTimer = System.currentTimeMillis();

			int playerCount = savePlayers();
			int entityCount = saveEntities();

			// Stop the timer
			long stopTimer = System.currentTimeMillis();
			double totalTime = (double) (stopTimer - startTimer);

			MiscUtil.log("info", entityCount + " entities and " + playerCount + " player(s) saved in " + (totalTime / 1000) + " seconds.");

			return true;
		}
		catch(Exception e)
		{
			MiscUtil.log("severe", "Something went wrong while saving.");
			e.printStackTrace();

			return false;
		}
	}

	public synchronized static int saveEntities()
	{
		start();
		int count = 0;

		try
		{
			ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(saveDir.getPath() + File.separator + "entities.slay"));
			oos.writeObject(EntityUtil.getEntityMap());
			oos.flush();
			oos.close();

			count += EntityUtil.getEntityMap().size();
		}
		catch(Exception e)
		{
			MiscUtil.log("severe", "Something went wrong while saving entities.");
			e.printStackTrace();
		}

		return count;
	}

	public synchronized static int savePlayers()
	{
		int count = 0;

		try
		{
			for(String key : DataUtil.getAllData().keySet())
			{
				count++;

				ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(saveDir.getPath() + File.separator + key + ".slay"));
				oos.writeObject(DataUtil.getAllData().get(key));
				oos.flush();
				oos.close();
			}
		}
		catch(Exception e)
		{
			MiscUtil.log("severe", "Something went wrong while saving players.");
			e.printStackTrace();
		}

		return count;
	}

	public synchronized static void savePlayer(OfflinePlayer player)
	{
		try
		{
			ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(saveDir.getPath() + File.separator + player.getName() + ".slay"));
			oos.writeObject(DataUtil.getAllData().get(player.getName()));
			oos.flush();
			oos.close();
		}
		catch(Exception e)
		{
			MiscUtil.log("severe", "Something went wrong while saving the player: " + player.getName());
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
			MiscUtil.log("info", "Loading all data...");

			// Start the timer
			long startTimer = System.currentTimeMillis();

			// Load the data
			int saveCount = loadData() - 1;

			// Stop the timer
			long stopTimer = System.currentTimeMillis();
			double totalTime = (double) (stopTimer - startTimer);

			MiscUtil.log("info", saveCount + " player(s) loaded in " + (totalTime / 1000) + " seconds.");
		}
		catch(Exception e)
		{
			MiscUtil.log("severe", "Something went wrong while loading data.");
			e.printStackTrace();
		}
	}

	@SuppressWarnings("unchecked")
	public synchronized static int loadData()
	{
		start();
		int count = 0;

		File[] fileList = saveDir.listFiles();
		if(fileList != null)
		{
			for(File element : fileList)
			{
				count++;

				String name = element.getName();
				if(name.endsWith(".slay") && !name.equalsIgnoreCase("entities.slay"))
				{
					name = name.substring(0, name.length() - 5);

					try
					{
						ObjectInputStream ois = new ObjectInputStream(new FileInputStream(element));
						Object data = ois.readObject();
						DataUtil.getAllData().put(name, (HashMap<String, Object>) data);
						ois.close();
					}
					catch(Exception e)
					{
						MiscUtil.log("severe", "Could not load player: " + name);
						e.printStackTrace();
					}
				}
				else if(name.equalsIgnoreCase("entities.slay"))
				{
					try
					{
						ObjectInputStream ois = new ObjectInputStream(new FileInputStream(element));
						Object data = ois.readObject();
						EntityUtil.getEntityMap().putAll((HashMap<Integer, HashMap<String, Object>>) data);
						ois.close();
					}
					catch(Exception e)
					{
						MiscUtil.log("severe", "There was an error while loading entities...");
						e.printStackTrace();
					}
				}
			}
		}

		return count;
	}
}
