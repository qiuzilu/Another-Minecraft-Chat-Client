package net.defekt.mc.chatclient.ui.swing;

import java.awt.Color;

import javax.swing.JTextPane;
import javax.swing.JWindow;

import net.defekt.mc.chatclient.ui.Main;

public class MinecraftToolTip {

	private final JWindow win = new JWindow();

	public MinecraftToolTip(String text) {
		JTextPane jtp = new JTextPane();
		jtp.setFont(Main.mcFont);
		jtp.setBackground(new Color(35, 35, 35));
		SwingUtils.appendColoredText(text, jtp);
		win.setContentPane(jtp);
	}

	public void show(int x, int y) {
		win.pack();
		win.setAlwaysOnTop(true);
		win.setLocation(x, y);
		win.setVisible(true);
	}

	public void hide() {
		win.setVisible(false);
	}

	public void dispose() {
		win.dispose();
	}
}
