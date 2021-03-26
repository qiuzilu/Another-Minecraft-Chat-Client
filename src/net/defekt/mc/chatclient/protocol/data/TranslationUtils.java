package net.defekt.mc.chatclient.protocol.data;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.util.HashMap;
import java.util.Map;

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

	protected static String translateKey(String key) {
		return translationKeys.containsKey(key) ? translationKeys.get(key).replace("%s", "\u00A7%s") : key;
	}
}
