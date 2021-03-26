package net.defekt.mc.chatclient.protocol;

import net.defekt.mc.chatclient.protocol.packets.Packet;
import net.defekt.mc.chatclient.protocol.packets.PacketRegistry;

public interface InternalPacketListener {
	public void packetReceived(Packet packet, PacketRegistry registry);
}
