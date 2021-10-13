package net.defekt.mc.chatclient.protocol.packets.alt.serverbound.play;

import net.defekt.mc.chatclient.protocol.packets.Packet;
import net.defekt.mc.chatclient.protocol.packets.PacketRegistry;
import net.defekt.mc.chatclient.protocol.packets.general.serverbound.play.ClientResourcePackStatusPacket.Status;

/**
 * An older version of
 * {@link net.defekt.mc.chatclient.protocol.packets.general.serverbound.play.ClientResourcePackStatusPacket}
 * 
 * @author Defective4
 *
 */
public class ClientResourcePackStatusPacket extends Packet {

	/**
	 * Constructs new {@link ClientKeepAlivePacket}
	 * 
	 * @param reg    packet registry used to construct this packet
	 * @param hash   resource pack's hash
	 * @param status resource pack's hash
	 */
	public ClientResourcePackStatusPacket(PacketRegistry reg, String hash, Status status) {
		super(reg);
		putString(hash);
		putVarInt(status.num);
	}

}
