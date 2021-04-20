package net.defekt.mc.chatclient.protocol.packets.general.clientbound.play;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import net.defekt.mc.chatclient.protocol.data.ItemStack;
import net.defekt.mc.chatclient.protocol.io.VarInputStream;
import net.defekt.mc.chatclient.protocol.packets.Packet;
import net.defekt.mc.chatclient.protocol.packets.PacketFactory;
import net.defekt.mc.chatclient.protocol.packets.PacketRegistry;

public class ServerWindowItemsPacket extends Packet {

	private final int windowID;
	private final List<ItemStack> items = new ArrayList<ItemStack>();

	public ServerWindowItemsPacket(PacketRegistry reg, byte[] data) throws IOException {
		super(reg, data);
		VarInputStream is = getInputStream();
		windowID = is.readUnsignedByte();
		short count = is.readShort();
		for (int x = 0; x < count; x++) {
			items.add(is.readSlotData(PacketFactory.getProtocolFor(reg)));
		}
	}

	public int getWindowID() {
		return windowID;
	}

	public List<ItemStack> getItems() {
		return new ArrayList<ItemStack>(items);
	}

}
