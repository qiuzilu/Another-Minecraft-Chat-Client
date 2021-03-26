package net.defekt.mc.chatclient.protocol.packets.general.clientbound.login;

import java.io.IOException;

import net.defekt.mc.chatclient.protocol.io.VarInputStream;
import net.defekt.mc.chatclient.protocol.packets.Packet;
import net.defekt.mc.chatclient.protocol.packets.PacketRegistry;

public class ServerLoginSuccessPacket extends Packet {

	private final String uuid;
	private final String username;

	public ServerLoginSuccessPacket(PacketRegistry reg, byte[] data) throws IOException {
		super(reg, data);
		VarInputStream is = getInputStream();
		uuid = is.readString();
		username = is.readString();
	}

	public String getUuid() {
		return uuid;
	}

	public String getUsername() {
		return username;
	}

}
