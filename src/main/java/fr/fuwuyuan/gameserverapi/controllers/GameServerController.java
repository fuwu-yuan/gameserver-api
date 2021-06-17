package fr.fuwuyuan.gameserverapi.controllers;

import javax.json.JsonObject;
import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.Consumes;
import javax.ws.rs.DELETE;
import javax.ws.rs.GET;
import javax.ws.rs.HeaderParam;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.Response;

import fr.fuwuyuan.gameserverapi.logs.ResponseHandler;
import fr.fuwuyuan.gameserverapi.services.GameServerService;
import fr.fuwuyuan.gameserverapi.services.GameServerServiceInterface;

/**
 * This is the controller to manipulate game server rooms.
 * @author julien-beguier
 */
@Path("/room")
@Consumes("application/json")
@Produces("application/json")
public class GameServerController {

	private GameServerServiceInterface service = new GameServerService();
	private ResponseHandler rh = new ResponseHandler();

	@POST
	public Response createGameServer(@Context final HttpServletRequest requestContext,
			@HeaderParam("auth_key") final String authKey, final JsonObject postInput) {
		String callerIp = requestContext.getRemoteAddr();

		rh.incoming(callerIp, "POST createGameServer : " + (postInput == null ? "<input null>" : postInput.toString()));
		return rh.outgoing(callerIp, this.service.createGameServer(authKey, postInput));
	}

	@GET
	public Response getGameServers(@Context final HttpServletRequest requestContext,
			@HeaderParam("auth_key") final String authKey) {
		String callerIp = requestContext.getRemoteAddr();

		rh.incoming(callerIp, "GET getGameServers");
		return rh.outgoing(callerIp, this.service.getGameServers(authKey));
	}

	@GET
	@Path("/{server-id}")
	public Response getGameServerById(@Context final HttpServletRequest requestContext,
			@HeaderParam("auth_key") final String authKey,
			@PathParam("server-id") final String serverId) {
		String callerIp = requestContext.getRemoteAddr();

		rh.incoming(callerIp, "GET getGameServerById : " + serverId);
		return rh.outgoing(callerIp, this.service.getGameServerById(authKey, serverId));
	}

	@GET
	@Path("/{game-name}/{game-version}")
	public Response getGameServerByGameNameAndGameVersion(@Context final HttpServletRequest requestContext,
			@HeaderParam("auth_key") final String authKey,
			@PathParam("game-name") final String gameName,
			@PathParam("game-version") final String gameVersion) {
		String callerIp = requestContext.getRemoteAddr();

		rh.incoming(callerIp, "GET getGameServerByGameNameAndGameVersion : " + gameName + ":" + gameVersion);
		return rh.outgoing(callerIp, this.service.getGameServerByGameNameAndGameVersion(authKey, gameName, gameVersion));
	}

	@DELETE
	@Path("/{server-id}")
	public Response shutdownGameServer(@Context final HttpServletRequest requestContext,
			@HeaderParam("auth_key") final String authKey,
			@PathParam("server-id") final String serverId) {
		String callerIp = requestContext.getRemoteAddr();

		rh.incoming(callerIp, "DELETE shutdownGameServer : " + serverId);
		return rh.outgoing(callerIp, this.service.shutdownGameServer(authKey, serverId));
	}
}
