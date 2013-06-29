/*
 * Copyright � 2012 Mcft Media, Jon la Cour
 * 
 * Permission is hereby granted, free of charge, to any person obtaining a copy of this software and associated
 * documentation files (the "Software"), to deal in the Software without restriction, including without limitation
 * the rights to use, copy, modify, merge, publish, distribute, sublicense, and/or sell copies of the Software,
 * and to permit persons to whom the Software is furnished to do so, subject to the following conditions:
 * 
 * The above copyright notice and this permission notice shall be included in all copies or substantial portions of
 * the Software.
 * 
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO
 * THE WARRANTIES OF MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER LIABILITY, WHETHER IN AN ACTION OF
 * CONTRACT, TORT OR OTHERWISE, ARISING FROM, OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS
 * IN THE SOFTWARE.
 */

package net.alexben.Slayer.Core.Handlers.Connectors;

import java.io.File;
import java.sql.*;
import java.util.logging.Logger;

/**
 * SQLite Inherited subclass for reading and writing to and from an SQLite file.
 * 
 * @author PatPeter, Jon la Cour
 */
public class SQLite extends DatabaseHandler
{
	public String location;
	public String name;
	private final File sqlFile;

	/**
	 * @param log Logger
	 * @param sqlname Name of database
	 * @param sqllocation Location of database
	 */
	public SQLite(final Logger log, final String sqlname, final String sqllocation)
	{
		super(log, " SQLite: ");
		this.name = sqlname;
		this.location = sqllocation;
		File folder = new File(this.location);
		if(this.name.contains("/") || this.name.contains("\\") || this.name.endsWith(".db"))
		{
			writeError("The database name cannot contain: /, \\, or .db", true);
		}
		if(!folder.exists())
		{
			folder.mkdir();
		}

		sqlFile = new File(folder.getAbsolutePath() + File.separator + name + ".db");
	}

	@Override
	public final boolean initialize()
	{
		try
		{
			Class.forName("org.sqlite.JDBC");
			return true;
		}
		catch(ClassNotFoundException e)
		{
			writeError("Class not found in initialize(): " + e, true);
			return false;
		}
	}

	@Override
	public final Connection open()
	{
		if(initialize())
		{
			try
			{
				connection = DriverManager.getConnection("jdbc:sqlite:" + sqlFile.getAbsolutePath());
				return connection;
			}
			catch(SQLException e)
			{
				writeError("SQL exception in open(): " + e, true);
			}
		}
		return null;
	}

	@Override
	public final void close()
	{
		if(connection != null)
		{
			try
			{
				connection.close();
			}
			catch(SQLException ex)
			{
				writeError("SQL exception in close(): " + ex, true);
			}
		}
	}

	@Override
	public final Connection getConnection()
	{
		if(connection == null)
		{
			return open();
		}
		return connection;
	}

	@Override
	public final boolean checkConnection()
	{
		return connection != null;
	}

	@Override
	public final ResultSet query(final String query)
	{
		Statement statement = null;
		ResultSet result = null;

		try
		{
			connection = this.open();
			statement = connection.createStatement();
			result = statement.executeQuery("SELECT date('now')");

			switch(this.getStatement(query))
			{
				case SELECT:
					result = statement.executeQuery(query);
					break;
				case INSERT:
				case UPDATE:
				case DELETE:
				case CREATE:
				case ALTER:
				case DROP:
				case TRUNCATE:
				case RENAME:
				case DO:
				case REPLACE:
				case LOAD:
				case HANDLER:
				case CALL:
					lastUpdate = statement.executeUpdate(query);
					break;
				default:
					result = statement.executeQuery(query);
			}
			return result;
		}
		catch(SQLException e)
		{
			if(e.getMessage().toLowerCase().contains("locking") || e.getMessage().toLowerCase().contains("locked"))
			{
				return retry(query);
			}
			else
			{
				writeError("SQL exception in query(): " + e.getMessage(), false);
			}
		}
		return null;
	}

	@Override
	public final PreparedStatement prepare(final String query)
	{
		try
		{
			connection = open();
			return connection.prepareStatement(query);
		}
		catch(SQLException e)
		{
			if(!e.toString().contains("not return ResultSet"))
			{
				writeError("SQL exception in prepare(): " + e.getMessage(), false);
			}
		}
		return null;
	}

	@Override
	public final boolean createTable(final String query)
	{
		Statement statement = null;
		try
		{
			if(query == null || query.equals(""))
			{
				writeError("Parameter 'query' empty or null in createTable().", true);
				return false;
			}

			statement = connection.createStatement();
			statement.execute(query);
			return true;
		}
		catch(SQLException ex)
		{
			writeError(ex.getMessage(), true);
			return false;
		}
	}

	@Override
	public final boolean checkTable(final String table)
	{
		DatabaseMetaData dbm = null;
		try
		{
			dbm = this.open().getMetaData();
			ResultSet tables = dbm.getTables(null, null, table, null);
			return tables.next();
		}
		catch(SQLException e)
		{
			writeError("Failed to check if table \"" + table + "\" exists: " + e.getMessage(), true);
			return false;
		}
	}

	@Override
	public final boolean wipeTable(final String table)
	{
		try
		{
			if(!this.checkTable(table))
			{
				writeError("Table \"" + table + "\" in wipeTable() does not exist.", true);
				return false;
			}
			Statement statement = connection.createStatement();
			ResultSet result = statement.executeQuery("DELETE FROM " + table + ";");
			result.close();
			return true;
		}
		catch(SQLException ex)
		{
			if(!(ex.getMessage().toLowerCase().contains("locking") || ex.getMessage().toLowerCase().contains("locked")) && !ex.toString().contains("not return ResultSet"))
			{
				writeError("Error at SQL Wipe Table Query: " + ex, false);
			}
			return false;
		}
	}

	/**
	 * Retries a statement and returns a ResultSet.
	 * 
	 * @param query
	 *        The SQL query to retry.
	 * 
	 * @return The SQL query result.
	 */
	public final ResultSet retry(final String query)
	{
		Statement statement = null;
		ResultSet result = null;
		try
		{
			statement = connection.createStatement();
			result = statement.executeQuery(query);
			return result;
		}
		catch(SQLException ex)
		{
			if(ex.getMessage().toLowerCase().contains("locking") || ex.getMessage().toLowerCase().contains("locked"))
			{
				writeError("Please close your previous ResultSet to run the query: \n\t" + query, false);
			}
			else
			{
				writeError("SQL exception in retry(): " + ex.getMessage(), false);
			}
		}
		return null;
	}
}
