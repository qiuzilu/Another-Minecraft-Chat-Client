package net.defekt.mc.chatclient.protocol.packets;

import java.util.HashMap;
import java.util.Map;

@SuppressWarnings("serial")
public abstract class PacketRegistry {
	public enum State {
		LOGIN, IN, OUT
	}

	protected abstract Map<Integer, Class<? extends Packet>> initLoginPackets();

	protected abstract Map<Integer, Class<? extends Packet>> initOutPackets();

	protected abstract Map<Integer, Class<? extends Packet>> initInPackets();

	private final Map<State, Map<Integer, Class<? extends Packet>>> packets = new HashMap<PacketRegistry.State, Map<Integer, Class<? extends Packet>>>() {
		{
			put(State.LOGIN, initLoginPackets());
			put(State.OUT, initOutPackets());
			put(State.IN, initInPackets());
		}
	};

	public int getPacketID(Class<? extends Packet> packet) {
		for (State s : packets.keySet()) {
			Map<Integer, Class<? extends Packet>> pmap = packets.get(s);
			for (int id : pmap.keySet())
				if (pmap.get(id).equals(packet))
					return id;
		}
		return -1;
	}

	public Class<? extends Packet> getByName(String packet) {
		for (State s : packets.keySet()) {
			Map<Integer, Class<? extends Packet>> pmap = packets.get(s);
			for (int id : pmap.keySet())
				if (pmap.get(id).getSimpleName().equals(packet))
					return pmap.get(id);
		}
		return null;
	}

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
