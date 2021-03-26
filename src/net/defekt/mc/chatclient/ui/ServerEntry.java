package net.defekt.mc.chatclient.ui;

import java.io.Serializable;

import net.defekt.mc.chatclient.protocol.MinecraftStat;
import net.defekt.mc.chatclient.protocol.data.StatusInfo;

public class ServerEntry implements Serializable {
	private static final long serialVersionUID = 4963617404444444550L;

	private final String host;
	private final int port;
	private String name;

	private String version = "Auto";

	private StatusInfo info = null;
	private String icon = null;

	protected boolean refreshing = false;

	protected ServerEntry(String host, int port, String name, String version) {
		this.host = host;
		this.port = port;
		this.name = name;
		this.version = version;
	}

	protected void ping() {
		if (!refreshing) {
			new Thread(new Runnable() {

				@Override
				public void run() {
					refreshing = true;
					info = null;
					try {
						info = MinecraftStat.serverListPing(host, port);
					} catch (Exception e) {
						info = new StatusInfo("\u00A74Can't connect to server", -1, -1, "", -1, null);
					}
					if (info != null && info.getProtocol() != -1)
						icon = info.getIcon();

					refreshing = false;

				}
			}).start();
		}
	}

	public String getHost() {
		return host;
	}

	public int getPort() {
		return port;
	}

	public String getName() {
		return name;
	}

	public StatusInfo getInfo() {
		return info;
	}

	public String getVersion() {
		return version;
	}

	public String getIcon() {
		return icon;
	}

	@Override
	public boolean equals(Object et) {
		if (!(et instanceof ServerEntry))
			return false;
		ServerEntry ent = (ServerEntry) et;
		return (ent.getHost().equals(host) && ent.getPort() == port && ent.getName().equals(name));
	}
}
