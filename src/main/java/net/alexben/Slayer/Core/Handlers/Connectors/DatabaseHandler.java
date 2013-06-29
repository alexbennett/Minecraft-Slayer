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

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.logging.Logger;

/**
 * Connectors Handler Abstract superclass for all subclass database files.
 * 
 * @author PatPeter, Jon la Cour
 */
public abstract class DatabaseHandler
{
	protected Logger log;
	protected final String databaseprefix;
	protected boolean connected;
	protected Connection connection;

	/**
	 * MySQL statements.
	 */
	protected enum Statements
	{
		SELECT, INSERT, UPDATE, DELETE, DO, REPLACE, LOAD, HANDLER, CALL, CREATE, ALTER, DROP, TRUNCATE, RENAME,

		// MySQL-specific
		START, COMMIT, ROLLBACK, SAVEPOINT, LOCK, UNLOCK, PREPARE, EXECUTE, DEALLOCATE, SET, SHOW, DESCRIBE, EXPLAIN, HELP, USE,

		// SQLite-specific
		ANALYZE, ATTACH, BEGIN, DETACH, END, INDEXED, ON, PRAGMA, REINDEX, RELEASE, VACUUM
	}

	public int lastUpdate;

	/**
	 * Database Handler.
	 * 
	 * @param dblog Logger.
	 * @param dbdp Database prefix.
	 */
	public DatabaseHandler(final Logger dblog, final String dbdp)
	{
		this.log = dblog;
		this.databaseprefix = dbdp;
		this.connected = false;
		this.connection = null;
	}

	/**
	 * Writes information to the console.
	 * 
	 * @param toWrite The String of content to write to the console.
	 */
	protected final void writeInfo(final String toWrite)
	{
		if(toWrite != null)
		{
			this.log.info(this.databaseprefix + toWrite);
		}
	}

	/**
	 * Writes either errors or warnings to the console.
	 * 
	 * @param toWrite
	 *        The String written to the console.
	 * @param severe
	 *        Whether console output should appear as an error or warning.
	 */
	protected final void writeError(final String toWrite, final boolean severe)
	{
		if(toWrite != null)
		{
			if(severe)
			{
				this.log.severe(this.databaseprefix + toWrite);
			}
			else
			{
				this.log.warning(this.databaseprefix + toWrite);
			}
		}
	}

	/**
	 * Used to check whether the class for the SQL engine is installed.
	 * 
	 * @return The success of the method.
	 */
	public abstract boolean initialize();

	/**
	 * Opens a connection with the database.
	 * 
	 * @return The success of the method.
	 */
	public abstract Connection open();

	/**
	 * Closes a connection with the database.
	 */
	public abstract void close();

	/**
	 * Gets the connection variable.
	 * 
	 * @return The Connection variable.
	 */
	public abstract Connection getConnection();

	/**
	 * Checks the connection between Java and the database engine.
	 * 
	 * @return The status of the connection, true for up, false for down.
	 */
	public abstract boolean checkConnection();

	/**
	 * Sends a query to the SQL database.
	 * 
	 * @param query
	 *        The SQL query to send to the database.
	 * @return The table of results from the query.
	 */
	public abstract ResultSet query(String query);

	/**
	 * Prepares to send a query to the database.
	 * 
	 * @param query
	 *        The SQL query to prepare to send to the database.
	 * @return The prepared statement.
	 */
	public abstract PreparedStatement prepare(String query);

	/**
	 * Statement type checker.
	 * 
	 * @param query
	 *        MySQL query.
	 * @return Statements
	 */
	protected final Statements getStatement(final String query)
	{
		String trimmedQuery = query.trim();
		if(trimmedQuery.substring(0, 6).equalsIgnoreCase("SELECT"))
		{
			return Statements.SELECT;
		}
		else if(trimmedQuery.substring(0, 6).equalsIgnoreCase("INSERT"))
		{
			return Statements.INSERT;
		}
		else if(trimmedQuery.substring(0, 6).equalsIgnoreCase("UPDATE"))
		{
			return Statements.UPDATE;
		}
		else if(trimmedQuery.substring(0, 6).equalsIgnoreCase("DELETE"))
		{
			return Statements.DELETE;
		}
		else if(trimmedQuery.substring(0, 6).equalsIgnoreCase("CREATE"))
		{
			return Statements.CREATE;
		}
		else if(trimmedQuery.substring(0, 5).equalsIgnoreCase("ALTER"))
		{
			return Statements.ALTER;
		}
		else if(trimmedQuery.substring(0, 4).equalsIgnoreCase("DROP"))
		{
			return Statements.DROP;
		}
		else if(trimmedQuery.substring(0, 8).equalsIgnoreCase("TRUNCATE"))
		{
			return Statements.TRUNCATE;
		}
		else if(trimmedQuery.substring(0, 6).equalsIgnoreCase("RENAME"))
		{
			return Statements.RENAME;
		}
		else if(trimmedQuery.substring(0, 2).equalsIgnoreCase("DO"))
		{
			return Statements.DO;
		}
		else if(trimmedQuery.substring(0, 7).equalsIgnoreCase("REPLACE"))
		{
			return Statements.REPLACE;
		}
		else if(trimmedQuery.substring(0, 4).equalsIgnoreCase("LOAD"))
		{
			return Statements.LOAD;
		}
		else if(trimmedQuery.substring(0, 7).equalsIgnoreCase("HANDLER"))
		{
			return Statements.HANDLER;
		}
		else if(trimmedQuery.substring(0, 4).equalsIgnoreCase("CALL"))
		{
			return Statements.CALL;
		}
		else
		{
			return Statements.SELECT;
		}
	}

	/**
	 * Creates a table in the database based on a specified query.
	 * 
	 * @param query
	 *        The SQL query for creating a table.
	 * @return the success of the method.
	 */
	public abstract boolean createTable(String query);

	/**
	 * Checks a table in a database based on the table's name.
	 * 
	 * @param table
	 *        Name of the table to check.
	 * @return Success of the method.
	 */
	public abstract boolean checkTable(String table);

	/**
	 * Wipes a table given its name.
	 * 
	 * @param table
	 *        Name of the table to wipe.
	 * @return Success of the method.
	 */
	public abstract boolean wipeTable(String table);
}
