package net.defekt.mc.chatclient.protocol.data;

import java.io.Serializable;
import java.util.UUID;

public class PlayerInfo implements Serializable {
	private static final long serialVersionUID = -1040109725883178962L;

	private final String name;
	private final String displayName;
	private String texture;
	private final int ping;
	private final UUID uuid;

	public PlayerInfo(String name, String texture, String displayName, int ping, UUID uuid) {
		this.displayName = displayName;
		this.name = name;
		this.texture = texture;
		this.ping = ping;
		this.uuid = uuid;
	}

	public String getName() {
		return name;
	}

	public String getDisplayName() {
		return displayName;
	}

	public String getTexture() {
		return texture != null && !texture.isEmpty() ? texture : "default";
	}

	public int getPing() {
		return ping;
	}

	public UUID getUuid() {
		return uuid;
	}

	public void setTexture(String texture) {
		this.texture = texture;
	}
}
