package net.defekt.mc.chatclient.protocol.packets.general.serverbound.play;

import net.defekt.mc.chatclient.protocol.packets.Packet;
import net.defekt.mc.chatclient.protocol.packets.PacketRegistry;

/**
 * Sent by client as response to transaction confirmation
 * 
 * @author Defective4
 *
 */
public class ClientConfirmTransactionPacket extends Packet {

	/**
	 * Constructs new {@link ClientConfirmTransactionPacket}
	 * 
	 * @param reg      packet registry used to construct this packet
	 * @param windowID window ID
	 * @param actionID action ID
	 * @param accepted accepted state
	 */
	public ClientConfirmTransactionPacket(PacketRegistry reg, Byte windowID, Short actionID, Boolean accepted) {
		super(reg);
		putByte(windowID);
		putShort(actionID);
		putBoolean(accepted);
	}

}
