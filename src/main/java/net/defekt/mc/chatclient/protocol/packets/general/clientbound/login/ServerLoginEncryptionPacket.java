package net.defekt.mc.chatclient.protocol.packets.general.clientbound.login;

import java.io.IOException;

import net.defekt.mc.chatclient.protocol.packets.Packet;
import net.defekt.mc.chatclient.protocol.packets.PacketRegistry;

public class ServerLoginEncryptionPacket extends Packet {

	public ServerLoginEncryptionPacket(PacketRegistry reg, byte[] data) throws IOException {
		super(reg, data);

	}

}
