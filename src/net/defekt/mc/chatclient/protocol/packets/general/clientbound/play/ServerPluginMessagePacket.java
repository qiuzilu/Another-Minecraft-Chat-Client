package net.defekt.mc.chatclient.protocol.packets.general.clientbound.play;

import java.io.IOException;

import net.defekt.mc.chatclient.protocol.io.VarInputStream;
import net.defekt.mc.chatclient.protocol.io.VarOutputStream;
import net.defekt.mc.chatclient.protocol.packets.Packet;
import net.defekt.mc.chatclient.protocol.packets.PacketRegistry;

public class ServerPluginMessagePacket extends Packet {

	private final String channel;
	private final byte[] data;

	public ServerPluginMessagePacket(PacketRegistry reg, byte[] data) throws IOException {
		super(reg, data);
		VarInputStream is = getInputStream();
		this.channel = is.readString();
		this.data = new byte[data.length - this.channel.length()
				- VarOutputStream.checkVarIntSize(this.channel.length())];
		is.readFully(this.data);
	}

	public byte[] getDataF() {
		return data;
	}

	public String getChannel() {
		return channel;
	}

}
