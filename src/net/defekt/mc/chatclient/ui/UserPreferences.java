package net.defekt.mc.chatclient.ui;

import java.io.FileInputStream;
import java.io.ObjectInputStream;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import net.defekt.mc.chatclient.protocol.packets.general.clientbound.play.ServerChatMessagePacket.Position;
import net.defekt.mc.chatclient.protocol.packets.general.serverbound.play.ClientResourcePackStatusPacket.Status;

public class UserPreferences implements Serializable {

	private UserPreferences() {
	}

	public enum SkinRule {
		SERVER, MOJANG_API, NONE
	}

	public static class Constants {
		public static final String TRAY_MESSAGES_KEY_ALWAYS = "Always";
		public static final String TRAY_MESSAGES_KEY_MENTION = "When mentioned";
		public static final String TRAY_MESSAGES_KEY_NEVER = "Never";

		public static final int WINDOW_CLOSE_ALWAYS_ASK = 0;
		public static final int WINDOW_CLOSE_TO_TRAY = 1;
		public static final int WINDOW_CLOSE_EXIT = 2;
	}

	private static final long serialVersionUID = 1L;

	protected final List<ServerEntry> servers = Collections.synchronizedList(new ArrayList<ServerEntry>());

	private Status resourcePackBehavior = Status.LOADED;
	private boolean showResourcePackMessages = true;
	private String resourcePackMessage = "[Resource Pack Received: %res]";
	private Position resourcePackMessagePosition = Position.HOTBAR;

	private SkinRule skinFetchRule = SkinRule.SERVER;

	private boolean ignoreKeepAlive = false;
	private boolean sendMCBrand = true;
	private String brand = "vanilla";

	private String trayMessageMode = Constants.TRAY_MESSAGES_KEY_MENTION;
	private boolean trayShowDisconnectMessages = true;
	private int closeMode = Constants.WINDOW_CLOSE_ALWAYS_ASK;

	protected static UserPreferences load() {
		try {
			if (Main.serverFile.exists()) {
				try (ObjectInputStream is = new ObjectInputStream(new FileInputStream(Main.serverFile))) {
					UserPreferences prefs = (UserPreferences) is.readObject();
					return prefs;
				}
			} else
				return new UserPreferences();
		} catch (Exception e) {
			e.printStackTrace();
			return new UserPreferences();
		}
	}

	public List<ServerEntry> getServers() {
		return servers;
	}

	public Status getResourcePackBehavior() {
		return resourcePackBehavior;
	}

	public void setResourcePackBehavior(Status resourcePackBehavior) {
		this.resourcePackBehavior = resourcePackBehavior;
	}

	public boolean isShowResourcePackMessages() {
		return showResourcePackMessages;
	}

	public String getResourcePackMessage() {
		return resourcePackMessage;
	}

	public void setShowResourcePackMessages(boolean showResourcePackMessages) {
		this.showResourcePackMessages = showResourcePackMessages;
	}

	public void setResourcePackMessage(String resourcePackMessage) {
		this.resourcePackMessage = resourcePackMessage;
	}

	public Position getResourcePackMessagePosition() {
		return resourcePackMessagePosition;
	}

	public void setResourcePackMessagePosition(Position resourcePackMessagePosition) {
		this.resourcePackMessagePosition = resourcePackMessagePosition;
	}

	public SkinRule getSkinFetchRule() {
		return skinFetchRule;
	}

	public void setSkinFetchRule(SkinRule skinFetchRule) {
		this.skinFetchRule = skinFetchRule;
	}

	public boolean isIgnoreKeepAlive() {
		return ignoreKeepAlive;
	}

	public boolean isSendMCBrand() {
		return sendMCBrand;
	}

	public String getBrand() {
		return brand;
	}

	public void setIgnoreKeepAlive(boolean ignoreKeepAlive) {
		this.ignoreKeepAlive = ignoreKeepAlive;
	}

	public void setSendMCBrand(boolean sendMCBrand) {
		this.sendMCBrand = sendMCBrand;
	}

	public void setBrand(String brand) {
		this.brand = brand;
	}

	public String getTrayMessageMode() {
		return trayMessageMode;
	}

	public int getCloseMode() {
		return closeMode;
	}

	public void setTrayMessageMode(String trayMessageMode) {
		this.trayMessageMode = trayMessageMode;
	}

	public void setCloseMode(int closeMode) {
		this.closeMode = closeMode;
	}

	public boolean isTrayShowDisconnectMessages() {
		return trayShowDisconnectMessages;
	}

	public void setTrayShowDisconnectMessages(boolean trayShowDisconnectMessages) {
		this.trayShowDisconnectMessages = trayShowDisconnectMessages;
	}
}
