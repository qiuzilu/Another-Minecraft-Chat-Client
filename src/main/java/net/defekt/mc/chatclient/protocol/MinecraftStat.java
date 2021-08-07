package net.defekt.mc.chatclient.protocol;

import java.io.IOException;
import java.io.OutputStream;
import java.net.DatagramPacket;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.MulticastSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import net.defekt.mc.chatclient.protocol.data.ChatMessages;
import net.defekt.mc.chatclient.protocol.data.ModInfo;
import net.defekt.mc.chatclient.protocol.data.StatusInfo;
import net.defekt.mc.chatclient.protocol.io.VarInputStream;
import net.defekt.mc.chatclient.protocol.packets.HandshakePacket;
import net.defekt.mc.chatclient.protocol.packets.Packet;
import net.defekt.mc.chatclient.protocol.packets.PacketFactory;

/**
 * This class contains methods for getting status of a Minecraft server and
 * discovering servers on LAN netowrk
 * 
 * @see LANListener
 * @see StatusInfo
 * @author Defective4
 *
 */
public class MinecraftStat {
	/**
	 * Perform a Server List Ping on specified server to get its status
	 * 
	 * @param host hostname of target server
	 * @param port port of target server
	 * @return An object containing data returned by server
	 * @throws IOException thrown when there was an error pinging target server
	 */
	public static StatusInfo serverListPing(String host, int port) throws IOException {
		try (Socket soc = new Socket()) {
			soc.connect(new InetSocketAddress(host, port));

			OutputStream os = soc.getOutputStream();
			VarInputStream is = new VarInputStream(soc.getInputStream());

			Packet handshake = new HandshakePacket(PacketFactory.constructPacketRegistry(47), -1, host, port, 1);
			os.write(handshake.getData(false));
			os.write(0x01);
			os.write(0x00);

			int len = is.readVarInt();
			if (len <= 0)
				throw new IOException("Invalid packet length received: " + Integer.toString(len));

			int id = is.readVarInt();
			if (id != 0x00)
				throw new IOException("Invalid packet ID received: 0x" + Integer.toHexString(id));

			String json = is.readString();
			soc.close();

			JsonObject obj = new JsonParser().parse(json).getAsJsonObject();

			int online = obj.get("players").getAsJsonObject().get("online").getAsInt();
			int max = obj.get("players").getAsJsonObject().get("max").getAsInt();
			String version = obj.get("version").getAsJsonObject().get("name").getAsString();
			int protocol = obj.get("version").getAsJsonObject().get("protocol").getAsInt();

			String description;
			try {
				description = ChatMessages.parse(obj.get("description").toString());
			} catch (Exception e) {
				description = obj.get("description").toString();
			}

			String icon = obj.has("favicon") ? obj.get("favicon").getAsString() : null;

			String modType = null;
			List<ModInfo> modList = new ArrayList<ModInfo>();

			if (obj.has("modinfo")) {
				JsonObject modinfo = (JsonObject) obj.get("modinfo");
				modType = modinfo.get("type").getAsString();
				JsonArray mods = modinfo.get("modList").getAsJsonArray();
				for (JsonElement modElement : mods) {
					JsonObject modObject = modElement.getAsJsonObject();
					modList.add(
							new ModInfo(modObject.get("modid").getAsString(), modObject.get("version").getAsString()));
				}
			}

			return new StatusInfo(description, online, max, version, protocol, icon, modType, modList);

		}
	}

	/**
	 * Starts listening for LAN servers
	 * 
	 * @param listener listener used to handle discovered servers
	 */
	public static void listenOnLAN(final LANListener listener) {
		new Thread(new Runnable() {

			@Override
			public void run() {
				try (MulticastSocket soc = new MulticastSocket(4445)) {
					soc.joinGroup(InetAddress.getByName("224.0.2.60"));
					byte[] recv = new byte[1024];
					while (true)
						try {
							DatagramPacket packet = new DatagramPacket(recv, recv.length);
							soc.receive(packet);
							String msg = new String(recv).trim();
							String motd = msg.substring(6, msg.lastIndexOf("[/MOTD]"));
							int port = 25565;
							try {
								port = Integer
										.parseInt(msg.substring(msg.lastIndexOf("[AD]") + 4, msg.lastIndexOf("[/AD]")));
							} catch (Exception e) {
								e.printStackTrace();
							}
							listener.serverDiscovered(packet.getAddress(), motd, port);

						} catch (Exception e) {
							e.printStackTrace();
						}
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		}).start();
	}
}
