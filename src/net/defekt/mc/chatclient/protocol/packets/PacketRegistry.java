package net.defekt.mc.chatclient.protocol.packets;

import java.util.HashMap;
import java.util.Map;

/**
 * An abstract class that has to be subclassed to contain all packet classes and
 * their ID according to protocol specification
 * 
 * @see PacketFactory
 * @see Packet
 * @see <a href="https://wiki.vg/Protocol">https://wiki.vg/Protocol</a>
 * @author Defective4
 *
 */
@SuppressWarnings("serial")
public abstract class PacketRegistry {
	public enum State {
		LOGIN, IN, OUT
	}

	/**
	 * A method used to initialize Login state packets. It has to return map
	 * containing ID of all packets from Login state
	 * 
	 * @return map containing IDs of corresponding state's packets
	 */
	protected abstract Map<Integer, Class<? extends Packet>> initLoginPackets();

	/**
	 * A method used to initialize Play state <b>serverbound</b> packets. It has to
	 * return map containing IDs of all <b>serverbound</b> packets from Play state
	 * 
	 * @return map containing IDs of corresponding state's packets
	 */
	protected abstract Map<Integer, Class<? extends Packet>> initOutPackets();

	/**
	 * A method used to initialize Play state <b>clientbound</b> packets. It has to
	 * return map containing IDs of all <b>clientbound</b> packets from Play state
	 * 
	 * @return map containing IDs of corresponding state's packets
	 */
	protected abstract Map<Integer, Class<? extends Packet>> initInPackets();

	private final Map<State, Map<Integer, Class<? extends Packet>>> packets = new HashMap<PacketRegistry.State, Map<Integer, Class<? extends Packet>>>() {
		{
			put(State.LOGIN, initLoginPackets());
			put(State.OUT, initOutPackets());
			put(State.IN, initInPackets());
		}
	};

	/**
	 * Get packet ID for specified packet's class
	 * 
	 * @param packet packet class
	 * @return packet ID, or -1 if not found
	 */
	public int getPacketID(Class<? extends Packet> packet) {
		for (State s : packets.keySet()) {
			Map<Integer, Class<? extends Packet>> pmap = packets.get(s);
			for (int id : pmap.keySet())
				if (pmap.get(id).equals(packet))
					return id;
		}
		return -1;
	}

	/**
	 * Get packet by its name
	 * 
	 * @param packet packet name
	 * @return packet's class, or null if not found
	 */
	public Class<? extends Packet> getByName(String packet) {
		for (State s : packets.keySet()) {
			Map<Integer, Class<? extends Packet>> pmap = packets.get(s);
			for (int id : pmap.keySet())
				if (pmap.get(id).getSimpleName().equals(packet))
					return pmap.get(id);
		}
		return null;
	}

	/**
	 * Get packet by ID
	 * 
	 * @param id    packet's ID
	 * @param state state the packet belongs to
	 * @return packet's class, or null if not found
	 */
	public Class<? extends Packet> getByID(int id, State state) {
		if (packets.containsKey(state)) {
			Map<Integer, Class<? extends Packet>> pmap = packets.get(state);

			if (pmap.containsKey(id)) {
				return pmap.get(id);
			}
		}
		return null;
	}
}
