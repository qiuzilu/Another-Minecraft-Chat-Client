package net.defekt.mc.chatclient.protocol.packets.general.serverbound.play;

import net.defekt.mc.chatclient.protocol.packets.Packet;
import net.defekt.mc.chatclient.protocol.packets.PacketRegistry;

public class ClientRespawnPacket extends Packet {

	public ClientRespawnPacket(PacketRegistry reg) {
		super(reg);
		putVarInt(0);
	}

}
