package net.defekt.mc.chatclient.protocol;

import java.io.IOException;
import java.net.URL;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import net.defekt.mc.chatclient.protocol.io.IOUtils;

/**
 * This class is used to communicate with Mojang API
 * 
 * @author Defective4
 *
 */
public class MojangAPI {
	/**
	 * Get last known UUID of specified player
	 * 
	 * @param username player's username
	 * @return UUID of player
	 * @throws IOException thrown whene there was error communicating with API
	 */
	public static String getUUID(String username) throws IOException {
		String js = new String(
				IOUtils.readFully(new URL("https://api.mojang.com/users/profiles/minecraft/" + username).openStream()));
		if (js.isEmpty())
			return null;

		return new JsonParser().parse(js).getAsJsonObject().get("id").getAsString();
	}

	/**
	 * Get skin URL of specified player
	 * 
	 * @param uuid player's UUID
	 * @return player's skin URL
	 * @throws IOException thrown when there was error communicating with API
	 */
	public static String getSkin(String uuid) throws IOException {
		JsonObject el = new JsonParser()
				.parse(new String(IOUtils.readFully(
						new URL("https://sessionserver.mojang.com/session/minecraft/profile/" + uuid).openStream())))
				.getAsJsonObject();
		if (el.has("error"))
			return null;

		for (JsonElement rel : el.get("properties").getAsJsonArray()) {
			JsonObject obj = rel.getAsJsonObject();
			if (obj.get("name").getAsString().equals("textures"))
				return obj.get("value").getAsString();
		}

		return null;
	}
}
