package net.defekt.mc.chatclient.protocol.packets.general.serverbound.play;

import net.defekt.mc.chatclient.protocol.packets.Packet;
import net.defekt.mc.chatclient.protocol.packets.PacketRegistry;

/**
 * Sent by client when it closes a window
 * 
 * @author Defective4
 *
 */
public class ClientCloseWindowPacket extends Packet {

	/**
	 * Constructs new {@link ClientCloseWindowPacket}
	 * 
	 * @param reg      packet registry used to construct this packet
	 * @param windowID window ID
	 */
	public ClientCloseWindowPacket(PacketRegistry reg, Integer windowID) {
		super(reg);
		putByte(windowID);
	}

}
