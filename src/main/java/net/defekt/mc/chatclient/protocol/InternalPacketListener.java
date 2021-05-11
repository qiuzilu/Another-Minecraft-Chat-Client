package net.defekt.mc.chatclient.protocol;

import net.defekt.mc.chatclient.protocol.packets.Packet;
import net.defekt.mc.chatclient.protocol.packets.PacketRegistry;

/**
 * A listener used for receiving packets from server
 * 
 * @see ClientPacketListener
 * @see MinecraftClient
 * @see PacketRegistry
 * @author Defective4
 *
 */
public interface InternalPacketListener {
	/**
	 * Invoked when a packet was received
	 * @param packet received packet. For some protocol versions packet classes may be different (see {@link PacketRegistry})
	 * @param registry packet registry used to construct received packet.
	 */
	public void packetReceived(Packet packet, PacketRegistry registry);
}
