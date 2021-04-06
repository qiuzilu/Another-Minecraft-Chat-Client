package net.defekt.mc.chatclient.protocol.packets;

/**
 * A HandshakePacket used in switching states on server.<br>
 * It's used in login and server list ping, and unlike other packets it's
 * independent from PacketRegistry - its ID will always be 0x00
 * 
 * @author Defective4
 *
 */
public class HandshakePacket extends Packet {

	/**
	 * Construct new Handshake Packet
	 * 
	 * @param reg      packet registry that will be used to determine ID for this
	 *                 packet. It's unused for {@link HandshakePacket}, but still
	 *                 can't be null
	 * @param protocol client's protocol
	 * @param host     server's host
	 * @param port     server's port
	 * @param state    next state:<br>
	 *                 1 - status<br>
	 *                 2 - login
	 */
	public HandshakePacket(PacketRegistry reg, int protocol, String host, int port, int state) {
		super(reg);
		this.id = 0x00;
		putVarInt(protocol);
		putString(host);
		putShort(port);
		putVarInt(state);
	}

}
