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
	private final static String INFO_LEVEL_PREFIX = "[INFO]";
	private final static String ERROR_LEVEL_PREFIX = "[ERROR]";
	private final static String FATAL_LEVEL_PREFIX = "[FATAL]";

	/**
	 * Simply print on {@link System#out} the message formatted as follows:</br>
	 * <pre>[***.***.***.***] [INFO] >> message</pre>
	 * @param callerIp as a String
	 * @param message as a String
	 */
	public void incoming(final String callerIp, final String message) {
		info("[" + callerIp + "] " + INFO_LEVEL_PREFIX + INCOMING_PREFIX + message, false);
	}

	/**
	 * Simply print on {@link System#out} 20X messages and on {@link System#err}
	 * 40X & 50X messages. Print format is as follows:</br>
	 * <pre>[***.***.***.***] [INFO]/[ERROR]>> message</pre>
	 * @param callerIp as a String
	 * @param response as a {@link Response}
	 * @return the unmodified response object
	 */
	public Response outgoing(final String callerIp, final Response response) {
		Jsonb jsonB = JsonbBuilder.create();
		String sJson = jsonB.toJson(response.getEntity());

		// If the response status code is 20X (not an error)
		if (response.getStatus() / 100 == 2)
			info("[" + callerIp + "] " + INFO_LEVEL_PREFIX + OUTGOING_PREFIX + sJson, false);
		else
			error("[" + callerIp + "] " + ERROR_LEVEL_PREFIX + OUTGOING_PREFIX + sJson, false);
		return response;
	}

	/**
	 * Log {@code message} on {@link System#out}.</br>
	 * If {@code format} is {@code true}, this method will print with level before
	 * the message as follows:
	 * <pre>[INFO] message</pre>
	 * Otherwise if {@code format} id {@code false}:
	 * <pre>message</pre>
	 * @param message as a String
	 * @param format as a boolean
	 */
	public static void info(final String message, final boolean format) {
		if (format) {
			System.out.println(INFO_LEVEL_PREFIX + " " + message);
		} else {
			System.out.println(message);
		}
	}

	/**
	 * Log {@code message} on {@link System#err}.
	 * If {@code format} is {@code true}, this method will print with level before
	 * the message as follows:
	 * <pre>[ERROR] message</pre>
	 * Otherwise if {@code format} id {@code false}:
	 * <pre>message</pre>
	 * @param message as a String
	 * @param format as a boolean
	 */
	public static void error(final String message, final boolean format) {
		if (format) {
			System.err.println(ERROR_LEVEL_PREFIX + " " + message);
		} else {
			System.err.println(message);
		}
	}

	/**
	 * Log {@code message} on {@link System#err}.
	 * If {@code format} is {@code true}, this method will print with level before
	 * the message as follows:
	 * <pre>[ERROR] message</pre>
	 * Otherwise if {@code format} id {@code false}:
	 * <pre>message</pre>
	 * @param message as a String
	 * @param format as a boolean
	 */
	public static void fatal(final String message, final boolean format) {
		if (format) {
			System.err.println(FATAL_LEVEL_PREFIX + " " + message);
		} else {
			System.err.println(message);
		}
	}
}
