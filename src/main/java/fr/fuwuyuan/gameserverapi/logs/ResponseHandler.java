package fr.fuwuyuan.gameserverapi.logs;

import javax.json.bind.Jsonb;
import javax.json.bind.JsonbBuilder;
import javax.ws.rs.core.Response;

/**
 * This a simple logger like class only used to print the incoming &
 * outgoing messages. 20X messages are printed on {@link System#out}
 * while errors (40X & 50X) are printed on {@link System#err}.
 * @author julien-beguier
 */
public class ResponseHandler {

	private final String INCOMING_PREFIX = " >> ";
	private final String OUTGOING_PREFIX = " << ";

	/**
	 * Simply print on {@link System#out} the message formatted as follows:</br>
	 * <pre>[***.***.***.***] >> message</pre>
	 * @param callerIp as a String
	 * @param message as a String
	 */
	public void incoming(final String callerIp, final String message) {
		info("[" + callerIp + "]" + INCOMING_PREFIX + message);
	}

	/**
	 * Simply print on {@link System#out} 20X messages and on {@link System#err}
	 * 40X & 50X messages. Print format is as follows:</br>
	 * <pre>[***.***.***.***] >> message</pre>
	 * @param callerIp as a String
	 * @param response as a {@link Response}
	 * @return the unmodified response object
	 */
	public Response outgoing(final String callerIp, final Response response) {
		Jsonb jsonB = JsonbBuilder.create();
		String sJson = jsonB.toJson(response.getEntity());

		// If the response status code is 20X (not an error)
		if (response.getStatus() / 100 == 2)
			info("[" + callerIp + "]" + OUTGOING_PREFIX + sJson);
		else
			error("[" + callerIp + "]" + OUTGOING_PREFIX + sJson);
		return response;
	}

	/**
	 * Log {@code message} on {@link System#out}
	 * @param message as a String
	 */
	public static void info(final String message) {
		System.out.println(message);
	}

	/**
	 * Log {@code message} on {@link System#err}
	 * @param message as a String
	 */
	public static void error(final String message) {
		System.err.println(message);
	}
}
