package net.defekt.mc.chatclient.protocol.packets.alt.clientbound.play;

import java.io.IOException;

import net.defekt.mc.chatclient.protocol.io.VarInputStream;
import net.defekt.mc.chatclient.protocol.packets.Packet;
import net.defekt.mc.chatclient.protocol.packets.PacketRegistry;

/**
 * An older version of
 * {@link net.defekt.mc.chatclient.protocol.packets.general.clientbound.play.ServerPlayerPositionAndLookPacket}
 * 
 * @see net.defekt.mc.chatclient.protocol.packets.general.clientbound.play.ServerPlayerPositionAndLookPacket
 * @author Defective4
 *
 */
public class ServerPlayerPositionAndLookPacket extends Packet {

	private final double x;
	private final double y;
	private final double z;
	private final float yaw;
	private final float pitch;
	private final byte flags;

	/**
	 * Constructs new {@link ServerPlayerPositionAndLookPacket}
	 * 
	 * @param reg  packet registry used to contruct this packet
	 * @param data packet data
	 * @throws IOException never thrown
	 */
	public ServerPlayerPositionAndLookPacket(PacketRegistry reg, byte[] data) throws IOException {
		super(reg, data);
		VarInputStream is = getInputStream();
		x = is.readDouble();
		y = is.readDouble();
		z = is.readDouble();

		yaw = is.readFloat();
		pitch = is.readFloat();

		flags = is.readByte();
	}

	/**
	 * Get client X
	 * 
	 * @return client's X position
	 */
	public double getX() {
		return x;
	}

	/**
	 * Get client Y
	 * 
	 * @return client's Y position
	 */
	public double getY() {
		return y;
	}

	/**
	 * Get client Z
	 * 
	 * @return client's Z position
	 */
	public double getZ() {
		return z;
	}

	/**
	 * Get client yaw
	 * 
	 * @return client's yaw
	 */
	public float getYaw() {
		return yaw;
	}

	/**
	 * Get client pitch
	 * 
	 * @return client's pitch position
	 */
	public float getPitch() {
		return pitch;
	}

	/**
	 * Get flags
	 * 
	 * @return flags
	 */
	public byte getFlags() {
		return flags;
	}

}
