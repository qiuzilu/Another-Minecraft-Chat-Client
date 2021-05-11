package net.defekt.mc.chatclient.protocol.packets.general.clientbound.play;

import java.io.IOException;

import net.defekt.mc.chatclient.protocol.packets.Packet;
import net.defekt.mc.chatclient.protocol.packets.PacketRegistry;

/**
 * Sent by server when a window is closed
 * 
 * @author Defective4
 *
 */
public class ServerCloseWindowPacket extends Packet {

	private final int windowID;

	/**
	 * Constructs {@link ServerCloseWindowPacket}
	 * 
	 * @param reg  packet registry used to construct this packet
	 * @param data packet's data
	 * @throws IOException never thrown
	 */
	public ServerCloseWindowPacket(PacketRegistry reg, byte[] data) throws IOException {
		super(reg, data);
		this.windowID = getInputStream().readUnsignedByte();
	}

	/**
	 * Get closed window's ID
	 * 
	 * @return window Id
	 */
	public int getWindowID() {
		return windowID;
	}

}
