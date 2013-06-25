/*
 * Originally made by github user @betterphp, adapted, and heavily modified by Censored Software.  Used with permission.
 * https://github.com/betterphp/UpdateChecker/blob/master/uk/co/jacekk/bukkit/updatechecker/UpdateChecker.java
 */

package net.alexben.Slayer.Modules;

import java.io.*;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.util.logging.Logger;

import javax.xml.parsers.DocumentBuilderFactory;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.PluginDescriptionFile;
import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

public class BukkitUpdate implements Listener
{
	private Plugin plugin;
	private URL filesFeed;
	private Logger logger;
	private String command, permission, version, link, jarLink;
	private boolean supported;

	public BukkitUpdate(Plugin plugin, String url, String command, String permission)
	{
		try
		{
			this.plugin = plugin;
			this.filesFeed = new URL(url);
			this.command = command;
			this.permission = permission;
			this.logger = Logger.getLogger("Minecraft");
		}
		catch(Exception e)
		{
			this.logger.severe("[" + this.plugin.getName() + "] Could not connect to BukkitDev");
		}
	}

	/**
	 * Checks for updates and auto-updates if need be.
	 */
	public void initialize()
	{
		// Define SUPPORTED
		this.supported = versionExists();

		// Define Update Listener
		this.plugin.getServer().getPluginManager().registerEvents(this, this.plugin);

		// Define variables
		boolean auto = this.plugin.getConfig().getBoolean("update.auto");
		boolean notify = this.plugin.getConfig().getBoolean("update.notify");

		// Check for updates, and then update if need be
		if(auto || notify || !this.supported)
		{
			if(check() || !this.supported)
			{
				if(auto) download();
				if(!this.supported)
				{
					Bukkit.broadcast(ChatColor.RED + "The version of " + ChatColor.YELLOW + this.plugin.getName() + ChatColor.RED + " you are using is not supported.", this.permission);
					Bukkit.broadcast(ChatColor.RED + "It has been removed from BukkitDev. This " + ChatColor.ITALIC + "may" + ChatColor.RESET + ChatColor.RED + " be because it", this.permission);
					if(auto)
					{
						Bukkit.broadcast(ChatColor.RED + "is not safe.", this.permission);
						Bukkit.broadcast("Please " + ChatColor.YELLOW + "reload the server " + ChatColor.WHITE + "to finish the auto-update.", this.permission);
					}
					else
					{
						Bukkit.broadcast(ChatColor.RED + "is not safe. It is " + ChatColor.BOLD + "strongly" + ChatColor.RESET + ChatColor.RED + " suggested that you update soon.", this.permission);
						Bukkit.broadcast("Please update by using " + ChatColor.YELLOW + this.command, this.permission);
					}
				}
				else if(notify)
				{
					Bukkit.broadcast(ChatColor.RED + "There is a new stable release for " + this.plugin.getName() + ".", this.permission);
					if(auto) Bukkit.broadcast("Please " + ChatColor.YELLOW + "reload the server " + ChatColor.WHITE + "to finish the auto-update.", this.permission);
					else Bukkit.broadcast("Please update by using " + ChatColor.YELLOW + this.command, this.permission);
				}
			}
		}
	}

	public boolean download()
	{
		try
		{
			// Define variables
			byte[] buffer = new byte[1024];
			int read = 0;
			int bytesTransferred = 0;
			String downloadLink = this.jarLink;

			this.logger.info("[" + this.plugin.getName() + "] Attempting to download the latest version...");

			// Set latest build URL
			URL plugin = new URL(downloadLink);

			// Open connection to latest build and set user-agent for download, also determine file size
			URLConnection pluginCon = plugin.openConnection();
			pluginCon.setRequestProperty("User-Agent", "Mozilla/5.0 (Macintosh; U; Intel Mac OS X 10.4; en-US; rv:1.9.2.2) Gecko/20100316 Firefox/3.6.2"); // FIXES 403 ERROR
			int contentLength = pluginCon.getContentLength();

			// Check for update directory
			File updateFolder = new File("plugins" + File.separator + Bukkit.getUpdateFolder());
			if(!updateFolder.exists()) updateFolder.mkdir();

			// Create new .jar file and add it to update directory
			File pluginUpdate = new File("plugins" + File.separator + Bukkit.getUpdateFolder() + File.separator + this.plugin + ".jar");
			this.logger.info("[" + this.plugin.getName() + "] File will be written to: " + pluginUpdate.getCanonicalPath());

			InputStream is = pluginCon.getInputStream();
			OutputStream os = new FileOutputStream(pluginUpdate);

			while((read = is.read(buffer)) > 0)
			{
				os.write(buffer, 0, read);
				bytesTransferred += read;

				if(contentLength > 0)
				{
					// Determine percent of file and add it to variable
					int percentTransferred = (int) (((float) bytesTransferred / contentLength) * 100);

					if(percentTransferred != 100)
					{
						this.logger.info("[" + this.plugin.getName() + "] Download progress: " + percentTransferred + "%");
					}
				}
			}

			is.close();
			os.flush();
			os.close();

			// Download complete!
			this.logger.info("[" + this.plugin.getName() + "] Download complete!");
			this.logger.info("[" + this.plugin.getName() + "] Update will complete on next server reload.");

			return true;
		}
		catch(MalformedURLException e)
		{
			this.logger.warning("[ " + this.plugin.getName() + "] Error accessing URL: " + e);
		}
		catch(FileNotFoundException e)
		{
			this.logger.warning("[ " + this.plugin.getName() + "] Error accessing URL: " + e);
		}
		catch(IOException e)
		{
			this.logger.warning("[ " + this.plugin.getName() + "] Error downloading file: " + e);
		}

		return false;
	}

	public synchronized boolean check()
	{
		try
		{
			InputStream input = this.filesFeed.openConnection().getInputStream();
			Document document = DocumentBuilderFactory.newInstance().newDocumentBuilder().parse(input);

			Node latestFile = document.getElementsByTagName("item").item(0);
			NodeList children = latestFile.getChildNodes();

			this.version = children.item(1).getTextContent().replaceAll("[a-zA-Z ]", "");
			try
			{
				this.link = children.item(3).getTextContent();
			}
			catch(Exception e)
			{
				this.logger.warning("[ " + this.plugin.getName() + "] Failed to find download page.");
			}
			input.close();

			try
			{
				input = (new URL(this.link)).openConnection().getInputStream();
			}
			catch(Exception e)
			{
				this.logger.warning("[" + this.plugin.getName() + "] Failed to open connection with download page.");
			}

			BufferedReader reader = new BufferedReader(new InputStreamReader(input));
			String line;

			while((line = reader.readLine()) != null)
			{
				if(line.trim().startsWith("<li class=\"user-action user-action-download\">"))
				{
					this.jarLink = line.substring(line.indexOf("href=\"") + 6, line.lastIndexOf("\""));
					break;
				}
			}

			reader.close();
			input.close();

			PluginDescriptionFile pdf = this.plugin.getDescription();
			String currentVersion = pdf.getVersion();

			if(currentVersion.equals(this.version)) return false;

			try
			{
				// Check for only new releases, must have 3 numbers (ex: 1.2.3)
				String[] current = currentVersion.split("\\.");
				String[] latest = this.version.split("\\.");

				// All possible updates that are new
				if(latest.length == 2)
				{
					if(Integer.parseInt(current[0]) < Integer.parseInt(latest[0])) return true;
					else if(Integer.parseInt(current[0]) == Integer.parseInt(latest[0]) && Integer.parseInt(current[1]) < Integer.parseInt(latest[1])) return true;
				}
				else if(latest.length == 3)
				{
					if(Integer.parseInt(current[0]) < Integer.parseInt(latest[0])) return true;
					else if(Integer.parseInt(current[0]) == Integer.parseInt(latest[0]) && Integer.parseInt(current[1]) < Integer.parseInt(latest[1])) return true;
					else if(Integer.parseInt(current[1]) == Integer.parseInt(latest[1]) && Integer.parseInt(current[2]) < Integer.parseInt(latest[2])) return true;
				}
				else if(latest.length == 4)
				{
					if(Integer.parseInt(current[0]) < Integer.parseInt(latest[0])) return true;
					else if(Integer.parseInt(current[0]) == Integer.parseInt(latest[0]) && Integer.parseInt(current[1]) < Integer.parseInt(latest[1])) return true;
					else if(Integer.parseInt(current[1]) == Integer.parseInt(latest[1]) && Integer.parseInt(current[2]) < Integer.parseInt(latest[2])) return true;
					else if(Integer.parseInt(current[2]) == Integer.parseInt(latest[2]) && Integer.parseInt(current[3]) < Integer.parseInt(latest[3])) return true;
				}
			}
			catch(Exception e)
			{
				this.logger.warning("[ " + this.plugin.getName() + "] Could not parse version number.");
			}
		}
		catch(Exception e)
		{
			this.logger.warning("[ " + this.plugin.getName() + "] Failed to read download page.");
		}

		return false;
	}

	public synchronized boolean versionExists()
	{
		try
		{
			InputStream input = this.filesFeed.openConnection().getInputStream();
			Document document = DocumentBuilderFactory.newInstance().newDocumentBuilder().parse(input);
			NodeList filelist = document.getElementsByTagName("item");
			input.close();

			for(int i = 0; i < filelist.getLength(); i++)
			{
				Node file = filelist.item(i);
				NodeList children = file.getChildNodes();
				String fileVersion = children.item(1).getTextContent().replaceAll("[a-zA-Z ]", "");
				PluginDescriptionFile pdf = this.plugin.getDescription();
				String currentVersion = pdf.getVersion();
				if(currentVersion.equals(fileVersion)) return true;
			}
		}
		catch(Exception e)
		{
			this.logger.warning("[ " + this.plugin.getName() + "] Failed to read download page.");
		}

		return false;
	}

	public String getLatestVersion()
	{
		check();
		return this.version;
	}

	public boolean supported()
	{
		return this.supported;
	}

	@EventHandler
	public void onPlayerJoin(PlayerJoinEvent event)
	{
		// Define Variables
		Player player = event.getPlayer();
		boolean auto = this.plugin.getConfig().getBoolean("update.auto");
		boolean notify = this.plugin.getConfig().getBoolean("update.notify");

		// Unsupported Notify
		if(!this.supported && player.isOp() || player.hasPermission(this.permission))
		{
			player.sendMessage(ChatColor.RED + "The version of " + ChatColor.YELLOW + this.plugin.getName() + ChatColor.RED + " you are using is not supported.");
			player.sendMessage(ChatColor.RED + "It has been removed from BukkitDev. This " + ChatColor.ITALIC + "may" + ChatColor.RESET + ChatColor.RED + " be because it");

			if(auto)
			{
				player.sendMessage(ChatColor.RED + "is not safe.");
				player.sendMessage("Please " + ChatColor.YELLOW + "reload the server " + ChatColor.WHITE + "to finish the auto-update.");
			}
			else
			{
				player.sendMessage(ChatColor.RED + "is not safe. It is " + ChatColor.BOLD + "strongly" + ChatColor.RESET + ChatColor.RED + " suggested that you update soon.");
				player.sendMessage("Please update by using " + ChatColor.YELLOW + this.command);
			}
		}

		// Update Notify
		else if(notify && player.isOp() || player.hasPermission(this.permission))
		{
			if(check())
			{
				player.sendMessage(ChatColor.RED + "There is a new stable release for " + this.plugin.getName() + ".");
				if(auto) player.sendMessage("Please " + ChatColor.YELLOW + "reload the server " + ChatColor.WHITE + "to finish an auto-update.");
				else player.sendMessage("Please update soon by using " + ChatColor.YELLOW + this.command);
			}
		}
	}
}
