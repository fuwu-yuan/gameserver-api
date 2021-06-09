package fr.fuwuyuan.gameserverapi.services;

import java.io.StringReader;
import java.sql.ResultSet;
import java.sql.SQLException;

import javax.json.Json;
import javax.json.JsonReader;

import fr.fuwuyuan.gameserverapi.database.DatabaseSession;
import fr.fuwuyuan.gameserverapi.logs.ResponseHandler;
import fr.fuwuyuan.gameserverapi.services.PortServiceInterface.PortError;
import fr.fuwuyuan.gameserverapi.utils.JsonUtils;

/**
 * Extends {@link AbstractPortService}
 * <p>
 * This is the port service class which implements the methods to
 * manipulate used & available ports. It uses the {@link JsonUtils}
 * class to do the json manipulation and {@link DatabaseSession} to
 * query the database.
 * </p>
 * @author julien-beguier
 * @see {@link DatabaseSession#executeQuery}
 * @see {@link DatabaseSession#executeUpdate}
 * @see {@link JsonUtils#addIntToJsonArray}
 * @see {@link JsonUtils#removeIntFromJsonArray}
 */
public class PortService extends AbstractPortService {

	/**
	 * {@inheritDoc}
	 */
	@Override
	public int getAvailablePort(final String ip) {

		if (null == this.portsUsed || null == this.portsAvailable) {
			int ret = fetchPortsArrays(ip);
			if (ret != RET_OK)
				return ret;
		}

		int returnCode = 0;

		// Check to is there is available ports to use
		if (this.portsAvailable.isEmpty())
			returnCode = PortError.NO_AVAILABLE_PORT_LEFT_ON_GIVEN_IP.getErrorCode();

		// Return the first available port
		returnCode = this.portsAvailable.getInt(0);
		resetPortsArrays();
		return returnCode;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public int addNewPortToUsedPorts(final String ip, final int port) {

		if (null == this.portsUsed || null == this.portsAvailable) {
			int ret = fetchPortsArrays(ip);
			if (ret != RET_OK)
				return ret;
			return addNewPortToUsedPorts(ip, port);
		} else {

			// Add the port to the portsUsed array to save it to DB
			this.portsUsed = JsonUtils.addIntToJsonArray(this.portsUsed, port);
			// Remove the port from the portsAvailable array to save it to DB
			this.portsAvailable = JsonUtils.removeIntFromJsonArray(this.portsAvailable, port);

			return updatePortsArrays(ip);
		}
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public int freeUsedPort(final String ip, final int port) {

		if (null == this.portsUsed || null == this.portsAvailable) {
			int ret = fetchPortsArrays(ip);
			if (ret != RET_OK)
				return ret;
			return freeUsedPort(ip, port);
		} else {

			// Remove the port from the portsUsed array to save it to DB
			this.portsUsed = JsonUtils.removeIntFromJsonArray(this.portsUsed, port);
			// Add the freed port to the portsAvailable array to save it to DB
			this.portsAvailable = JsonUtils.addIntToJsonArray(this.portsAvailable, port);

			return updatePortsArrays(ip);
		}
	}

	// ##########################################################################

	/**
	 * This method fetch a record of the {@code 'ports'} table of the
	 * {@code 'gameserver'} database using the given {@code ip} and save it
	 * as a member of the
	 * {@link AbstractPortService} class for manipulation.
	 * @param ip as a String
	 * @return {@code RET_OK} if successful, a {@link PortError} otherwise
	 * @see {@link AbstractPortService#portsUsed}
	 * @see {@link AbstractPortService#portsAvailable}
	 * @see {@link ServiceInterface#RET_OK}
	 * @see {@link PortError}
	 */
	private int fetchPortsArrays(final String ip) {
		// SQL - Get the two arrays of ports to check & save them to later build value for the update
		//       -> This prevent to redo the same select later
		String selectSql = "SELECT `ports`.`used`, `ports`.`available` FROM `ports` "
						+ "WHERE `ports`.`public_ip` = '" + ip + "'";

		try {
			ResultSet resultSet = DatabaseSession.getInstance().executeQuery(selectSql);

			// Check to see if the server machine is registered
			if (!resultSet.next())
				return PortError.NO_PORT_CORRESPONDING_TO_GIVEN_IP.getErrorCode();

			// Read both arrays to use them later - prevent the same SQL request to be executed
			JsonReader jsonReader = Json.createReader(new StringReader(resultSet.getString(1)));
			this.portsUsed = jsonReader.readArray();
			jsonReader.close();

			jsonReader = Json.createReader(new StringReader(resultSet.getString(2)));
			this.portsAvailable = jsonReader.readArray();
			jsonReader.close();

			resultSet.getStatement().close();
			return RET_OK; // Fetch has succeeded
		} catch (SQLException e) {
			String errorMessage = "ERROR : #" + e.getErrorCode() + " " + e.getMessage();
			ResponseHandler.error(errorMessage); // Logger ERROR
			return PortError.SQL_ERROR_FETCH_LOG_AND_DO_NOTHING.getErrorCode();
		}
	}

	/**
	 * This method update a record of the {@code 'ports'} table of the
	 * {@code 'gameserver'} database using the given {@code ip}.
	 * @param ip as a String
	 * @return {@code RET_OK} if successful, a {@link PortError} otherwise
	 * @see {@link AbstractPortService#resetPortsArrays}
	 * @see {@link ServiceInterface#RET_OK}
	 * @see {@link PortError}
	 */
	private int updatePortsArrays(final String ip) {
		int returnCode = 0;
		String updateSql = "UPDATE `ports` SET "
						+ "`ports`.`used` = '" + this.portsUsed.toString() + "', "
						+ "`ports`.`available` = '" + this.portsAvailable.toString() + "' "
						+ "WHERE `ports`.`public_ip` = '" + ip + "'";

		try {
			int requestResult = DatabaseSession.getInstance().executeUpdate(updateSql);

			if (requestResult == 0) // Check to see if the record exists
				returnCode = PortError.NO_PORT_CORRESPONDING_TO_GIVEN_IP.getErrorCode();

			returnCode = RET_OK; // Update has succeeded
		} catch (SQLException e) {
			String errorMessage = "ERROR : #" + e.getErrorCode() + " " + e.getMessage();
			ResponseHandler.error(errorMessage); // Logger ERROR
			returnCode = PortError.SQL_ERROR_UPDATE_LOG_AND_DO_NOTHING.getErrorCode();
		} finally {
			resetPortsArrays();
		}
		return returnCode;
	}
}
