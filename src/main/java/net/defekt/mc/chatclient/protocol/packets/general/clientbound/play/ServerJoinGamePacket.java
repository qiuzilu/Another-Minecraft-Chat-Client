package net.defekt.mc.chatclient.protocol.packets.general.clientbound.play;

import java.io.IOException;

import net.defekt.mc.chatclient.protocol.packets.Packet;
import net.defekt.mc.chatclient.protocol.packets.PacketRegistry;

/**
 * Sent by server when client has successfully joined the game
 * 
 * @author Defective4
 *
 */
public class ServerJoinGamePacket extends Packet {

	private final int entityID;

	/**
	 * Constructs {@link ServerJoinGamePacket}
	 * 
	 * @param reg  packet registry used to construct this packet
	 * @param data packet's data
	 * @throws IOException never thrown
	 */
	public ServerJoinGamePacket(PacketRegistry reg, byte[] data) throws IOException {
		super(reg, data);
		this.entityID = getInputStream().readInt();
	}

	/**
	 * Get player's entity ID
	 * 
	 * @return entity ID
	 */
	public int getEntityID() {
		return entityID;
	}

}
