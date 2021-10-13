package net.defekt.mc.chatclient.protocol.packets.general.serverbound.login;

import net.defekt.mc.chatclient.protocol.packets.Packet;
import net.defekt.mc.chatclient.protocol.packets.PacketRegistry;

/**
 * Sent by client to start login process
 * 
 * @author Defective4
 *
 */
public class ClientLoginRequestPacket extends Packet {

	/**
	 * Constructs new {@link ClientLoginRequestPacket}
	 * 
	 * @param reg      packet registry used to contruct this packet
	 * @param username player's username
	 */
	public ClientLoginRequestPacket(PacketRegistry reg, String username) {
		super(reg);
		putString(username);
		id = 0;
	}

}
