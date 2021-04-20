package net.defekt.mc.chatclient.protocol.packets.general.serverbound.play;

import net.defekt.mc.chatclient.protocol.packets.Packet;
import net.defekt.mc.chatclient.protocol.packets.PacketRegistry;

public class ClientCloseWindowPacket extends Packet {

	public ClientCloseWindowPacket(PacketRegistry reg, Integer windowID) {
		super(reg);
		putByte(windowID);
	}

}
