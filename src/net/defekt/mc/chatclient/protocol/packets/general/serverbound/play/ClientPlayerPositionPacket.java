package net.defekt.mc.chatclient.protocol.packets.general.serverbound.play;

import net.defekt.mc.chatclient.protocol.packets.Packet;
import net.defekt.mc.chatclient.protocol.packets.PacketRegistry;

/**
 * Sent by client to update its position on server.
 * 
 * @author Defective4
 *
 */
public class ClientPlayerPositionPacket extends Packet {

	/**
	 * Contructs new {@link ClientPlayerPositionAndLookPacket}
	 * 
	 * @param reg      packet registry used to construct this packet
	 * @param x        new X position
	 * @param y        new Y position
	 * @param z        new Z position
	 * @param onGround if player is on ground
	 */
	public ClientPlayerPositionPacket(PacketRegistry reg, Double x, Double y, Double z, Boolean onGround) {
		super(reg);
		putDouble(x);
		putDouble(y);
		putDouble(z);
		putBoolean(onGround);
	}

}
