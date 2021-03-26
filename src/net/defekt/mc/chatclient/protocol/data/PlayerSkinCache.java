package net.defekt.mc.chatclient.protocol.data;

import java.awt.image.BufferedImage;
import java.net.URL;
import java.util.ArrayList;
import java.util.Base64;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import javax.imageio.ImageIO;

import com.google.gson.JsonParser;

import net.defekt.mc.chatclient.protocol.MojangAPI;
import net.defekt.mc.chatclient.protocol.io.FallbackHashMap;
import net.defekt.mc.chatclient.protocol.io.IOUtils;
import net.defekt.mc.chatclient.ui.Main;
import net.defekt.mc.chatclient.ui.UserPreferences.SkinRule;

public class PlayerSkinCache {
	private static final FallbackHashMap<UUID, PlayerSkinInfo> skinCache = new FallbackHashMap<UUID, PlayerSkinInfo>() {
		private static final long serialVersionUID = 1L;
		{
			try {
				setDefaultValue(new PlayerSkinInfo(
						ImageIO.read(PlayerSkinCache.class.getResourceAsStream("/resources/steve.png")), ""));
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	};
	private static final List<UUID> pending = new ArrayList<UUID>();

	public static void putSkin(final UUID uid, String texturesO, String username) {
		if (!pending.contains(uid)) {
			pending.add(uid);
			new Thread(new Runnable() {

				@Override
				public void run() {
					try {
						String textures = texturesO;
						SkinRule sr = Main.up.getSkinFetchRule();
						if (sr == SkinRule.MOJANG_API) {

							textures = MojangAPI.getSkin(MojangAPI.getUUID(username));
							if (textures == null)
								return;
						} else if (sr == SkinRule.NONE)
							textures = "default";

						if (textures.equals("default"))
							return;
						String skData = new String(Base64.getDecoder().decode(textures.getBytes()));
						String skUrl = new JsonParser().parse(skData).getAsJsonObject().get("textures")
								.getAsJsonObject().get("SKIN").getAsJsonObject().get("url").getAsString();
						BufferedImage skin = ImageIO.read(new URL(skUrl));
						skinCache.put(uid, new PlayerSkinInfo(skin, skUrl));
					} catch (Exception e) {

					} finally {
						pending.remove(uid);
					}
				}
			}).start();
		}
	}

	public static BufferedImage getHead(UUID id) {
		return IOUtils.trimSkinHead(skinCache.get(id).getImg(), true);
	}

	public static Map<UUID, PlayerSkinInfo> getSkincache() {
		return skinCache;
	}
}
