package net.defekt.mc.chatclient.protocol.packets.general.clientbound.play;

import java.io.IOException;

import net.defekt.mc.chatclient.protocol.io.VarInputStream;
import net.defekt.mc.chatclient.protocol.packets.Packet;
import net.defekt.mc.chatclient.protocol.packets.PacketRegistry;

/**
 * Sent by server to update resource pack used by client
 * 
 * @author Defective4
 *
 */
public class ServerResourcePackSendPacket extends Packet {

	private final String url;
	private final String hash;

	/**
	 * constructs {@link ServerResourcePackSendPacket}
	 * 
	 * @param reg  packet registry used to construct this packet
	 * @param data packet's data
	 * @throws IOException never thrown
	 */
	public ServerResourcePackSendPacket(PacketRegistry reg, byte[] data) throws IOException {
		super(reg, data);
		VarInputStream in = getInputStream();
		url = in.readString();
		hash = in.readString();
	}

	/**
	 * Get resource pack URL
	 * 
	 * @return resource pack URL
	 */
	public String getUrl() {
		return url;
	}

	/**
	 * Get resource pack's hash
	 * 
	 * @return resource pack's hash
	 */
	public String getHash() {
		return hash;
	}

}
