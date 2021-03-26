package net.defekt.mc.chatclient.protocol;

public enum ProtocolNumber {
	V1_16_4(754, "1.16.4"),
	V1_16_3(753, "1.16.3"),
//	V1_16_2(736, "1.16.2"),
//	V1_16_1(736, "1.16.1"),
//	V1_16(735, "1.16"),
	V1_15_2(578, "1.15.2"),
	V1_15_1(575, "1.15.1"),
	V1_15(573, "1.15"),
	V1_14_4(498, "1.14.4"),
	V1_14_3(490, "1.14.3"),
	V1_14_2(485, "1.14.2"),
	V1_14_1(480, "1.14.1"),
	V1_14(477, "1.14"),
	V1_13_2(404, "1.13.2"),
	V1_13_1(401, "1.13.1"),
	V1_13(393, "1.13"),
	V1_12_2(340, "1.12.2"),
	V1_12_1(338, "1.12.1"),
	V1_12(335, "1.12"),
	V1_11_2(316, "1.11.2"),
	V1_11(315, "1.11"),
	V1_10_X(210, "1.10"),
	V1_9_4(110, "1.9.4"),
	V1_9_2(109, "1.9.2"),
	V1_9_1(108, "1.9.1"),
	V1_9(107, "1.9"),
	V1_8(47, "1.8");
	
	public final int protocol;
	public final String name;
	
	private ProtocolNumber(int protocol, String name) {
		this.protocol = protocol;
		this.name = name;
	}

	public int getProtocol() {
		return protocol;
	}

	public String getName() {
		return name;
	}
	
	public static ProtocolNumber getForName(String name) {
		for(ProtocolNumber num : ProtocolNumber.values())
			if(num.name.equals(name))
				return num;
		return ProtocolNumber.values()[ProtocolNumber.values().length-1];
	}
}
