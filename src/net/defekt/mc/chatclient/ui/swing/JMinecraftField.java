package net.defekt.mc.chatclient.ui.swing;

import java.awt.Color;

import net.defekt.mc.chatclient.ui.Main;

public class JMinecraftField extends JPlaceholderField {
	private static final long serialVersionUID = 1L;
	public JMinecraftField(String placeholder) {
		super(placeholder);
		setBackground(Color.black);
		setForeground(Color.white);
		setCaretColor(Color.white);
		setFont(Main.mcFont);
		setText("");
	}
	
	

}
