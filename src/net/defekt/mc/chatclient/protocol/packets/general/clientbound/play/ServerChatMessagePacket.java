package net.defekt.mc.chatclient.protocol.packets.general.clientbound.play;

import java.io.IOException;

import net.defekt.mc.chatclient.protocol.io.VarInputStream;
import net.defekt.mc.chatclient.protocol.packets.Packet;
import net.defekt.mc.chatclient.protocol.packets.PacketRegistry;

public class ServerChatMessagePacket extends Packet {

	public enum Position {
		CHAT, SYSTEM, HOTBAR
	}

	private final String message;
	private final byte position;

	public ServerChatMessagePacket(PacketRegistry reg, byte[] data) throws IOException {
		super(reg, data);
		VarInputStream is = getInputStream();
		this.message = is.readString();
		this.position = is.readByte();
	}

	public String getMessage() {
		return message;
	}

	public Position getPosition() {
		switch (position) {
			case 0: {
				return Position.CHAT;
			}
			case 1: {
				return Position.SYSTEM;
			}
			case 2: {
				return Position.HOTBAR;
			}
			default: {
				return Position.CHAT;
			}
		}
	}

}
