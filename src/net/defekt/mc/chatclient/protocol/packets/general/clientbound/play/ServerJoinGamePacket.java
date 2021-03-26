package net.defekt.mc.chatclient.protocol.packets.general.clientbound.play;

import java.io.IOException;

import net.defekt.mc.chatclient.protocol.packets.Packet;
import net.defekt.mc.chatclient.protocol.packets.PacketRegistry;

public class ServerJoinGamePacket extends Packet {

	private final int entityID;
	
	public ServerJoinGamePacket(PacketRegistry reg, byte[] data) throws IOException {
		super(reg, data);
		this.entityID = getInputStream().readInt();
	}

	public int getEntityID() {
		return entityID;
	}

}
