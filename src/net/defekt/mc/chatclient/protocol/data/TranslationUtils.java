package net.defekt.mc.chatclient.protocol.data;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import net.defekt.mc.chatclient.protocol.ProtocolNumber;
import net.defekt.mc.chatclient.protocol.packets.PacketFactory;

/**
 * This class contains translation keys used by Minecraft chat messages.<br>
 * They are used in parsing of chat messages
 * 
 * @see ChatMessage
 * @author Defective4
 *
 */
@SuppressWarnings("serial")
public class TranslationUtils {
	private TranslationUtils() {
	}

	private static final Map<String, String> translationKeys = new HashMap<String, String>() {
		{
			try (BufferedReader br = new BufferedReader(
					new InputStreamReader(TranslationUtils.class.getResourceAsStream("/resources/en_us.lang")))) {
				String line;
				while ((line = br.readLine()) != null) {
					if (line.contains("=") && line.split("=").length > 1) {
						String[] ags = line.split("=");
						put(ags[0], ags[1]);
					}
				}

				br.close();
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	};

	private static final Map<Integer, Map<Integer, ItemInfo>> items = new HashMap<Integer, Map<Integer, ItemInfo>>() {
		{
			List<Integer> protocols = new ArrayList<Integer>();
			Map<Integer, Integer> protocolBinds = PacketFactory.getProtocolBinds();
			for (ProtocolNumber protocol : ProtocolNumber.values()) {
				int protocolNum = protocol.protocol;
				if (protocolBinds.containsKey(protocolNum))
					protocolNum = protocolBinds.get(protocolNum);
				if (!protocols.contains(protocolNum))
					protocols.add(protocolNum);
			}

			for (int protocol : protocols) {
				ProtocolNumber pNum = ProtocolNumber.getForNumber(protocol);
				try {
					HashMap<Integer, ItemInfo> infs = new HashMap<>();
					String pName = pNum.name;
					JsonArray el = new JsonParser()
							.parse(new InputStreamReader(
									TranslationUtils.class.getResourceAsStream("/resources/items/" + pName + ".json")))
							.getAsJsonArray();
					for (JsonElement elem : el) {
						JsonObject job = elem.getAsJsonObject();
						JsonObject items = job.get("items").getAsJsonObject().get("item").getAsJsonObject();
						for (Entry<String, JsonElement> item : items.entrySet()) {
							String itemS = item.getKey();
							JsonObject itemData = item.getValue().getAsJsonObject();
							int itemID = itemData.get("numeric_id").getAsInt();
							String itemName = itemS;
							if (itemData.has("name")) {
								itemName = translateKey("item." + itemData.get("name").getAsString() + ".name");
								if (itemName.equals(itemData.get("name").getAsString()))
									if (itemData.has("display_name"))
										itemName = itemData.get("display_name").getAsString();
							} else if (itemData.has("display_name"))
								itemName = itemData.get("display_name").getAsString();
							infs.put(itemID, new ItemInfo(itemName, itemS));
						}
					}
					put(protocol, infs);

				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		}
	};

	public static ItemInfo getItemForID(int id, int protocol) {
		if (PacketFactory.getProtocolBinds().containsKey(protocol))
			protocol = PacketFactory.getProtocolBinds().get(protocol);
		ItemInfo none = new ItemInfo("" + id, "" + id);

		if (items.containsKey(protocol)) {
			Map<Integer, ItemInfo> itemMap = items.get(protocol);
			return itemMap.containsKey(id) ? itemMap.get(id) : none;
		}
		return none;
	}

	/**
	 * Translate a key
	 * 
	 * @param key key
	 * @return translated string
	 */
	public static String translateKey(String key) {
		return translationKeys.containsKey(key) ? translationKeys.get(key).replace("%s", "\u00A7%s") : key;
	}
}
