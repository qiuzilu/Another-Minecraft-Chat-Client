package net.defekt.mc.chatclient.protocol.io;

import java.io.DataInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.UUID;

public class VarInputStream extends DataInputStream {

	public VarInputStream(InputStream in) {
		super(in);
	}

	public String readString() throws IOException {
		byte[] data = new byte[readVarInt()];
		readFully(data);
		return new String(data, StandardCharsets.UTF_8);
	}

	public UUID readUUID() throws IOException {
		long mostSig = readLong();
		long leastSig = readLong();
		return new UUID(mostSig, leastSig);
	}

	public int readVarInt() throws IOException {
		int numRead = 0;
		int result = 0;
		byte read;
		do {
			read = readByte();
			int value = (read & 0b01111111);
			result |= (value << (7 * numRead));

			numRead++;
			if (numRead > 5) {
				throw new RuntimeException("VarInt is too big");
			}
		} while ((read & 0b10000000) != 0);

		return result;
	}

}
