package net.defekt.mc.chatclient.protocol.packets.alt.clientbound.play;

import java.io.IOException;

import net.defekt.mc.chatclient.protocol.packets.Packet;
import net.defekt.mc.chatclient.protocol.packets.PacketRegistry;

/**
 * An older version of
 * {@link net.defekt.mc.chatclient.protocol.packets.general.clientbound.play.ServerKeepAlivePacket}
 * packet used in protocol versions below 340
 * 
 * @see net.defekt.mc.chatclient.protocol.packets.general.clientbound.play.ServerKeepAlivePacket
 * @author Defective4
 *
 */
public class ServerKeepAlivePacket extends Packet {

	private final int id;

	/**
	 * Constructs new {@link ServerKeepAlivePacket}
	 * 
	 * @param reg  packet registry used to contruct this packet
	 * @param data packet data
	 * @throws IOException never thrown
	 */
	public ServerKeepAlivePacket(PacketRegistry reg, byte[] data) throws IOException {
		super(reg, data);
		this.id = getInputStream().readVarInt();
	}

	/**
	 * Get keep-alive ID
	 * 
	 * @return keep-alive ID as VarInt
	 */
	public int getId() {
		return id;
	}

}
