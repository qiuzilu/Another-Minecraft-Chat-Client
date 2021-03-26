package net.defekt.mc.chatclient.protocol;

import net.defekt.mc.chatclient.protocol.packets.general.clientbound.play.ServerChatMessagePacket.Position;

public interface ClientListener {
	public void messageReceived(String message, Position position);
	public void disconnected(String reason);
	public void healthUpdate(float health, int food);
}
