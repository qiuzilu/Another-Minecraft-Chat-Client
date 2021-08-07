package net.defekt.mc.chatclient.protocol;

import java.io.IOException;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import net.defekt.mc.chatclient.protocol.data.ChatMessages;
import net.defekt.mc.chatclient.protocol.data.ItemStack;
import net.defekt.mc.chatclient.protocol.data.ItemsWindow;
import net.defekt.mc.chatclient.protocol.data.PlayerInfo;
import net.defekt.mc.chatclient.protocol.packets.Packet;
import net.defekt.mc.chatclient.protocol.packets.PacketFactory;
import net.defekt.mc.chatclient.protocol.packets.PacketRegistry;
import net.defekt.mc.chatclient.protocol.packets.PacketRegistry.State;
import net.defekt.mc.chatclient.protocol.packets.general.clientbound.login.ServerLoginSuccessPacket;
import net.defekt.mc.chatclient.protocol.packets.general.clientbound.play.ServerChatMessagePacket;
import net.defekt.mc.chatclient.protocol.packets.general.clientbound.play.ServerChatMessagePacket.Position;
import net.defekt.mc.chatclient.protocol.packets.general.clientbound.play.ServerCloseWindowPacket;
import net.defekt.mc.chatclient.protocol.packets.general.clientbound.play.ServerConfirmTransactionPacket;
import net.defekt.mc.chatclient.protocol.packets.general.clientbound.play.ServerDisconnectPacket;
import net.defekt.mc.chatclient.protocol.packets.general.clientbound.play.ServerJoinGamePacket;
import net.defekt.mc.chatclient.protocol.packets.general.clientbound.play.ServerKeepAlivePacket;
import net.defekt.mc.chatclient.protocol.packets.general.clientbound.play.ServerOpenWindowPacket;
import net.defekt.mc.chatclient.protocol.packets.general.clientbound.play.ServerPlayerListItemPacket;
import net.defekt.mc.chatclient.protocol.packets.general.clientbound.play.ServerPlayerListItemPacket.Action;
import net.defekt.mc.chatclient.protocol.packets.general.clientbound.play.ServerPlayerPositionAndLookPacket;
import net.defekt.mc.chatclient.protocol.packets.general.clientbound.play.ServerPluginMessagePacket;
import net.defekt.mc.chatclient.protocol.packets.general.clientbound.play.ServerResourcePackSendPacket;
import net.defekt.mc.chatclient.protocol.packets.general.clientbound.play.ServerSetSlotPacket;
import net.defekt.mc.chatclient.protocol.packets.general.clientbound.play.ServerStatisticsPacket;
import net.defekt.mc.chatclient.protocol.packets.general.clientbound.play.ServerTimeUpdatePacket;
import net.defekt.mc.chatclient.protocol.packets.general.clientbound.play.ServerUpdateHealthPacket;
import net.defekt.mc.chatclient.protocol.packets.general.clientbound.play.ServerWindowItemsPacket;
import net.defekt.mc.chatclient.protocol.packets.general.serverbound.play.ClientTeleportConfirmPacket;
import net.defekt.mc.chatclient.ui.Main;
import net.defekt.mc.chatclient.ui.UserPreferences;

/**
 * An implementation of {@link InternalPacketListener} responsible for handling
 * all packets received from server. It is added to every instance of
 * {@link MinecraftClient} after successfully connecting to server.
 * 
 * @see MinecraftClient
 * @see ClientListener
 * @author Defective4
 *
 */
public class ClientPacketListener implements InternalPacketListener {

	private final OutputStream os;
	private final int protocol;
	private final MinecraftClient cl;
	private final UserPreferences up = Main.up;

	/**
	 * Constructs packet listener bound to specified client
	 * 
	 * @param client A Minecraft client this listener is bound o
	 */
	protected ClientPacketListener(MinecraftClient client) {
		this.cl = client;
		this.os = cl.getOutputStream();
		this.protocol = cl.getProtocol();
	}

	@SuppressWarnings("unchecked")
	@Override
	public void packetReceived(Packet packet, PacketRegistry registry) {
		try {
			if (packet instanceof ServerTimeUpdatePacket) {
				ServerTimeUpdatePacket sti = (ServerTimeUpdatePacket) packet;
				for (ClientListener cls : cl.getClientListeners())
					cls.timeUpdated(sti.getTime(), sti.getWorldAge());
			} else if (packet instanceof ServerConfirmTransactionPacket) {
				if (!up.isEnableInventoryHandling() || protocol == 755)
					return;

				int windowID = (int) packet.accessPacketMethod("getWindowID");
				short actionID = (short) packet.accessPacketMethod("getActionID");
				boolean accepted = (boolean) packet.accessPacketMethod("isAccepted");

				ItemsWindow win = windowID == 0 ? cl.getInventory()
						: cl.getOpenWindows().containsKey(windowID) ? cl.getOpenWindows().get(windowID) : null;

				if (win != null)
					if (accepted)
						win.finishTransaction(actionID);
					else
						win.cancelTransaction(actionID);

				if (!accepted)
					cl.sendPacket(PacketFactory.constructPacket(registry, "ClientConfirmTransactionPacket",
							(byte) windowID, actionID, accepted));

			} else if (packet instanceof ServerSetSlotPacket) {
				if (!up.isEnableInventoryHandling() || protocol == 755)
					return;

				int windowID = (int) packet.accessPacketMethod("getWindowID");

				short slot = (short) packet.accessPacketMethod("getSlot");
				ItemStack item = (ItemStack) packet.accessPacketMethod("getItem");
				if (windowID == 0
						|| (cl.getOpenWindows().containsKey(windowID) && cl.getOpenWindows().get(windowID) != null)) {
					ItemsWindow iWin = windowID == 0 ? cl.getInventory() : cl.getOpenWindows().get(windowID);
					iWin.putItem(slot, item);
				}
			} else if (packet instanceof ServerCloseWindowPacket) {
				if (!up.isEnableInventoryHandling() || protocol == 755)
					return;

				int windowID = (int) packet.accessPacketMethod("getWindowID");
				if (cl.getOpenWindows().containsKey(windowID) && cl.getOpenWindows().get(windowID) != null)
					cl.getOpenWindows().get(windowID).closeWindow();
				else if (windowID == 0)
					cl.getInventory().closeWindow();
			} else if (packet instanceof ServerWindowItemsPacket) {
				if (!up.isEnableInventoryHandling() || protocol == 755)
					return;

				int windowID = (int) packet.accessPacketMethod("getWindowID");
				List<ItemStack> items = (List<ItemStack>) packet.accessPacketMethod("getItems");
				if (windowID == 0
						|| (cl.getOpenWindows().containsKey(windowID) && cl.getOpenWindows().get(windowID) != null)) {
					ItemsWindow iWin = windowID == 0 ? cl.getInventory() : cl.getOpenWindows().get(windowID);
					for (int x = 0; x < items.size(); x++)
						iWin.putItem(x, items.get(x));
				}
			} else if (packet instanceof ServerOpenWindowPacket
					|| packet instanceof net.defekt.mc.chatclient.protocol.packets.alternate.clientbound.play.ServerOpenWindowPacket) {
				if (!up.isEnableInventoryHandling() || protocol == 755)
					return;

				int windowID = (int) packet.accessPacketMethod("getWindowID");
				String windowTitle = ChatMessages
						.removeColors(ChatMessages.parse((String) packet.accessPacketMethod("getWindowTitle")));
				int slots = 0;
				if (packet instanceof ServerOpenWindowPacket)
					slots = ((int) packet.accessPacketMethod("getSlots"));
				else {
					int windowType = (int) packet.accessPacketMethod("getWindowType");
					if (windowID <= 5)
						slots = (windowType + 1) * 9;
					else
						return;
				}

				ItemsWindow win = new ItemsWindow(windowTitle, slots, windowID, cl, registry);
				cl.setOpenWindow(windowID, win);
				for (ClientListener l : cl.getClientListeners())
					l.windowOpened(windowID, win, registry);

			} else if (packet instanceof ServerStatisticsPacket) {
				Map<String, Integer> values = (Map<String, Integer>) packet.accessPacketMethod("getValues");
				for (ClientListener l : cl.getClientListeners())
					l.statisticsReceived(values);
			} else if (packet instanceof ServerPlayerListItemPacket) {
				HashMap<UUID, PlayerInfo> playersTabList = cl.getPlayersTabList();
				Action action = (Action) packet.accessPacketMethod("getAction");
				UUID pid = (UUID) packet.accessPacketMethod("getUUID");
				switch (action) {
					case ADD_PLAYER: {
						playersTabList.put(pid,
								new PlayerInfo((String) packet.accessPacketMethod("getPlayerName"),
										(String) packet.accessPacketMethod("getTextures"),
										(String) packet.accessPacketMethod("getDisplayName"),
										(int) packet.accessPacketMethod("getPing"), pid));
						break;
					}
					case UPDATE_DISPLAY_NAME: {
						if (!playersTabList.containsKey(pid))
							break;
						PlayerInfo old = playersTabList.get(pid);
						playersTabList.put(pid, new PlayerInfo(old.getName(), old.getTexture(),
								(String) packet.accessPacketMethod("getDisplayName"), old.getPing(), pid));
						break;
					}
					case REMOVE_PLAYER: {
						playersTabList.remove(pid);
						break;
					}
					case UPDATE_LATENCY: {
						if (!playersTabList.containsKey(pid))
							break;
						PlayerInfo old = playersTabList.get(pid);
						playersTabList.put(pid, new PlayerInfo(old.getName(), old.getTexture(), old.getDisplayName(),
								(int) packet.accessPacketMethod("getPing"), pid));
						break;
					}
					case UPDATE_GAMEMODE: {
						break;
					}
				}

			} else if (packet instanceof ServerJoinGamePacket) {
				int entityID = (int) packet.accessPacketMethod("getEntityID");
				cl.setEntityID(entityID);
				cl.getPlayersTabList().clear();

				if (!up.isSendMCBrand())
					return;

				String cname = protocol > 340 ? "minecraft:brand" : "MC|Brand";

				for (int x = 0; x < cl.getInventory().getSize(); x++)
					cl.getInventory().putItem(x, new ItemStack((short) 0, 1, (short) 0, null));

				os.write(PacketFactory
						.constructPacket(registry, "ClientPluginMessagePacket", cname, up.getBrand().getBytes())
						.getData(cl.isCompressionEnabled()));

				for (int x = 0; x <= 1; x++)
					os.write(PacketFactory.constructPacket(registry, "ClientStatusPacket", x)
							.getData(cl.isCompressionEnabled()));
			} else if (packet instanceof ServerUpdateHealthPacket) {
				if (((float) packet.accessPacketMethod("getHealth") <= 0))
					os.write(PacketFactory.constructPacket(registry, "ClientStatusPacket", 0)
							.getData(cl.isCompressionEnabled()));
				float hp = (float) packet.accessPacketMethod("getHealth");
				int food = (int) packet.accessPacketMethod("getFood");
				for (ClientListener ls : cl.getClientListeners())
					ls.healthUpdate(hp, food);
			} else if (packet instanceof ServerLoginSuccessPacket)
				cl.setCurrentState(State.IN);
			else if (packet instanceof ServerKeepAlivePacket
					|| packet instanceof net.defekt.mc.chatclient.protocol.packets.alternate.clientbound.play.ServerKeepAlivePacket) {
				if (up.isIgnoreKeepAlive())
					return;
				new Thread(new Runnable() {

					@Override
					public void run() {
						try {
							Thread.sleep(up.getAdditionalPing());
							if (packet instanceof ServerKeepAlivePacket) {
								long id = (long) packet.accessPacketMethod("getId");
								os.write(PacketFactory.constructPacket(registry, "ClientKeepAlivePacket", id)
										.getData(cl.isCompressionEnabled()));
							} else {
								int id = (int) packet.accessPacketMethod("getId");
								os.write(PacketFactory.constructPacket(registry, "ClientKeepAlivePacket", id)
										.getData(cl.isCompressionEnabled()));
							}
						} catch (Exception e) {
							e.printStackTrace();
						}
					}
				}).start();
			} else if (packet instanceof ServerChatMessagePacket) {
				String json = (String) packet.accessPacketMethod("getMessage");

				for (ClientListener ls : cl.getClientListeners())
					ls.messageReceived(ChatMessages.parse(json), (Position) packet.accessPacketMethod("getPosition"));
			} else if (packet instanceof ServerPlayerPositionAndLookPacket
					|| packet instanceof net.defekt.mc.chatclient.protocol.packets.alternate.clientbound.play.ServerPlayerPositionAndLookPacket) {
				double x = (double) packet.accessPacketMethod("getX");
				double y = (double) packet.accessPacketMethod("getY");
				double z = (double) packet.accessPacketMethod("getZ");
				float yaw = (float) packet.accessPacketMethod("getYaw");
				float pitch = (float) packet.accessPacketMethod("getPitch");

				cl.setX(x);
				cl.setY(y);
				cl.setZ(z);
				cl.setYaw(yaw);
				cl.setPitch(pitch);

				if (packet instanceof ServerPlayerPositionAndLookPacket) {
					int teleportID = (int) packet.accessPacketMethod("getTeleportID");
					os.write(new ClientTeleportConfirmPacket(registry, teleportID).getData(cl.isCompressionEnabled()));
				}

				os.write(PacketFactory
						.constructPacket(registry, "ClientPlayerPositionAndLookPacket", x, y, z, yaw, pitch, true)
						.getData(cl.isCompressionEnabled()));

				synchronized (cl.getLock()) {
					cl.getLock().notify();
				}
			} else if (packet instanceof ServerDisconnectPacket) {
				String json = (String) packet.accessPacketMethod("getReason");

				for (ClientListener ls : cl.getClientListeners())
					ls.disconnected(ChatMessages.parse(json));
				cl.close();
			} else if (packet instanceof ServerPluginMessagePacket) {
				String channel = (String) packet.accessPacketMethod("getChannel");
				byte[] data = (byte[]) packet.accessPacketMethod("getDataF");

				String commonChannelName = channel.replace("minecraft:", "").replace("MC|", "").toLowerCase();

				if (commonChannelName.equals("register"))
					os.write(PacketFactory.constructPacket(registry, "ClientPluginMessagePacket", channel, data)
							.getData(cl.isCompressionEnabled()));
			} else if (packet instanceof ServerResourcePackSendPacket) {
				if (up.isShowResourcePackMessages())
					for (ClientListener ls : cl.getClientListeners())
						ls.messageReceived(
								up.getResourcePackMessage().replace("%res",
										(String) packet.accessPacketMethod("getUrl")),
								up.getResourcePackMessagePosition());

				boolean altResourcePack = protocol <= 110;

				List<Object> rsPackArgs = new ArrayList<>();
				if (altResourcePack)
					rsPackArgs.add(packet.accessPacketMethod("getHash"));

				rsPackArgs.add(up.getResourcePackBehavior());

				Object[] rsPackArgsArray = new Object[rsPackArgs.size()];
				rsPackArgsArray = rsPackArgs.toArray(rsPackArgsArray);

				os.write(PacketFactory.constructPacket(registry, "ClientResourcePackStatusPacket", rsPackArgsArray)
						.getData(cl.isCompressionEnabled()));
			}

		} catch (IOException e) {
			e.printStackTrace();
		}
	}

}
