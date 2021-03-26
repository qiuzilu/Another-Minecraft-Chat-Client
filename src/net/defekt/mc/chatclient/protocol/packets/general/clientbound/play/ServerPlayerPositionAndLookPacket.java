package net.defekt.mc.chatclient.protocol.packets.general.clientbound.play;

import java.io.IOException;

import net.defekt.mc.chatclient.protocol.io.VarInputStream;
import net.defekt.mc.chatclient.protocol.packets.Packet;
import net.defekt.mc.chatclient.protocol.packets.PacketRegistry;

public class ServerPlayerPositionAndLookPacket extends Packet {

	private final double x;
	private final double y;
	private final double z;
	private final float yaw;
	private final float pitch;
	private final byte flags;
	private final int teleportID;

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

	public double getX() {
		return x;
	}

	public double getY() {
		return y;
	}

	public double getZ() {
		return z;
	}

	public float getYaw() {
		return yaw;
	}

	public float getPitch() {
		return pitch;
	}

	public byte getFlags() {
		return flags;
	}

	public int getTeleportID() {
		return teleportID;
	}

}
