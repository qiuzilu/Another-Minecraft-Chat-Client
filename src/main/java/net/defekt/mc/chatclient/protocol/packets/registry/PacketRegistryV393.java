package net.defekt.mc.chatclient.protocol.packets.registry;

import java.util.HashMap;
import java.util.Map;

import net.defekt.mc.chatclient.protocol.packets.Packet;
import net.defekt.mc.chatclient.protocol.packets.PacketRegistry;
import net.defekt.mc.chatclient.protocol.packets.general.clientbound.login.ServerLoginSuccessPacket;
import net.defekt.mc.chatclient.protocol.packets.general.clientbound.play.ServerChatMessagePacket;
import net.defekt.mc.chatclient.protocol.packets.general.clientbound.play.ServerCloseWindowPacket;
import net.defekt.mc.chatclient.protocol.packets.general.clientbound.play.ServerConfirmTransactionPacket;
import net.defekt.mc.chatclient.protocol.packets.general.clientbound.play.ServerDisconnectPacket;
import net.defekt.mc.chatclient.protocol.packets.general.clientbound.play.ServerJoinGamePacket;
import net.defekt.mc.chatclient.protocol.packets.general.clientbound.play.ServerKeepAlivePacket;
import net.defekt.mc.chatclient.protocol.packets.general.clientbound.play.ServerOpenWindowPacket;
import net.defekt.mc.chatclient.protocol.packets.general.clientbound.play.ServerPlayerListItemPacket;
import net.defekt.mc.chatclient.protocol.packets.general.clientbound.play.ServerPlayerPositionAndLookPacket;
import net.defekt.mc.chatclient.protocol.packets.general.clientbound.play.ServerPluginMessagePacket;
import net.defekt.mc.chatclient.protocol.packets.general.clientbound.play.ServerResourcePackSendPacket;
import net.defekt.mc.chatclient.protocol.packets.general.clientbound.play.ServerSetSlotPacket;
import net.defekt.mc.chatclient.protocol.packets.general.clientbound.play.ServerUpdateHealthPacket;
import net.defekt.mc.chatclient.protocol.packets.general.clientbound.play.ServerWindowItemsPacket;
import net.defekt.mc.chatclient.protocol.packets.general.serverbound.login.ClientLoginRequestPacket;
import net.defekt.mc.chatclient.protocol.packets.general.serverbound.play.ClientChatMessagePacket;
import net.defekt.mc.chatclient.protocol.packets.general.serverbound.play.ClientCloseWindowPacket;
import net.defekt.mc.chatclient.protocol.packets.general.serverbound.play.ClientConfirmTransactionPacket;
import net.defekt.mc.chatclient.protocol.packets.general.serverbound.play.ClientEntityActionPacket;
import net.defekt.mc.chatclient.protocol.packets.general.serverbound.play.ClientHeldItemChangePacket;
import net.defekt.mc.chatclient.protocol.packets.general.serverbound.play.ClientKeepAlivePacket;
import net.defekt.mc.chatclient.protocol.packets.general.serverbound.play.ClientPlayerDiggingPacket;
import net.defekt.mc.chatclient.protocol.packets.general.serverbound.play.ClientPlayerPositionAndLookPacket;
import net.defekt.mc.chatclient.protocol.packets.general.serverbound.play.ClientPlayerPositionPacket;
import net.defekt.mc.chatclient.protocol.packets.general.serverbound.play.ClientPluginMessagePacket;
import net.defekt.mc.chatclient.protocol.packets.general.serverbound.play.ClientResourcePackStatusPacket;
import net.defekt.mc.chatclient.protocol.packets.general.serverbound.play.ClientStatusPacket;
import net.defekt.mc.chatclient.protocol.packets.general.serverbound.play.ClientUseItemPacket;
import net.defekt.mc.chatclient.protocol.packets.general.serverbound.play.ClientWindowClickPacket;

/**
 * A packet registry implementation for protocol 393
 * 
 * @author Defective4
 */
public class PacketRegistryV393 extends PacketRegistry {

	@Override
	public Map<Integer, Class<? extends Packet>> initLoginPackets() {
		return new HashMap<Integer, Class<? extends Packet>>() {
			private static final long serialVersionUID = 1L;
			{
				put(0x00, ClientLoginRequestPacket.class);
				put(0x02, ServerLoginSuccessPacket.class);
			}
		};
	}

	@Override
	protected Map<Integer, Class<? extends Packet>> initOutPackets() {
		return new HashMap<Integer, Class<? extends Packet>>() {
			private static final long serialVersionUID = 1L;
			{
				put(0x0E, ClientKeepAlivePacket.class);
				put(0x02, ClientChatMessagePacket.class);
				put(0x1D, ClientResourcePackStatusPacket.class);
				put(0x0A, ClientPluginMessagePacket.class);
				put(0x03, ClientStatusPacket.class);
				put(0x19, ClientEntityActionPacket.class);
				put(0x10, ClientPlayerPositionPacket.class);
				put(0x11, ClientPlayerPositionAndLookPacket.class);
				put(0x08, ClientWindowClickPacket.class);
				put(0x09, ClientCloseWindowPacket.class);
				put(0x21, ClientHeldItemChangePacket.class);
				put(0x18, ClientPlayerDiggingPacket.class);
				put(0x2A, ClientUseItemPacket.class);
				put(0x06, ClientConfirmTransactionPacket.class);
			}
		};
	}

	@Override
	protected Map<Integer, Class<? extends Packet>> initInPackets() {
		return new HashMap<Integer, Class<? extends Packet>>() {
			private static final long serialVersionUID = 1L;
			{
				put(0x21, ServerKeepAlivePacket.class);
				put(0x0E, ServerChatMessagePacket.class);
				put(0x32, ServerPlayerPositionAndLookPacket.class);
				put(0x1B, ServerDisconnectPacket.class);
				put(0x37, ServerResourcePackSendPacket.class);
				put(0x19, ServerPluginMessagePacket.class);
				put(0x44, ServerUpdateHealthPacket.class);
				put(0x25, ServerJoinGamePacket.class);
				put(0x30, ServerPlayerListItemPacket.class);
				put(0x13, ServerCloseWindowPacket.class);
				put(0x14, ServerOpenWindowPacket.class);
				put(0x15, ServerWindowItemsPacket.class);
				put(0x17, ServerSetSlotPacket.class);
				put(0x12, ServerConfirmTransactionPacket.class);
			}
		};
	}

}
