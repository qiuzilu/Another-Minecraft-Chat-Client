package net.defekt.mc.chatclient.protocol.packets;

public class HandshakePacket extends Packet {

	public HandshakePacket(PacketRegistry reg, int protocol, String host, int port, int state) {
		super(reg);
		this.id = 0x00;
		putVarInt(protocol);
		putString(host);
		putShort(port);
		putVarInt(state);
	}

}
