package net.defekt.mc.chatclient.protocol.packets.general.serverbound.play;

import net.defekt.mc.chatclient.protocol.packets.Packet;
import net.defekt.mc.chatclient.protocol.packets.PacketRegistry;

/**
 * Sent by client to perform an action (sneaking, sprinting, etc.)
 * 
 * @author Defective4
 *
 */
public class ClientEntityActionPacket extends Packet {

	/**
	 * Action type
	 * 
	 * @author Defective4
	 *
	 */
	public enum EntityAction {
		START_SNEAKING(0), STOP_SNEAKING(1), LEAVE_BED(2), START_SPRINTING(3), STOP_SPRINTING(4);

		protected final int id;

		private EntityAction(int id) {
			this.id = id;
		}
	}

	/**
	 * Constructs new {@link ClientEntityActionPacket}
	 * 
	 * @param reg      packet registry used to construct this packet
	 * @param entityID client's entity Id
	 * @param action   action to perform
	 */
	public ClientEntityActionPacket(PacketRegistry reg, Integer entityID, EntityAction action) {
		super(reg);
		putVarInt(entityID);
		putVarInt(action.id);
		putVarInt(0);
	}

}
