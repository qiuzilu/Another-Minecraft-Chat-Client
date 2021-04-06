package net.defekt.mc.chatclient.ui.swing;

import java.awt.Color;

import net.defekt.mc.chatclient.ui.Main;

/**
 * A Minecraft styled text field.<br>
 * Other than appearance there is no difference between this text field and a
 * regular {@link JPlaceholderField}
 * 
 * @author Defective4
 *
 */
public class JMinecraftField extends JPlaceholderField {
	private static final long serialVersionUID = 1L;

	/**
	 * Default constructor
	 * 
	 * @param placeholder placeholder to display on this field
	 */
	public JMinecraftField(String placeholder) {
		super(placeholder);
		setBackground(Color.black);
		setForeground(Color.white);
		setCaretColor(Color.white);
		setFont(Main.mcFont);
		setText("");
	}

}
