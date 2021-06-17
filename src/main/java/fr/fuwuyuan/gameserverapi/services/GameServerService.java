package fr.fuwuyuan.gameserverapi.services;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import javax.json.JsonObject;
import javax.json.JsonValue;
import javax.ws.rs.core.Response;

import fr.fuwuyuan.gameserverapi.data.GameServerSlim;
import fr.fuwuyuan.gameserverapi.database.DatabaseSession;
import fr.fuwuyuan.gameserverapi.database.dto.GameServerDTO;
import fr.fuwuyuan.gameserverapi.logs.ResponseHandler;
import fr.fuwuyuan.gameserverapi.responses.GameServerApiResponse;
import fr.fuwuyuan.gameserverapi.responses.GameServerCreatedResponse;
import fr.fuwuyuan.gameserverapi.responses.GameServerDTOResponse;
import fr.fuwuyuan.gameserverapi.responses.GameServerListResponse;

/**
 * Extends {@link AbstractGameServerService}
 * <p>
 * This is the game server service class which implements the methods to create,
 * manipulate and shutdown a game server. It uses the {@link IpService}, the
 * {@link PortService} and {@link DatabaseSession} to query the database.
 * It also contains methods to do some error checking.</br></br>
 * The {@link Response} is built with a {@link GameServerApiResponse} or an
 * extended class of it
 * ({@link fr.fuwuyuan.gameserverapi.responses.ErrorResponse ErrorResponse},
 * {@link GameServerCreatedResponse}, {@link GameServerDTOResponse} or
 * {@link GameServerListResponse}).
 * </p>
 * @author julien-beguier
 * @see {@link IpService#getPublicIp}
 * @see {@link PortService#getAvailablePort}
 * @see {@link PortService#addNewPortToUsedPorts}
 * @see {@link PortService#freeUsedPort}
 * @see {@link DatabaseSession#prepareInserting}
 * @see {@link DatabaseSession#executeQuery}
 * @see {@link DatabaseSession#executeUpdate}
 */
public class GameServerService extends AbstractGameServerService {

	public ServerIdServiceInterface idService = new ServerIdService();
	public IpServiceInterface ipService = new IpService();
	public PortServiceInterface portService = new PortService();

	/**
	 * Note: CGSI = CreateGameServerInput
	 * <p>
	 * This method checks if the json received as input contains all mandatory
	 * fields and if they are of the right type ('name'=String, 'game'=String,
	 * 'game_version'=String and 'n_max_players'=Number). Also checks for the
	 * optional field 'description'=String.</br>
	 * </p>
	 * @param postInput as a {@link javax.json.JsonObject}
	 * @return {@code null} if the json is well formed and the first missing
	 * property or of invalid type as a {@link GameServerDTO.Fields} otherwise
	 * @see {@link GameServerDTO#Fields}
	 */
	private GameServerDTO.Fields isCGSIIntegrityOK(final JsonObject postInput) {
		String n = GameServerDTO.Fields.Name.getFieldName();
		String gn = GameServerDTO.Fields.Game.getFieldName();
		String gv = GameServerDTO.Fields.GameVersion.getFieldName();
		String nmp = GameServerDTO.Fields.NMaxPlayers.getFieldName();
		String d = GameServerDTO.Fields.Description.getFieldName();

		boolean b1 = postInput.containsKey(n) && postInput.get(n).getValueType() == JsonValue.ValueType.STRING;
		boolean b2 = postInput.containsKey(gn) && postInput.get(gn).getValueType() == JsonValue.ValueType.STRING;
		boolean b3 = postInput.containsKey(gv) && postInput.get(gv).getValueType() == JsonValue.ValueType.STRING;
		boolean b4 = postInput.containsKey(nmp) && postInput.get(nmp).getValueType() == JsonValue.ValueType.NUMBER;

		if (!b1)
			return GameServerDTO.Fields.Name;
		if (!b2)
			return GameServerDTO.Fields.Game;
		if (!b3)
			return GameServerDTO.Fields.GameVersion;
		if (!b4)
			return GameServerDTO.Fields.NMaxPlayers;

		if (postInput.containsKey(d)) {
			boolean optionalB5 = postInput.get(d).getValueType() == JsonValue.ValueType.STRING;
			if (!optionalB5)
				return GameServerDTO.Fields.Description;
		}

		// If fields exists & is of the right type
		return null;
	}

	/**
	 * This method checks if any of the mandatory string property ('name', 'game',
	 * 'game_version') is either null or blank and if any of the mandatory numeric
	 * property ('n_max_players') is a negative value.</br>
	 * Note: CGSI = CreateGameServerInput
	 * @param postInput as a {@link javax.json.JsonObject}
	 * @return a positive int value if any mandatory string property ('name',
	 * 'game', 'game_version') is null or blank or if any mandatory numeric
	 * property ('n_max_players') is a negative value and {@code 0} otherwise
	 * @see {@link GameServerDTO#Fields}
	 */
	private int hasCGSIAnyMandatoryPropertyInvalid(final JsonObject postInput) {
		String sn = postInput.getString(GameServerDTO.Fields.Name.getFieldName());
		String gn = postInput.getString(GameServerDTO.Fields.Game.getFieldName());
		String gv = postInput.getString(GameServerDTO.Fields.GameVersion.getFieldName());
		int nmp = postInput.getInt(GameServerDTO.Fields.NMaxPlayers.getFieldName());

		int p1 = (sn == null || sn.isBlank()) ? 1 : 0;
		int p2 = (gn == null || gn.isBlank()) ? 1 : 0;
		int p3 = (gv == null || gv.isBlank()) ? 1 : 0;
		int p4 = (nmp < 0) ? 1 : 0;

		return p1 + p2 + p3 + p4;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public Response createGameServer(final String authKey, final JsonObject postInput) {
		// ####################### BASIC ERROR CHECKS
		// Check the auth_key
		int authKeyRet = isAuthorized(authKey);
		if (authKeyRet != RET_OK)
			return authKeyComparisonErrorResponse(authKeyRet);

		// Check the postInput (Json properties)
		if (postInput == null)
			return badRequestEmptyInputResponse();

		// Check for missing property
		GameServerDTO.Fields f = isCGSIIntegrityOK(postInput);
		if (f != null)
			return badRequestMalformedInputResponse(f);

		// Check mandatory properties not null or blank
		int n = hasCGSIAnyMandatoryPropertyInvalid(postInput);
		if (n != 0)
			return badRequestMandatoryPropertyInvalidResponse(n);

		// Get the description if present
		String optionalDescription = null;
		if (postInput.containsKey(GameServerDTO.Fields.Description.getFieldName()))
			optionalDescription = postInput.getString(GameServerDTO.Fields.Description.getFieldName());

		// Build the base game server object
		GameServerDTO gs = GameServerDTO.initFromPostInputValues(
				postInput.getString(GameServerDTO.Fields.Name.getFieldName()),
				optionalDescription,
				postInput.getString(GameServerDTO.Fields.Game.getFieldName()),
				postInput.getString(GameServerDTO.Fields.GameVersion.getFieldName()),
				postInput.getInt(GameServerDTO.Fields.NMaxPlayers.getFieldName()));

		// ####################### DETERMINE SERVER ID
		String serverId = idService.getNextServerId();
		if (serverId.equals(ServerIdServiceInterface.ServerIdError.SQL_DATABASE_SESSION_NOT_CONNECTED.getErrorString())
				|| serverId.equals(ServerIdServiceInterface.ServerIdError.SQL_ERROR_FETCH_LOG_AND_DO_NOTHING.getErrorString()))
			return ((AbstractServerIdService) idService).nextIdCannotBeDeterminedResponse(serverId);

		gs.setServerId(serverId);

		// ####################### DETERMINE IP
		// Determine public IP from which (physical) server to launch
		String ip = ipService.getPublicIp();
		if (ip == null)
			return ((AbstractIpService) ipService).ipCannotBeDeterminedResponse();

		gs.setIp(ip);

		// ####################### DETERMINE PORT
		// Determine which available port it can use
		int port = portService.getAvailablePort(ip);
		if (port < 0)
			return ((AbstractPortService) portService).portCannotBeDeterminedUpdatedOrChangedResponse(port);

		gs.setPort(port);

		// ####################### LAUNCH THE SERVER BINARY
		boolean newServerLaunched = true; // TODO
		// Check if the new game server has launched
		if (!newServerLaunched)
			return Response.status(Response.Status.NOT_IMPLEMENTED).entity("This is still a Work-In-Progress feature").build();

		// ############### SQL ### UPDATE USED PORT TO DB
		int updateIsDone = portService.addNewPortToUsedPorts(ip, port);
		if (updateIsDone != RET_OK)
			return ((AbstractPortService) portService).portCannotBeDeterminedUpdatedOrChangedResponse(updateIsDone);

		// ############### SQL ### SAVE GS TO DB
		// SQL - Saving the game server to DB
		String insertSql = "SELECT `server_id`, `ip`, `port`, `name`, `description`, "
						+ "`game`, `game_version`, `n_max_players`, `opened_on`, `ready_for_shutdown` "
						+ "FROM `servers`";

		try {
			DatabaseSession dbSession = DatabaseSession.getInstance();
			// Check if the database session is indeed connected to the database
			if (!dbSession.isConnected()) {
				return gameServerCannotBeFetchOrChangedResponse(GameServerError.SQL_DATABASE_SESSION_NOT_CONNECTED.getErrorCode());
			} else {
				// The Database session is connected, executing the query
				ResultSet resultSet = dbSession.prepareInserting(insertSql);

				// ############### SQL ### PREPARE THE INSERT
				resultSet.moveToInsertRow();
				resultSet.updateString(GameServerDTO.Fields.ServerId.getFieldName(), gs.getServerId());
				resultSet.updateString(GameServerDTO.Fields.Ip.getFieldName(), gs.getIp());
				resultSet.updateInt(GameServerDTO.Fields.Port.getFieldName(), gs.getPort());
				resultSet.updateString(GameServerDTO.Fields.Name.getFieldName(), gs.getName());
				resultSet.updateString(GameServerDTO.Fields.Description.getFieldName(), gs.getDescription());
				resultSet.updateString(GameServerDTO.Fields.Game.getFieldName(), gs.getGame());
				resultSet.updateString(GameServerDTO.Fields.GameVersion.getFieldName(), gs.getGameVersion());
				resultSet.updateInt(GameServerDTO.Fields.NMaxPlayers.getFieldName(), gs.getNMaxPlayers());
				resultSet.insertRow();

				resultSet.getStatement().close();

				// ####################### RETURN THE CREATED GAME SERVER (SLIM)
				GameServerSlim gss = new GameServerSlim(gs);
				GameServerCreatedResponse gscr = new GameServerCreatedResponse(gss);
				return Response.status(Response.Status.CREATED).entity(gscr).build();
			}
		} catch (SQLException e) {
			String errorMessage = "ERROR #" + e.getErrorCode() + " " + e.getMessage();
			ResponseHandler.error(errorMessage, true);
			return gameServerCannotBeFetchOrChangedResponse(GameServerError.SQL_ERROR_CREATED_LOG_AND_DO_NOTHING.getErrorCode());
		}
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public Response getGameServers(final String authKey) {
		// Check the auth_key
		int authKeyRet = isAuthorized(authKey);
		if (authKeyRet != RET_OK)
			return authKeyComparisonErrorResponse(authKeyRet);

		// ############### SQL ### FETCH ALL GAME SERVERS
		String selectSql = "SELECT `server_id`, `ip`, `port`, `name`, `description`, "
						+ "`game`, `game_version`, `n_max_players`"
						+ "FROM `servers`";

		try {
			DatabaseSession dbSession = DatabaseSession.getInstance();
			// Check if the database session is indeed connected to the database
			if (!dbSession.isConnected()) {
				return gameServerCannotBeFetchOrChangedResponse(GameServerError.SQL_DATABASE_SESSION_NOT_CONNECTED.getErrorCode());
			} else {
				// The Database session is connected, executing the query
				ResultSet resultSet = dbSession.executeQuery(selectSql);

				// ####################### BUILD THE GAME SERVER LIST
				GameServerSlim gss = null;
				List<GameServerSlim> servers = new ArrayList<GameServerSlim>();
				while (resultSet.next()) {
					gss = new GameServerSlim();
					gss.setServerId(resultSet.getString(GameServerDTO.Fields.ServerId.getFieldName()));
					gss.setIp(resultSet.getString(GameServerDTO.Fields.Ip.getFieldName()));
					gss.setPort(resultSet.getInt(GameServerDTO.Fields.Port.getFieldName()));
					gss.serName(resultSet.getString(GameServerDTO.Fields.Name.getFieldName()));
					gss.setDescription(resultSet.getString(GameServerDTO.Fields.Description.getFieldName()));
					gss.setGame(resultSet.getString(GameServerDTO.Fields.Game.getFieldName()));
					gss.setGameVersion(resultSet.getString(GameServerDTO.Fields.GameVersion.getFieldName()));
					gss.setNMaxPlayers(resultSet.getInt(GameServerDTO.Fields.NMaxPlayers.getFieldName()));

					servers.add(gss);
				}
				resultSet.getStatement().close();

				// ####################### RETURN GAME SERVERS (SLIM)
				GameServerListResponse gslr = new GameServerListResponse(servers);
				return Response.status(Response.Status.OK).entity(gslr).build();
			}
		} catch (SQLException e) {
			String errorMessage = "ERROR #" + e.getErrorCode() + " " + e.getMessage();
			ResponseHandler.error(errorMessage, true);
			return gameServerCannotBeFetchOrChangedResponse(GameServerError.SQL_ERROR_FETCH_LOG_AND_DO_NOTHING.getErrorCode());
		}
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public Response getGameServerById(final String authKey, final String serverId) {
		// Check the auth_key
		int authKeyRet = isAuthorized(authKey);
		if (authKeyRet != RET_OK)
			return authKeyComparisonErrorResponse(authKeyRet);

		// ############### SQL ### FETCH GAME SERVER BY ITS ID
		if (null == this.gameServer) {
			int ret = fetchGameServerById(serverId);
			if (ret != RET_OK)
				return gameServerCannotBeFetchOrChangedResponse(ret);
			return getGameServerById(authKey, serverId);
		} else {
			// ####################### RETURN GAME SERVER (FULL)
			GameServerDTOResponse gsr = new GameServerDTOResponse(this.gameServer);
			Response response = Response.status(Response.Status.OK).entity(gsr).build();
			resetGameServerObj();
			return response;
		}
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public Response getGameServerByGameNameAndGameVersion(final String authKey,
			final String gameName, final String gameVersion) {
		// Check the auth_key
		int authKeyRet = isAuthorized(authKey);
		if (authKeyRet != RET_OK)
			return authKeyComparisonErrorResponse(authKeyRet);

		// ############### SQL ### FETCH GAME SERVER BY GAMENAME & GAMEVERSION
		String selectSql = "SELECT `server_id`, `ip`, `port`, `name`, `description`, "
						+ "`game`, `game_version`, `n_max_players`"
						+ "FROM `servers` "
						+ "WHERE `servers`.`game` LIKE '" + gameName + "' AND `servers`.`game_version` = '" + gameVersion + "'";

		try {
			DatabaseSession dbSession = DatabaseSession.getInstance();
			// Check if the database session is indeed connected to the database
			if (!dbSession.isConnected()) {
				return gameServerCannotBeFetchOrChangedResponse(GameServerError.SQL_DATABASE_SESSION_NOT_CONNECTED.getErrorCode());
			} else {
				// The Database session is connected, executing the query
				ResultSet resultSet = dbSession.executeQuery(selectSql);

				// ####################### BUILD THE GAME SERVER LIST
				GameServerSlim gss = null;
				List<GameServerSlim> servers = new ArrayList<GameServerSlim>();
				while (resultSet.next()) {
					gss = new GameServerSlim();
					gss.setServerId(resultSet.getString(GameServerDTO.Fields.ServerId.getFieldName()));
					gss.setIp(resultSet.getString(GameServerDTO.Fields.Ip.getFieldName()));
					gss.setPort(resultSet.getInt(GameServerDTO.Fields.Port.getFieldName()));
					gss.serName(resultSet.getString(GameServerDTO.Fields.Name.getFieldName()));
					gss.setDescription(resultSet.getString(GameServerDTO.Fields.Description.getFieldName()));
					gss.setGame(resultSet.getString(GameServerDTO.Fields.Game.getFieldName()));
					gss.setGameVersion(resultSet.getString(GameServerDTO.Fields.GameVersion.getFieldName()));
					gss.setNMaxPlayers(resultSet.getInt(GameServerDTO.Fields.NMaxPlayers.getFieldName()));

					servers.add(gss);
				}
				resultSet.getStatement().close();

				// ####################### RETURN GAME SERVERS (SLIM)
				GameServerListResponse gslr = new GameServerListResponse(servers);
				return Response.status(Response.Status.OK).entity(gslr).build();
			}
		} catch (SQLException e) {
			String errorMessage = "ERROR #" + e.getErrorCode() + " " + e.getMessage();
			ResponseHandler.error(errorMessage, true);
			return gameServerCannotBeFetchOrChangedResponse(GameServerError.SQL_ERROR_FETCH_LOG_AND_DO_NOTHING.getErrorCode());
		}
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public Response shutdownGameServer(final String authKey, final String serverId) {
		// Check the auth_key
		int authKeyRet = isAuthorized(authKey);
		if (authKeyRet != RET_OK)
			return authKeyComparisonErrorResponse(authKeyRet);

		// ############### SQL ### FETCH GAME SERVER BY ITS ID
		if (null == this.gameServer) {
			int ret = fetchGameServerById(serverId);
			if (ret != RET_OK)
				return gameServerCannotBeFetchOrChangedResponse(ret);
			return shutdownGameServer(authKey, serverId);
		} else {
			// ####################### SHUTDOWN THE SERVER BINARY
			// TODO
			// ############### SQL ### FREE USED PORT TO DB
			int portFreedReturnCode = portService.freeUsedPort(this.gameServer.getIp(), this.gameServer.getPort());
			if (portFreedReturnCode != RET_OK)
				return ((AbstractPortService) portService).portCannotBeDeterminedUpdatedOrChangedResponse(portFreedReturnCode);
			// ############### SQL ### DELETE GAME SERVER RECORD FROM DB
			Response response;
			String deleteServerSql = "DELETE FROM `servers` WHERE `servers`.`server_id`= '" + serverId + "'";

			try {
				DatabaseSession dbSession = DatabaseSession.getInstance();
				// Check if the database session is indeed connected to the database
				if (!dbSession.isConnected()) {
					response = gameServerCannotBeFetchOrChangedResponse(GameServerError.SQL_DATABASE_SESSION_NOT_CONNECTED.getErrorCode());
				} else {
					// The Database session is connected, executing the query
					int requestResult = dbSession.executeUpdate(deleteServerSql);

					if (requestResult == 0) { // Check to see if the game server exists
						response = gameServerCannotBeFetchOrChangedResponse(GameServerError.NO_GAMESERVER_CORRESPONDING_TO_GIVEN_ID.getErrorCode());
					} else {
						GameServerApiResponse gsaResponse = new GameServerApiResponse(Response.Status.OK, JsonValue.EMPTY_JSON_OBJECT);
						response = Response.status(Response.Status.OK).entity(gsaResponse).build();
					}
				}
			} catch (SQLException e) {
				String errorMessage = "ERROR #" + e.getErrorCode() + " " + e.getMessage();
				ResponseHandler.error(errorMessage, true);
				response = gameServerCannotBeFetchOrChangedResponse(GameServerError.SQL_ERROR_FETCH_LOG_AND_DO_NOTHING.getErrorCode());
			} finally {
				resetGameServerObj();
			}
			return response;
		}
	}

	// ##########################################################################

	/**
	 * This method fetch a {@link GameServerDTO} by its {@code serverId} and
	 * save it as a member of the {@link AbstractGameServerService} class for
	 * manipulation.
	 * @param serverId as a String
	 * @return {@code RET_OK} if successful, a {@link GameServerError} otherwise
	 * @see {@link AbstractGameServerService#gameServer}
	 * @see {@link ServiceInterface#RET_OK}
	 * @see {@link GameServerError}
	 */
	private int fetchGameServerById(final String serverId) {
		String selectSql = "SELECT `server_id`, `ip`, `port`, `name`, `description`, "
						+ "`game`, `game_version`, `n_max_players`, `opened_on`, `ready_for_shutdown` "
						+ "FROM `servers` "
						+ "WHERE `servers`.`server_id` = '" + serverId + "'";

		try {
			DatabaseSession dbSession = DatabaseSession.getInstance();
			// Check if the database session is indeed connected to the database
			if (!dbSession.isConnected()) {
				return GameServerError.SQL_DATABASE_SESSION_NOT_CONNECTED.getErrorCode();
			} else {
				// The Database session is connected, executing the query
				ResultSet resultSet = dbSession.executeQuery(selectSql);

				if (!resultSet.next()) { // Check to see if the game server exists
					return GameServerError.NO_GAMESERVER_CORRESPONDING_TO_GIVEN_ID.getErrorCode();
				}

				GameServerDTO gs = new GameServerDTO();
				gs.setServerId(resultSet.getString(GameServerDTO.Fields.ServerId.getFieldName()));
				gs.setIp(resultSet.getString(GameServerDTO.Fields.Ip.getFieldName()));
				gs.setPort(resultSet.getInt(GameServerDTO.Fields.Port.getFieldName()));
				gs.setName(resultSet.getString(GameServerDTO.Fields.Name.getFieldName()));
				gs.setDescription(resultSet.getString(GameServerDTO.Fields.Description.getFieldName()));
				gs.setGame(resultSet.getString(GameServerDTO.Fields.Game.getFieldName()));
				gs.setGameVersion(resultSet.getString(GameServerDTO.Fields.GameVersion.getFieldName()));
				gs.setNMaxPlayers(resultSet.getInt(GameServerDTO.Fields.NMaxPlayers.getFieldName()));
				gs.setOpenedOn(resultSet.getString(GameServerDTO.Fields.OpenedOn.getFieldName()));
				int rfs = resultSet.getInt(GameServerDTO.Fields.ReadyForShutdown.getFieldName());
				gs.setReadyForShutdown(rfs == 1 ? true : false);

				resultSet.getStatement().close();
				this.gameServer = gs;
				return RET_OK;
			}
		} catch (SQLException e) {
			String errorMessage = "ERROR #" + e.getErrorCode() + " " + e.getMessage();
			ResponseHandler.error(errorMessage, true);
			return GameServerError.SQL_ERROR_FETCH_LOG_AND_DO_NOTHING.getErrorCode();
		}
	}
}
