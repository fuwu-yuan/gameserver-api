package fr.fuwuyuan.gameserverapi.services;

/**
 * Extends {@link ServiceInterface}
 * <p>
 * This is the port service interface that describes the methods to
 * manipulate used & available ports. It also contains a public enumeration
 * with possible errors concerning SQL interactions.
 * </p>
 * @author julien-beguier
 * @see {@link PortError}
 * @see {@link PortServiceInterface#getAvailablePort}
 * @see {@link PortServiceInterface#addNewPortToUsedPorts}
 * @see {@link PortServiceInterface#freeUsedPort}
 */
public interface PortServiceInterface extends ServiceInterface {

	/**
	 * This enumeration represents the possible cases that can happen while
	 * manipulating JsonArrays, contained in the {@code 'ports'} table, during
	 * SQL requests.
	 * @author julien-beguier
	 */
	public enum PortError {
		SQL_DATABASE_SESSION_NOT_CONNECTED(-301),
		SQL_ERROR_FETCH_LOG_AND_DO_NOTHING(-302),
		SQL_ERROR_UPDATE_LOG_AND_DO_NOTHING(-303),
		NO_PORT_CORRESPONDING_TO_GIVEN_IP(-311),
		NO_AVAILABLE_PORT_LEFT_ON_GIVEN_IP(-321);

		private int errorCode;

		PortError(int errorCode) {
			this.errorCode = errorCode;
		}

		public int getErrorCode() {
			return this.errorCode;
		}
	}

	/**
	 * This method will try to find the first available port on the given
	 * {@code ip}. It will do so by fetching, if necessary, the
	 * {@link javax.json.JsonArray} from the {@code 'ports'} table.
	 * @param ip as a String for the SQL request
	 * @return the first available port found or a {@link PortError} otherwise
	 * @see {@link PortService#fetchPortsArrays}
	 * @see {@link PortService#resetPortsArrays}
	 * @see {@link PortError}
	 */
	public int getAvailablePort(final String ip);

	/**
	 * This method adds {@code port} to {@code used_ports}
	 * {@link javax.json.JsonArray} and removes {@code port} from
	 * {@code available_ports} {@link javax.json.JsonArray} and then update the
	 * record in the database using the {@code ip}.</br>
	 * It will do so by fetching, if necessary, the {@link javax.json.JsonArray} from
	 * the {@code 'ports'} table.
	 * @param ip as a String for the SQL request
	 * @param port as an int value for the SQL request
	 * @return {@code RET_OK} if successful, a {@link PortError} otherwise
	 * @see {@link PortService#fetchPortsArrays}
	 * @see {@link PortService#updatePortsArrays}
	 * @see {@link fr.fuwuyuan.gameserverapi.utils.JsonUtils#removeIntFromJsonArray JsonUtils.removeIntFromJsonArray}
	 * @see {@link fr.fuwuyuan.gameserverapi.utils.JsonUtils#addIntToJsonArray JsonUtils.addIntToJsonArray}
	 * @see {@link ServiceInterface#RET_OK}
	 * @see {@link PortError}
	 */
	public int addNewPortToUsedPorts(final String ip, final int port);

	/**
	 * This method removes {@code port} from {@code used_ports}
	 * {@link javax.json.JsonArray} and adds {@code port} to
	 * {@code available_ports} {@link javax.json.JsonArray} and then update the
	 * record in the database using the {@code ip}.</br>
	 * It will do so by fetching, if necessary, the {@link javax.json.JsonArray} from
	 * the {@code 'ports'} table.
	 * @param ip as a String for the SQL request
	 * @param port as an int value for the SQL request
	 * @return {@code RET_OK} if successful, a {@link PortError} otherwise
	 * @see {@link PortService#fetchPortsArrays}
	 * @see {@link PortService#updatePortsArrays}
	 * @see {@link fr.fuwuyuan.gameserverapi.utils.JsonUtils#removeIntFromJsonArray JsonUtils.removeIntFromJsonArray}
	 * @see {@link fr.fuwuyuan.gameserverapi.utils.JsonUtils#addIntToJsonArray JsonUtils.addIntToJsonArray}
	 * @see {@link ServiceInterface#RET_OK}
	 * @see {@link PortError}
	 */
	public int freeUsedPort(final String ip, final int port);
}
