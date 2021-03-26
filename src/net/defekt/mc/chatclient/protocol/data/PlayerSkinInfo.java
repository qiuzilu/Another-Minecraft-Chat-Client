package net.defekt.mc.chatclient.protocol.data;

import java.awt.image.BufferedImage;

public class PlayerSkinInfo {
	private final BufferedImage img;
	private final String url;

	protected PlayerSkinInfo(BufferedImage img, String url) {
		this.img = img;
		this.url = url;
	}

	public BufferedImage getImg() {
		return img;
	}

	public String getUrl() {
		return url;
	}
}
