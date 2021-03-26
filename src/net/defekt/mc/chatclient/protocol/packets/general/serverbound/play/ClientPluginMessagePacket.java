package net.defekt.mc.chatclient.protocol.packets.general.serverbound.play;

import java.io.IOException;

import net.defekt.mc.chatclient.protocol.packets.Packet;
import net.defekt.mc.chatclient.protocol.packets.PacketRegistry;

public class ClientPluginMessagePacket extends Packet {

	public ClientPluginMessagePacket(PacketRegistry reg, String channel, byte[] data) throws IOException {
		super(reg);
		putString(channel);
		putBytes(data);
	}

}
