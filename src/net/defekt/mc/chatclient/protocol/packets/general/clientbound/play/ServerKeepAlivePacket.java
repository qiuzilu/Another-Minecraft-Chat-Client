package net.defekt.mc.chatclient.protocol.packets.general.clientbound.play;

import java.io.IOException;

import net.defekt.mc.chatclient.protocol.packets.Packet;
import net.defekt.mc.chatclient.protocol.packets.PacketRegistry;

public class ServerKeepAlivePacket extends Packet {

	private final long id;

	public ServerKeepAlivePacket(PacketRegistry reg, byte[] data) throws IOException {
		super(reg, data);
		this.id = getInputStream().readLong();
	}

	public long getId() {
		return id;
	}

}
