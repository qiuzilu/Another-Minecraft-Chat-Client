package net.defekt.mc.chatclient.protocol.packets.general.serverbound.play;

import net.defekt.mc.chatclient.protocol.packets.Packet;
import net.defekt.mc.chatclient.protocol.packets.PacketRegistry;

/**
 * Sent by client as response to server's resource pack
 * 
 * @author Defective4
 *
 */
public class ClientResourcePackStatusPacket extends Packet {

	/**
	 * Resource pack status
	 * 
	 * @author Defective4
	 *
	 */
	public enum Status {
		/**
		 * Incoming resource pack will be declined
		 */
		DECLINED(1),
		/**
		 * Incoming resource pack will be accepted
		 */
		ACCEPTED(3),
		/**
		 * Incoming resource pack will be loaded
		 */
		LOADED(0),
		/**
		 * Incoming resource pack will fail to load
		 */
		FAILED(2);

		/**
		 * Status number
		 */
		public final int num;

		private Status(int num) {
			this.num = num;
		}
	}

	/**
	 * Constructs new {@link ClientResourcePackStatusPacket}
	 * 
	 * @param reg    packet registry used to construct this packet
	 * @param status resource pack status
	 */
	public ClientResourcePackStatusPacket(PacketRegistry reg, Status status) {
		super(reg);
		putVarInt(status.num);
	}

}
