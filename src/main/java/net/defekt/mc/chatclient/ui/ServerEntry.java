package net.defekt.mc.chatclient.ui;

import java.io.Serializable;
import java.net.UnknownHostException;

import net.defekt.mc.chatclient.protocol.MinecraftStat;
import net.defekt.mc.chatclient.protocol.data.StatusInfo;

/**
 * A container for a server list entry
 * 
 * @author Defective4
 *
 */
public class ServerEntry implements Serializable {
	private static final long serialVersionUID = 4963617404444444550L;

	private final String host;
	private final int port;
	private String name;

	private String version = "Auto";

	/**
	 * @return true if client is still trying to ping server for its status
	 */
	public boolean isRefreshing() {
		return refreshing;
	}

	private transient StatusInfo info = null;
	private String icon = null;

	/**
	 * Indicates if server is pinged for status data
	 */
	protected transient boolean refreshing = false;

	/**
	 * True if there was an error while retrieving server's status
	 */
	protected transient boolean error = false;

	/**
	 * Creates new server entry
	 * 
	 * @param host    server's hostname
	 * @param port    server's port
	 * @param name    server name
	 * @param version human-readable server version
	 */
	protected ServerEntry(String host, int port, String name, String version) {
		this.host = host;
		this.port = port;
		this.name = name;
		this.version = version;
	}

	/**
	 * @return true if there was an error while retrieving server's status
	 */
	public boolean isError() {
		return error;
	}

	/**
	 * Performs server list ping on this server and updates it on server list
	 */
	protected void ping() {
		if (!refreshing)
			new Thread(new Runnable() {

				@Override
				public void run() {
					refreshing = true;
					error = false;
					info = null;
					try {
						info = MinecraftStat.serverListPing(host, port);
					} catch (UnknownHostException e) {
						info = new StatusInfo(Messages.getString("ServerEntry.serverEntryUnknownHost"), -1, -1, "", -1,
								null, null, null);
						error = true;
					} catch (Exception e) {
						info = new StatusInfo(Messages.getString("ServerEntry.serverEntryCantConnect"), -1, -1, "", -1,
								null, null, null);
						error = true;
					}
					if (info != null && info.getProtocol() != -1)
						icon = info.getIcon();

					refreshing = false;

				}
			}).start();
	}

	/**
	 * Get server host name
	 * 
	 * @return server's host name
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
	 * Get server's name
	 * 
	 * @return server's name
	 */
	public String getName() {
		return name;
	}

	/**
	 * Get latest server's status info
	 * 
	 * @return server's status
	 */
	public StatusInfo getInfo() {
		return info;
	}

	/**
	 * Get server's version
	 * 
	 * @return server's version
	 */
	public String getVersion() {
		return version;
	}

	/**
	 * Get base64 server icon
	 * 
	 * @return server's icon
	 */
	public String getIcon() {
		return icon;
	}

	@Override
	public int hashCode() {
		return super.hashCode();
	}
	
	@Override
	public boolean equals(Object et) {
		if (!(et instanceof ServerEntry))
			return false;
		ServerEntry ent = (ServerEntry) et;
		return (ent.getHost().equals(host) && ent.getPort() == port && ent.getName().equals(name));
	}
}
