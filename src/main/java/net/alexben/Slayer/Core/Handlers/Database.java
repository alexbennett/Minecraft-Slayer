package net.alexben.Slayer.Core.Handlers;

import net.alexben.Slayer.Core.Handlers.Connectors.MySQL;
import net.alexben.Slayer.Core.Handlers.Connectors.SQLite;
import net.alexben.Slayer.Core.Slayer;
import net.alexben.Slayer.Utilities.ConfigUtil;
import net.alexben.Slayer.Utilities.MiscUtil;

public class Database
{
	// Define the database variables
	private MySQL mysql;
	private SQLite sqlite;

	/**
	 * Initializes the connection with the database.
	 */
	public void initializeConnection()
	{
		if(ConfigUtil.getSettingBoolean("data.mysql.use"))
		{
			// Handle MySQL
			mysql = new MySQL(Slayer.plugin.getLogger(), ConfigUtil.getSettingString("data.mysql.host"), ConfigUtil.getSettingString("data.mysql.port"), ConfigUtil.getSettingString("data.mysql.database"), ConfigUtil.getSettingString("data.mysql.user"), ConfigUtil.getSettingString("data.mysql.pass"));

			// Open the connection and report back
			mysql.open();

			if(mysql.checkConnection())
			{
				MiscUtil.log("info", "MySQL connection established.");
			}
		}

		if(ConfigUtil.getSettingBoolean("data.sqlite.use"))
		{
			// Handle SQLite
			sqlite = new SQLite(Slayer.plugin.getLogger(), ConfigUtil.getSettingString("data.sqlite.database"), Slayer.plugin.getDataFolder().getPath());

			// Open the connection and report back
			sqlite.open();

			if(sqlite.checkConnection())
			{
				MiscUtil.log("info", "SQLite connection established.");
			}
		}
	}

	/**
	 * Creates the tables needed for SQL storage.
	 */

}
