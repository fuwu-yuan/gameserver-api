package fr.fuwuyuan.gameserverapi.services;

import javax.ws.rs.core.Response;

import fr.fuwuyuan.gameserverapi.responses.ErrorResponse;

/**
 * Implements {@link IpServiceInterface}
 * <p>
 * This is the abstract ip service class from which the service extends.
 * It contains a method to build a {@link Response} object in any case of an
 * error happening.
 * </p>
 * @author julien-beguier
 */
public abstract class AbstractIpService implements IpServiceInterface {

	/**
	 * This method is called if the public ip cannot be determined because the
	 * api cannot communicate with the simple-ip-ws.
	 * @return a Response object with an {@code error} field and the http code set
	 * to 500 Internal Server Error
	 * @see {@link javax.ws.rs.core.Response#status}
	 * @see {@link javax.ws.rs.core.Response.ResponseBuilder#entity}
	 * @see {@link javax.ws.rs.core.Response.ResponseBuilder#build}
	 */
	protected Response ipCannotBeDeterminedResponse() {
		String errorMessage = "The public IP cannot be determined (check status of simple-ip-service)";
		ErrorResponse er = new ErrorResponse(Response.Status.INTERNAL_SERVER_ERROR, errorMessage);
		return Response.status(Response.Status.INTERNAL_SERVER_ERROR).entity(er).build();
	}

	public abstract String getPublicIp();
}
