package net.defekt.mc.chatclient.protocol.packets.general.serverbound.login;

import net.defekt.mc.chatclient.protocol.packets.Packet;
import net.defekt.mc.chatclient.protocol.packets.PacketRegistry;

public class ClientLoginRequestPacket extends Packet {

	public ClientLoginRequestPacket(PacketRegistry reg, String username) {
		super(reg);
		putString(username);
	}

}
