package net.defekt.mc.chatclient.protocol.data;

import java.io.Serializable;

public class StatusInfo implements Serializable {
	private static final long serialVersionUID = -5082589117119994307L;
	private final int onlinePlayers;
	private final int maxPlayers;
	private final int protocol;
	private final String versionName;
	private final String description;
	private String icon;

	public StatusInfo(String description, int online, int max, String version, int protocol, String icon) {
		this.description = description;
		this.onlinePlayers = online;
		this.maxPlayers = max;
		this.versionName = version;
		this.protocol = protocol;
		this.icon = icon;
	}

	public int getOnlinePlayers() {
		return onlinePlayers;
	}

	public int getMaxPlayers() {
		return maxPlayers;
	}

	public int getProtocol() {
		return protocol;
	}

	public String getVersionName() {
		return versionName;
	}

	public String getDescription() {
		return description;
	}

	public String getIcon() {
		if (icon == null)
			return null;
		return icon.substring(icon.indexOf(",") + 1);
	}

	public void setIcon(String icon) {
		this.icon = icon;
	}
}
