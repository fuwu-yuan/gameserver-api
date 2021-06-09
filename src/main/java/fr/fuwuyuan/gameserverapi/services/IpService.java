package fr.fuwuyuan.gameserverapi.services;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.StringReader;
import java.net.HttpURLConnection;
import java.net.URL;

import javax.json.Json;
import javax.json.JsonObject;
import javax.json.JsonReader;

/**
 * Extends {@link AbstractIpService}
 * <p>
 * This is the ip service class which implements the method to query the
 * public ip webservice.
 * </p>
 * @author julien-beguier
 */
public class IpService extends AbstractIpService {

	private final String SIMPLE_IP_WS_URL = "http://julienbeguier.fr:7380/";

	private final String JSON_IP = "ip";

	/**
	 * {@inheritDoc}
	 */
	@Override
	public String getPublicIp() {
		String httpGetResponse = null;
		String ip = null;
		URL url = null;
		HttpURLConnection con = null;

		try {
			url = new URL(SIMPLE_IP_WS_URL);
			con = (HttpURLConnection) url.openConnection();
			con.setRequestMethod("GET");

			BufferedReader br = new BufferedReader(new InputStreamReader(con.getInputStream(), "utf-8"));
			StringBuilder response = new StringBuilder();
			String responseLine = null;
			while ((responseLine = br.readLine()) != null) {
				response.append(responseLine.trim());
			}
			br.close();
			con.disconnect();
			httpGetResponse = response.toString();

			JsonReader jsonReader = Json.createReader(new StringReader(httpGetResponse));
			JsonObject ipObject = jsonReader.readObject();
			jsonReader.close();

			ip = ipObject.getString(JSON_IP);
		} catch (IOException e) {
			e.printStackTrace();
		}
		return ip;
	}
}
