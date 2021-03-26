package net.defekt.mc.chatclient.protocol.packets.general.serverbound.play;

import net.defekt.mc.chatclient.protocol.packets.Packet;
import net.defekt.mc.chatclient.protocol.packets.PacketRegistry;

public class ClientEntityActionPacket extends Packet {

	public enum EntityAction {
		START_SNEAKING(0), STOP_SNEAKING(1), LEAVE_BED(2), START_SPRINTING(3), STOP_SPRINTING(4);

		protected final int id;

		private EntityAction(int id) {
			this.id = id;
		}
	}

	public ClientEntityActionPacket(PacketRegistry reg, Integer entityID, EntityAction action) {
		super(reg);
		putVarInt(entityID);
		putVarInt(action.id);
		putVarInt(0);
	}

}
