package net.defekt.mc.chatclient.protocol.packets.general.serverbound.play;

import net.defekt.mc.chatclient.protocol.packets.Packet;
import net.defekt.mc.chatclient.protocol.packets.PacketRegistry;

public class ClientKeepAlivePacket extends Packet {

	public ClientKeepAlivePacket(PacketRegistry reg, Long id) {
		super(reg);
		putLong(id);
	}

}
