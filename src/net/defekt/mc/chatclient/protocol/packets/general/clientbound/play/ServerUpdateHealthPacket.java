package net.defekt.mc.chatclient.protocol.packets.general.clientbound.play;

import java.io.IOException;

import net.defekt.mc.chatclient.protocol.io.VarInputStream;
import net.defekt.mc.chatclient.protocol.packets.Packet;
import net.defekt.mc.chatclient.protocol.packets.PacketRegistry;

/**
 * Sent by server when client's health or hunger is updated
 * 
 * @author Defective4
 *
 */
public class ServerUpdateHealthPacket extends Packet {

	private final float health;
	private final int food;
	private final float saturation;

	/**
	 * Contructs {@link ServerUpdateHealthPacket}
	 * 
	 * @param reg  packet registry used to construct this packet
	 * @param data packet's data
	 * @throws IOException never thrown
	 */
	public ServerUpdateHealthPacket(PacketRegistry reg, byte[] data) throws IOException {
		super(reg, data);
		VarInputStream is = getInputStream();
		health = is.readFloat();
		food = is.readVarInt();
		saturation = is.readFloat();
	}

	/**
	 * Get player's new health
	 * 
	 * @return player's health
	 */
	public float getHealth() {
		return health;
	}

	/**
	 * Get player's new hunger status
	 * 
	 * @return player's hunger
	 */
	public int getFood() {
		return food;
	}

	/**
	 * Get player's saturation
	 * 
	 * @return player's saturation
	 */
	public float getSaturation() {
		return saturation;
	}

}
