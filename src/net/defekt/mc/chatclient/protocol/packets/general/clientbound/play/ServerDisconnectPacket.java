package net.defekt.mc.chatclient.protocol.packets.general.clientbound.play;

import java.io.IOException;

import net.defekt.mc.chatclient.protocol.packets.Packet;
import net.defekt.mc.chatclient.protocol.packets.PacketRegistry;

public class ServerDisconnectPacket extends Packet {

	private final String reason;
	
	public ServerDisconnectPacket(PacketRegistry reg, byte[] data) throws IOException {
		super(reg, data);
		reason = getInputStream().readString();
	}

	public String getReason() {
		return reason;
	}

}
