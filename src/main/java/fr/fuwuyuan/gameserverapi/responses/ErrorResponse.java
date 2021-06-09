package fr.fuwuyuan.gameserverapi.responses;

import javax.ws.rs.core.Response;

/**
 * Extends {@link GameServerApiResponse}
 * <p>
 * This is the error response class. It contains an error String giving an
 * explanation of the error to the client or the developer using this api.
 * </p>
 * @author julien-beguier
 * @see {@link Response.Status}
 */
public class ErrorResponse extends GameServerApiResponse {

	private String error;

	public ErrorResponse() {
		super();
		this.error = null;
	}

	public ErrorResponse(Response.Status status, String error) {
		super(status, null);
		this.error = error;
	}

	public String getError() {
		return error;
	}

	public void setError(String error) {
		this.error = error;
	}
}
