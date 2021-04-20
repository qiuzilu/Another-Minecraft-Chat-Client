package net.defekt.mc.chatclient.protocol.packets.general.clientbound.play;

import java.io.IOException;

import net.defekt.mc.chatclient.protocol.packets.Packet;
import net.defekt.mc.chatclient.protocol.packets.PacketRegistry;

public class ServerCloseWindowPacket extends Packet {

	private final int windowID;
	
	public ServerCloseWindowPacket(PacketRegistry reg, byte[] data) throws IOException {
		super(reg, data);
		this.windowID = getInputStream().readUnsignedByte();
	}

	public int getWindowID() {
		return windowID;
	}

}
