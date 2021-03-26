package net.defekt.mc.chatclient.protocol.packets.general.serverbound.play;

import net.defekt.mc.chatclient.protocol.packets.Packet;
import net.defekt.mc.chatclient.protocol.packets.PacketRegistry;

public class ClientChatMessagePacket extends Packet {

	public ClientChatMessagePacket(PacketRegistry reg, String message) {
		super(reg);
		putString(message);
	}

}
