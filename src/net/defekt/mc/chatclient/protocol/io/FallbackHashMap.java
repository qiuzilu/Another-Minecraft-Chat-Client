package net.defekt.mc.chatclient.protocol.io;

import java.util.HashMap;

/**
 * An extension of Hash Map that returns previously set default value instead of
 * null when a key does not exist
 * 
 * @author Defective4
 *
 * @param <K> the type of keys maintained by this map
 * @param <V> the type of mapped values
 */
public class FallbackHashMap<K, V> extends HashMap<K, V> {
	private static final long serialVersionUID = 1L;
	private V defaultValue = null;

	@Override
	/**
	 * Returns the value to which the specified key is mapped, or preset default
	 * value if this map contains no mapping for the key.
	 */
	public V get(Object key) {
		return containsKey(key) ? super.get(key) : defaultValue;
	}

	/**
	 * Get default value used by this map
	 * 
	 * @return default value
	 */
	public V getDefaultValue() {
		return defaultValue;
	}

	/**
	 * Set default value for this map
	 * 
	 * @param defaultValue default value
	 */
	public void setDefaultValue(V defaultValue) {
		this.defaultValue = defaultValue;
	}
}
