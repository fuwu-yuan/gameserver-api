package fr.fuwuyuan.gameserverapi.utils;

import java.io.StringReader;
import java.util.ArrayList;
import java.util.List;

import javax.json.Json;
import javax.json.JsonArray;
import javax.json.JsonReader;

/**
 * This utility class is used to manipulate content of {@link JsonArray}.
 * @author julien-beguier
 * @see {@link JsonArray}
 */
public class JsonUtils {

	/**
	 * This method adds an int value {@code n} inside a
	 * {@link javax.json.JsonArray} of int value.</br>
	 * Can't find a better way but to loop and build another
	 * {@link javax.json.JsonArray} and then add the new value {@code n} at
	 * the end.</br></br>
	 * I know this is ugly, please don't judge me.
	 * @param jsonArray as a {@linkjavax.json.JsonArray}
	 * @param n as an int value
	 * @return a clone of {@code jsonArray} with {@code n} added as last
	 * element in the new {@link javax.json.JsonArray}
	 * @see {@link Json#createReader(java.io.InputStream)}
	 * @see {@link JsonReader#readArray}
	 */
	public static JsonArray addIntToJsonArray(JsonArray jsonArray, int n) {

		StringBuilder sb = new StringBuilder();
		sb.append('[');
		for (int i = 0; i < jsonArray.size(); i++) {
			sb.append(jsonArray.getInt(i));
			sb.append(',');
		}
		sb.append(n);
		sb.append(']');

		JsonReader jsonReader = Json.createReader(new StringReader(sb.toString()));
		JsonArray newJsonArray = jsonReader.readArray();
		jsonReader.close();

		return newJsonArray;
	}

	/**
	 * This method removes an int value {@code n} from a
	 * {@link javax.json.JsonArray} of int value.</br>
	 * Can't find a better way but to loop and build another
	 * {@link javax.json.JsonArray} with a condition to exclude the int value
	 * {@code n}. I understand this will exclude all occurrence of {@code n}
	 * inside {@code jsonArray} but in my case, it's fine.</br></br>
	 * I know this is ugly, please don't judge me.
	 * @param jsonArray as a {@link javax.json.JsonArray}
	 * @param n as an int value
	 * @return a clone of {@code jsonArray} with {@code n} removed in the
	 * new {@link javax.json.JsonArray}
	 * @see {@link Json#createReader(java.io.InputStream)}
	 * @see {@link JsonReader#readArray}
	 */
	public static JsonArray removeIntFromJsonArray(JsonArray jsonArray, int n) {

		List<Integer> list = new ArrayList<Integer>();
		for (int i = 0; i < jsonArray.size(); i++) {
			list.add(jsonArray.getInt(i));
		}

		StringBuilder sb = new StringBuilder();
		sb.append('[');
		for (Integer integer : list) {
			if (integer.intValue() != n) {
				sb.append(integer.intValue());
				sb.append(',');
			}
		}
		sb.append(']');

		JsonReader jsonReader = Json.createReader(new StringReader(sb.toString().replaceAll(",]", "]")));
		JsonArray newJsonArray = jsonReader.readArray();
		jsonReader.close();

		return newJsonArray;
	}
}
