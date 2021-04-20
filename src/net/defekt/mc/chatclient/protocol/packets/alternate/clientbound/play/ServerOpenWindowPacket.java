package net.defekt.mc.chatclient.protocol.packets.alternate.clientbound.play;

import java.io.IOException;

import net.defekt.mc.chatclient.protocol.io.VarInputStream;
import net.defekt.mc.chatclient.protocol.packets.Packet;
import net.defekt.mc.chatclient.protocol.packets.PacketRegistry;

public class ServerOpenWindowPacket extends Packet {

	private final int windowID;
	private final int windowType;
	private final String windowTitle;

	public ServerOpenWindowPacket(PacketRegistry reg, byte[] data) throws IOException {
		super(reg, data);
		VarInputStream is = getInputStream();
		windowID = is.readUnsignedByte();
		windowType = is.readVarInt();
		windowTitle = is.readString();
	}

	public int getWindowID() {
		return windowID;
	}

	public int getWindowType() {
		return windowType;
	}

	public String getWindowTitle() {
		return windowTitle;
	}

}
