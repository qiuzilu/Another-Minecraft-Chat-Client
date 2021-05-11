package net.defekt.mc.chatclient.ui.swing;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.Timer;
import java.util.TimerTask;

import javax.swing.JTextPane;
import javax.swing.JWindow;

import net.defekt.mc.chatclient.ui.Main;

/**
 * A Minecraft-like tool tip shown when hovering over an item
 * 
 * @author Defective4
 *
 */
public class MinecraftToolTip {

	private final JWindow win = new JWindow();

	/**
	 * Construct a Minecraft tool tip
	 * 
	 * @param text tool tip colored text
	 */
	public MinecraftToolTip(String text) {
		@SuppressWarnings("serial")
		JTextPane jtp = new JTextPane() {
			@Override
			public void paintComponent(Graphics g) {
				super.paintComponent(g);
				Graphics2D g2 = (Graphics2D)g;
				g2.setColor(new Color(100, 0, 100));
				g2.fillRect(0, 0, getWidth(), 2);
				g2.fillRect(getWidth()-2, 0, 2, getHeight());
				g2.fillRect(0, getHeight()-2, getWidth(), 2);
				g2.fillRect(0, 0, 2, getHeight());
			}
		};
		jtp.setFont(Main.mcFont);
		jtp.setBackground(new Color(35, 35, 35).darker());
		SwingUtils.appendColoredText(text, jtp);
		win.setContentPane(jtp);
		win.addMouseListener(new MouseAdapter() {
			@Override
			public void mousePressed(MouseEvent e) {
				hide();
			}
		});
	}

	/**
	 * Show tool tip
	 * 
	 * @param x X position
	 * @param y Y position
	 */
	public void show(int x, int y) {
		win.pack();
		win.setAlwaysOnTop(true);
		win.setLocation(x, y);
		win.setVisible(true);
		Timer timer = new Timer("tooltipHideDaemon", true);
		timer.schedule(new TimerTask() {

			@Override
			public void run() {
				hide();
			}
		}, 1000 * 25);
	}

	/**
	 * Hide this tool tip
	 */
	public void hide() {
		win.setVisible(false);
	}

	/**
	 * Dispose this tool tip
	 */
	public void dispose() {
		win.dispose();
	}
}
