package net.defekt.mc.chatclient.protocol.packets.general.clientbound.play;

import java.io.IOException;
import java.util.UUID;

import net.defekt.mc.chatclient.protocol.data.ChatMessage;
import net.defekt.mc.chatclient.protocol.io.VarInputStream;
import net.defekt.mc.chatclient.protocol.packets.Packet;
import net.defekt.mc.chatclient.protocol.packets.PacketRegistry;

/**
 * Sent by server when player's TAB list is updatet
 * 
 * @author Defective4
 *
 */
public class ServerPlayerListItemPacket extends Packet {

	/**
	 * Player List action type
	 * 
	 * @author Defective4
	 *
	 */
	public enum Action {
		/**
		 * A player is added
		 */
		ADD_PLAYER(0),
		/**
		 * Player's gamemode is updated
		 */
		UPDATE_GAMEMODE(1),
		/**
		 * Player's latency is updated
		 */
		UPDATE_LATENCY(2),
		/**
		 * Player's display name is updated
		 */
		UPDATE_DISPLAY_NAME(3),
		/**
		 * A player is removed
		 */
		REMOVE_PLAYER(4);

		protected final int number;

		private Action(int num) {
			this.number = num;
		}

		/**
		 * Get Action for action ID
		 * 
		 * @param num action ID
		 * @return corresponding Action, or null if not found
		 */
		public static Action getForNumber(int num) {
			for (Action action : values())
				if (action.number == num)
					return action;
			return null;
		}
	}

	private final Action action;
	private final int players;
	private final UUID uuid;

	private String playerName = null;
	private String textures = null;
	private String displayName = null;
	private int ping = -1;

	/**
	 * Contructs {@link ServerPlayerListItemPacket}
	 * 
	 * @param reg  packet registry used to construct this packet
	 * @param data packet's data
	 * @throws IOException never thrown
	 */
	public ServerPlayerListItemPacket(PacketRegistry reg, byte[] data) throws IOException {
		super(reg, data);
		VarInputStream is = getInputStream();

		action = Action.getForNumber(is.readVarInt());
		players = is.readVarInt();
		uuid = is.readUUID();

		switch (action) {
			case ADD_PLAYER: {
				playerName = is.readString();
				int propertiesNum = is.readVarInt();
				textures = null;
				displayName = null;

				for (int x = 0; x < propertiesNum; x++) {
					String pName = is.readString();
					String value = is.readString();
					boolean isSigned = is.readBoolean();
					if (isSigned)
						is.readString();

					if (pName.equals("textures"))
						textures = value;
				}

				is.readVarInt();
				ping = is.readVarInt();
				if (is.readBoolean())
					displayName = is.readString();

				break;
			}
			case UPDATE_DISPLAY_NAME: {
				if (is.readBoolean())
					displayName = is.readString();
				break;
			}
			case UPDATE_LATENCY: {
				ping = is.readVarInt();
				break;
			}
			default: {
				break;
			}
		}

		if (displayName != null)
			displayName = ChatMessage.parse(displayName);
	}

	/**
	 * Get Player List action
	 * 
	 * @return player list action
	 */
	public Action getAction() {
		return action;
	}

	/**
	 * Get player list max players count
	 * 
	 * @return max players
	 */
	public int getPlayers() {
		return players;
	}

	/**
	 * Get UUID of player involved in this packet
	 * 
	 * @return player's UUID
	 */
	public UUID getUUID() {
		return uuid;
	}

	/**
	 * Get name of player
	 * 
	 * @return player's name
	 */
	public String getPlayerName() {
		return playerName;
	}

	/**
	 * Get player's skin data
	 * 
	 * @return player's skin data, or null if none
	 */
	public String getTextures() {
		return textures;
	}

	/**
	 * Get player's display name
	 * 
	 * @return player's display name, or null if none
	 */
	public String getDisplayName() {
		return displayName;
	}

	/**
	 * Get player's latency
	 * 
	 * @return player's latency, or -1 if unknown
	 */
	public int getPing() {
		return ping;
	}

}
