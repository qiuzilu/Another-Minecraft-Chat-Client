package net.defekt.mc.chatclient.protocol.packets.general.clientbound.play;

import java.io.IOException;

import net.defekt.mc.chatclient.protocol.io.VarInputStream;
import net.defekt.mc.chatclient.protocol.packets.Packet;
import net.defekt.mc.chatclient.protocol.packets.PacketRegistry;

/**
 * Sent by server when client's position is updated in-game
 * 
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
	private final int teleportID;

	/**
	 * constructs {@link ServerPlayerPositionAndLookPacket}
	 * 
	 * @param reg  packet registry used to construct this packet
	 * @param data packet's data
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

		teleportID = is.readVarInt();
	}

	/**
	 * Get player's X
	 * 
	 * @return player's X position
	 */
	public double getX() {
		return x;
	}

	/**
	 * Get player's Y
	 * 
	 * @return player's Y position
	 */
	public double getY() {
		return y;
	}

	/**
	 * Get player's Z
	 * 
	 * @return player's Z position
	 */
	public double getZ() {
		return z;
	}

	/**
	 * Get player's yaw
	 * 
	 * @return player's yaw
	 */
	public float getYaw() {
		return yaw;
	}

	/**
	 * Get player's pitch
	 * 
	 * @return player's pitch
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

	/**
	 * Get teleport ID
	 * 
	 * @return teleport ID
	 */
	public int getTeleportID() {
		return teleportID;
	}

}
