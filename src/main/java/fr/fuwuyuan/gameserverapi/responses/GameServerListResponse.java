package fr.fuwuyuan.gameserverapi.responses;

import java.util.List;

import javax.ws.rs.core.Response;

import fr.fuwuyuan.gameserverapi.data.GameServerSlim;

/**
 * Extends {@link GameServerApiResponse}
 * <p>
 * This class is the response to the {@link GameServerService.getGameServers} &
 * {@link GameServerService.getGameServerByGameNameAndGameVersion} methods when
 * no error occurs. It contains a list of {@link GameServerSlim} to avoid sending
 * unwanted informations, only 'server_id', 'ip', 'port', 'name', 'description',
 * 'game', 'game_version' and 'n_max_players'. The status is set by the
 * constructor to {@link Response.Status#OK}.
 * </p>
 * @author julien-beguier
 * @see {@link fr.fuwuyuan.gameserverapi.services.GameServerService#getGameServers GameServerService.getGameServers}
 * @see {@link fr.fuwuyuan.gameserverapi.services.GameServerService#getGameServerByGameNameAndGameVersion GameServerService.getGameServerByGameNameAndGameVersion}
 * @see {@link GameServerApiResponse}
 * @see {@link GameServerSlim}
 */
public class GameServerListResponse extends GameServerApiResponse {

	public GameServerListResponse(List<GameServerSlim> data) {
		super(Response.Status.OK, data);
	}

	@SuppressWarnings("unchecked")
	public List<GameServerSlim> getData() {
		return (List<GameServerSlim>) data;
	}

	public void setData(List<GameServerSlim> data) {
		this.data = data;
	}
}
