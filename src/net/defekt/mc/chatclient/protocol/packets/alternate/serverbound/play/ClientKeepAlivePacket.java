package net.defekt.mc.chatclient.protocol.packets.alternate.serverbound.play;

import net.defekt.mc.chatclient.protocol.packets.Packet;
import net.defekt.mc.chatclient.protocol.packets.PacketRegistry;

/**
 * An older version of
 * {@link net.defekt.mc.chatclient.protocol.packets.general.serverbound.play.ClientKeepAlivePacket}
 * used in protocol versions below 340
 * 
 * @author Defective4
 *
 */
public class ClientKeepAlivePacket extends Packet {

	/**
	 * Constructs new {@link ClientKeepAlivePacket}
	 * 
	 * @param reg packet registry used to construct this packet
	 * @param id  keep-alive ID as VarInt
	 */
	public ClientKeepAlivePacket(PacketRegistry reg, Integer id) {
		super(reg);
		putVarInt(id);
	}

}
