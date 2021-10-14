package net.defekt.mc.chatclient.protocol;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.zip.Inflater;

import net.defekt.mc.chatclient.protocol.data.ItemsWindow;
import net.defekt.mc.chatclient.protocol.data.PlayerInfo;
import net.defekt.mc.chatclient.protocol.io.ListenerHashMap;
import net.defekt.mc.chatclient.protocol.io.VarInputStream;
import net.defekt.mc.chatclient.protocol.io.VarOutputStream;
import net.defekt.mc.chatclient.protocol.packets.HandshakePacket;
import net.defekt.mc.chatclient.protocol.packets.Packet;
import net.defekt.mc.chatclient.protocol.packets.PacketFactory;
import net.defekt.mc.chatclient.protocol.packets.PacketRegistry;
import net.defekt.mc.chatclient.protocol.packets.PacketRegistry.State;
import net.defekt.mc.chatclient.protocol.packets.general.clientbound.play.ServerStatisticsPacket;
import net.defekt.mc.chatclient.protocol.packets.general.serverbound.play.ClientEntityActionPacket.EntityAction;
import net.defekt.mc.chatclient.ui.Messages;

/**
 * MinecraftClient is Minecraft protocol implementation at IO level. It is
 * responsible for connecting to a Minecraft server and handling all data
 * received from it.
 * 
 * @see ClientListener
 * @see MinecraftStat
 * @see ProtocolNumber
 * @author Defective4
 */
public class MinecraftClient {

	private final String host;
	private final int port;
	private final int protocol;
	private final PacketRegistry reg;

	private String username = "";

	private double x = Integer.MIN_VALUE;
	private double y = 0;
	private double z = 0;
	private float yaw = 0;
	private float pitch = 0;

	private int entityID = 0;

	private Socket soc = null;
	private OutputStream os = null;

	private boolean compression = false;
	private int cThreshold = -1;

	private boolean sneaking = false;
	private boolean sprinting = false;

	private final Object lock = new Object();

	private static final String notConnectedError = Messages.getString("MinecraftClient.clientErrorNotConnected");

	private final ListenerHashMap<UUID, PlayerInfo> playersTabList = new ListenerHashMap<UUID, PlayerInfo>();

	private List<InternalPacketListener> packetListeners = new ArrayList<InternalPacketListener>();
	private List<ClientListener> clientListeners = new ArrayList<ClientListener>();

	private final Map<Integer, ItemsWindow> openWindows = new HashMap<Integer, ItemsWindow>();
	private ItemsWindow inventory = null;

	private Thread packetReaderThread = null;
	private Thread playerPositionThread = null;

	/**
	 * Add a client listener to receive client events
	 * 
	 * @param listener a client listener for receiving client updates
	 */
	public void addClientListener(ClientListener listener) {
		clientListeners.add(listener);
	}

	/**
	 * Remove a client listener
	 * 
	 * @param listener client listener to remove
	 */
	public void removeClientListener(ClientListener listener) {
		clientListeners.remove(listener);
	}

	/**
	 * Get a copy of client listener list
	 * 
	 * @return list of client listeners added to this client
	 */
	public List<ClientListener> getClientListeners() {
		return new ArrayList<ClientListener>(clientListeners);
	}

	/**
	 * Creates a new Minecraft Client ready to connect to specified server
	 * 
	 * @param host     address of server to connect to
	 * @param port     port of target server
	 * @param protocol protocol that will be used to connect to server
	 * @throws IOException thrown when there was an error initializing
	 *                     {@link PacketRegistry} for specified protocol (for
	 *                     example when could not find a matching packet registry
	 *                     implementation for specified protocol)
	 */
	public MinecraftClient(String host, int port, int protocol) throws IOException {
		this.host = host;
		this.port = port;
		this.protocol = protocol;
		this.reg = PacketFactory.constructPacketRegistry(protocol);
		state = State.LOGIN;
	}

	/**
	 * Closes this MinecraftClient
	 */
	public void close() {
		if (soc != null && !soc.isClosed())
			try {
				connected = false;
				try {
					for (ItemsWindow win : openWindows.values())
						win.closeWindow();
					inventory.closeWindow();
				} catch (Exception e) {
					e.printStackTrace();
				}
				soc.close();
				if (packetReaderThread != null)
					packetReaderThread.interrupt();
				if (playerPositionThread != null)
					playerPositionThread.interrupt();
			} catch (IOException e) {
				e.printStackTrace();
			}
	}

	private boolean connected = false;

	private State state;

	/**
	 * Set current client state. This method is used internally by
	 * {@link ClientPacketListener} bound to this client
	 * 
	 * @param state next client state
	 */
	protected void setCurrentState(State state) {
		this.state = state;
	}

	/**
	 * Connect to server specified in constructor
	 * 
	 * @param username username of connecting client
	 * @throws IOException thrown when client was unable to connect to target server
	 */
	public void connect(String username) throws IOException {
		this.username = username;

		try {
			if (connected || this.soc != null)
				throw new IOException(Messages.getString("MinecraftClient.clientErrorAlreadyConnected"));

			this.soc = new Socket();
			soc.connect(new InetSocketAddress(host, port));
			this.connected = true;

			this.os = soc.getOutputStream();
			final VarInputStream is = new VarInputStream(soc.getInputStream());
			inventory = new ItemsWindow(Messages.getString("MinecraftClient.clientInventoryName"), 46, 0, this, reg);
			packetListeners.add(new ClientPacketListener(this));

			Packet handshake = new HandshakePacket(reg, protocol, host, port, 2);
			os.write(handshake.getData(compression));

			Packet login = PacketFactory.constructPacket(reg, "ClientLoginRequestPacket", username);
			os.write(login.getData(compression));

//			int len = is.readVarInt();
//			if (len < 0)
//				throw new IOException(
//						Messages.getString("MinecraftClient.clientErrorInvalidPacketLen") + Integer.toString(len));
//
//			int id = is.readVarInt();
//			switch (id) {
//				case 0x01: {
//					throw new IOException(Messages.getString("MinecraftClient.clientErrorDisconnectedNoAuth"));
//				}
//				case 0x00: {
//					String reason = ChatMessages.parse(is.readString());
//					throw new IOException(Messages.getString("MinecraftClient.clientErrorDisconnected") + reason);
//				}
//				case 0x03: {
//					cThreshold = is.readVarInt();
//					if (cThreshold > -1)
//						compression = true;
//					break;
//				}
//			}

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
							final int id;
							final byte[] packetData;

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
								packetData = new byte[len - 1];
								packetbuf.readFully(packetData);
							}

							if (id != -1) {
								Class<? extends Packet> pClass = reg.getByID(id, state);
								if (pClass == null)
									continue;
								Packet packet = PacketFactory.constructPacket(reg, pClass.getSimpleName(), packetData);
								for (InternalPacketListener lis : packetListeners)
									lis.packetReceived(packet, reg);
							}
						}
					} catch (Exception e) {
						e.printStackTrace();
						for (ClientListener cl : clientListeners)
							cl.disconnected(e.toString());
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
			playerPositionThread = new Thread(new Runnable() {

				@Override
				public void run() {
					try {
						while (true) {
							Thread.sleep(1000);
							if (x == Integer.MIN_VALUE)
								continue;
							try {
								if (soc.isClosed()) {
									close();
									return;
								}
								Packet playerPositionPacket = PacketFactory.constructPacket(reg,
										"ClientPlayerPositionPacket", x, y, z, true);
								os.write(playerPositionPacket.getData(isCompressionEnabled()));
							} catch (Exception e) {
								e.printStackTrace();
							}
						}
					} catch (InterruptedException e) {
					}
				}
			});
			playerPositionThread.start();

		} catch (IOException ex) {
			close();
			throw ex;
		}
	}

	/**
	 * Get protocol of this client
	 * 
	 * @return protocol used by this client
	 */
	protected int getProtocol() {
		return protocol;
	}

	/**
	 * Check if compression is enabled by server
	 * 
	 * @return compression state
	 */
	protected boolean isCompressionEnabled() {
		return compression;
	}

	/**
	 * Get compression threshold
	 * 
	 * @return compression threshold sent by server. -1 if none
	 */
	protected int getCThreshold() {
		return cThreshold;
	}

	protected Object getLock() {
		return lock;
	}

	/**
	 * Get X position of this client in-game
	 * 
	 * @return X coordinates of client
	 */
	public double getX() {
		return x;
	}

	/**
	 * Get Y position of this client in-game
	 * 
	 * @return Y coordinates of client
	 */
	public double getY() {
		return y;
	}

	/**
	 * Get Z position of this client in-game
	 * 
	 * @return Z coordinates of client
	 */
	public double getZ() {
		return z;
	}

	/**
	 * Set X position of this client. This method only sets internal variable, it
	 * does NOT change client's position on server.
	 * 
	 * @param x new X position
	 */
	protected void setX(double x) {
		this.x = x;
		for (ClientListener cl : clientListeners)
			cl.positionChanged(this.x, this.y, this.z);
	}

	/**
	 * Set Y position of this client. This method only sets internal variable, it
	 * does NOT change client's position on server.
	 * 
	 * @param y new Y position
	 */
	protected void setY(double y) {
		this.y = y;
		for (ClientListener cl : clientListeners)
			cl.positionChanged(this.x, this.y, this.z);
	}

	/**
	 * Set Z position of this client. This method only sets internal variable, it
	 * does NOT change client's position on server.
	 * 
	 * @param z new Z position
	 */
	protected void setZ(double z) {
		this.z = z;
		for (ClientListener cl : clientListeners)
			cl.positionChanged(this.x, this.y, this.z);
	}

	/**
	 * Toggle client sneaking state. It also sets client sneaking in-game/
	 * 
	 * @throws IOException thrown when server was not connected, or there was an
	 *                     error sending packet to server
	 */
	public void toggleSneaking() throws IOException {
		sneaking = !sneaking;
		EntityAction action = sneaking ? EntityAction.START_SNEAKING : EntityAction.STOP_SNEAKING;
		try {
			sendPacket(PacketFactory.constructPacket(reg, "ClientEntityActionPacket", entityID, action));
		} catch (Exception e) {
			sneaking = !sneaking;
			throw e;
		}
	}

	/**
	 * Toggle client sprinting state. It also sets client sprinting in-game/
	 * 
	 * @throws IOException thrown when server was not connected, or there was an
	 *                     error sending packet to server
	 */
	public void toggleSprinting() throws IOException {
		sprinting = !sprinting;
		EntityAction action = sprinting ? EntityAction.START_SPRINTING : EntityAction.STOP_SPRINTING;
		try {
			sendPacket(PacketFactory.constructPacket(reg, "ClientEntityActionPacket", entityID, action));
		} catch (Exception e) {
			sprinting = !sprinting;
			throw e;
		}
	}

	/**
	 * Sends a packet to server
	 * 
	 * @param packet packet to send
	 * @throws IOException thrown when there was an error sending packet
	 */
	public void sendPacket(Packet packet) throws IOException {
		if (connected && soc != null && !soc.isClosed())
			os.write(packet.getData(compression));
		else
			throw new IOException(notConnectedError);
	}

	/**
	 * Send chat message to server
	 * 
	 * @param message a chat message to send
	 * @throws IOException thrown when server was not connected, or there was an
	 *                     error sending packet to server
	 */
	public void sendChatMessage(String message) throws IOException {
		sendPacket(PacketFactory.constructPacket(reg, "ClientChatMessagePacket", message));
	}

	/**
	 * Get entity ID of this client on server
	 * 
	 * @return client's entity ID
	 */
	public int getEntityID() {
		return entityID;
	}

	/**
	 * Used internally by {@link ClientPacketListener} to set client's entity ID
	 * 
	 * @param entityID new entity ID
	 */
	protected void setEntityID(int entityID) {
		this.entityID = entityID;
	}

	/**
	 * Get client sneaking state of this client. It only returns variable stored
	 * locally
	 * 
	 * @return sneaking state
	 */
	public boolean isSneaking() {
		return sneaking;
	}

	/**
	 * Get client sprinting state of this client. It only returns variable stored
	 * locally
	 * 
	 * @return sprinting state
	 */
	public boolean isSprinting() {
		return sprinting;
	}

	/**
	 * Get player list
	 * 
	 * @return player list received from server
	 */
	public ListenerHashMap<UUID, PlayerInfo> getPlayersTabList() {
		return playersTabList;
	}

	/**
	 * Get server's address
	 * 
	 * @return server's hostname
	 */
	public String getHost() {
		return host;
	}

	/**
	 * Get server's port
	 * 
	 * @return server's port
	 */
	public int getPort() {
		return port;
	}

	/**
	 * Get client's username (only if client is connected)
	 * 
	 * @return client's username
	 */
	public String getUsername() {
		return username;
	}

	/**
	 * Get output stream used by this client
	 * 
	 * @return client's output stream
	 */
	protected OutputStream getOutputStream() {
		return os;
	}

	/**
	 * Get client's yaw
	 * 
	 * @return client's yaw
	 */
	public float getYaw() {
		return yaw;
	}

	/**
	 * Get client's yaw
	 * 
	 * @return client's yaw
	 */
	public float getPitch() {
		return pitch;
	}

	/**
	 * Set client's yaw. This method only sets internal variable, it does NOT change
	 * client's position on server.
	 * 
	 * @param yaw new yaw value
	 */
	protected void setYaw(float yaw) {
		this.yaw = yaw;
	}

	/**
	 * Set client's pitch. This method only sets internal variable, it does NOT
	 * change client's position on server.
	 * 
	 * @param pitch new pitch value
	 */
	protected void setPitch(float pitch) {
		this.pitch = pitch;
	}

	private Thread movingThread = null;

	/**
	 * Set client's look on server.
	 * 
	 * @param direction player's look
	 */
	public void setLook(float direction) {
		try {
			this.yaw = direction;
			os.write(PacketFactory
					.constructPacket(reg, "ClientPlayerPositionAndLookPacket", x, y, z, direction, 0f, true)
					.getData(isCompressionEnabled()));
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	/**
	 * Move client on server.
	 * 
	 * @param direction    direction to move to from 0 to 7
	 * @param speed        walking speed, from 0 to 1. Too high values may cause
	 *                     client to be kicked or even banned from server.
	 * @param blocks       distance to move
	 * @param lockPosition if set to true, client will only look in target position
	 *                     without moving
	 */
	public void move(final int direction, final double speed, final double blocks, final boolean lockPosition) {
		if (movingThread == null || !movingThread.isAlive()) {
			movingThread = new Thread(new Runnable() {
				private final double speedModifier = speed;

				@Override
				public void run() {
					for (int x = 0; x < (1 / speedModifier) * blocks; x++) {
						try {
							Thread.sleep(1000 / 20);
						} catch (InterruptedException e1) {
							e1.printStackTrace();
						}
						if (MinecraftClient.this.x == Integer.MIN_VALUE)
							return;
						double tx = MinecraftClient.this.x;
						double tz = MinecraftClient.this.z;
						float nyaw = 0;
						switch (direction) {
							case 0: {
								tz += speedModifier;
								nyaw = 0;
								break;
							}
							case 1: {
								tz += speedModifier;
								tx += speedModifier;
								nyaw = -45;
								break;
							}
							case 2: {
								tx += speedModifier;
								nyaw = -90;
								break;
							}
							case 3: {
								tz -= speedModifier;
								tx += speedModifier;
								nyaw = -135;
								break;
							}
							case 4: {
								tz -= speedModifier;
								nyaw = 180;
								break;
							}
							case 5: {
								tz -= speedModifier;
								tx -= speedModifier;
								nyaw = 135;
								break;
							}
							case 6: {
								tx -= speedModifier;
								nyaw = 90;
								break;
							}
							case 7: {
								tz += speedModifier;
								tx -= speedModifier;
								nyaw = 45;
								break;
							}
							default: {
								break;
							}
						}

						setLook(nyaw);
						if (lockPosition)
							return;
						try {
							setX(tx);
							setZ(tz);
							sendPacket(PacketFactory.constructPacket(reg, "ClientPlayerPositionAndLookPacket", tx,
									MinecraftClient.this.y, tz, MinecraftClient.this.yaw, 0f, true));
						} catch (Exception e) {

						}

					}
				}
			});
			movingThread.start();
		}
	}

	/**
	 * Request statistics update from server.<br>
	 * Server will respond with Statistics (see {@link ServerStatisticsPacket}), but
	 * it may not respond at all if another request was made recently.
	 * 
	 * @throws IOException thrown when there was an error sending packet.
	 */
	public void refreshStatistics() throws IOException {
		sendPacket(PacketFactory.constructPacket(reg, "ClientStatusPacket", 1));
	}

	/**
	 * Sets window currently shown to client
	 * 
	 * @param id  window's id
	 * @param win opened window
	 */
	protected void setOpenWindow(int id, ItemsWindow win) {
		for (ItemsWindow window : openWindows.values())
			window.closeWindow(true);
		inventory.closeWindow(true);
		openWindows.clear();
		openWindows.put(id, win);
	}

	/**
	 * Get all opened window, except player's inventory window
	 * 
	 * @return map containing currently open windows with their IDs as keys
	 */
	public Map<Integer, ItemsWindow> getOpenWindows() {
		return new HashMap<Integer, ItemsWindow>(openWindows);
	}

	/**
	 * Get player's inventory
	 * 
	 * @return player's inventory window
	 */
	public ItemsWindow getInventory() {
		return inventory;
	}

	public boolean isCompression() {
		return compression;
	}

	public void setCompression(boolean compression) {
		this.compression = compression;
	}

	public boolean isConnected() {
		return connected;
	}
}
