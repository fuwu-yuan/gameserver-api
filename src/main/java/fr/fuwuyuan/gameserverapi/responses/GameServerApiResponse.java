package fr.fuwuyuan.gameserverapi.responses;

import javax.ws.rs.core.Response;

/**
 * This is the base class for response. It only contains the {@code statusCode},
 * the {@code statusReason} and a simple {@link Object} {@code data} that contains
 * either a list of {@link GameServerSlim}, a {@link GameServerDTO}, or null.</br>
 * Other Response-like class extends this class.
 * @author julien-beguier
 * @see {@link Response.Status}
 */
public class GameServerApiResponse {

	protected int statusCode;
	protected String statusReason;
	protected Object data;

	public GameServerApiResponse() {
		this.statusCode = 0;
		this.statusReason = null;
		this.data = null;
	}

	public GameServerApiResponse(Response.Status status, Object data) {
		this.statusCode = status.getStatusCode();
		this.statusReason = status.getReasonPhrase();
		this.data = data;
	}

	public int getStatusCode() {
		return statusCode;
	}

	public void setStatusCode(int statusCode) {
		this.statusCode = statusCode;
	}

	public String getStatusReason() {
		return statusReason;
	}

	public void setStatusReason(String statusReason) {
		this.statusReason = statusReason;
	}

	public void setStatus(Response.Status status) {
		this.statusCode = status.getStatusCode();
		this.statusReason = status.getReasonPhrase();
	}
}
