package net.defekt.mc.chatclient.protocol.io;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * An extension of Hash Map that invokes and method in each of its listeners
 * when an item is changed (incomplete)
 * 
 * @author Defective4
 *
 * @param <K> the type of keys maintained by this map
 * @param <V> the type of mapped values
 */
public class ListenerHashMap<K, V> extends HashMap<K, V> {
	private static final long serialVersionUID = 1L;
	private final List<MapChangeListener<K, V>> listeners = new ArrayList<ListenerHashMap.MapChangeListener<K, V>>();

	/**
	 * Add a map change listener
	 * 
	 * @param listener change listener
	 */
	public void addChangeListener(MapChangeListener<K, V> listener) {
		listeners.add(listener);
	}

	/**
	 * Remove a map change listener
	 * 
	 * @param listener change listener
	 */
	public void removeChangeListener(MapChangeListener<K, V> listener) {
		if (listeners.contains(listener))
			listeners.remove(listener);
	}

	/**
	 * Get change listeners added to this map
	 * 
	 * @return list of change listeners
	 */
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

	/**
	 * A listener used in receiving change events from {@link ListenerHashMap}
	 * 
	 * @author Defective4
	 *
	 * @param <K> the type of keys maintained by parent map
	 * @param <V> the type of mapped values
	 */
	public static interface MapChangeListener<K, V> {
		/**
		 * Invoked when an item is added to map
		 * 
		 * @param key   item's key
		 * @param value item's value
		 * @param map   map where the item is added
		 */
		public void itemAdded(K key, V value, HashMap<K, V> map);

		/**
		 * Invoked when an item is removed from map
		 * 
		 * @param key   item's key
		 * @param value item's value
		 * @param map   map from where the item is removed
		 */
		public void itemRemoved(Object key, V value, HashMap<K, V> map);
	}
}
