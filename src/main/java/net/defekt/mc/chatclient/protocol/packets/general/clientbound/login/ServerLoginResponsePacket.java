package net.defekt.mc.chatclient.protocol.packets.general.clientbound.login;

import java.io.IOException;

import net.defekt.mc.chatclient.protocol.packets.Packet;
import net.defekt.mc.chatclient.protocol.packets.PacketRegistry;

public class ServerLoginResponsePacket extends Packet {

	private final String response;

	public ServerLoginResponsePacket(PacketRegistry reg, byte[] data) throws IOException {
		super(reg, data);
		this.response = getInputStream().readString();
	}

	public String getResponse() {
		return response;
	}

}
