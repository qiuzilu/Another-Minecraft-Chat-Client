package net.defekt.mc.chatclient.protocol.packets.general.clientbound.play;

import java.io.IOException;

import net.defekt.mc.chatclient.protocol.io.VarInputStream;
import net.defekt.mc.chatclient.protocol.packets.Packet;
import net.defekt.mc.chatclient.protocol.packets.PacketRegistry;

public class ServerUpdateHealthPacket extends Packet {

	private final float health;
	private final int food;
	private final float saturation;
	
	public ServerUpdateHealthPacket(PacketRegistry reg, byte[] data) throws IOException {
		super(reg, data);
		VarInputStream is = getInputStream();
		health = is.readFloat();
		food = is.readVarInt();
		saturation = is.readFloat();
	}

	public float getHealth() {
		return health;
	}

	public int getFood() {
		return food;
	}

	public float getSaturation() {
		return saturation;
	}

}
