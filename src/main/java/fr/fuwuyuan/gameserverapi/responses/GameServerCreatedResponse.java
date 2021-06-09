package fr.fuwuyuan.gameserverapi.responses;

import javax.ws.rs.core.Response;

import fr.fuwuyuan.gameserverapi.data.GameServerSlim;

/**
 * Extends {@link GameServerApiResponse}
 * <p>
 * This class is the response to the {@link GameServerService.createGameServer}
 * method when no error occurs. It contains a {@link GameServerSlim} to avoid
 * sending unwanted informations, only 'server_id', 'ip', 'port', 'name',
 * 'description', 'game', 'game_version' and 'n_max_players' and the status is
 * set by the constructor to {@link Response.Status#CREATED}.
 * </p>
 * @author julien-beguier
 * @see {@link fr.fuwuyuan.gameserverapi.services.GameServerService#createGameServer GameServerService.createGameServer}
 * @see {@link GameServerApiResponse}
 * @see {@link GameServerSlim}
 */
public class GameServerCreatedResponse extends GameServerApiResponse {

	public GameServerCreatedResponse(GameServerSlim data) {
		super(Response.Status.CREATED, data);
	}

	public GameServerSlim getData() {
		return (GameServerSlim) data;
	}

	public void setData(GameServerSlim data) {
		this.data = data;
	}
}
