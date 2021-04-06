package net.defekt.mc.chatclient.protocol.data;

import java.io.Serializable;

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

	/**
	 * Constructs status info object
	 * 
	 * @param description server's MOTD
	 * @param online      online players count
	 * @param max         max players
	 * @param version     server's version name
	 * @param protocol    protocol used by server
	 * @param icon        server's icon, or null if none
	 */
	public StatusInfo(String description, int online, int max, String version, int protocol, String icon) {
		this.description = description;
		this.onlinePlayers = online;
		this.maxPlayers = max;
		this.versionName = version;
		this.protocol = protocol;
		this.icon = icon;
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
	 * @return server icon
	 */
	public String getIcon() {
		if (icon == null)
			return null;
		return icon.substring(icon.indexOf(",") + 1);
	}

	public void setIcon(String icon) {
		this.icon = icon;
	}
}
