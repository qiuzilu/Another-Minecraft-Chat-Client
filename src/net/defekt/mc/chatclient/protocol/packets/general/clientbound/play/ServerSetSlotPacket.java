package net.defekt.mc.chatclient.protocol.packets.general.clientbound.play;

import java.io.IOException;

import net.defekt.mc.chatclient.protocol.data.ItemStack;
import net.defekt.mc.chatclient.protocol.io.VarInputStream;
import net.defekt.mc.chatclient.protocol.packets.Packet;
import net.defekt.mc.chatclient.protocol.packets.PacketFactory;
import net.defekt.mc.chatclient.protocol.packets.PacketRegistry;

/**
 * Sent by server when a slot is changed
 * 
 * @author Defective4
 *
 */
public class ServerSetSlotPacket extends Packet {

	private final int windowID;
	private final short slot;
	private final ItemStack item;

	/**
	 * Constructs {@link ServerSetSlotPacket}
	 * 
	 * @param reg  packet registry used to construct this packet
	 * @param data packet's data
	 * @throws IOException never thrown
	 */
	public ServerSetSlotPacket(PacketRegistry reg, byte[] data) throws IOException {
		super(reg, data);
		VarInputStream is = getInputStream();
		windowID = is.readByte();
		slot = is.readShort();
		item = is.readSlotData(PacketFactory.getProtocolFor(reg));
	}

	/**
	 * Get window's ID
	 * 
	 * @return window ID
	 */
	public int getWindowID() {
		return windowID;
	}

	/**
	 * Get slot of this item
	 * 
	 * @return item slot
	 */
	public short getSlot() {
		return slot;
	}

	/**
	 * Get net item stack
	 * 
	 * @return item stack
	 */
	public ItemStack getItem() {
		return item;
	}

}
