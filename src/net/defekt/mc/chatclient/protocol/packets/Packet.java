package net.defekt.mc.chatclient.protocol.packets;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;

import net.defekt.mc.chatclient.protocol.io.VarInputStream;
import net.defekt.mc.chatclient.protocol.io.VarOutputStream;

public class Packet {

	protected int id = -1;

	private final ByteArrayOutputStream rawBuffer = new ByteArrayOutputStream();
	private final VarOutputStream varBuffer = new VarOutputStream(rawBuffer);

	public Packet(PacketRegistry reg) {
		id = reg.getPacketID(this.getClass());
	}

	public Packet(PacketRegistry reg, byte[] data) throws IOException {
		id = reg.getPacketID(this.getClass());
		varBuffer.write(data);
	}

	protected VarInputStream getInputStream() {
		return new VarInputStream(new ByteArrayInputStream(rawBuffer.toByteArray()));
	}

	public int getID() {
		return id;
	}

	protected boolean equalsName(String name) {
		return this.getClass().getSimpleName().equals(name);
	}

	protected void putVarInt(int v) {
		try {
			varBuffer.writeVarInt(v);
		} catch (Exception e) {
			throw new IllegalStateException(e);
		}
	}

	protected void putLong(long v) {
		try {
			varBuffer.writeLong(v);
		} catch (Exception e) {
			throw new IllegalStateException(e);
		}
	}

	protected void putBytes(byte[] v) {
		try {
			varBuffer.write(v);
		} catch (Exception e) {
			throw new IllegalStateException(e);
		}
	}

	protected void putShort(int v) {
		try {
			varBuffer.writeShort(v);
		} catch (Exception e) {
			throw new IllegalStateException(e);
		}
	}

	protected void putString(String v) {
		try {
			varBuffer.writeString(v);
		} catch (Exception e) {
			throw new IllegalStateException(e);
		}
	}

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
