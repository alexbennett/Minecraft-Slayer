/*
 * Copyright (c) 2013
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

import java.io.*;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;

import net.alexben.Slayer.Libraries.BukkitDevDownload;

import org.bukkit.Bukkit;

public class SUpdateUtil
{
	private static BukkitDevDownload bukkitDev = new BukkitDevDownload("http://dev.bukkit.org/server-mods/slayer/files.rss");

	public static boolean check()
	{
		return bukkitDev.updateNeeded();
	}

	public static String getLatestVersion()
	{
		check();
		return bukkitDev.getVersion();
	}

	public static boolean execute()
	{
		try
		{
			// Define variables
			byte[] buffer = new byte[1024];
			int read = 0;
			int bytesTransferred = 0;
			String downloadLink = bukkitDev.getJarLink();

			SMiscUtil.log("info", "Attempting to download latest version...");

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
			File pluginUpdate = new File("plugins" + File.separator + Bukkit.getUpdateFolder() + File.separator + "Slayer.jar");
			SMiscUtil.log("info", "File will been written to: " + pluginUpdate.getCanonicalPath());

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
						SMiscUtil.log("info", "Download progress: " + percentTransferred + "%");
					}
				}
			}

			is.close();
			os.flush();
			os.close();

			// Download complete!
			SMiscUtil.log("info", "Download complete!");
			SMiscUtil.log("info", "Update will complete on next server reload.");
			return true;
		}
		catch(MalformedURLException ex)
		{
			SMiscUtil.log("severe", "Error accessing URL: " + ex);
		}
		catch(FileNotFoundException ex)
		{
			SMiscUtil.log("severe", "Error accessing URL: " + ex);
		}
		catch(IOException ex)
		{
			SMiscUtil.log("severe", "Error downloading file: " + ex);
		}
		return false;
	}
}
