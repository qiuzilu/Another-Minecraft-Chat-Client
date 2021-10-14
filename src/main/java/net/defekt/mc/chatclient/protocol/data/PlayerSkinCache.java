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
import net.defekt.mc.chatclient.ui.UserPreferences;
import net.defekt.mc.chatclient.ui.UserPreferences.SkinRule;

/**
 * Static class used to store information about player skins.<br>
 * This class may not be static in future as it is planned for it to be saved on
 * disk.
 * 
 * @see PlayerSkinInfo
 * @author Defective4
 *
 */
public class PlayerSkinCache {
	private PlayerSkinCache() {
	}

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

	/**
	 * Fetch and put player's skin in cache. It will obey {@link UserPreferences}'s
	 * skin rule
	 * 
	 * @param uid       UUID of player
	 * @param texturesO texture URL of player
	 * @param username  username of player
	 */
	public static void putSkin(final UUID uid, final String texturesO, final String username) {
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
//						e.printStackTrace();
					} finally {
						pending.remove(uid);
					}
				}
			}).start();
		}
	}

	/**
	 * Get head texture of player
	 * 
	 * @param id player's UUID
	 * @return image of player's head
	 */
	public static BufferedImage getHead(UUID id) {
		return IOUtils.trimSkinHead(skinCache.get(id).getImg(), true);
	}

	/**
	 * Get skin cache
	 * 
	 * @return skin cache
	 */
	public static Map<UUID, PlayerSkinInfo> getSkincache() {
		return skinCache;
	}
}
