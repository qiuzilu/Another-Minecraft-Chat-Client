package net.defekt.mc.chatclient.protocol.packets.alt.clientbound.play;

import java.io.IOException;

import net.defekt.mc.chatclient.protocol.io.VarInputStream;
import net.defekt.mc.chatclient.protocol.packets.Packet;
import net.defekt.mc.chatclient.protocol.packets.PacketRegistry;

/**
 * Newer version of
 * {@link net.defekt.mc.chatclient.protocol.packets.general.clientbound.play.ServerResourcePackSendPacket}
 * <br>
 * Used in Minecraft 1.18
 * 
 * @author Defective4
 *
 */
public class ServerResourcePackSendPacket extends Packet {
	private final String url;
	private final String hash;
	private final boolean forced;
	private final boolean hasPrompt;
	private final String prompt;

	/**
	 * constructs {@link ServerResourcePackSendPacket}
	 * 
	 * @param reg  packet registry used to construct this packet
	 * @param data packet's data
	 * @throws IOException never thrown
	 */
	public ServerResourcePackSendPacket(PacketRegistry reg, byte[] data) throws IOException {
		super(reg, data);
		VarInputStream is = getInputStream();
		url = is.readString();
		hash = is.readString();
		forced = is.readBoolean();
		hasPrompt = is.readBoolean();
		prompt = hasPrompt ? is.readString() : "";
	}

	/**
	 * Get resource pack URL
	 * 
	 * @return URL to resource pack
	 */
	public String getUrl() {
		return url;
	}

	/**
	 * Get resoruce pack's hash
	 * 
	 * @return resource pack hash
	 */
	public String getHash() {
		return hash;
	}

	/**
	 * If this resource pack must be accepted or not
	 * 
	 * @return true if pack must be accepted by client
	 */
	public boolean isForced() {
		return forced;
	}

	/**
	 * If this resource pack has custom prompt
	 * 
	 * @return true if pack has custom prompt
	 */
	public boolean isHasPrompt() {
		return hasPrompt;
	}

	/**
	 * Get resource pack's custom chat prompt
	 * 
	 * @return pack's prompt message
	 */
	public String getPrompt() {
		return prompt;
	}
}
