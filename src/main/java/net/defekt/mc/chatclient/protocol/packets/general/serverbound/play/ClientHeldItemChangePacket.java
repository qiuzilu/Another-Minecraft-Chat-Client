package net.defekt.mc.chatclient.protocol.packets.general.serverbound.play;

import net.defekt.mc.chatclient.protocol.packets.Packet;
import net.defekt.mc.chatclient.protocol.packets.PacketRegistry;

/**
 * Sent by client when it changes held item slot
 * 
 * @author Defective4
 *
 */
public class ClientHeldItemChangePacket extends Packet {

	/**
	 * Constructs new {@link ClientHeldItemChangePacket}
	 * 
	 * @param reg  packet registry used to construct this packet
	 * @param slot slot to change to
	 */
	public ClientHeldItemChangePacket(PacketRegistry reg, Short slot) {
		super(reg);
		putShort(slot);
	}

}
