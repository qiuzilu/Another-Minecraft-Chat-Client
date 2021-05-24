package net.defekt.mc.chatclient.protocol.packets.general.serverbound.play;

import net.defekt.mc.chatclient.protocol.packets.Packet;
import net.defekt.mc.chatclient.protocol.packets.PacketRegistry;

/**
 * Sent by client when it's trying to send a chat message
 * 
 * @author Defective4
 *
 */
public class ClientChatMessagePacket extends Packet {

	/**
	 * Constructs new {@link ClientChatMessagePacket}
	 * 
	 * @param reg     packet registry used to construct this packet
	 * @param message chat message
	 */
	public ClientChatMessagePacket(PacketRegistry reg, String message) {
		super(reg);
		putString(message);
	}

}
