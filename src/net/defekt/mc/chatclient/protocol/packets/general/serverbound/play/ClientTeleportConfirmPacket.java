package net.defekt.mc.chatclient.protocol.packets.general.serverbound.play;

import net.defekt.mc.chatclient.protocol.packets.Packet;
import net.defekt.mc.chatclient.protocol.packets.PacketRegistry;

/**
 * Sent by client to confirm changing positions
 * 
 * @author Defective4
 *
 */
public class ClientTeleportConfirmPacket extends Packet {

	/**
	 * Constructs new {@link ClientTeleportConfirmPacket}
	 * 
	 * @param reg        packet registry used to construct this packet
	 * @param teleportID teleportation ID
	 */
	public ClientTeleportConfirmPacket(PacketRegistry reg, int teleportID) {
		super(reg);
		this.id = 0x00;
		putVarInt(teleportID);
	}

}
