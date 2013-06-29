package net.alexben.Slayer.Core.Handlers;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import net.alexben.Slayer.Core.Handlers.Connectors.MySQL;
import net.alexben.Slayer.Core.Handlers.Connectors.SQLite;
import net.alexben.Slayer.Core.Slayer;
import net.alexben.Slayer.Utilities.ConfigUtil;
import net.alexben.Slayer.Utilities.MiscUtil;

import org.bukkit.OfflinePlayer;

public class Database
{
	// Define the database variables
	private static MySQL mysql;
	private static SQLite sqlite;

	/**
	 * Initializes the connection with the database.
	 */
	public static void initializeConnection()
	{
		// Handle SQLite
		if(ConfigUtil.getSettingBoolean("data.sqlite.use"))
		{
			// Create the object
			sqlite = new SQLite(Slayer.plugin.getLogger(), ConfigUtil.getSettingString("data.sqlite.database"), Slayer.plugin.getDataFolder().getPath());

			// Open the connection and report back
			sqlite.open();

			if(sqlite.checkConnection())
			{
				MiscUtil.logSqlite("info", "Connection established.");
			}
		}
		// Handle MySQL
		else if(ConfigUtil.getSettingBoolean("data.mysql.use"))
		{
			// Create the object
			mysql = new MySQL(Slayer.plugin.getLogger(), ConfigUtil.getSettingString("data.mysql.host"), ConfigUtil.getSettingString("data.mysql.port"), ConfigUtil.getSettingString("data.mysql.database"), ConfigUtil.getSettingString("data.mysql.user"), ConfigUtil.getSettingString("data.mysql.pass"));

			// Open the connection and report back
			mysql.open();

			if(mysql.checkConnection())
			{
				MiscUtil.logMysql("info", "Connection established.");
			}
		}

		// Create the tables if need be
		createTables();
	}

	/**
	 * Creates the tables needed for SQL storage.
	 */
	public static void createTables()
	{
		// Define table queries
		String players = "CREATE TABLE players (unique_id INT AUTO_INCREMENT, PRIMARY KEY (unique_id), player VARCHAR(24), points INT, join_date VARCHAR(128));";
		String assignments = "CREATE TABLE assignments (unique_id INT AUTO_INCREMENT, PRIMARY KEY (unique_id), player_id INT, task BLOB, progress INT, expiration BIGINT, display BOOLEAN, active BOOLEAN, failed BOOLEAN, expired BOOLEAN, forfeited BOOLEAN);";
		String objects = "CREATE TABLE objects (unique_id INT AUTO_INCREMENT, PRIMARY KEY (unique_id), class_name VARCHAR(24), object BLOB);";

		// Handle SQLite
		if(sqlite != null && sqlite.checkConnection())
		{
			// Check if the tables exist and if not create them
			if(!sqlite.checkTable("players"))
			{
				MiscUtil.logSqlite("info", "Creating \"players\" table...");
				sqlite.createTable(players);
				MiscUtil.logSqlite("info", "\"players\" table created!");
			}

			if(!sqlite.checkTable("assignments"))
			{
				MiscUtil.logSqlite("info", "Creating \"assignments\" table...");
				sqlite.createTable(assignments);
				MiscUtil.logSqlite("info", "\"assignments\" table created!");
			}

			if(!sqlite.checkTable("objects"))
			{
				MiscUtil.logSqlite("info", "Creating \"objects\" table...");
				sqlite.createTable(objects);
				MiscUtil.logSqlite("info", "\"assignments\" table created!");
			}
		}
		// Handle MySQL
		else if(mysql != null && mysql.checkConnection())
		{
			// Check if the tables exist and if not create them
			if(!mysql.checkTable("players"))
			{
				MiscUtil.logMysql("info", "Creating \"players\" table...");
				mysql.createTable(players);
				MiscUtil.logMysql("info", "\"players\" table created!");
			}

			if(!mysql.checkTable("assignments"))
			{
				MiscUtil.logMysql("info", "Creating \"assignments\" table...");
				mysql.createTable(assignments);
				MiscUtil.logMysql("info", "\"assignments\" table created!");
			}

			if(!mysql.checkTable("objects"))
			{
				MiscUtil.logMysql("info", "Creating \"objects\" table...");
				mysql.createTable(objects);
				MiscUtil.logMysql("info", "\"assignments\" table created!");
			}
		}
	}

	/**
	 * Returns a PreparedStatement for SQLite or MySQL, depending on which is enabled.
	 * 
	 * @return PreparedStatement
	 */
	public static PreparedStatement toPreparedStatement(String query)
	{
		// Define variable
		PreparedStatement ps = null;

		// Handle SQLite
		if(sqlite != null && sqlite.checkConnection())
		{
			ps = sqlite.prepare(query);
		}
		// Handle MySQL
		else if(mysql != null && mysql.checkConnection())
		{
			ps = mysql.prepare(query);
		}

		return ps;
	}

	/**
	 * Returns the SQL-generated id for the <code>player</code>.
	 * 
	 * @param player the player whose id to return.
	 */
	public static int getPlayerId(OfflinePlayer player) throws SQLException
	{
		// Define variables
		String query = "SELECT * FROM players WHERE player = ?";
		PreparedStatement ps = toPreparedStatement(query);

		// Execute and handle the result
		ResultSet result = ps.executeQuery();

		if(result.next())
		{
			while(result.next())
			{
				return result.getInt("unique_id");
			}
		}

		// Return -1 and log if a player can't be found (shouldn't happen)
		MiscUtil.log("severe", "A player wasn't saved into the database. Reload the server.");
		return -1;
	}
}
