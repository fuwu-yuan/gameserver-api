package fr.fuwuyuan.gameserverapi.services;

import java.sql.ResultSet;
import java.sql.SQLException;

import javax.json.JsonObject;
import javax.ws.rs.core.Response;

import fr.fuwuyuan.gameserverapi.database.DatabaseSession;
import fr.fuwuyuan.gameserverapi.database.dto.GameServerDTO;
import fr.fuwuyuan.gameserverapi.logs.ResponseHandler;
import fr.fuwuyuan.gameserverapi.responses.ErrorResponse;
import fr.fuwuyuan.gameserverapi.services.GameServerServiceInterface.GameServerError;

/**
 * Implements {@link GameServerServiceInterface}
 * <p>
 * This is the abstract game server service class from which the service extends.
 * It contains a method to check if the {@code auth_key} is correct which means
 * that the request received from the outside is authorized as well as methods
 * to build a {@link Response} object in any case of an error happening.
 * </p>
 * @author julien-beguier
 * @see {@link AbstractGameServerService#initHeaderAuthKey}
 * @see {@link AbstractGameServerService#isAuthorized}
 */
public abstract class AbstractGameServerService implements GameServerServiceInterface {

	private static String headerAuthKey = null;

	protected GameServerDTO gameServer = null;

	/**
	 * This method is used to reset the current {@link GameServerDTO} object in order to
	 * be fetch again by another request.
	 */
	protected void resetGameServerObj() {
		this.gameServer = null;
	}

	/**
	 * This method is called the first time {@link AbstractGameServerService#isAuthorized isAuthorized()}
	 * is called to initialize the auth_key. All requests sent without or with an
	 * incorrect auth_key will be rejected with a 401 Unauthorized response.
	 * @return the auth_key stored in the {@code 'settings'} table or null if it
	 * fails
	 */
	private int initHeaderAuthKey() {
		String sql = "SELECT `settings`.`setting_value` FROM `settings` "
					+ "WHERE `settings`.`setting_key` = 'header_auth_key'";

		try {
			DatabaseSession dbSession = DatabaseSession.getInstance();
			// Check if the database session is indeed connected to the database
			if (!dbSession.isConnected()) {
				return AuthKeyError.SQL_DATABASE_SESSION_NOT_CONNECTED.getErrorCode();
			} else {
				// The Database session is connected, executing the query
				ResultSet resultSet = dbSession.executeQuery(sql);

				resultSet.next();
				// Authentication key is set
				headerAuthKey = resultSet.getString(1);
				resultSet.getStatement().close();

				return RET_OK;
			}
		} catch (SQLException e) {
			String errorMessage = "ERROR #" + e.getErrorCode() + " " + e.getMessage();
			ResponseHandler.error(errorMessage, true);
			return AuthKeyError.SQL_ERROR_FETCH_LOG_AND_DO_NOTHING.getErrorCode();
		}
	}

	/**
	 * This method compares the 'good' auth_key stored in the {@code 'settings'}
	 * table and the one given as parameter.
	 * @param authKey as a String
	 * @return an error code if the auth_key could not be fetch from database or
	 * if the given auth_key doesn't match the one store in {@code 'settings'}
	 * table and {@code true} otherwise
	 * @see {@link AuthKeyError}
	 */
	protected int isAuthorized(String authKey) {
		// To only initialize it one time and not each time a request is received
		if (headerAuthKey == null) {
			int ret = initHeaderAuthKey();
			if (ret != RET_OK)
				return ret;
		}

		if (authKey == null || headerAuthKey == null || !authKey.equals(headerAuthKey))
			return AuthKeyError.AUTH_KEY_MISMATCH_WITH_PROVIDED.getErrorCode();
		return RET_OK;
	}

	/**
	 * This method build the response according to the {@code errorCode} which
	 * correspond to an error that has occurred while fetching the
	 * {@code auth_key} from the {@code 'settings'} table.</br>
	 * @param errorCode correspond to a {@link AuthKeyError}
	 * @return a Response object with an {@code error} field and the http code set
	 * accordingly
	 * @see {@link javax.ws.rs.core.Response#status}
	 * @see {@link javax.ws.rs.core.Response.ResponseBuilder#entity}
	 * @see {@link javax.ws.rs.core.Response.ResponseBuilder#build}
	 */
	protected Response authKeyComparisonErrorResponse(int errorCode) {
		String errorMessage;
		ErrorResponse er = new ErrorResponse();

		if (errorCode == AuthKeyError.AUTH_KEY_MISMATCH_WITH_PROVIDED.getErrorCode()) {
			return unauthorizedResponse();
		} else if (errorCode == AuthKeyError.SQL_DATABASE_SESSION_NOT_CONNECTED.getErrorCode()) {
			errorMessage = "A session to the database cannot be established";
			er.setError(errorMessage);
			er.setStatus(Response.Status.INTERNAL_SERVER_ERROR);
			return Response.status(Response.Status.INTERNAL_SERVER_ERROR).entity(er).build();
		} else {
			errorMessage = "The auth key cannot be fetched";
			er.setError(errorMessage);
			er.setStatus(Response.Status.INTERNAL_SERVER_ERROR);
			return Response.status(Response.Status.INTERNAL_SERVER_ERROR).entity(er).build();
		}
	}

	/**
	 * This method build the response with the http code 401 Unauthorized.
	 * @return a Response object with an {@code error} field and the http code set
	 * accordingly
	 * @see {@link javax.ws.rs.core.Response#status}
	 * @see {@link javax.ws.rs.core.Response.ResponseBuilder#entity}
	 * @see {@link javax.ws.rs.core.Response.ResponseBuilder#build}
	 */
	protected Response unauthorizedResponse() {
		String errorMessage = "Unauthorized access";
		ErrorResponse er = new ErrorResponse(Response.Status.UNAUTHORIZED, errorMessage);
		return Response.status(Response.Status.UNAUTHORIZED).entity(er).build();
	}

	/**
	 * This method build the response with the http code 400 Bad Request when no
	 * or an empty input was sent to the api.
	 * @return a Response object with an {@code error} field and the http code set
	 * accordingly
	 * @see {@link javax.ws.rs.core.Response#status}
	 * @see {@link javax.ws.rs.core.Response.ResponseBuilder#entity}
	 * @see {@link javax.ws.rs.core.Response.ResponseBuilder#build}
	 */
	protected Response badRequestEmptyInputResponse() {
		String errorMessage = "Input json is not provided";
		ErrorResponse er = new ErrorResponse(Response.Status.BAD_REQUEST, errorMessage);
		return Response.status(Response.Status.BAD_REQUEST).entity(er).build();
	}

	/**
	 * This method build the response with the http code 400 Bad Request when a
	 * malformed input was sent to the api with the property specified.</br>
	 * When a mandatory property is missing for instance or if the type of a
	 * mandatory property is invalid.
	 * @param field as a {@link GameServerDTO.Fields}
	 * @return a Response object with an {@code error} field and the http code set
	 * accordingly
	 * @see {@link javax.ws.rs.core.Response#status}
	 * @see {@link javax.ws.rs.core.Response.ResponseBuilder#entity}
	 * @see {@link javax.ws.rs.core.Response.ResponseBuilder#build}
	 */
	protected Response badRequestMalformedInputResponse(GameServerDTO.Fields field) {
		String errorMessage = "Input json is malformed: property '" + field.getFieldName() + "' is missing";
		ErrorResponse er = new ErrorResponse(Response.Status.BAD_REQUEST, errorMessage);
		return Response.status(Response.Status.BAD_REQUEST).entity(er).build();
	}

	/**
	 * This method build the response with the http code 400 Bad Request when a
	 * malformed input was sent to the api.</br>
	 * When a mandatory string property is null or blank or when a mandatory numeric
	 * property is a negative number for instance.
	 * @param n as an int value correspond to the number of invalid property/ies and
	 * is only used to be more precise in the error message
	 * @return a Response object with an {@code error} field and the http code set
	 * accordingly
	 * @see {@link javax.ws.rs.core.Response#status}
	 * @see {@link javax.ws.rs.core.Response.ResponseBuilder#entity}
	 * @see {@link javax.ws.rs.core.Response.ResponseBuilder#build}
	 */
	protected Response badRequestMandatoryPropertyInvalidResponse(int n) {
		String errorMessage = "Input json has (" + n + ") mandatory property invalid";
		ErrorResponse er = new ErrorResponse(Response.Status.BAD_REQUEST, errorMessage);
		return Response.status(Response.Status.BAD_REQUEST).entity(er).build();
	}

	/**
	 * This method build the response according to the {@code errorCode} which
	 * correspond to an error that has occurred while fetching or manipulating a
	 * {@link GameServerDTO}.</br>
	 * The http code will either be 404 Not Found or 500 Internal Server Error if
	 * the SQL fails.
	 * @param errorCode correspond to a {@link GameServerError}
	 * @return a Response object with an {@code error} field and the http code set
	 * accordingly
	 * @see {@link javax.ws.rs.core.Response#status}
	 * @see {@link javax.ws.rs.core.Response.ResponseBuilder#entity}
	 * @see {@link javax.ws.rs.core.Response.ResponseBuilder#build}
	 */
	protected Response gameServerCannotBeFetchOrChangedResponse(int errorCode) {
		String errorMessage;
		ErrorResponse er = new ErrorResponse();

		if (errorCode == GameServerError.NO_GAMESERVER_CORRESPONDING_TO_GIVEN_ID.getErrorCode()) {
			errorMessage = "No game server with given id found";
			er.setError(errorMessage);
			er.setStatus(Response.Status.NOT_FOUND);
			return Response.status(Response.Status.NOT_FOUND).entity(er).build();
		} else if (errorCode == GameServerError.SQL_DATABASE_SESSION_NOT_CONNECTED.getErrorCode()) {
			errorMessage = "A session to the database cannot be established";
			er.setError(errorMessage);
			er.setStatus(Response.Status.INTERNAL_SERVER_ERROR);
			return Response.status(Response.Status.INTERNAL_SERVER_ERROR).entity(er).build();
		} else if (errorCode == GameServerError.SQL_ERROR_CREATED_LOG_AND_DO_NOTHING.getErrorCode()) {
			errorMessage = "The game server cannot be created";
			er.setError(errorMessage);
			er.setStatus(Response.Status.INTERNAL_SERVER_ERROR);
			return Response.status(Response.Status.INTERNAL_SERVER_ERROR).entity(er).build();
		} else {
			errorMessage = "The game server cannot be fetched";
			er.setError(errorMessage);
			er.setStatus(Response.Status.INTERNAL_SERVER_ERROR);
			return Response.status(Response.Status.INTERNAL_SERVER_ERROR).entity(er).build();
		}
	}

	public abstract Response createGameServer(String authKey, JsonObject postInput);
	public abstract Response getGameServers(String authKey);
	public abstract Response getGameServerById(String authKey, String serverId);
	public abstract Response getGameServerByGameNameAndGameVersion(String authKey,String gameName, String gameVersion);
	public abstract Response shutdownGameServer(String authKey, String serverId);
}
