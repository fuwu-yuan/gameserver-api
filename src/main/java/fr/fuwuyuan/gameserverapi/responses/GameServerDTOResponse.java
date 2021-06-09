package fr.fuwuyuan.gameserverapi.responses;

import javax.ws.rs.core.Response;

import fr.fuwuyuan.gameserverapi.database.dto.GameServerDTO;
import fr.fuwuyuan.gameserverapi.services.GameServerService;

/**
 * Extends {@link GameServerApiResponse}
 * <p>
 * This class is the response to the {@link GameServerService.getGameServeById}
 * method when no error occurs. It contains a {@link GameServerDTO} and contains
 * all properties, 'server_id', 'ip', 'port', 'name', 'description', 'game',
 * 'game_version', 'n_max_players', 'opened_on' and 'ready_for_shutdown'. The
 * status is set by the constructor to {@link Response.Status#OK}.
 * </p>
 * @author julien-beguier
 * @see {@link fr.fuwuyuan.gameserverapi.services.GameServerService#getGameServeById GameServerService.getGameServeById}
 * @see {@link GameServerApiResponse}
 * @see {@link GameServerDTO}
 */
public class GameServerDTOResponse extends GameServerApiResponse {

	public GameServerDTOResponse(GameServerDTO data) {
		super(Response.Status.OK, data);
	}

	public GameServerDTO getData() {
		return (GameServerDTO) data;
	}

	public void setData(GameServerDTO data) {
		this.data = data;
	}
}
