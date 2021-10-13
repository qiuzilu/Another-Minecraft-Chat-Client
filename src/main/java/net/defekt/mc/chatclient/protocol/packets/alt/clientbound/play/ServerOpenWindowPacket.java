package net.defekt.mc.chatclient.protocol.packets.alt.clientbound.play;

import java.io.IOException;

import net.defekt.mc.chatclient.protocol.io.VarInputStream;
import net.defekt.mc.chatclient.protocol.packets.Packet;
import net.defekt.mc.chatclient.protocol.packets.PacketRegistry;

/**
 * Old version of
 * {@link net.defekt.mc.chatclient.protocol.packets.general.clientbound.play.ServerOpenWindowPacket}
 * 
 * @author Defective4
 *
 */
public class ServerOpenWindowPacket extends Packet {

	private final int windowID;
	private final int windowType;
	private final String windowTitle;

	/**
	 * Constructs {@link ServerOpenWindowPacket}
	 * 
	 * @param reg  packet registry used to construct this packet
	 * @param data packet's data
	 * @throws IOException never thrown
	 */
	public ServerOpenWindowPacket(PacketRegistry reg, byte[] data) throws IOException {
		super(reg, data);
		VarInputStream is = getInputStream();
		windowID = is.readUnsignedByte();
		windowType = is.readVarInt();
		windowTitle = is.readString();
	}

	/**
	 * Get opened window's ID
	 * 
	 * @return window ID
	 */
	public int getWindowID() {
		return windowID;
	}

	/**
	 * Get opened window type
	 * 
	 * @return window type
	 */
	public int getWindowType() {
		return windowType;
	}

	/**
	 * Get opened window title
	 * 
	 * @return window title
	 */
	public String getWindowTitle() {
		return windowTitle;
	}

}
