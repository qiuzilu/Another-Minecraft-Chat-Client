package net.defekt.mc.chatclient.protocol.packets.general.clientbound.play;

import java.io.IOException;

import net.defekt.mc.chatclient.protocol.packets.Packet;
import net.defekt.mc.chatclient.protocol.packets.PacketRegistry;

/**
 * Sent by server to ensure that client is still alive.<br>
 * Client has to respons with the same packet, with the same ID.
 * 
 * @author Defective4
 *
 */
public class ServerKeepAlivePacket extends Packet {

	private final long id;

	/**
	 * constructs {@link ServerKeepAlivePacket}
	 * 
	 * @param reg  packet registry used to construct this packet
	 * @param data packet's data
	 * @throws IOException never thrown
	 */
	public ServerKeepAlivePacket(PacketRegistry reg, byte[] data) throws IOException {
		super(reg, data);
		this.id = getInputStream().readLong();
	}

	/**
	 * Get packet's Keep-Alive ID
	 * 
	 * @return keep-alive ID as {@link Long}
	 */
	public long getId() {
		return id;
	}

}
