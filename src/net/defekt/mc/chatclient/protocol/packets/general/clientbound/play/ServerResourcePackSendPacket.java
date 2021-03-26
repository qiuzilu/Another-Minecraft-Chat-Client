package net.defekt.mc.chatclient.protocol.packets.general.clientbound.play;

import java.io.IOException;

import net.defekt.mc.chatclient.protocol.io.VarInputStream;
import net.defekt.mc.chatclient.protocol.packets.Packet;
import net.defekt.mc.chatclient.protocol.packets.PacketRegistry;

public class ServerResourcePackSendPacket extends Packet {

	
	
	private final String url;
	private final String hash;
	
	public ServerResourcePackSendPacket(PacketRegistry reg, byte[] data) throws IOException {
		super(reg, data);
		VarInputStream in = getInputStream();
		url = in.readString();
		hash = in.readString();
	}

	public String getUrl() {
		return url;
	}

	public String getHash() {
		return hash;
	}

}
