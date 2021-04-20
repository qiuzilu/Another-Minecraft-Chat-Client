/**
 * 
 */
package net.defekt.mc.chatclient.protocol.packets.general.clientbound.play;

import java.io.IOException;

import net.defekt.mc.chatclient.protocol.io.VarInputStream;
import net.defekt.mc.chatclient.protocol.packets.Packet;
import net.defekt.mc.chatclient.protocol.packets.PacketRegistry;

/**
 * @author Defective4
 *
 */
public class ServerOpenWindowPacket extends Packet {

	private final int windowID;
	private final String windowType;
	private final String windowTitle;
	private final int slots;

	public ServerOpenWindowPacket(PacketRegistry reg, byte[] data) throws IOException {
		super(reg, data);
		VarInputStream is = getInputStream();
		windowID = is.readUnsignedByte();
		windowType = is.readString();
		windowTitle = is.readString();
		slots = is.readUnsignedByte();
	}

	public int getWindowID() {
		return windowID;
	}

	public String getWindowType() {
		return windowType;
	}

	public String getWindowTitle() {
		return windowTitle;
	}

	public int getSlots() {
		return slots;
	}

}
