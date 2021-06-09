package fr.fuwuyuan.gameserverapi.services;

/**
 * Extends {@link ServiceInterface}
 * <p>
 * This is the server id service interface that describes a method to determine
 * the next server id when creating a game server.
 * </p>
 * @author julien-beguier
 * @see {@link ServerIdServiceInterface#getNextServerId}
 */
public interface ServerIdServiceInterface extends ServiceInterface {

	/**
	 * This method query the database to return the next available server id as
	 * follows: <pre>MAX(server_id) + 1</pre>
	 * @return the next available server id
	 * @see {@link fr.fuwuyuan.gameserverapi.database.DatabaseSession DatabaseSession}
	 */
	public String getNextServerId();
}
