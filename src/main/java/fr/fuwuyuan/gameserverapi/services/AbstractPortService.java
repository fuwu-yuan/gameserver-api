package fr.fuwuyuan.gameserverapi.services;

import javax.json.JsonArray;
import javax.ws.rs.core.Response;

import fr.fuwuyuan.gameserverapi.responses.ErrorResponse;

/**
 * Implements {@link PortServiceInterface}
 * <p>
 * This is the abstract port service class from which the service extends.
 * It contains methods to build a {@link Response} object in any case of an
 * error happening.
 * </p>
 * @author julien-beguier
 */
public abstract class AbstractPortService implements PortServiceInterface {

	protected JsonArray portsUsed = null;
	protected JsonArray portsAvailable = null;

	/**
	 * This method is used to reset the current {@link javax.json.JsonArray} objects in order to
	 * be fetch again by another request.
	 */
	protected void resetPortsArrays() {
		this.portsUsed = null;
		this.portsAvailable = null;
	}

	/**
	 * This method build the response according to the {@code portErrorCode}
	 * which correspond to an error that has occurred while fetching or manipulating
	 * a {@link javax.json.JsonArray}.</br>
	 * The http code will either be 404 Not Found, 503 Service Unavailable or 500
	 * Internal Server Error if the SQL fails.
	 * @param portErrorCode correspond to a {@link PortError}
	 * @return a Response object with an {@code error} field and the http code set
	 * accordingly
	 * @see {@link javax.ws.rs.core.Response#status}
	 * @see {@link javax.ws.rs.core.Response.ResponseBuilder#entity}
	 * @see {@link javax.ws.rs.core.Response.ResponseBuilder#build}
	 */
	public Response portCannotBeDeterminedUpdatedOrChangedResponse(int portErrorCode) {
		String errorMessage;
		ErrorResponse er = new ErrorResponse();
		Response response = null;

		if (portErrorCode == PortError.NO_PORT_CORRESPONDING_TO_GIVEN_IP.getErrorCode()) {
			errorMessage = "The given ip is not registered";
			er.setError(errorMessage);
			er.setStatus(Response.Status.NOT_FOUND);
			response = Response.status(Response.Status.NOT_FOUND).entity(er).build();
		}	else if (portErrorCode == PortError.NO_AVAILABLE_PORT_LEFT_ON_GIVEN_IP.getErrorCode()) {
			errorMessage = "No available port left corresponding to the given IP";
			er.setError(errorMessage);
			er.setStatus(Response.Status.SERVICE_UNAVAILABLE);
			response = Response.status(Response.Status.SERVICE_UNAVAILABLE).entity(er).build();
		} else {
			errorMessage = "An available port cannot be determined";
			er.setError(errorMessage);
			er.setStatus(Response.Status.INTERNAL_SERVER_ERROR);
			response = Response.status(Response.Status.INTERNAL_SERVER_ERROR).entity(er).build();
		}
		return response;
	}

	public abstract int getAvailablePort(String ip);
	public abstract int addNewPortToUsedPorts(String ip, int port);
	public abstract int freeUsedPort(String ip, int port);
}
