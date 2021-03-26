package net.defekt.mc.chatclient.protocol.packets.v47.serverbound.play;


import net.defekt.mc.chatclient.protocol.packets.Packet;
import net.defekt.mc.chatclient.protocol.packets.PacketRegistry;

public class ClientKeepAlivePacket extends Packet {

	public ClientKeepAlivePacket(PacketRegistry reg, Integer id) {
		super(reg);
		putVarInt(id);
	}

}
