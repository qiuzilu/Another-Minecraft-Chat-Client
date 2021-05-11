package net.defekt.mc.chatclient.protocol.packets.general.serverbound.play;

import net.defekt.mc.chatclient.protocol.packets.Packet;
import net.defekt.mc.chatclient.protocol.packets.PacketRegistry;

/**
 * Sent by client when it uses item in its hand
 * 
 * @author Defective4
 *
 */
public class ClientUseItemPacket extends Packet {

	/**
	 * Constructs new {@link ClientUseItemPacket}
	 * 
	 * @param reg packet registry used to construct this packet
	 */
	public ClientUseItemPacket(PacketRegistry reg) {
		super(reg);
		putVarInt(0);
	}
}
