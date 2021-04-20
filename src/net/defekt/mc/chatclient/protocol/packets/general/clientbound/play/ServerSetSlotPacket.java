package net.defekt.mc.chatclient.protocol.packets.general.clientbound.play;

import java.io.IOException;

import net.defekt.mc.chatclient.protocol.data.ItemStack;
import net.defekt.mc.chatclient.protocol.io.VarInputStream;
import net.defekt.mc.chatclient.protocol.packets.Packet;
import net.defekt.mc.chatclient.protocol.packets.PacketFactory;
import net.defekt.mc.chatclient.protocol.packets.PacketRegistry;

public class ServerSetSlotPacket extends Packet {

	private final int windowID;
	private final short slot;
	private final ItemStack item;

	public ServerSetSlotPacket(PacketRegistry reg, byte[] data) throws IOException {
		super(reg, data);
		VarInputStream is = getInputStream();
		windowID = is.readByte();
		slot = is.readShort();
		item = is.readSlotData(PacketFactory.getProtocolFor(reg));
	}

	public int getWindowID() {
		return windowID;
	}

	public short getSlot() {
		return slot;
	}

	public ItemStack getItem() {
		return item;
	}

}
