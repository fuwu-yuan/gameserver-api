package fr.fuwuyuan.gameserverapi.database.dto;

import javax.json.bind.annotation.JsonbProperty;

/**
 * A game server as it is saved in the database.
 * @author julien-beguier
 * @see {@link Fields GameServerDTO.Fields}
 */
public class GameServerDTO {

	/**
	 * An enum corresponding to the {@code 'gamerserver'} database's fields
	 * of the table {@code 'servers'}.
	 * @author julien-beguier
	 */
	public enum Fields {
		ServerId("server_id"),
		Ip("ip"),
		Port("port"),
		Name("name"),
		Description("description"),
		Game("game"),
		GameVersion("game_version"),
		NMaxPlayers("n_max_players"),
		OpenedOn("opened_on"),
		ReadyForShutdown("ready_for_shutdown");

		private String fieldName;

		Fields(String fieldName) {
			this.fieldName = fieldName;
		}

		public String getFieldName() {
			return this.fieldName;
		}
	}

	/**
	 * The unique id for a game server.
	 */
	private String serverId;

	/**
	 * The ip of a game server.
	 */
	private String ip;

	/**
	 * The port of a game server.
	 */
	private int port;

	/**
	 * The name for a game server.
	 */
	private String name;

	/**
	 * The description of a game server.
	 */
	private String description;

	/**
	 * The name of the game of server.
	 */
	private String game;

	/**
	 * The version of the game of server.
	 */
	private String gameVersion;

	/**
	 * The maximum number of players of the game of server.
	 */
	private int nMaxPlayers;

	/**
	 * The date the game server was opened.
	 */
	private String openedOn;

	/**
	 * Show whether or not the game server can be shutdown.
	 */
	private boolean readyForShutdown;

	public GameServerDTO() {
		this.serverId = null;
		this.ip = null;
		this.port = 0;
		this.name = null;
		this.description = null;
		this.game = null;
		this.gameVersion = null;
		this.nMaxPlayers = 0;
		this.openedOn = null;
		this.readyForShutdown = false;
	}

	public GameServerDTO(String serverId, String ip, int port, String serverName, String serverDesc,
			String gameName, String gameVersion, int nMaxPlayers, String openedOn,
			boolean readyForShutdown) {
		this.serverId = serverId;
		this.ip = ip;
		this.port = port;
		this.name = serverName;
		this.description = serverDesc;
		this.game = gameName;
		this.gameVersion = gameVersion;
		this.nMaxPlayers = nMaxPlayers;
		this.openedOn = openedOn;
		this.readyForShutdown = readyForShutdown;
	}

	/**
	 * This method create a {@link GameServerDTO} using the basic properties
	 * given via the input of {@link GameServerController.createGameServer}.
	 * @param serverName as a String
	 * @param serverDesc as a String, can be null
	 * @param gameName as a String
	 * @param gameVersion as a String
	 * @param nMaxPlayers as a int value
	 * @return a newly create {@link GameServerDTO} with its basic properties
	 * set
	 */
	public static GameServerDTO initFromPostInputValues(String serverName,
			String serverDesc, String gameName, String gameVersion, int nMaxPlayers) {
		GameServerDTO gs = new GameServerDTO();
		gs.setName(serverName);
		gs.setDescription(serverDesc);
		gs.setGame(gameName);
		gs.setGameVersion(gameVersion);
		gs.setNMaxPlayers(nMaxPlayers);

		return gs;
	}

	// GETTERS & SETTERS

	/**
	 * The unique id for a game server.
	 */
	@JsonbProperty("server_id")
	public String getServerId() {
		return serverId;
	}

	/**
	 * The unique id for a game server.
	 */
	public void setServerId(String serverId) {
		this.serverId = serverId;
	}

	/**
	 * The ip of a game server.
	 */
	@JsonbProperty("ip")
	public String getIp() {
		return ip;
	}

	/**
	 * The ip of a game server.
	 */
	public void setIp(String ip) {
		this.ip = ip;
	}

	/**
	 * The port of a game server.
	 */
	@JsonbProperty("port")
	public int getPort() {
		return port;
	}

	/**
	 * The port of a game server.
	 */
	public void setPort(int port) {
		this.port = port;
	}

	/**
	 * The name for a game server.
	 */
	@JsonbProperty("name")
	public String getName() {
		return name;
	}

	/**
	 * The name for a game server.
	 */
	public void setName(String serverName) {
		this.name = serverName;
	}

	/**
	 * The description of a game server.
	 */
	@JsonbProperty("description")
	public String getDescription() {
		return description;
	}

	/**
	 * The description of a game server.
	 */
	public void setDescription(String serverDesc) {
		this.description = serverDesc;
	}

	/**
	 * The name of the game of server.
	 */
	@JsonbProperty("game")
	public String getGame() {
		return game;
	}

	/**
	 * The name of the game of server.
	 */
	public void setGame(String gameName) {
		this.game = gameName;
	}

	/**
	 * The version of the game of server.
	 */
	@JsonbProperty("game_version")
	public String getGameVersion() {
		return gameVersion;
	}

	/**
	 * The version of the game of server.
	 */
	public void setGameVersion(String gameVersion) {
		this.gameVersion = gameVersion;
	}

	/**
	 * The maximum number of players of the game of server.
	 */
	@JsonbProperty("n_max_players")
	public int getNMaxPlayers() {
		return nMaxPlayers;
	}

	/**
	 * The maximum number of players of the game of server.
	 */
	public void setNMaxPlayers(int nMaxPlayers) {
		this.nMaxPlayers = nMaxPlayers;
	}

	/**
	 * The date the game server was created.
	 */
	@JsonbProperty("opened_on")
	public String getOpenedOn() {
		return openedOn;
	}

	/**
	 * The date the game server was created.
	 */
	public void setOpenedOn(String openedOn) {
		this.openedOn = openedOn;
	}

	/**
	 * Show whether or not the game server can be shutdown.
	 */
	@JsonbProperty("ready_for_shutdown")
	public boolean getReadyForShutdown() {
		return readyForShutdown;
	}

	/**
	 * Show whether or not the game server can be shutdown.
	 */
	public void setReadyForShutdown(boolean readyForShutdown) {
		this.readyForShutdown = readyForShutdown;
	}

	@Override
	public String toString() {
		StringBuilder sb = new StringBuilder();
		sb.append(GameServerDTO.class.getName()).append('@').append(
				Integer.toHexString(System.identityHashCode(this))).append('[');
		sb.append("ip:port=");
		sb.append(((this.ip == null) ? "<null>" : this.ip));
		sb.append(":");
		sb.append(((this.port == 0) ? "<null>" : this.port));
		sb.append(", ");
		sb.append("name=");
		sb.append(((this.name == null) ? "<null>" : this.name));
		sb.append(", ");
		sb.append("description=");
		sb.append(((this.description == null) ? "<null>" : this.description));
		sb.append(", ");
		sb.append("game:version=");
		sb.append(((this.game == null) ? "<null>" : this.game));
		sb.append(":");
		sb.append(((this.gameVersion == null) ? "<null>" : this.gameVersion));
		sb.append(", ");
		sb.append("nMaxPlayers=");
		sb.append(((this.nMaxPlayers == 0) ? "0=unlimited" : this.nMaxPlayers));
		sb.append(", ");
		sb.append("openedOn=");
		sb.append(((this.openedOn == null) ? "<null>" : this.openedOn));
		sb.append(", ");
		sb.append("readyForShutdown=");
		sb.append(this.readyForShutdown);
		sb.append(']');

		return sb.toString();
	}

	@Override
	public int hashCode() {
		int result = 1;
		result = ((result * 31) + ((this.serverId == null) ? 0 : this.serverId.hashCode()));
		result = ((result * 31) + ((this.ip == null) ? 0 : this.ip.hashCode()));
		result = (result * 17) + this.port;
		result = ((result * 31) + ((this.name == null) ? 0 : this.name.hashCode()));
		result = ((result * 31) + ((this.description == null) ? 0 : this.description.hashCode()));
		result = ((result * 31) + ((this.game == null) ? 0 : this.game.hashCode()));
		result = ((result * 31) + ((this.gameVersion == null) ? 0 : this.gameVersion.hashCode()));
		result = (result * 17) + this.nMaxPlayers;
		result = ((result * 31) + ((this.openedOn == null) ? 0 : this.openedOn.hashCode()));
		result += Boolean.hashCode(this.readyForShutdown);
		return result;
	}

	@Override
	public boolean equals(Object other) {
		if (other == this) {
			return true;
		}
		if ((other instanceof GameServerDTO) == false) {
			return false;
		}
		GameServerDTO s = ((GameServerDTO) other);
		return ((this.ip == s.ip) || ((this.ip != null) && this.ip.equals(s.ip)))
				&& (this.port == s.port)
				&& ((this.name == s.name) || ((this.name != null) && this.name.equals(s.name)))
				&& ((this.description == s.description) || ((this.description != null) && this.description.equals(s.description)))
				&& ((this.game == s.game) || ((this.game != null) && this.game.equals(s.game)))
				&& ((this.gameVersion == s.gameVersion) || ((this.gameVersion != null) && this.gameVersion.equals(s.gameVersion)))
				&& (this.nMaxPlayers == s.nMaxPlayers)
				&& ((this.openedOn == s.openedOn) || ((this.openedOn != null) && this.openedOn.equals(s.openedOn)))
				&& (this.readyForShutdown == s.readyForShutdown);
	}
}
