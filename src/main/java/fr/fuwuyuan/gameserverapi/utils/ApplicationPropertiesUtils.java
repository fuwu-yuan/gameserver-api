package fr.fuwuyuan.gameserverapi.utils;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

import fr.fuwuyuan.gameserverapi.logs.ResponseHandler;

/**
 * This utility class is used to read the {@code application.properties} file
 * located in the root of the class path.</br>Usually:
 * <pre>/WEB-INF/classes/application.properties</pre>
 * @author julien-beguier
 */
public class ApplicationPropertiesUtils {

	private final static String propertyFile = "/application.properties";

	/**
	 * This method reads the {@code application.properties} file located in
	 * the root of the class path.</br>Usually:
	 * <pre>/WEB-INF/classes/application.properties</pre>
	 * @return the loaded property file if the file is found as a
	 * {@link Properties} and {@code null} otherwise
	 * @throws IOException if the file is not found
	 * @see {@link ClassLoader#getResourceAsStream
	 * @see {@link Properties#load(InputStream)
	 */
	public static Properties readPropertiesFile() throws IOException {
		ClassLoader loader = Thread.currentThread().getContextClassLoader();
		InputStream is = null;
		Properties properties = null;

		try {
			is = loader.getResourceAsStream(propertyFile);
			if (is != null) {
				properties = new Properties();
				properties.load(is);
			}
		} catch (IOException ioe) {
			ioe.printStackTrace();
			ResponseHandler.fatal("application.properties file cannot be loaded: " + ioe.getMessage(), true);
		} finally {
			if (is != null)
				is.close();
		}
		return properties;
	}
}
