package net.defekt.mc.chatclient.protocol.data;

/**
 * Class encapsulating a item name info
 * 
 * @author Defective4
 *
 */
public class ItemInfo {

	private final String name, fileName;

	/**
	 * Constructs new item info object
	 * 
	 * @param name     item name
	 * @param fileName item's internal name
	 */
	protected ItemInfo(String name, String fileName) {
		this.name = name;
		this.fileName = fileName;
	}

	/**
	 * Get item's name
	 * 
	 * @return item name
	 */
	public String getName() {
		return name;
	}

	/**
	 * Get item's internal name
	 * 
	 * @return item's internal name
	 */
	public String getFileName() {
		return fileName;
	}
}
