package net.defekt.mc.chatclient.protocol.io;

import java.io.DataInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.UUID;

import com.flowpowered.nbt.Tag;
import com.flowpowered.nbt.stream.NBTInputStream;

import net.defekt.mc.chatclient.protocol.MinecraftClient;
import net.defekt.mc.chatclient.protocol.data.ItemStack;

/**
 * An extension of {@link DataInputStream} with methods to read Minecraft's data
 * types
 * 
 * @see VarOutputStream
 * @see MinecraftClient
 * @author Defective4
 *
 */
public class VarInputStream extends DataInputStream {

	/**
	 * Wrap an input stream in {@link VarInputStream}
	 * 
	 * @param in input steam to wrap
	 */
	public VarInputStream(InputStream in) {
		super(in);
	}

	/**
	 * Read a string from stream<br>
	 * This methods first reads VarInt indicating string lenght, and then it reads a
	 * string
	 * 
	 * @return read String
	 * @throws IOException thrown when there was an error reading from stream
	 */
	public String readString() throws IOException {
		byte[] data = new byte[readVarInt()];
		readFully(data);
		return new String(data, StandardCharsets.UTF_8);
	}

	/**
	 * Read a UUID from stream<br>
	 * This method reads two {@link Long}s, the most and least significant one, and
	 * creates {@link UUID} from them
	 * 
	 * @return read UUIF
	 * @throws IOException thrown when there was an error reading from stream
	 */
	public UUID readUUID() throws IOException {
		long mostSig = readLong();
		long leastSig = readLong();
		return new UUID(mostSig, leastSig);
	}

	/**
	 * Read a VarInt from stream Snippet from
	 * <a href="https://wiki.vg/Protocol#VarInt_and_VarLong">wiki.vg</a>
	 * 
	 * @return read VarInT
	 * @throws IOException thrown when there was an error reading from stream
	 */
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

	/**
	 * Read item data from stream
	 * 
	 * @param protocol protocol determining slot data format
	 * @return read item stack
	 * @throws IOException thrown when there was an error reading from stream
	 */
	@SuppressWarnings("resource")
	public ItemStack readSlotData(int protocol) throws IOException {
		if (protocol >= 477)
			if (!readBoolean())
				return new ItemStack((short) 0, 0, (short) 0, null);
		int id = protocol >= 477 ? readVarInt() : readShort();
		int count = 0;
		short damage = 0;
		Tag<?> tag = null;
		if (id != -1) {
			count = readByte();
			damage = protocol <= 340 ? readShort() : 0;
			try {
				NBTInputStream is = new NBTInputStream(this, false);
				tag = is.readTag();
			} catch (Exception e) {
				tag = null;
			}
		}
		return new ItemStack(id == -1 ? 0 : id, count, damage, tag);

	}
}
