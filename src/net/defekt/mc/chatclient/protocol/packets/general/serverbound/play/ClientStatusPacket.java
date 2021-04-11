package net.defekt.mc.chatclient.protocol.packets.general.serverbound.play;

import net.defekt.mc.chatclient.protocol.packets.Packet;
import net.defekt.mc.chatclient.protocol.packets.PacketRegistry;

/**
 * Sent by client when it's trying to respawn after death or requesting
 * statistics from server
 * 
 * @author Defective4
 *
 */
public class ClientStatusPacket extends Packet {

	/**
	 * Constructs new {@link ClientStatusPacket}
	 * 
	 * @param reg      packet registry used to construct this packet
	 * @param actionID action ID:<br>
	 *                 0 for respawn<br>
	 *                 1 for stats
	 */
	public ClientStatusPacket(PacketRegistry reg, Integer actionID) {
		super(reg);
		putVarInt(actionID);
	}

}
