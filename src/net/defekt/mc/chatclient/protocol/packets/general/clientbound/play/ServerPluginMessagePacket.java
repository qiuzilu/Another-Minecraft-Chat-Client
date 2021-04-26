package net.defekt.mc.chatclient.protocol.packets.general.clientbound.play;

import java.io.IOException;

import net.defekt.mc.chatclient.protocol.io.VarInputStream;
import net.defekt.mc.chatclient.protocol.io.VarOutputStream;
import net.defekt.mc.chatclient.protocol.packets.Packet;
import net.defekt.mc.chatclient.protocol.packets.PacketRegistry;

/**
 * A plugin message packet sent by server
 * 
 * @author Defective4
 *
 */
public class ServerPluginMessagePacket extends Packet {

	private final String channel;
	private final byte[] data;

	/**
	 * constructs {@link ServerPluginMessagePacket}
	 * 
	 * @param reg  packet registry used to construct this packet
	 * @param data packet's data
	 * @throws IOException never thrown
	 */
	public ServerPluginMessagePacket(PacketRegistry reg, byte[] data) throws IOException {
		super(reg, data);
		VarInputStream is = getInputStream();
		this.channel = is.readString();
		this.data = new byte[data.length - this.channel.length()
				- VarOutputStream.checkVarIntSize(this.channel.length())];
		is.readFully(this.data);
	}

	/**
	 * Get data of this message
	 * 
	 * @return message data
	 */
	public byte[] getDataF() {
		return data;
	}

	/**
	 * Get message channel
	 * 
	 * @return message channel
	 */
	public String getChannel() {
		return channel;
	}

}
