package net.defekt.mc.chatclient.protocol.packets.general.serverbound.play;

import net.defekt.mc.chatclient.protocol.data.ItemStack;
import net.defekt.mc.chatclient.protocol.packets.Packet;
import net.defekt.mc.chatclient.protocol.packets.PacketRegistry;

public class ClientWindowClickPacket extends Packet {

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
