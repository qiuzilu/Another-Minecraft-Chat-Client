package net.defekt.mc.chatclient.protocol.packets.general.clientbound.play;

import java.io.IOException;

import net.defekt.mc.chatclient.protocol.io.VarInputStream;
import net.defekt.mc.chatclient.protocol.packets.Packet;
import net.defekt.mc.chatclient.protocol.packets.PacketRegistry;

/**
 * Sent by server to confirm inventory transaction
 * 
 * @author Defective4
 *
 */
public class ServerConfirmTransactionPacket extends Packet {

	private final int windowID;
	private final short actionID;
	private final boolean accepted;

	/**
	 * Constructs {@link ServerConfirmTransactionPacket}
	 * 
	 * @param reg  packet registry used to construct this packet
	 * @param data packet's data
	 * @throws IOException never thrown
	 */
	public ServerConfirmTransactionPacket(PacketRegistry reg, byte[] data) throws IOException {
		super(reg, data);
		VarInputStream is = getInputStream();
		windowID = is.readByte();
		actionID = is.readShort();
		accepted = is.readBoolean();
	}

	/**
	 * Get window ID for this transaction
	 * 
	 * @return window's ID
	 */
	public int getWindowID() {
		return windowID;
	}

	/**
	 * Get transaction ID
	 * 
	 * @return action Id
	 */
	public short getActionID() {
		return actionID;
	}

	/**
	 * Get accepted state of this transaction
	 * 
	 * @return accepted state
	 */
	public boolean isAccepted() {
		return accepted;
	}

}
