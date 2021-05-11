package net.defekt.mc.chatclient.protocol.packets.general.serverbound.play;

import net.defekt.mc.chatclient.protocol.packets.Packet;
import net.defekt.mc.chatclient.protocol.packets.PacketRegistry;

/**
 * Sent by client when it performs an in-game action
 * 
 * @author Defective4
 *
 */
public class ClientPlayerDiggingPacket extends Packet {

	/**
	 * Actions enum
	 * 
	 * @author Defective4
	 *
	 */
	public static enum Status {
		/**
		 * Start digging
		 */
		STARTED_DIGGING(0),
		/**
		 * Cancel digging
		 */
		CANCELLED_DIGGING(1),
		/**
		 * Finish digging
		 */
		FINISHED_DIGGING(2),
		/**
		 * Drop an item stack
		 */
		DROP_ITEM_STACK(3),
		/**
		 * Drop an item
		 */
		DROP_ITEM(4),
		/**
		 * Finish an action (shot a bow, finish eating, etc.)
		 */
		FINISH_ACTION(5),
		/**
		 * Swap items in hands (< 1.8)
		 */
		SWAP_ITEMS(6);

		private final int status;

		private Status(int status) {
			this.status = status;
		}

		/**
		 * Get status number
		 * 
		 * @return status number
		 */
		public int getStatus() {
			return status;
		}
	}

	/**
	 * Constructs new {@link ClientPlayerDiggingPacket}
	 * 
	 * @param reg    packet registry used to construct this packet
	 * @param status action to perform
	 * @param x      action X
	 * @param y      action Y
	 * @param z      action Z
	 * @param face   action face
	 */
	public ClientPlayerDiggingPacket(PacketRegistry reg, Status status, Integer x, Integer y, Integer z, Byte face) {
		super(reg);
		putVarInt(status.getStatus());
		putLong(((x & 0x3FFFFFF) << 38) | ((z & 0x3FFFFFF) << 12) | (y & 0xFFF));
		putByte(face);
	}

}
