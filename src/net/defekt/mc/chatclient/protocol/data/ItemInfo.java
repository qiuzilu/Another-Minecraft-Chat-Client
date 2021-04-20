package net.defekt.mc.chatclient.protocol.data;

public class ItemInfo {

	private final String name, fileName;

	protected ItemInfo(String name, String fileName) {
		this.name = name;
		this.fileName = fileName;
	}

	public String getName() {
		return name;
	}

	public String getFileName() {
		return fileName;
	}
}
