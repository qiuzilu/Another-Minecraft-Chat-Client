package net.defekt.mc.chatclient.protocol.io;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class ListenerHashMap<K, V> extends HashMap<K, V> {
	private static final long serialVersionUID = 1L;
	private final List<MapChangeListener<K, V>> listeners = new ArrayList<ListenerHashMap.MapChangeListener<K, V>>();

	public void addChangeListener(MapChangeListener<K, V> listener) {
		listeners.add(listener);
	}

	public void removeChangeListener(MapChangeListener<K, V> listener) {
		if (listeners.contains(listener))
			listeners.remove(listener);
	}

	public List<MapChangeListener<K, V>> getChangeListeners() {
		return listeners;
	}

	@Override
	public V put(K key, V value) {
		V v = super.put(key, value);
		activateAdd(key, value);
		return v;
	}

	@Override
	public V remove(Object key) {
		V v = super.remove(key);
		activateRemove(key, get(key));
		return v;
	}

	private void activateAdd(K key, V value) {
		for (MapChangeListener<K, V> listener : listeners) {
			listener.itemAdded(key, value, this);
		}
	}

	private void activateRemove(Object key, V value) {
		for (MapChangeListener<K, V> listener : listeners) {
			listener.itemRemoved(key, value, this);
		}
	}

	public static interface MapChangeListener<K, V> {
		public void itemAdded(K key, V value, HashMap<K, V> map);
		public void itemRemoved(Object key, V value, HashMap<K, V> map);
	}
}
