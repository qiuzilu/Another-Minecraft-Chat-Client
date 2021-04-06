package net.defekt.mc.chatclient.protocol.packets.general.serverbound.play;

import net.defekt.mc.chatclient.protocol.packets.Packet;
import net.defekt.mc.chatclient.protocol.packets.PacketRegistry;

/**
 * Sent by client when it's trying to respawn after death
 * 
 * @author Defective4
 *
 */
public class ClientRespawnPacket extends Packet {

	/**
	 * Constructs new {@link ClientRespawnPacket}
	 * 
	 * @param reg packet registry used to construct this packet
	 */
	public ClientRespawnPacket(PacketRegistry reg) {
		super(reg);
		putVarInt(0);
	}

}
