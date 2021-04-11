package net.defekt.mc.chatclient.protocol.packets.general.serverbound.play;

import net.defekt.mc.chatclient.protocol.packets.Packet;
import net.defekt.mc.chatclient.protocol.packets.PacketRegistry;

/**
 * Sent by client to update its position and look on server.
 * 
 * @author Defective4
 *
 */
public class ClientPlayerPositionAndLookPacket extends Packet {

	/**
	 * Contructs new {@link ClientPlayerPositionAndLookPacket}
	 * 
	 * @param reg      packet registry used to construct this packet
	 * @param x        new X position
	 * @param y        new Y position
	 * @param z        new Z position
	 * @param yaw      new yaw
	 * @param pitch    new pitch
	 * @param onGround if player is on ground
	 */
	public ClientPlayerPositionAndLookPacket(PacketRegistry reg, Double x, Double y, Double z, Float yaw, Float pitch,
			Boolean onGround) {
		super(reg);
		putDouble(x);
		putDouble(y);
		putDouble(z);
		putFloat(yaw);
		putFloat(pitch);
		putBoolean(onGround);
	}

}
