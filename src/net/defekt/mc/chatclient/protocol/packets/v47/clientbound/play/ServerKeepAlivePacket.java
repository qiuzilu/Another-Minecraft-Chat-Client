package net.defekt.mc.chatclient.protocol.packets.v47.clientbound.play;

import java.io.IOException;

import net.defekt.mc.chatclient.protocol.packets.Packet;
import net.defekt.mc.chatclient.protocol.packets.PacketRegistry;

public class ServerKeepAlivePacket extends Packet {

	private final int id;

	public ServerKeepAlivePacket(PacketRegistry reg, byte[] data) throws IOException {
		super(reg, data);
		this.id = getInputStream().readVarInt();
	}

	public int getId() {
		return id;
	}

}
