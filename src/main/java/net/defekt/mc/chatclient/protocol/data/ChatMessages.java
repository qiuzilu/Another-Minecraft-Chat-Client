package net.defekt.mc.chatclient.protocol.data;

import java.util.Map.Entry;
import java.util.concurrent.atomic.AtomicReference;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import net.defekt.mc.chatclient.protocol.ClientListener;

/**
 * Class containing methods to make parsing chat messages easier
 * 
 * @see ClientListener
 * @see ChatColor
 * @author Defective4
 *
 */
public class ChatMessages {

	private static final String pChar = "\u00A7";

	private ChatMessages() {
	}

	/**
	 * Parse JSON chat message
	 * 
	 * @param json chat message
	 * @return parsed text
	 */
	public static String parse(String json) {
		json = json.replace(pChar + "k", "").replace(pChar + "l", "").replace(pChar + "m", "").replace(pChar + "n", "");
		try {
			JsonElement el = new JsonParser().parse(json);
			JsonObject root;
			if (el.isJsonPrimitive())
				return el.getAsJsonPrimitive().getAsString();

			root = el.getAsJsonObject();

			AtomicReference<String> strRef = new AtomicReference<String>("");

			if (root.has("text")) {
				strRef.set(root.get("text").getAsString());
				root.remove("text");
			}

			String colorAppend = "";
			if (root.has("color"))
				colorAppend = pChar + ChatColor.translateColorName(root.get("color").getAsString());

			strRef.set(colorAppend + strRef.get());

			recursiveParse(root, strRef);

			if (strRef.get().contains(pChar) && root.has("translate"))
				strRef.set(root.get("translate").getAsString());
			return strRef.get();
		} catch (Exception e) {
			return json;
		}
	}

	private static void recursiveParse(JsonElement ob, AtomicReference<String> str) {

		if ((ob.isJsonPrimitive()) && !ob.getAsString().isEmpty())
			if (str.get().contains(pChar + "%s")) {
				str.set(str.get().replaceFirst(pChar + "%s", ob.getAsString()));
			} else {
				str.set(str.get() + ob.getAsString());
			}

		if (ob.isJsonArray())
			for (JsonElement el : ob.getAsJsonArray())
				recursiveParse(el, str);

		if (ob.isJsonObject()) {
			JsonObject obj = ob.getAsJsonObject();
			for (Entry<String, JsonElement> entry : obj.entrySet()) {

				String key = entry.getKey();
				JsonElement value = entry.getValue();

				switch (key) {
					case "translate": {

						String translated = TranslationUtils.translateKey(value.getAsString());
						str.set(str.get() + translated);
						break;
					}

					case "text": {
						if (!value.getAsString().isEmpty())
							if (str.get().contains(pChar + "%s"))
								str.set(str.get().replaceFirst(pChar + "%s", value.getAsString()));
							else {

								String colorAppend = "";
								if (obj.has("color"))
									colorAppend = pChar + ChatColor.translateColorName(obj.get("color").getAsString());
								else
									colorAppend = pChar + "f";

								str.set(str.get() + colorAppend + value.getAsString());
							}
						break;
					}
					case "with": {
						recursiveParse(value, str);
						break;
					}
					case "extra": {
						recursiveParse(value, str);
						break;
					}
					default: {
						break;
					}

				}
			}
		}
	}

	/**
	 * Remove colors from message.<br>
	 * This method removes all color and formatting codes from message.
	 * 
	 * @param message message to remove colors from
	 * @return colorless message
	 */
	public static String removeColors(String message) {
		StringBuilder colorless = new StringBuilder();
		char[] chs = message.toCharArray();
		for (int x = 0; x < chs.length; x++)
			if (chs[x] == pChar.charAt(0))
				x++;
			else
				colorless.append(chs[x]);

		return colorless.toString();
	}
}
