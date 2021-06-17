package fr.fuwuyuan.gameserverapi.services;

import javax.ws.rs.core.Response;

import fr.fuwuyuan.gameserverapi.responses.ErrorResponse;

/**
 * Implements {@link ServerIdServiceInterface}
 * <p>
 * This is the abstract server id service class from which the service extends.
 * It contain a method to build a {@link Response} object in any case of an
 * error happening.
 * </p>
 * @author julien-beguier
 */
public abstract class AbstractServerIdService implements ServerIdServiceInterface {

	/**
	 * This method build the response when it was not possible to determine
	 * the next available server id.</br>
	 * The http code is set to 500 Internal Server Error.
	 * @param errorString correspond to a {@link ServerIdError}
	 * @return a Response object with an {@code error} field and the http code set
	 * accordingly
	 * @see {@link javax.ws.rs.core.Response#status}
	 * @see {@link javax.ws.rs.core.Response.ResponseBuilder#entity}
	 * @see {@link javax.ws.rs.core.Response.ResponseBuilder#build}
	 */
	public Response nextIdCannotBeDeterminedResponse(String errorString) {
		String errorMessage;
		ErrorResponse er = new ErrorResponse();

		if (errorString.equals(ServerIdError.SQL_DATABASE_SESSION_NOT_CONNECTED.getErrorString())) {
			errorMessage = "A session to the database cannot be established";
			er.setError(errorMessage);
			er.setStatus(Response.Status.INTERNAL_SERVER_ERROR);
			return Response.status(Response.Status.INTERNAL_SERVER_ERROR).entity(er).build();
		} else {
			errorMessage = "The next available server id cannot be determined";
			er.setError(errorMessage);
			er.setStatus(Response.Status.INTERNAL_SERVER_ERROR);
			return Response.status(Response.Status.INTERNAL_SERVER_ERROR).entity(er).build();
		}
	}

	public abstract String getNextServerId();
}
