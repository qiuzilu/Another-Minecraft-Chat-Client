package net.defekt.mc.chatclient.protocol.packets.general.serverbound.play;

import net.defekt.mc.chatclient.protocol.packets.Packet;
import net.defekt.mc.chatclient.protocol.packets.PacketRegistry;

public class ClientResourcePackStatusPacket extends Packet {

	public enum Status {
		DECLINED(1), ACCEPTED(3), LOADED(0), FAILED(2);

		public final int num;

		private Status(int num) {
			this.num = num;
		}
	}

	public ClientResourcePackStatusPacket(PacketRegistry reg, Status status) {
		super(reg);
		putVarInt(status.num);
	}

}
