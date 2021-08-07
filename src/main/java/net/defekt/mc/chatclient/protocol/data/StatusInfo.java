package net.defekt.mc.chatclient.protocol.data;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import net.defekt.mc.chatclient.protocol.MinecraftStat;

/**
 * Stores information about server's status
 * 
 * @see MinecraftStat
 * @author Defective4
 *
 */
public class StatusInfo implements Serializable {
	private static final long serialVersionUID = -5082589117119994307L;
	private final int onlinePlayers;
	private final int maxPlayers;
	private final int protocol;
	private final String versionName;
	private final String description;
	private String icon;
	private String modType;
	private List<ModInfo> modList;

	/**
	 * Constructs status info object
	 * 
	 * @param description server's MOTD
	 * @param online      online players count
	 * @param max         max players
	 * @param version     server's version name
	 * @param protocol    protocol used by server
	 * @param icon        server's icon, or null if none
	 * @param modType     server's mod loader type if present, else it should be
	 *                    null.
	 * @param modList     server's mods list if present, else it should be null, or
	 *                    empty.
	 */
	public StatusInfo(String description, int online, int max, String version, int protocol, String icon,
			String modType, List<ModInfo> modList) {
		this.description = description;
		this.onlinePlayers = online;
		this.maxPlayers = max;
		this.versionName = version;
		this.protocol = protocol;
		this.icon = icon;
		this.modType = modType;
		this.modList = modList == null ? new ArrayList<ModInfo>() : modList;
	}

	/**
	 * Get online players count
	 * 
	 * @return online players count
	 */
	public int getOnlinePlayers() {
		return onlinePlayers;
	}

	/**
	 * Get max players
	 * 
	 * @return max players
	 */
	public int getMaxPlayers() {
		return maxPlayers;
	}

	/**
	 * Get server's protocol
	 * 
	 * @return server's protocol
	 */
	public int getProtocol() {
		return protocol;
	}

	/**
	 * Get server's version name
	 * 
	 * @return version name
	 */
	public String getVersionName() {
		return versionName;
	}

	/**
	 * Get server's motd
	 * 
	 * @return server's motd
	 */
	public String getDescription() {
		return description;
	}

	/**
	 * Get server icon
	 * 
	 * @return server icon icon encoded as Base64 string
	 */
	public String getIcon() {
		if (icon == null)
			return null;
		return icon.substring(icon.indexOf(",") + 1);
	}

	/**
	 * Set server icon
	 * 
	 * @param icon server icon encoded as Base64 string
	 */
	public void setIcon(String icon) {
		this.icon = icon;
	}

	/**
	 * @return return server's mod loader type.
	 */
	public String getModType() {
		return modType;
	}

	/**
	 * @return return server's mods list.
	 */
	public List<ModInfo> getModList() {
		return modList;
	}
}
