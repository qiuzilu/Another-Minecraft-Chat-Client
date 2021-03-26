package net.defekt.mc.chatclient.protocol.packets.general.clientbound.play;

import java.io.IOException;
import java.util.UUID;

import net.defekt.mc.chatclient.protocol.data.ChatMessage;
import net.defekt.mc.chatclient.protocol.io.VarInputStream;
import net.defekt.mc.chatclient.protocol.packets.Packet;
import net.defekt.mc.chatclient.protocol.packets.PacketRegistry;

public class ServerPlayerListItemPacket extends Packet {

	public enum Action {
		ADD_PLAYER(0), UPDATE_GAMEMODE(1), UPDATE_LATENCY(2), UPDATE_DISPLAY_NAME(3), REMOVE_PLAYER(4);

		protected final int number;

		private Action(int num) {
			this.number = num;
		}

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

	public Action getAction() {
		return action;
	}

	public int getPlayers() {
		return players;
	}

	public UUID getUUID() {
		return uuid;
	}

	public String getPlayerName() {
		return playerName;
	}

	public String getTextures() {
		return textures;
	}

	public String getDisplayName() {
		return displayName;
	}

	public int getPing() {
		return ping;
	}

}
