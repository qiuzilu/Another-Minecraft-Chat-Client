package net.defekt.mc.chatclient.protocol.packets.v47.serverbound.play;

import net.defekt.mc.chatclient.protocol.packets.Packet;
import net.defekt.mc.chatclient.protocol.packets.PacketRegistry;
import net.defekt.mc.chatclient.protocol.packets.general.serverbound.play.ClientResourcePackStatusPacket.Status;

public class ClientResourcePackStatusPacket extends Packet {

	public ClientResourcePackStatusPacket(PacketRegistry reg, String hash, Status status) {
		super(reg);
		putString(hash);
		putVarInt(status.num);
	}

}
