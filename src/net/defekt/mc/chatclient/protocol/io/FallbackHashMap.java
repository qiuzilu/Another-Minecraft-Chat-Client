package net.defekt.mc.chatclient.protocol.io;

import java.util.HashMap;

public class FallbackHashMap<K, V> extends HashMap<K, V> {
	private static final long serialVersionUID = 1L;
	private V defaultValue = null;

	@Override
	public V get(Object key) {
		return containsKey(key) ? super.get(key) : defaultValue;
	}

	public V getDefaultValue() {
		return defaultValue;
	}

	public void setDefaultValue(V defaultValue) {
		this.defaultValue = defaultValue;
	}
}
