package fr.fuwuyuan.gameserverapi.services;

import java.sql.ResultSet;
import java.sql.SQLException;

import fr.fuwuyuan.gameserverapi.database.DatabaseSession;
import fr.fuwuyuan.gameserverapi.database.dto.GameServerDTO;
import fr.fuwuyuan.gameserverapi.logs.ResponseHandler;

/**
 * Extends {@link AbstractServerIdService}
 * <p>
 * This is the server id service class which implements a method to
 * determine the next available server id. It uses {@link DatabaseSession}
 * to query the database.
 * </p>
 * @author julien-beguier
 * @see {@link DatabaseSession#executeQuery}
 */
public class ServerIdService extends AbstractServerIdService {

	/**
	 * {@inheritDoc}
	 */
	@Override
	public String getNextServerId() {
		// SQL - Select the maximum value for the server_id field
		String selectSql = "SELECT MAX(`servers`.`server_id`) as `server_id` FROM `servers`";

		try {
			ResultSet resultSet = DatabaseSession.getInstance().executeQuery(selectSql);

			String serverId = null;
			
			// Check to see if there is a result
			if (resultSet.next()) {
				Integer maxServerIdInt = resultSet.getInt(GameServerDTO.Fields.ServerId.getFieldName());
				// Increment server id by 1
				maxServerIdInt += 1;
				serverId = maxServerIdInt.toString();
			} else {
				// There is no running game server
				serverId =  "1";
			}

			resultSet.getStatement().close();
			return serverId; // Query has succeeded
		} catch (SQLException e) {
			String errorMessage = "ERROR : #" + e.getErrorCode() + " " + e.getMessage();
			ResponseHandler.error(errorMessage); // Logger ERROR
			return null;
		}
	}
}
