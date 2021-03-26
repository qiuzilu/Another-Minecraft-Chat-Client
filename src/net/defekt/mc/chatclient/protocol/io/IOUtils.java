package net.defekt.mc.chatclient.protocol.io;

import java.awt.Graphics2D;
import java.awt.geom.AffineTransform;
import java.awt.image.AffineTransformOp;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;

public class IOUtils {
	public static byte[] readFully(InputStream is) throws IOException {
		ByteArrayOutputStream bos = new ByteArrayOutputStream();
		byte[] buffer = new byte[1024];
		int num = 0;
		while ((num = is.read(buffer)) > 0) {
			bos.write(buffer, 0, num);
		}
		is.close();
		return bos.toByteArray();
	}

	public static BufferedImage trimSkinHead(BufferedImage img, boolean hatOn) {
		return trimSkinHead(img, hatOn, 0);
	}

	public static BufferedImage trimSkinHead(BufferedImage img, boolean hatOn, int side) {
		int sx, sy;
		switch (side) {
			case 0: {
				sx = 8;
				sy = 8;
				break;
			}
			case 1: {
				sx = 24;
				sy = 8;
				break;
			}
			default: {
				sx = 8;
				sy = 8;
				break;
			}
		}

		BufferedImage hat = new BufferedImage(8, 8, BufferedImage.TYPE_INT_ARGB);
		Graphics2D g2 = hat.createGraphics();
		g2.drawImage(trim(img, sx, sy, 8, 8), 0, 0, null);
		if (hatOn)
			g2.drawImage(trim(img, sx + 32, sy, 8, 8), 0, 0, null);

		return hat;
	}

	public static BufferedImage rescaleImageProp(BufferedImage img, int height) {
		return rescaleImage(img, height / img.getHeight());
	}

	public static BufferedImage rescaleImage(BufferedImage img, double scale) {
		BufferedImage nw = new BufferedImage((int) (img.getWidth() * scale), (int) (img.getHeight() * scale),
				img.getType());
		Graphics2D g2 = nw.createGraphics();
		g2.drawImage(img, 0, 0, nw.getWidth(), nw.getHeight(), null);
		return nw;
	}

	public static BufferedImage renderPlayerSkin(BufferedImage skin, int direction) {
		if (direction > 2)
			return null;
		BufferedImage et = new BufferedImage(16, 32, BufferedImage.TYPE_INT_ARGB);
		Graphics2D g2 = et.createGraphics();

		BufferedImage head = trimSkinHead(skin, true, direction);
		g2.drawImage(head, 4, 0, null);

		int bx, ax, lx;
		switch (direction) {
			case 0: {
				bx = 20;
				ax = 44;
				lx = 4;
				break;
			}
			case 1: {
				bx = 32;
				ax = 52;
				lx = 12;
				break;
			}
			default: {
				bx = 20;
				ax = 44;
				lx = 4;
				break;
			}
		}

		BufferedImage arms = trim(skin, ax, 20, 4, 12);
		BufferedImage legs = trim(skin, lx, 20, 4, 12);

		g2.drawImage(trim(skin, bx, 20, 8, 12), 4, 8, null);

		g2.drawImage(arms, 0, 8, null);
		g2.drawImage(flipImage(arms), 12, 8, null);

		g2.drawImage(legs, 4, 20, null);
		g2.drawImage(flipImage(legs), 8, 20, null);
		return et;
	}

	private static BufferedImage flipImage(BufferedImage img) {
		AffineTransform at = AffineTransform.getScaleInstance(-1, 1);
		at.translate(-img.getWidth(), 0);
		AffineTransformOp ap = new AffineTransformOp(at, AffineTransformOp.TYPE_NEAREST_NEIGHBOR);
		return ap.filter(img, null);
	}

	private static BufferedImage trim(BufferedImage img, int x, int y, int w, int h) {
		BufferedImage br = new BufferedImage(w, h, img.getType());
		Graphics2D g2 = br.createGraphics();
		g2.drawImage(img, 0, 0, w, h, x, y, x + w, y + h, null);
		return br;
	}
}
