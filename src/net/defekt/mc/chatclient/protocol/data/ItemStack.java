package net.defekt.mc.chatclient.protocol.data;

import com.flowpowered.nbt.Tag;

/**
 * Class encapsulating information about item stack
 * 
 * @author Defective4
 *
 */
public class ItemStack {

	private final int id;
	private final int count;
	private final short damage;
	private final Tag<?> nbt;

	/**
	 * Constructs a new item stack
	 * 
	 * @param id     item ID
	 * @param count  items in this item stack
	 * @param damage item damage
	 * @param nbt    item's NBT data
	 */
	public ItemStack(int id, int count, short damage, Tag<?> nbt) {
		this.id = id;
		this.count = count;
		this.damage = damage;
		this.nbt = nbt;
	}

	/**
	 * Get item's ID
	 * 
	 * @return item's ID
	 */
	public int getId() {
		return id;
	}

	/**
	 * Get item count in this item stack
	 * 
	 * @return item count
	 */
	public int getCount() {
		return count;
	}

	/**
	 * Get item's damage
	 * 
	 * @return item damage
	 */
	public short getDamage() {
		return damage;
	}

	/**
	 * Get item's NBT data
	 * 
	 * @return item's NBT data
	 */
	public Tag<?> getNbt() {
		return nbt;
	}
}
