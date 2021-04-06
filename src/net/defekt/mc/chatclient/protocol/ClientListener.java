package net.defekt.mc.chatclient.protocol;

import net.defekt.mc.chatclient.protocol.packets.general.clientbound.play.ServerChatMessagePacket.Position;

/**
 * A listener interface for receiving client related events, like chat messages
 * or health updates
 * 
 * @see ClientPacketListener
 * @author Defective4
 */
public interface ClientListener {
	/**
	 * Invoked when a chat message was received.
	 * 
	 * @param message  chat message in parsed text form
	 * @param position position of this chat message
	 */
	public void messageReceived(String message, Position position);

	/**
	 * Invoked when client got disconnected from server.
	 * 
	 * @param reason reason of disconnecting
	 */
	public void disconnected(String reason);

	/**
	 * Invoked when client receives a health update.
	 * 
	 * @param health client's new health. Value equal or less than zero means that
	 *               client is dead
	 * @param food   client's new hunger
	 */
	public void healthUpdate(float health, int food);
}
