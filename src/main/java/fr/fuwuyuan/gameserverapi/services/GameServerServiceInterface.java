package fr.fuwuyuan.gameserverapi.services;

import javax.json.JsonObject;
import javax.ws.rs.core.Response;

import fr.fuwuyuan.gameserverapi.database.DatabaseSession;
import fr.fuwuyuan.gameserverapi.database.dto.GameServerDTO;
import fr.fuwuyuan.gameserverapi.responses.GameServerApiResponse;

/**
 * Extends {@link ServiceInterface}
 * <p>
 * This is the game server service interface that describes the methods to
 * manipulate game servers. It also contains a public enumeration with
 * possible errors concerning SQL interactions.
 * </p>
 * @author julien-beguier
 * @see {@link GameServerError}
 * @see {@link GameServerServiceInterface#createGameServer}
 * @see {@link GameServerServiceInterface#getGameServers}
 * @see {@link GameServerServiceInterface#getGameServerById}
 * @see {@link GameServerServiceInterface#getGameServerByGameNameAndGameVersion}
 * @see {@link GameServerServiceInterface#shutdownGameServer}
 */
public interface GameServerServiceInterface extends ServiceInterface {

	/**
	 * This enumeration represents the possible cases that can happen while
	 * manipulating {@link GameServerDTO} during SQL requests.
	 * @author julien-beguier
	 * @see {@link fr.fuwuyuan.gameserverapi.database.dto.GameServerDTO GameServerDTO}
	 */
	public enum GameServerError {
		SQL_ERROR_FETCH_LOG_AND_DO_NOTHING(-101),
		SQL_ERROR_CREATED_LOG_AND_DO_NOTHING(-102),
		NO_GAMESERVER_CORRESPONDING_TO_GIVEN_ID(-110);

		private int errorCode;

		GameServerError(int errorCode) {
			this.errorCode = errorCode;
		}

		public int getErrorCode() {
			return this.errorCode;
		}
	}

	/**
	 * This enumeration represents the possible cases that can happen while
	 * fetching the auth key duringID SQL requests.
	 * @author julien-beguier
	 * @see {@link AbstractGameServerService}
	 */
	public enum AuthKeyError {
		SQL_DATABASE_SESSION_NOT_CONNECTED(-201),
		SQL_ERROR_FETCH_LOG_AND_DO_NOTHING(-202),
		AUTH_KEY_MISMATCH_WITH_PROVIDED(-210);

		private int errorCode;

		AuthKeyError(int errorCode) {
			this.errorCode = errorCode;
		}

		public int getErrorCode() {
			return this.errorCode;
		}
	}

	/**
	 * This method is called by the controller to build a game server object using
	 * the json input, determine the ip and port for the server binary it will
	 * launch and save those informations into the database as it can be requested
	 * later for clients to connect to the game server itself.
	 * @param authKey as a String to be compared with the one in the database to see
	 * if the caller is not unknown
	 * @param postInput as a {@link javax.json.JsonObject}. This is the basic data
	 * used to create the game server (server name, description, game name,
	 * game version, maximum number of players)
	 * @return upon successful creation, a {@link GameServerSlim} (to avoid
	 * sending unwanted informations, only 'server_id', 'ip', 'port', 'name',
	 * 'description', 'game', 'game_version' and 'n_max_players') otherwise the
	 * response will contain an {@link ErrorResponse} with status code and
	 * error set accordingly
	 * @see {@link fr.fuwuyuan.gameserverapi.services.AbstractGameServerService#isAuthorized AbstractGameServerService.isAuthorized}
	 * @see {@link IpService}
	 * @see {@link PortService}
	 * @see {@link DatabaseSession}
	 * @see {@link fr.fuwuyuan.gameserverapi.data.GameServerSlim GameServerSlim}
	 * @see {@link fr.fuwuyuan.gameserverapi.responses.ErrorResponse ErrorResponse}
	 */
	public Response createGameServer(String authKey, JsonObject postInput);

	/**
	 * This method is called by the controller to build a list of all game
	 * server saved in the {@code 'servers'} table.
	 * @param authKey as a String to be compared with the one in the database to see
	 * if the caller is not unknown
	 * @return a json object containing a list of {@link GameServerSlim}
	 * (to avoid sending unwanted informations, only 'server_id', 'ip', 'port',
	 * 'name', 'description', 'game', 'game_version' and 'n_max_players') otherwise
	 * the response will contain an {@link ErrorResponse} with status code
	 * and error set accordingly
	 * @see {@link fr.fuwuyuan.gameserverapi.services.AbstractGameServerService#isAuthorized AbstractGameServerService.isAuthorized}
	 * @see {@link DatabaseSession}
	 * @see {@link fr.fuwuyuan.gameserverapi.data.GameServerSlim GameServerSlim}
	 * @see {@link fr.fuwuyuan.gameserverapi.responses.ErrorResponse ErrorResponse}
	 */
	public Response getGameServers(String authKey);

	/**
	 * This method is called by the controller to fetch a game server by its
	 * {@code serverId} saved in the {@code 'servers'} table.
	 * @param authKey as a String to be compared with the one in the database to
	 * see if the caller is not unknown
	 * @param serverId as a String for the SQL request
	 * @return a {@link GameServerSlim} (to avoid sending unwanted informations,
	 * only 'server_id', 'ip', 'port', 'name', 'description', 'game',
	 * 'game_version' and 'n_max_players') otherwise the response will contain
	 * an {@link ErrorResponse} with status code and error set accordingly
	 * @see {@link fr.fuwuyuan.gameserverapi.services.AbstractGameServerService#isAuthorized AbstractGameServerService.isAuthorized}
	 * @see {@link DatabaseSession}
	 * @see {@link fr.fuwuyuan.gameserverapi.data.GameServerSlim GameServerSlim}
	 * @see {@link fr.fuwuyuan.gameserverapi.responses.ErrorResponse ErrorResponse}
	 */
	public Response getGameServerById(String authKey, String serverId);

	/**
	 * This method is called by the controller to build a list of game server
	 * filtered by the {@code gameName} and the {@code gameVersion} saved in
	 * the {@code 'servers'} table.
	 * @param authKey as a String to be compared with the one in the database to see
	 * if the caller is not unknown
	 * @param gameName as a String for the SQL request
	 * @param gameVersion as a String for the SQL request
	 * @return a json object containing a list of {@link GameServerSlim} (to
	 * avoid sending unwanted informations, only 'server_id', 'ip', 'port',
	 * 'name', 'description', 'game', 'game_version' and 'n_max_players') otherwise
	 * the response will contain an {@link ErrorResponse} with status code
	 * and error set accordingly
	 * @see {@link fr.fuwuyuan.gameserverapi.services.AbstractGameServerService#isAuthorized AbstractGameServerService.isAuthorized}
	 * @see {@link DatabaseSession}
	 * @see {@link fr.fuwuyuan.gameserverapi.data.GameServerSlim GameServerSlim}
	 * @see {@link fr.fuwuyuan.gameserverapi.responses.ErrorResponse ErrorResponse}
	 */
	public Response getGameServerByGameNameAndGameVersion(String authKey,
			String gameName, String gameVersion);

	/**
	 * This method is called by the controller to shutdown a running game server.
	 * To do so, it will first fetch the game server by its {@code serverId},
	 * send a signal to stop the process of the game server, free the port
	 * used by the game server from the {@code used} field of the
	 * {@code 'ports'} table and finally delete the record corresponding to that
	 * game server from the {@code 'servers'} table of the same database.
	 * @param authKey as a String to be compared with the one in the database to
	 * see if the caller is not unknown
	 * @param serverId as a String for the SQL request
	 * @return Upon successful shutdown, a {@link GameServerApiResponse} with
	 * status code {@code 200} or an {@link ErrorResponse} with status code
	 * and error set accordingly
	 * @see {@link fr.fuwuyuan.gameserverapi.services.AbstractGameServerService#isAuthorized AbstractGameServerService.isAuthorized}
	 * @see {@link fr.fuwuyuan.gameserverapi.services.GameServerService#fetchGameServerById GameServerService.fetchGameServerById}
	 * @see {@link PortService}
	 * @see {@link DatabaseSession}
	 * @see {@link fr.fuwuyuan.gameserverapi.responses.GameServerApiResponse GameServerApiResponse}
	 * @see {@link fr.fuwuyuan.gameserverapi.responses.ErrorResponse ErrorResponse}
	 */
	public Response shutdownGameServer(String authKey, String serverId);
}
