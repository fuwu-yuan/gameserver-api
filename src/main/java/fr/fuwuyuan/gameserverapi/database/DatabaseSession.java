package fr.fuwuyuan.gameserverapi.database;

import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Properties;

import fr.fuwuyuan.gameserverapi.logs.ResponseHandler;
import fr.fuwuyuan.gameserverapi.utils.ApplicationPropertiesUtils;

/**
 * This is a singleton class design to manipulate the MySQL Database.
 * It allow connection as well as executing INSERT, SELECT, UPDATE and DELETE
 * queries.
 * @author julien-beguier
 * @see {@link DriverManager}
 * @see {@link ApplicationPropertiesUtils}
 */
public class DatabaseSession {

	private final String			SQL_ADDR = "SQL_ADDR";
	private final String			SQL_PORT = "SQL_PORT";
	private final String			SQL_DATABASE = "SQL_DATABASE";
	private final String			SQL_USER = "SQL_USER";
	private final String			SQL_PASSWORD = "SQL_PASSWORD";

	private String					databaseAddress = null;
	private String					databasePort = null;
	private String					databaseDB = null;
	private String					databaseUser = null;
	private String					databasePassword = null;
	private String					databaseConnectionInfos = null;
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
		try {
			Properties properties = ApplicationPropertiesUtils.readPropertiesFile();
			if (properties == null) {
				ResponseHandler.fatal("application.properties file not found! Database connection impossible!", true);
			} else {
				if (properties.isEmpty()) {
					ResponseHandler.fatal(
							"application.properties file is empty: can't find database connection infos", true);
				} else {
					this.databaseAddress = properties.getProperty(SQL_ADDR);
					this.databasePort = properties.getProperty(SQL_PORT);
					this.databaseDB = properties.getProperty(SQL_DATABASE);
					this.databaseUser = properties.getProperty(SQL_USER);
					this.databasePassword = properties.getProperty(SQL_PASSWORD);
					this.databaseConnectionInfos = databaseAddress + ":" + databasePort;

					connect();
				}
			}
		} catch (IOException e) {
			ResponseHandler.fatal("application.properties file cannot be closed: " + e.getMessage(), true);
		}
	}

	/**
	 * This method simply initialize the {@link Connection} object and
	 * connect to the database specified.
	 * @see {@link DriverManager#getConnection}
	 */
	private void connect() {
		if (databaseAddress == null || databasePort == null ||
			databaseUser == null || databasePassword == null) {
			ResponseHandler.fatal("Database connection credentials not set. Abort connect()", true);
			return;
		}
		try {
			Class.forName("com.mysql.cj.jdbc.Driver");
			this.conn = DriverManager.getConnection("jdbc:mysql://"
			+ databaseConnectionInfos + "/" + databaseDB,
				databaseUser, databasePassword);

			ResponseHandler.info("Connected to database on : "
			+ databaseConnectionInfos + "/" + databaseDB, true);
		} catch (SQLException sqlException) {
			ResponseHandler.fatal("SQLException: " + sqlException.getCause(), true);
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
	public boolean isConnected() {
		try {
			return (this.conn != null && this.conn.isValid(3000));
		} catch (SQLException sqlException) {
			ResponseHandler.fatal("SQLException: " + sqlException.getMessage(), true);
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
			if (this.conn != null)
				this.conn.close();
			this.conn = null;

			connect();
		} catch (SQLException sqlException) {
			ResponseHandler.fatal("SQLException: " + sqlException.getCause(), true);
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
