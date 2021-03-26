package net.defekt.mc.chatclient.protocol.packets;

import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.util.HashMap;
import java.util.Map;

import net.defekt.mc.chatclient.protocol.packets.registry.PacketRegistryV107;
import net.defekt.mc.chatclient.protocol.packets.registry.PacketRegistryV315;
import net.defekt.mc.chatclient.protocol.packets.registry.PacketRegistryV335;
import net.defekt.mc.chatclient.protocol.packets.registry.PacketRegistryV338;
import net.defekt.mc.chatclient.protocol.packets.registry.PacketRegistryV340;
import net.defekt.mc.chatclient.protocol.packets.registry.PacketRegistryV393;
import net.defekt.mc.chatclient.protocol.packets.registry.PacketRegistryV47;
import net.defekt.mc.chatclient.protocol.packets.registry.PacketRegistryV477;
import net.defekt.mc.chatclient.protocol.packets.registry.PacketRegistryV573;
import net.defekt.mc.chatclient.protocol.packets.registry.PacketRegistryV735;
import net.defekt.mc.chatclient.protocol.packets.registry.PacketRegistryV751;

@SuppressWarnings("serial")
public class PacketFactory {

	private static final Map<Integer, Integer> protocolBinds = new HashMap<Integer, Integer>() {
		{
			put(110, 107);
			put(109, 107);
			put(108, 107);
			put(210, 315);
			put(316, 315);
			put(401, 393);
			put(404, 393);
			put(480, 477);
			put(485, 477);
			put(490, 477);
			put(498, 477);
			put(575, 573);
			put(578, 573);
			put(736, 735);
			put(753, 751);
			put(754, 751);
		}
	};

	private static final Map<Integer, PacketRegistry> packetRegistries = new HashMap<Integer, PacketRegistry>() {
		{
			try {
				put(47, new PacketRegistryV47());
				put(107, new PacketRegistryV107());
				put(315, new PacketRegistryV315());
				put(335, new PacketRegistryV335());
				put(338, new PacketRegistryV338());
				put(393, new PacketRegistryV393());
				put(340, new PacketRegistryV340());
				put(393, new PacketRegistryV393());
				put(477, new PacketRegistryV477());
				put(573, new PacketRegistryV573());
				put(735, new PacketRegistryV735());
				put(751, new PacketRegistryV751());
			} catch (NoClassDefFoundError e) {
				e.printStackTrace();
			}
		}
	};

	public static PacketRegistry constructPacketRegistry(int protocol) throws IOException {
		if (protocolBinds.containsKey(protocol))
			protocol = protocolBinds.get(protocol);

		if (packetRegistries.containsKey(protocol))
			return packetRegistries.get(protocol);
		else
			throw new IOException("Protocol not implemented: " + Integer.toString(protocol));
	}

	public static Packet constructPacket(PacketRegistry reg, String name, Object... arguments) throws IOException {
		Object[] relArguments = new Object[arguments.length + 1];
		for (int x = 1; x < relArguments.length; x++)
			relArguments[x] = arguments[x - 1];
		relArguments[0] = reg;

		Class<?>[] argumentClasses = new Class<?>[relArguments.length];
		for (int x = 1; x < argumentClasses.length; x++)
			argumentClasses[x] = relArguments[x].getClass();
		argumentClasses[0] = PacketRegistry.class;

		try {
			Packet pk = reg.getByName(name).getDeclaredConstructor(argumentClasses).newInstance(relArguments);
			return pk;
		} catch (InstantiationException | IllegalAccessException | IllegalArgumentException | InvocationTargetException
				| NoSuchMethodException | SecurityException | NullPointerException e) {

			e.printStackTrace();

			throw new IOException("No such packet: " + name);
		}

	}
}
