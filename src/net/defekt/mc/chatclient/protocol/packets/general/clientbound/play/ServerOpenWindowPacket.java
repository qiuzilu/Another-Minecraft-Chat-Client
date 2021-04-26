/**
 * 
 */
package net.defekt.mc.chatclient.protocol.packets.general.clientbound.play;

import java.io.IOException;

import net.defekt.mc.chatclient.protocol.io.VarInputStream;
import net.defekt.mc.chatclient.protocol.packets.Packet;
import net.defekt.mc.chatclient.protocol.packets.PacketRegistry;

/**
 * Sent by server when a window is opened
 * 
 * @author Defective4
 */
public class ServerOpenWindowPacket extends Packet {

	private final int windowID;
	private final String windowType;
	private final String windowTitle;
	private final int slots;

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
		windowType = is.readString();
		windowTitle = is.readString();
		slots = is.readUnsignedByte();
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
	public String getWindowType() {
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

	/**
	 * Get window size
	 * 
	 * @return window size in slots
	 */
	public int getSlots() {
		return slots;
	}

}
