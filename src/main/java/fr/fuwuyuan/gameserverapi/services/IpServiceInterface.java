package fr.fuwuyuan.gameserverapi.services;

/**
 * Extends {@link ServiceInterface}
 * <p>
 * This is the ip service interface that describes a method to query a
 * homemade public ip webservice.
 * </p>
 * @author julien-beguier
 * @see {@link IpServiceInterface#getPublicIp}
 */
public interface IpServiceInterface extends ServiceInterface {

	/**
	 * This method send a GET request to simple-ip-ws (homemade public ip webservice)
	 * @return the public IP to connect to the server as a String or null if it fails
	 * @see {@link java.net.HttpURLConnection}
	 */
	public String getPublicIp();
}
