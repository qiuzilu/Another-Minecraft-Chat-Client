package net.defekt.mc.chatclient.ui;

import java.awt.Color;
import java.io.FileInputStream;
import java.io.ObjectInputStream;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import net.defekt.mc.chatclient.protocol.packets.general.clientbound.play.ServerChatMessagePacket.Position;
import net.defekt.mc.chatclient.protocol.packets.general.serverbound.play.ClientResourcePackStatusPacket.Status;
import net.defekt.mc.chatclient.ui.swing.SwingUtils;

/**
 * Class containing user's preferences.<br>
 * It is intended to be saved on disk every time the application exits.
 * 
 * @author Defective4
 *
 */
@SuppressWarnings("javadoc")
public class UserPreferences implements Serializable {

	private static final long serialVersionUID = 5064975536053236721L;

	private UserPreferences() {
	}

	/**
	 * Skin rules are used to adjust skin cache behavior
	 * 
	 * @author Defective4
	 *
	 */
	public static enum SkinRule {
		/**
		 * Indicates that skins should be fetched from server
		 */
		SERVER,
		/**
		 * Indicates that skins should be downloaded using Mojang API
		 */
		MOJANG_API,
		/**
		 * Skins won't be fetched
		 */
		NONE
	}

	public static class Constants {
		public static final String TRAY_MESSAGES_KEY_ALWAYS = "Always";
		public static final String TRAY_MESSAGES_KEY_MENTION = "When mentioned";
		public static final String TRAY_MESSAGES_KEY_NEVER = "Never";

		public static final int WINDOW_CLOSE_ALWAYS_ASK = 0;
		public static final int WINDOW_CLOSE_TO_TRAY = 1;
		public static final int WINDOW_CLOSE_EXIT = 2;
	}

	public static final ColorPreferences defaultColorPreferences = new ColorPreferences();

	protected final List<ServerEntry> servers = Collections.synchronizedList(new ArrayList<ServerEntry>());

	public static class ColorPreferences implements Serializable {

		private static final long serialVersionUID = 1L;
		private String colorEnabledButton = "6f6f6f";
		private String colorEnabledHoverButton = "7c86be";
		private String colorDisabledButton = "2d2d2d";
		private String colorText = SwingUtils.getHexRGB(Color.white);
		private String disabledColorText = SwingUtils.getHexRGB(Color.lightGray);

		public ColorPreferences() {
		}

		public String getColorEnabledButton() {
			return colorEnabledButton;
		}

		public String getColorEnabledHoverButton() {
			return colorEnabledHoverButton;
		}

		public String getColorDisabledButton() {
			return colorDisabledButton;
		}

		public void setColorEnabledButton(String colorEnabledButton) {
			this.colorEnabledButton = colorEnabledButton;
		}

		public void setColorEnabledHoverButton(String colorEnabledHoverButton) {
			this.colorEnabledHoverButton = colorEnabledHoverButton;
		}

		public void setColorDisabledButton(String colorDisabledButton) {
			this.colorDisabledButton = colorDisabledButton;
		}

		public String getColorText() {
			return colorText;
		}

		public String getDisabledColorText() {
			return disabledColorText;
		}

		public void setColorText(String colorText) {
			this.colorText = colorText;
		}

		public void setDisabledColorText(String disabledColorText) {
			this.disabledColorText = disabledColorText;
		}
	}

	private ColorPreferences colorPreferences = new ColorPreferences();
	private List<String> lastUsernames = new ArrayList<String>();
	private transient boolean usernameAlertSeen = false;

	private Status resourcePackBehavior = Status.LOADED;
	private boolean showResourcePackMessages = true;
	private String resourcePackMessage = "[Resource Pack Received: %res]";
	private Position resourcePackMessagePosition = Position.HOTBAR;

	private SkinRule skinFetchRule = SkinRule.SERVER;

	private boolean ignoreKeepAlive = false;
	private int additionalPing = 0;
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

	public ColorPreferences getColorPreferences() {
		if (colorPreferences == null)
			colorPreferences = new ColorPreferences();
		return colorPreferences;
	}

	public int getAdditionalPing() {
		return additionalPing;
	}

	public void setAdditionalPing(int additionalPing) {
		this.additionalPing = additionalPing;
	}

	public void putUserName(String username) {
		if (lastUsernames.contains(username))
			lastUsernames.remove(username);
		if (!lastUsernames.contains(username)) {
			lastUsernames.add(" ");
			for (int x = lastUsernames.size() - 1; x > 0; x--) {
				lastUsernames.set(x, lastUsernames.get(x - 1));
			}
			lastUsernames.set(0, username);
		}
	}

	public List<String> getLastUserNames() {
		if (lastUsernames == null)
			lastUsernames = new ArrayList<String>();
		return new ArrayList<String>(lastUsernames);
	}

	public boolean isUsernameAlertSeen() {
		return usernameAlertSeen;
	}

	public void setUsernameAlertSeen(boolean usernameAlertSeen) {
		this.usernameAlertSeen = usernameAlertSeen;
	}
}
