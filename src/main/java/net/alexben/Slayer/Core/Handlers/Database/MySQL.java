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

package net.alexben.Slayer.Core.Handlers.Database;

import java.sql.*;
import java.util.logging.Logger;

/**
 * MySQL Inherited subclass for making a connection to a MySQL server.
 * 
 * @author PatPeter, Jon la Cour
 */
public class MySQL extends DatabaseHandler
{
	private String hostname = "localhost";
	private String portnmbr = "3306";
	private String username = "minecraft";
	private String password = "";
	private String database = "minecraft";

	/**
	 * @param sqlhostname MySQL server hostname
	 * @param sqlport MySQL server port
	 * @param sqldatabase MySQL database
	 * @param sqluser MySQL user
	 * @param sqlpassword MySQL password
	 */
	public MySQL(final Logger log, final String sqlhostname, final String sqlport, final String sqldatabase, final String sqluser, final String sqlpassword)
	{
		super(log, " MySQL: ");
		hostname = sqlhostname;
		portnmbr = sqlport;
		database = sqldatabase;
		username = sqluser;
		password = sqlpassword;
	}

	@Override
	public final boolean initialize()
	{
		try
		{
			Class.forName("com.mysql.jdbc.Driver");
			return true;
		}
		catch(ClassNotFoundException e)
		{
			writeError("Class not found in initialize(): " + e.getMessage() + ".", true);
			return false;
		}
	}

	@Override
	public final Connection open()
	{
		if(initialize())
		{
			String url = "";
			try
			{
				url = "jdbc:mysql://" + hostname + ":" + portnmbr + "/" + database;
				connection = DriverManager.getConnection(url, username, password);
				return connection;
			}
			catch(SQLException e)
			{
				writeError(url, true);
				writeError("SQL exception in open(): " + e.getMessage() + ".", true);
			}
		}
		return connection;
	}

	@Override
	public final void close()
	{
		try
		{
			if(connection != null)
			{
				connection.close();
			}
		}
		catch(Exception e)
		{
			writeError("Exception in close(): " + e.getMessage(), true);
		}
	}

	@Override
	public final Connection getConnection()
	{
		return this.connection;
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
			statement = this.connection.createStatement();
			result = statement.executeQuery("SELECT CURTIME()");

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
			this.writeError("SQL exception in query(): " + e.getMessage(), false);
		}
		return result;
	}

	@Override
	public final PreparedStatement prepare(final String query)
	{
		PreparedStatement ps = null;
		try
		{
			ps = connection.prepareStatement(query);
			return ps;
		}
		catch(SQLException e)
		{
			if(!e.toString().contains("not return ResultSet"))
			{
				writeError("SQL exception in prepare(): " + e.getMessage(), false);
			}
		}
		return ps;
	}

	@Override
	public final boolean createTable(final String query)
	{
		Statement statement = null;
		try
		{
			if(query == null || query.equals(""))
			{
				writeError("Parameter 'query' empty or null in createTable(): " + query, true);
				return false;
			}

			statement = connection.createStatement();
			statement.execute(query);
			return true;
		}
		catch(SQLException e)
		{
			writeError(e.getMessage(), true);
			return false;
		}
		catch(Exception e)
		{
			writeError(e.getMessage(), true);
			return false;
		}
	}

	@Override
	public final boolean checkTable(final String table)
	{
		try
		{
			Statement statement = connection.createStatement();
			ResultSet result = statement.executeQuery("SELECT * FROM " + table);

			return result != null;
		}
		catch(SQLException e)
		{
			if(e.getMessage().contains("exist"))
			{
				return false;
			}
			else
			{
				writeError("SQL exception in checkTable(): " + e.getMessage(), false);
			}
		}

		return query("SELECT * FROM " + table) == null;
	}

	@Override
	public final boolean wipeTable(final String table)
	{
		Statement statement = null;
		String query = null;
		try
		{
			if(!checkTable(table))
			{
				writeError("Table \"" + table + "\" in wipeTable() does not exist.", true);
				return false;
			}
			statement = this.connection.createStatement();
			query = "DELETE FROM " + table + ";";
			statement.executeUpdate(query);

			return true;
		}
		catch(SQLException e)
		{
			if(!e.toString().contains("not return ResultSet"))
			{
				return false;
			}
		}
		return false;
	}
}
