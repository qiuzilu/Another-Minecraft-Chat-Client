package net.defekt.mc.chatclient.protocol.packets;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;

import net.defekt.mc.chatclient.protocol.io.VarInputStream;
import net.defekt.mc.chatclient.protocol.io.VarOutputStream;

/**
 * Base class for all packets
 * 
 * @see PacketFactory
 * @see PacketRegistry
 * @author Defective4
 *
 */
public class Packet {

	/**
	 * Packet's current ID determined by its entry in provide {@link PacketRegistry}
	 */
	protected int id = -1;

	private final ByteArrayOutputStream rawBuffer = new ByteArrayOutputStream();
	private final VarOutputStream varBuffer = new VarOutputStream(rawBuffer);

	/**
	 * Constructs a packet (serverbound)<br>
	 * This constructor is usually used to create serverbound packets
	 * 
	 * @param reg packet registry used to determine this packet's id
	 */
	protected Packet(PacketRegistry reg) {
		id = reg.getPacketID(this.getClass());
	}

	/**
	 * Constructs a packet (clientbound)<br>
	 * This constructor is usually used to create clientbound packets
	 * 
	 * @param reg  packet registry used to determine this packet's id
	 * @param data data contained in this packet
	 * @throws IOException never thrown
	 */
	protected Packet(PacketRegistry reg, byte[] data) throws IOException {
		id = reg.getPacketID(this.getClass());
		varBuffer.write(data);
	}

	/**
	 * Get {@link VarInputStream} with this packet's contents
	 * 
	 * @return input stream with packet's contents
	 */
	protected VarInputStream getInputStream() {
		return new VarInputStream(new ByteArrayInputStream(rawBuffer.toByteArray()));
	}

	/**
	 * Get this packet's determined ID
	 * 
	 * @return packet's ID
	 */
	public int getID() {
		return id;
	}

	/**
	 * Check if this packet's name equals String
	 * 
	 * @param name string to compare
	 * @return true if this packet's name is the same as provided String
	 */
	protected boolean equalsName(String name) {
		return this.getClass().getSimpleName().equals(name);
	}

	/**
	 * Put a VarInt to this packet
	 * 
	 * @param v VarInt value
	 */
	protected void putVarInt(int v) {
		try {
			varBuffer.writeVarInt(v);
		} catch (Exception e) {
			throw new IllegalStateException(e);
		}
	}

	/**
	 * Put a Long to this packet
	 * 
	 * @param v value
	 */
	protected void putLong(long v) {
		try {
			varBuffer.writeLong(v);
		} catch (Exception e) {
			throw new IllegalStateException(e);
		}
	}

	/**
	 * Put a Float to this packet
	 * 
	 * @param v value
	 */
	protected void putFloat(float v) {
		try {
			varBuffer.writeFloat(v);
		} catch (Exception e) {
			throw new IllegalStateException(e);
		}
	}

	/**
	 * Put a Boolean to this packet
	 * 
	 * @param v value
	 */
	protected void putBoolean(boolean v) {
		try {
			varBuffer.writeBoolean(v);
		} catch (Exception e) {
			throw new IllegalStateException(e);
		}
	}

	/**
	 * Put a Double to this packet
	 * 
	 * @param v value
	 */
	protected void putDouble(double v) {
		try {
			varBuffer.writeDouble(v);
		} catch (Exception e) {
			throw new IllegalStateException(e);
		}
	}

	/**
	 * Put a byte array to this packet
	 * 
	 * @param v byte array
	 */
	protected void putBytes(byte[] v) {
		try {
			varBuffer.write(v);
		} catch (Exception e) {
			throw new IllegalStateException(e);
		}
	}

	/**
	 * Put short to this packet
	 * 
	 * @param v value
	 */
	protected void putShort(int v) {
		try {
			varBuffer.writeShort(v);
		} catch (Exception e) {
			throw new IllegalStateException(e);
		}
	}

	/**
	 * Put String to this packet
	 * 
	 * @param v value
	 */
	protected void putString(String v) {
		try {
			varBuffer.writeString(v);
		} catch (Exception e) {
			throw new IllegalStateException(e);
		}
	}

	/**
	 * Get this packet's bytes as ready to send data in Minecraft's packet format
	 * 
	 * @param compression whether to use post-compression format
	 * @return byte array with packet's data
	 */
	public byte[] getData(boolean compression) {
		try {
			byte[] data = rawBuffer.toByteArray();
			int tlong = compression ? 2 : 1;

			ByteArrayOutputStream tmpBuf = new ByteArrayOutputStream();
			VarOutputStream varBuf = new VarOutputStream(tmpBuf);
			varBuf.writeVarInt(data.length + tlong);
			if (compression)
				varBuf.writeByte(0);
			varBuf.writeVarInt(id);
			varBuf.write(data);

			return tmpBuf.toByteArray();

		} catch (Exception e) {
			throw new IllegalStateException(e);
		}
	}

	/**
	 * Access packet's method via reflection
	 * 
	 * @param name method name
	 * @return method's return value
	 */
	public Object accessPacketMethod(String name) {
		Class<? extends Packet> cl = getClass();
		try {
			return cl.getDeclaredMethod(name).invoke(this);
		} catch (SecurityException | IllegalArgumentException | NoSuchMethodException | IllegalAccessException
				| InvocationTargetException e) {
			e.printStackTrace();
			return null;
		}
	}
}
