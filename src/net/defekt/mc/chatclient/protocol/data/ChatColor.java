package net.defekt.mc.chatclient.protocol.data;

import java.awt.Color;
import java.util.HashMap;
import java.util.Map;

/**
 * Class containing Minecraft's color codes, their names and their RGB
 * representation
 * 
 * @see <a href="https://wiki.vg/Chat#Colors">Chat Colors (wiki.vg)</a>
 * @author Defective4
 *
 */
public class ChatColor {
	private ChatColor() {
	}

	private static final Map<String, String> colorCodes = new HashMap<String, String>() {
		private static final long serialVersionUID = 1L;
		{
			put("black", "0");
			put("dark_blue", "1");
			put("dark_green", "2");
			put("dark_aqua", "3");
			put("dark_red", "4");
			put("dark_purple", "5");
			put("gold", "6");
			put("gray", "7");
			put("dark_gray", "8");
			put("blue", "9");
			put("green", "a");
			put("aqua", "b");
			put("red", "c");
			put("light_purple", "d");
			put("yellow", "e");
			put("white", "f");
		}
	};

	private static final Map<String, String> colors = new HashMap<String, String>() {
		private static final long serialVersionUID = 1L;
		{
			put("0", "0:0:0");
			put("1", "0:0:170");
			put("2", "0:170:0");
			put("3", "0:170:170");
			put("4", "170:0:0");
			put("5", "170:0:170");
			put("6", "255:170:0");
			put("7", "170:170:170");
			put("8", "85:85:85");
			put("9", "85:85:255");
			put("a", "85:255:85");
			put("b", "85:255:255");
			put("c", "255:85:85");
			put("d", "255:85:255");
			put("e", "255:255:85");
			put("f", "255:255:255");
			put("r", "255:255:255");
		}
	};

	/**
	 * Translate Minecraft color code (0-9 a-f) to {@link Color} object.<br>
	 * Formatting codes (k-m) are NOT supported yet.
	 * 
	 * @param code color code
	 * @return RGB color
	 */
	public static Color translateColorCode(String code) {
		if (colors.containsKey(code)) {
			String[] rgb = colors.get(code).split(":");
			int r = Integer.parseInt(rgb[0]);
			int g = Integer.parseInt(rgb[1]);
			int b = Integer.parseInt(rgb[2]);
			return new Color(r, g, b);
		} else
			return Color.white;
	}

	/**
	 * Translate Minecraft chat color name to color code
	 * 
	 * @param name color name
	 * @return color code
	 */
	public static String translateColorName(String name) {
		return colorCodes.containsKey(name) ? colorCodes.get(name) : colorCodes.get("white");
	}
}
