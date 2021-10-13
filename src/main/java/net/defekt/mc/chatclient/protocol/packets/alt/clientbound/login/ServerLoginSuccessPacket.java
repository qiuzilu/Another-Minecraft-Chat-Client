package net.defekt.mc.chatclient.protocol.packets.alt.clientbound.login;

import java.io.IOException;

import net.defekt.mc.chatclient.protocol.io.VarInputStream;
import net.defekt.mc.chatclient.protocol.packets.Packet;
import net.defekt.mc.chatclient.protocol.packets.PacketRegistry;

/**
 * Packet sent by server when client finished logging in
 * 
 * @author Defective4
 *
 */
public class ServerLoginSuccessPacket extends Packet {

	private final String uuid;
	private final String username;

	/**
	 * Contructs {@link ServerLoginSuccessPacket}
	 * 
	 * @param reg  packet registry used to construct this packet
	 * @param data packet's data
	 * @throws IOException never thrown
	 */
	public ServerLoginSuccessPacket(PacketRegistry reg, byte[] data) throws IOException {
		super(reg, data);
		VarInputStream is = getInputStream();
		uuid = is.readUUID().toString();
		username = is.readString();
	}

	/**
	 * Get player's UUID
	 * 
	 * @return players's UUID
	 */
	public String getUuid() {
		return uuid;
	}

	/**
	 * Get player's username
	 * 
	 * @return player's username
	 */
	public String getUsername() {
		return username;
	}

}
