package net.defekt.mc.chatclient.protocol.packets.general.serverbound.play;

import net.defekt.mc.chatclient.protocol.data.ItemStack;
import net.defekt.mc.chatclient.protocol.packets.Packet;
import net.defekt.mc.chatclient.protocol.packets.PacketRegistry;

/**
 * Sent by client when it clicks on an item in a window
 * 
 * @author Defective4
 *
 */
public class ClientWindowClickPacket extends Packet {

	/**
	 * Constructs new {@link ClientWindowClickPacket}
	 * 
	 * @param reg      packet registry used to construct this packet
	 * @param windowID window ID
	 * @param slot     clicked slot
	 * @param button   button used to click
	 * @param action   action ID
	 * @param mode     click mode
	 * @param item     clicked item (currently must be empty)
	 */
	public ClientWindowClickPacket(PacketRegistry reg, Integer windowID, Short slot, Byte button, Short action,
			Integer mode, ItemStack item) {
		super(reg);
		putByte(windowID);
		putShort(slot);
		putByte(button);
		putShort(action);
		putVarInt(mode);
		putSlotData(item);
	}

}
