package net.defekt.mc.chatclient.protocol.packets.general.serverbound.play;

import net.defekt.mc.chatclient.protocol.packets.Packet;
import net.defekt.mc.chatclient.protocol.packets.PacketRegistry;
import net.defekt.mc.chatclient.protocol.packets.general.clientbound.play.ServerKeepAlivePacket;

/**
 * Sent by client as response to server's {@link ServerKeepAlivePacket}
 * 
 * @author Defective4
 *
 */
public class ClientKeepAlivePacket extends Packet {

	/**
	 * Constructs new {@link ClientKeepAlivePacket}
	 * 
	 * @param reg packet registry used to construct this packet
	 * @param id  keep-alive ID
	 */
	public ClientKeepAlivePacket(PacketRegistry reg, Long id) {
		super(reg);
		putLong(id);
	}

}
