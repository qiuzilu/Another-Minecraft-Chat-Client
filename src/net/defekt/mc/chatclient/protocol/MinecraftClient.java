package net.defekt.mc.chatclient.protocol;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.zip.Inflater;

import net.defekt.mc.chatclient.protocol.data.ChatMessage;
import net.defekt.mc.chatclient.protocol.data.PlayerInfo;
import net.defekt.mc.chatclient.protocol.io.ListenerHashMap;
import net.defekt.mc.chatclient.protocol.io.VarInputStream;
import net.defekt.mc.chatclient.protocol.io.VarOutputStream;
import net.defekt.mc.chatclient.protocol.packets.HandshakePacket;
import net.defekt.mc.chatclient.protocol.packets.Packet;
import net.defekt.mc.chatclient.protocol.packets.PacketFactory;
import net.defekt.mc.chatclient.protocol.packets.PacketRegistry;
import net.defekt.mc.chatclient.protocol.packets.PacketRegistry.State;
import net.defekt.mc.chatclient.protocol.packets.general.serverbound.play.ClientEntityActionPacket.EntityAction;

public class MinecraftClient {

	private final String host;
	private final int port;
	private final int protocol;
	private final PacketRegistry reg;

	private String username = "";

	private double x = 0;
	private double y = 0;
	private double z = 0;

	private int entityID = 0;

	private Socket soc = null;
	private OutputStream os = null;

	private boolean compression = false;
	private int cTreshold = -1;

	private boolean sneaking = false;
	private boolean sprinting = false;

	private final Object lock = new Object();

	private final ListenerHashMap<UUID, PlayerInfo> playersTabList = new ListenerHashMap<UUID, PlayerInfo>();

	private ClientPacketListener listener;
	private List<ClientListener> clientListeners = new ArrayList<ClientListener>();

	private Thread packetReaderThread = null;

	public void addClientListener(ClientListener listener) {
		clientListeners.add(listener);
	}

	public void removeClientListener(ClientListener listener) {
		clientListeners.remove(listener);
	}

	public List<ClientListener> getClientListeners() {
		return new ArrayList<ClientListener>(clientListeners);
	}

	public MinecraftClient(String host, int port, int protocol) throws IOException {
		this.host = host;
		this.port = port;
		this.protocol = protocol;
		this.reg = PacketFactory.constructPacketRegistry(protocol);
		state = protocol >= 753 ? State.IN : State.LOGIN;
	}

	public void close() {
		if (soc != null && !soc.isClosed())
			try {
				connected = false;
				soc.close();
				if (packetReaderThread != null)
					packetReaderThread.interrupt();
			} catch (IOException e) {
				e.printStackTrace();
			}
	}

	private boolean connected = false;

	private State state;

	protected void setCurrentState(State state) {
		this.state = state;
	}

	public void connect(String username) throws IOException {
		this.username = username;
		try {
			if (connected || this.soc != null)
				throw new IOException("Already connected!");

			this.soc = new Socket();
			soc.connect(new InetSocketAddress(host, port));
			this.connected = true;

			this.os = soc.getOutputStream();
			final VarInputStream is = new VarInputStream(soc.getInputStream());
			listener = new ClientPacketListener(this);

			Packet handshake = new HandshakePacket(reg, protocol, host, port, 2);
			os.write(handshake.getData(compression));

			Packet login = PacketFactory.constructPacket(reg, "ClientLoginRequestPacket", username);
			os.write(login.getData(compression));

			int len = is.readVarInt();
			if (len < 0)
				throw new IOException("Invalid received packet length: " + Integer.toString(len));

			int id = is.readVarInt();
			switch (id) {
				case 0x01: {
					throw new IOException("Disconnected: Target server requires authentication to join");
				}
				case 0x00: {
					String reason = ChatMessage.parse(is.readString());
					throw new IOException("Disconnected: " + reason);
				}
				case 0x03: {
					cTreshold = is.readVarInt();
					if (cTreshold > -1)
						compression = true;
					break;
				}
			}

			packetReaderThread = new Thread(new Runnable() {

				private final Inflater inflater = new Inflater();

				@Override
				public void run() {
					try {
						while (connected) {
							int len = is.readVarInt();
							byte[] data = new byte[len];
							is.readFully(data);

							VarInputStream packetbuf = new VarInputStream(new ByteArrayInputStream(data));
							int id = -1;
							byte[] packetData = new byte[0];

							if (compression) {
								int dlen = packetbuf.readVarInt();
								if (dlen == 0) {
									id = packetbuf.readVarInt();
									packetData = new byte[len - VarOutputStream.checkVarIntSize(dlen) - 1];
									packetbuf.readFully(packetData);
								} else {
									byte[] toProcess = new byte[len - VarOutputStream.checkVarIntSize(dlen)];
									packetbuf.readFully(toProcess);

									byte[] inflated = new byte[dlen];
									inflater.setInput(toProcess);
									inflater.inflate(inflated);
									inflater.reset();

									packetbuf = new VarInputStream(new ByteArrayInputStream(inflated));
									id = packetbuf.readVarInt();

									packetData = new byte[dlen - 1];
									packetbuf.readFully(packetData);

								}
							} else {
								id = packetbuf.readVarInt();
								packetData = new byte[len - 2];
								packetbuf.readFully(packetData);
							}

							if (id != -1) {
								Class<? extends Packet> pClass = reg.getByID(id, state);
								if (pClass == null)
									continue;

								Packet packet = PacketFactory.constructPacket(reg, pClass.getSimpleName(), packetData);

								listener.packetReceived(packet, reg);
							}
						}
					} catch (Exception e) {
						close();
					}
				}
			});
			packetReaderThread.start();

			synchronized (lock) {
				try {
					lock.wait(5000);
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
			}

		} catch (IOException ex) {
			close();
			throw ex;
		}
	}

	protected int getProtocol() {
		return protocol;
	}

	protected OutputStream getOutputStream() {
		return os;
	}

	protected boolean isCompressionEnabled() {
		return compression;
	}

	protected int getCTreshold() {
		return cTreshold;
	}

	protected Object getLock() {
		return lock;
	}

	public double getX() {
		return x;
	}

	public double getY() {
		return y;
	}

	public double getZ() {
		return z;
	}

	protected void setX(double x) {
		this.x = x;
	}

	protected void setY(double y) {
		this.y = y;
	}

	protected void setZ(double z) {
		this.z = z;
	}

	public void toggleSneaking() throws IOException {
		if (connected && soc != null && !soc.isClosed()) {
			sneaking = !sneaking;
			EntityAction action = sneaking ? EntityAction.START_SNEAKING : EntityAction.STOP_SNEAKING;
			os.write(PacketFactory.constructPacket(reg, "ClientEntityActionPacket", entityID, action)
					.getData(compression));
		} else
			throw new IOException("Not connected!");
	}

	public void toggleSprinting() throws IOException {
		if (connected && soc != null && !soc.isClosed()) {
			sprinting = !sprinting;
			EntityAction action = sprinting ? EntityAction.START_SPRINTING : EntityAction.STOP_SPRINTING;
			os.write(PacketFactory.constructPacket(reg, "ClientEntityActionPacket", entityID, action)
					.getData(compression));
		} else
			throw new IOException("Not connected!");
	}

	public void sendChatMessage(String message) throws IOException {
		if (connected && soc != null && !soc.isClosed()) {
			os.write(PacketFactory.constructPacket(reg, "ClientChatMessagePacket", message).getData(compression));
		} else
			throw new IOException("Not connected!");
	}

	public int getEntityID() {
		return entityID;
	}

	protected void setEntityID(int entityID) {
		this.entityID = entityID;
	}

	public boolean isSneaking() {
		return sneaking;
	}

	public boolean isSprinting() {
		return sprinting;
	}

	public ListenerHashMap<UUID, PlayerInfo> getPlayersTabList() {
		return playersTabList;
	}

	public String getHost() {
		return host;
	}

	public int getPort() {
		return port;
	}

	public String getUsername() {
		return username;
	}
}
