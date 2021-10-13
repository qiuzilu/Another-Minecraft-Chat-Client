package net.defekt.mc.chatclient.protocol.packets.general.clientbound.login;

import java.io.IOException;

import net.defekt.mc.chatclient.protocol.packets.Packet;
import net.defekt.mc.chatclient.protocol.packets.PacketRegistry;

public class ServerLoginSetCompressionPacket extends Packet {

	private final int threshold;

	public ServerLoginSetCompressionPacket(PacketRegistry reg, byte[] data) throws IOException {
		super(reg, data);
		this.threshold = getInputStream().readVarInt();
	}

	public int getThreshold() {
		return threshold;
	}

}
