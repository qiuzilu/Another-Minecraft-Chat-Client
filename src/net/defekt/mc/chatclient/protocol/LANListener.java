package net.defekt.mc.chatclient.protocol;

import java.net.InetAddress;

@FunctionalInterface
public interface LANListener {
	public void serverDiscovered(InetAddress sender, String motd, int port);
}
