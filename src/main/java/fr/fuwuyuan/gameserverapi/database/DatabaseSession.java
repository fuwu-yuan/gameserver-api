package fr.fuwuyuan.gameserverapi.database;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

import fr.fuwuyuan.gameserverapi.logs.ResponseHandler;

/**
 * This is a singleton class design to manipulate the MySQL Database.
 * It allow connection as well as executing INSERT, SELECT, UPDATE and DELETE
 * queries.
 * @author julien-beguier
 * @see {@link DriverManager}
 */
public class DatabaseSession {

	// TODO configuration file
	private final String			databaseAddress = "MYSQL_ADDR_TO_SET";
	private final String			databasePort = "MYSQL_PORT_TO_SET";
	private final String			statapiSqlDB = "MYSQL_DATABASE_TO_SET";
	private final String			statapiSqlUser = "MYSQL_USER_TO_SET";
	private final String			statapiSqlPassword = "MYSQL_USER_PASSWORD_TO_SET";

	private final String			databaseConnectionInfos = databaseAddress + ":" + databasePort;
	private Connection				conn = null;

	private static DatabaseSession	instance = null;

	public static DatabaseSession getInstance() {
		if (instance == null) {
			instance = new DatabaseSession();
		} else if (instance != null && !instance.isConnected()) {
			instance.reconnect();
		}
		return instance;
	}

	private DatabaseSession() {
		connect();
	}

	/**
	 * This method simply initialize the {@link Connection} object and
	 * connect to the database specified.
	 * @see {@link DriverManager#getConnection}
	 */
	private void connect() {
		try {
			Class.forName("com.mysql.cj.jdbc.Driver");
			this.conn = DriverManager.getConnection(
					"jdbc:mysql://" + databaseConnectionInfos + "/" + statapiSqlDB,
					statapiSqlUser, statapiSqlPassword);

			ResponseHandler.info("Connected to database on : " + databaseConnectionInfos);
		} catch (SQLException sqlException) {
			ResponseHandler.error("SQLException: " + sqlException.getMessage());
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
		}
	}

	/**
	 * This method checks and return whether or not the {@link Connection} object
	 * is in fact connected to the database.
	 * @return {@code true} if this class is connected to the database,
	 * {@code false} otherwise
	 * @see {@link Connection#isValid}
	 */
	private boolean isConnected() {
		try {
			return (this.conn != null && this.conn.isValid(3000));
		} catch (SQLException sqlException) {
			ResponseHandler.error("SQLException: " + sqlException.getMessage());
			return false;
		}
	}

	/**
	 * This method is used to reconnect the {@link Connection} object when it is
	 * no longer connected to the database.
	 * @see {@link Connection#close}
	 * @see {@link DatabaseSession#connect}
	 */
	private void reconnect() {
		try {
			this.conn.close();
			this.conn = null;

			connect();
		} catch (SQLException sqlException) {
			ResponseHandler.error("SQLException: " + sqlException.getMessage());
		}
	}

	/**
	 * This method executes the given SQL query and returns the
	 * {@link ResultSet}.</br>
	 * Used primarily for SELECT.
	 * @param sql as a String
	 * @return a {@link ResultSet} if no error occurs, throws an
	 * {@link SQLException} otherwise
	 * @throws SQLException
	 * @see {@link ResultSet}
	 */
	public ResultSet executeQuery(String sql) throws SQLException {
		try {
			Statement statement = this.conn.createStatement();
			return statement.executeQuery(sql);
		} catch (SQLException e) {
			throw e;
		}
	}

	/**
	 * This method prepares a {@link Statement} by executing the given SQL query
	 * and returns the {@link ResultSet}.</br>
	 * @param sql as a String
	 * @return a {@link ResultSet} if no error occurs, throws an
	 * {@link SQLException} otherwise
	 * @throws SQLException
	 * @see {@link Statement}
	 * @see {@link ResultSet}
	 */
	public ResultSet prepareInserting(String sqlForInsert) throws SQLException {
		try {
			Statement statement = this.conn.createStatement(ResultSet.TYPE_SCROLL_SENSITIVE, ResultSet.CONCUR_UPDATABLE);
			return statement.executeQuery(sqlForInsert);
		} catch (SQLException e) {
			throw e;
		}
	}

	/**
	 * This method executes the given SQL query and returns an int value
	 * corresponding to the number of row affected.<br>
	 * Used primarily for UPDATE & DELETE.
	 * @param sql as a String
	 * @return an int value if no error occurs, throws an
	 * {@link SQLException} otherwise
	 * @throws SQLException
	 * @see {@link ResultSet}
	 */
	public int executeUpdate(String sql) throws SQLException {
		try {
			Statement statement = this.conn.createStatement();
			int result = statement.executeUpdate(sql);

			statement.close();
			return result;
		} catch (SQLException e) {
			throw e;
		}
	}
}
