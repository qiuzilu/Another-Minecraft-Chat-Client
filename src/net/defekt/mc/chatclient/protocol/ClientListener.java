package net.defekt.mc.chatclient.protocol;

import java.util.Map;

import net.defekt.mc.chatclient.protocol.data.ItemsWindow;
import net.defekt.mc.chatclient.protocol.packets.PacketRegistry;
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

	/**
	 * Invoked when client's position on server is updated.
	 * 
	 * @param x new X position
	 * @param y new Y position
	 * @param z new Z position
	 */
	public void positionChanged(double x, double y, double z);

	/**
	 * Invoked when client receives statistics after ClientStatus packet
	 * 
	 * @param values map of received values
	 */
	public void statisticsReceived(Map<String, Integer> values);

	/**
	 * Invoked when server shows a window to client (a enderchest, interactive GUI,
	 * etc.)
	 * 
	 * @param id  opened window's ID
	 * @param win opened window
	 * @param reg client's packet registry
	 */
	public void windowOpened(int id, ItemsWindow win, PacketRegistry reg);
}
