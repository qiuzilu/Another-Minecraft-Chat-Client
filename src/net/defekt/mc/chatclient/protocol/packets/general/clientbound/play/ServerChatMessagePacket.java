package net.defekt.mc.chatclient.protocol.packets.general.clientbound.play;

import java.io.IOException;

import net.defekt.mc.chatclient.protocol.io.VarInputStream;
import net.defekt.mc.chatclient.protocol.packets.Packet;
import net.defekt.mc.chatclient.protocol.packets.PacketRegistry;

/**
 * Sent by server when client received a chat message
 * 
 * @author Defective4
 *
 */
public class ServerChatMessagePacket extends Packet {

	/**
	 * Chat message position
	 * 
	 * @author Defective4
	 *
	 */
	public enum Position {
		/**
		 * Message position is in chat box
		 */
		CHAT,
		/**
		 * System message
		 */
		SYSTEM,
		/**
		 * Message is displayed above hotbar
		 */
		HOTBAR
	}

	private final String message;
	private final byte position;

	/**
	 * Contructs {@link ServerChatMessagePacket}
	 * 
	 * @param reg  packet registry used to construct this packet
	 * @param data packet's data
	 * @throws IOException never thrown
	 */
	public ServerChatMessagePacket(PacketRegistry reg, byte[] data) throws IOException {
		super(reg, data);
		VarInputStream is = getInputStream();
		this.message = is.readString();
		this.position = is.readByte();
	}

	/**
	 * Get JSON message
	 * 
	 * @return raw JSON message
	 */
	public String getMessage() {
		return message;
	}

	/**
	 * Get message's position
	 * 
	 * @return message's position
	 */
	public Position getPosition() {
		switch (position) {
			case 0: {
				return Position.CHAT;
			}
			case 1: {
				return Position.SYSTEM;
			}
			case 2: {
				return Position.HOTBAR;
			}
			default: {
				return Position.CHAT;
			}
		}
	}

}
