package net.defekt.mc.chatclient.protocol.data;

import com.flowpowered.nbt.Tag;

public class ItemStack {
	
	private final int id;
	private final int count;
	private final short damage;
	private final Tag<?> nbt;
	
	public ItemStack(int id, int count, short damage, Tag<?> nbt) {
		this.id = id;
		this.count = count;
		this.damage = damage;
		this.nbt = nbt;
	}

	public int getId() {
		return id;
	}

	public int getCount() {
		return count;
	}

	public short getDamage() {
		return damage;
	}

	public Tag<?> getNbt() {
		return nbt;
	}
}
