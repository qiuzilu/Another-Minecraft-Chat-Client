package net.defekt.mc.chatclient.protocol.data;

/**
 * An object containing information about a modification,
 * 
 * @author Defective4
 *
 */
public class ModInfo {

	private final String modID;
	private final String version;

	/**
	 * Constructs new mod info object
	 * 
	 * @param modID   mod ID
	 * @param version mod's version
	 */
	public ModInfo(String modID, String version) {
		this.modID = modID;
		this.version = version;
	}

	/**
	 * @return get mod's ID
	 */
	public String getModID() {
		return modID;
	}

	/**
	 * @return get mod's version
	 */
	public String getVersion() {
		return version;
	}
}
