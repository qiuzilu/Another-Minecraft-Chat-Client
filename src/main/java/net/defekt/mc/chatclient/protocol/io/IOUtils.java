package net.defekt.mc.chatclient.protocol.io;

import java.awt.Graphics2D;
import java.awt.geom.AffineTransform;
import java.awt.image.AffineTransformOp;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;

import net.defekt.mc.chatclient.protocol.data.PlayerSkinCache;
import net.defekt.mc.chatclient.ui.AutoResponseRule;
import net.defekt.mc.chatclient.ui.swing.JAutoResponseList;
import net.defekt.mc.chatclient.ui.swing.JMemList;

/**
 * Class containing some IO and image manipulation utilities
 * 
 * @see PlayerSkinCache
 * @author Defective4
 *
 */
public class IOUtils {

	/**
	 * Read all bytes from stream
	 * 
	 * @param is        input stream to read from
	 * @param autoClose close stream after reading all bytes
	 * @return byte array read from stream
	 * @throws IOException thrown when there was an error reading from stream
	 */
	public static byte[] readFully(InputStream is, boolean autoClose) throws IOException {
		ByteArrayOutputStream bos = new ByteArrayOutputStream();
		byte[] buffer = new byte[1024];
		int num = 0;
		while ((num = is.read(buffer)) > 0)
			bos.write(buffer, 0, num);
		if (autoClose)
			is.close();
		return bos.toByteArray();
	}

	/**
	 * Read all bytes from stream and close it
	 * 
	 * @param is input stream to read from
	 * @return byte array read from stream
	 * @throws IOException thrown when there was an error reading from stream
	 */
	public static byte[] readFully(InputStream is) throws IOException {
		return readFully(is, true);
	}

	/**
	 * Trim skin image to head
	 * 
	 * @param img   skin image (must be original skin size)
	 * @param hatOn if true a skin hat will also be included if present
	 * @return image of skin head
	 */
	public static BufferedImage trimSkinHead(BufferedImage img, boolean hatOn) {
		return trimSkinHead(img, hatOn, 0);
	}

	/**
	 * Trim skin image to head
	 * 
	 * @param img   skin image (must be original skin size)
	 * @param hatOn if true a skin hat will also be included if present
	 * @param side  sets head direction. Current values are:<br>
	 *              0 - forward, <br>
	 *              1 - backward
	 * @return image of skin head
	 */
	public static BufferedImage trimSkinHead(BufferedImage img, boolean hatOn, int side) {
		int sx, sy;
		switch (side) {
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

	/**
	 * Resize image to match given height
	 * 
	 * @param img    image to rescale
	 * @param height target height
	 * @return resized image
	 */
	public static BufferedImage resizeImageProp(BufferedImage img, double height) {
		return scaleImage(img, height / img.getHeight());
	}

	/**
	 * Scale image with a given scale
	 * 
	 * @param img   image to rescale
	 * @param scale resize scale
	 * @return scaled image
	 */
	public static BufferedImage scaleImage(BufferedImage img, double scale) {
		BufferedImage nw = new BufferedImage((int) (img.getWidth() * scale), (int) (img.getHeight() * scale),
				img.getType());
		Graphics2D g2 = nw.createGraphics();
		g2.drawImage(img, 0, 0, nw.getWidth(), nw.getHeight(), null);
		return nw;
	}

	/**
	 * Creates image of player skin displayed as in-game
	 * 
	 * @param skin      original skin image
	 * @param direction direction of output image, same as in
	 *                  {@link #trimSkinHead(BufferedImage, boolean, int)
	 *                  trimSkinHead}
	 * @return rendered skin of player
	 */
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

	/**
	 * Flip an image along X axis
	 * 
	 * @param img image to flip
	 * @return flipped image
	 */
	private static BufferedImage flipImage(BufferedImage img) {
		AffineTransform at = AffineTransform.getScaleInstance(-1, 1);
		at.translate(-img.getWidth(), 0);
		AffineTransformOp ap = new AffineTransformOp(at, AffineTransformOp.TYPE_NEAREST_NEIGHBOR);
		return ap.filter(img, null);
	}

	/**
	 * Trim an image
	 * 
	 * @param img image to trim
	 * @param x   X coordinate of left upper corner
	 * @param y   Y coordinate of left upper corner
	 * @param w   width
	 * @param h   height
	 * @return trimmed image
	 */
	private static BufferedImage trim(BufferedImage img, int x, int y, int w, int h) {
		BufferedImage br = new BufferedImage(w, h, img.getType());
		Graphics2D g2 = br.createGraphics();
		g2.drawImage(img, 0, 0, w, h, x, y, x + w, y + h, null);
		return br;
	}

	/**
	 * Pads a string with provided character
	 * 
	 * @param string       string to pad
	 * @param len          string's target length
	 * @param padCharacter characters used in padding
	 * @param mode         padding mode 0 - append 1 - prepend
	 * @return padded string
	 */
	public static String padString(String string, int len, String padCharacter, int mode) {
		String s = string;
		while (s.length() < len)
			if (mode == 0)
				s += padCharacter;
			else
				s = padCharacter + s;
		return s;
	}

	public static void saveAmfFile(File out, JMemList<String> autoMessages) throws IOException {
		forceExtension(out, ".amf");
		String[] data = autoMessages.getListData();
		if (data == null)
			data = new String[0];
		try (DataOutputStream dos = new DataOutputStream(new FileOutputStream(out))) {
			dos.write("AMCCAMFV1".getBytes(StandardCharsets.UTF_8));
			for (String s : data) {
				byte[] sd = s.getBytes();
				dos.writeInt(sd.length);
				dos.write(sd);
			}
			dos.writeInt(-1);
		} catch (Exception e) {
			throw new IOException(e);
		}
	}

	public static String[] loadAmfFile(File in) throws IOException {
		try (DataInputStream dis = new DataInputStream(new FileInputStream(in))) {
			byte[] header = new byte[9];
			dis.read(header);
			if (!new String(header).equals("AMCCAMFV1"))
				throw new IOException("Invalid file header!");

			List<String> items = new ArrayList<String>();
			int len;
			while ((len = dis.readInt()) != -1) {
				byte[] sd = new byte[len];
				dis.read(sd);
				items.add(new String(sd));
			}
			return (String[]) items.toArray(new String[items.size()]);
		} catch (Exception e) {
			throw new IOException(e);
		}
	}

	@SuppressWarnings("unchecked")
	public static List<AutoResponseRule> loadArfFile(File in) throws IOException {
		try (ObjectInputStream is = new ObjectInputStream(new FileInputStream(in))) {
			List<AutoResponseRule> rules = (List<AutoResponseRule>) is.readObject();
			return rules;
		} catch (Exception e) {
			throw new IOException(e);
		}
	}

	public static void writeArfFile(File out, JAutoResponseList list) throws IOException {
		if (list.getListData() == null)
			throw new IOException("Rules list empty");
		try (ObjectOutputStream os = new ObjectOutputStream(new FileOutputStream(out))) {
			List<AutoResponseRule> rules = new ArrayList<AutoResponseRule>();
			for (AutoResponseRule rule : list.getListData())
				rules.add(rule);
			os.writeObject(rules);
		} catch (Exception e) {
			throw new IOException(e);
		}
	}

	public static File forceExtension(File file, String extension) {
		String ext = getFileExtension(file);
		if (ext == null || !ext.equals(extension)) {
			file = new File(
					(ext == null ? file.getPath() : file.getPath().substring(0, file.getPath().lastIndexOf(".")))
							+ extension);
		}
		return file;
	}

	public static String getFileExtension(File file) {
		String name = file.getName();
		return name.contains(".") ? name.substring(name.indexOf(".")) : null;
	}
}
