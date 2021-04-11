package net.defekt.mc.chatclient.protocol;

import java.io.IOException;
import java.io.OutputStream;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import net.defekt.mc.chatclient.protocol.data.ChatMessage;
import net.defekt.mc.chatclient.protocol.data.PlayerInfo;
import net.defekt.mc.chatclient.protocol.packets.Packet;
import net.defekt.mc.chatclient.protocol.packets.PacketFactory;
import net.defekt.mc.chatclient.protocol.packets.PacketRegistry;
import net.defekt.mc.chatclient.protocol.packets.PacketRegistry.State;
import net.defekt.mc.chatclient.protocol.packets.general.clientbound.play.ServerChatMessagePacket.Position;
import net.defekt.mc.chatclient.protocol.packets.general.clientbound.play.ServerPlayerListItemPacket.Action;
import net.defekt.mc.chatclient.protocol.packets.general.clientbound.play.ServerPlayerPositionAndLookPacket;
import net.defekt.mc.chatclient.protocol.packets.general.serverbound.play.ClientResourcePackStatusPacket.Status;
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
			switch (packet.getClass().getSimpleName()) {
				case "ServerStatisticsPacket": {
					Map<String, Integer> values = (Map<String, Integer>) packet.accessPacketMethod("getValues");
					for (ClientListener l : cl.getClientListeners())
						l.statisticsReceived(values);
					break;
				}
				case "ServerPlayerListItemPacket": {
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
							playersTabList.put(pid, new PlayerInfo(old.getName(), old.getTexture(),
									old.getDisplayName(), (int) packet.accessPacketMethod("getPing"), pid));
							break;
						}
						case UPDATE_GAMEMODE: {
							break;
						}
					}

					break;
				}
				case "ServerJoinGamePacket": {
					int entityID = (int) packet.accessPacketMethod("getEntityID");
					cl.setEntityID(entityID);
					cl.getPlayersTabList().clear();

					if (!up.isSendMCBrand())
						break;

					String cname = protocol > 340 ? "minecraft:brand" : "MC|Brand";

					os.write(PacketFactory
							.constructPacket(registry, "ClientPluginMessagePacket", cname, up.getBrand().getBytes())
							.getData(cl.isCompressionEnabled()));
					os.write(PacketFactory.constructPacket(registry, "ClientStatusPacket", 0)
							.getData(cl.isCompressionEnabled()));
					os.write(PacketFactory.constructPacket(registry, "ClientStatusPacket", 1)
							.getData(cl.isCompressionEnabled()));
					break;
				}
				case "ServerUpdateHealthPacket": {
					if (((float) packet.accessPacketMethod("getHealth") <= 0))
						os.write(PacketFactory.constructPacket(registry, "ClientStatusPacket", 0)
								.getData(cl.isCompressionEnabled()));
					float hp = (float) packet.accessPacketMethod("getHealth");
					int food = (int) packet.accessPacketMethod("getFood");
					for (ClientListener ls : cl.getClientListeners())
						ls.healthUpdate(hp, food);
					break;
				}
				case "ServerLoginSuccessPacket": {
					cl.setCurrentState(State.IN);
					break;
				}
				case "ServerKeepAlivePacket": {
					if (up.isIgnoreKeepAlive())
						break;
					new Thread(new Runnable() {

						@Override
						public void run() {
							try {
								Thread.sleep(up.getAdditionalPing());
								if (protocol >= 339) {
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
					break;
				}
				case "ServerChatMessagePacket": {
					String json = (String) packet.accessPacketMethod("getMessage");

					for (ClientListener ls : cl.getClientListeners()) {
						ls.messageReceived(ChatMessage.parse(json),
								(Position) packet.accessPacketMethod("getPosition"));
					}
					break;
				}
				case "ServerPlayerPositionAndLookPacket": {
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
						os.write(new ClientTeleportConfirmPacket(registry, teleportID)
								.getData(cl.isCompressionEnabled()));
					}

					os.write(PacketFactory
							.constructPacket(registry, "ClientPlayerPositionAndLookPacket", x, y, z, yaw, pitch, true)
							.getData(cl.isCompressionEnabled()));

					synchronized (cl.getLock()) {
						cl.getLock().notify();
					}
					break;
				}
				case "ServerDisconnectPacket": {
					String json = (String) packet.accessPacketMethod("getReason");

					for (ClientListener ls : cl.getClientListeners())
						ls.disconnected(ChatMessage.parse(json));
					cl.close();
					break;
				}
				case "ServerPluginMessagePacket": {
					String channel = (String) packet.accessPacketMethod("getChannel");
					byte[] data = (byte[]) packet.accessPacketMethod("getDataF");

					String commonChannelName = channel.replace("minecraft:", "").replace("MC|", "").toLowerCase();

					if (commonChannelName.equals("register"))
						os.write(PacketFactory.constructPacket(registry, "ClientPluginMessagePacket", channel, data)
								.getData(cl.isCompressionEnabled()));

					break;
				}
				case "ServerResourcePackSendPacket": {
					if (up.isShowResourcePackMessages())
						for (ClientListener ls : cl.getClientListeners())
							ls.messageReceived(
									up.getResourcePackMessage().replace("%res",
											(String) packet.accessPacketMethod("getUrl")),
									up.getResourcePackMessagePosition());

					if (up.getResourcePackBehavior() == Status.LOADED) {
						if (protocol <= 110)
							os.write(PacketFactory
									.constructPacket(registry, "ClientResourcePackStatusPacket",
											packet.accessPacketMethod("getHash"), Status.ACCEPTED)
									.getData(cl.isCompressionEnabled()));
						else
							os.write(PacketFactory
									.constructPacket(registry, "ClientResourcePackStatusPacket", Status.ACCEPTED)
									.getData(cl.isCompressionEnabled()));
					}

					if (protocol <= 110)
						os.write(PacketFactory
								.constructPacket(registry, "ClientResourcePackStatusPacket",
										packet.accessPacketMethod("getHash"), up.getResourcePackBehavior())
								.getData(cl.isCompressionEnabled()));
					else
						os.write(PacketFactory.constructPacket(registry, "ClientResourcePackStatusPacket",
								up.getResourcePackBehavior()).getData(cl.isCompressionEnabled()));

					break;
				}
			}

		} catch (IOException e) {
			e.printStackTrace();
		}
	}

}
