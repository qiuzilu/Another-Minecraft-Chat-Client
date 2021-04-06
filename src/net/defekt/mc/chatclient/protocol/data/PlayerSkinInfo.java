package net.defekt.mc.chatclient.protocol.data;

import java.awt.image.BufferedImage;

/**
 * Stores info about player skin
 * 
 * @see PlayerSkinCache
 * @see BufferedImage
 * @author Defective4
 *
 */
public class PlayerSkinInfo {
	private final BufferedImage img;
	private final String url;

	/**
	 * Constructs player skin information
	 * 
	 * @param img player's skin image
	 * @param url player's skin url
	 */
	protected PlayerSkinInfo(BufferedImage img, String url) {
		this.img = img;
		this.url = url;
	}

	/**
	 * Get skin image
	 * 
	 * @return skin image
	 */
	public BufferedImage getImg() {
		return img;
	}

	/**
	 * Get skin URL
	 * 
	 * @return skin URL
	 */
	public String getUrl() {
		return url;
	}
}
