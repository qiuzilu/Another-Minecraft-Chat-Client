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
		/**
		 * Client will start sneaking.
		 */
		START_SNEAKING(0),
		/**
		 * Client will stop sneaking.
		 */
		STOP_SNEAKING(1),
		/**
		 * Client has to leave bed.
		 */
		LEAVE_BED(2),
		/**
		 * Client will start sprinting.
		 */
		START_SPRINTING(3),
		/**
		 * Client will stop sprinting.
		 */
		STOP_SPRINTING(4);

		/**
		 * Action ID
		 */
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
