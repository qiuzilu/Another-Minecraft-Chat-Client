package net.defekt.mc.chatclient.ui.swing;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Insets;
import java.awt.RenderingHints;
import java.awt.Shape;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.font.GlyphVector;

import javax.swing.JButton;

import net.defekt.mc.chatclient.ui.Main;

public class JMinecraftButton extends JButton {
	private static final long serialVersionUID = 1L;
	private boolean hover = false;

	public JMinecraftButton(String text) {
		super(text);
		setFont(Main.mcFont.deriveFont((float) 14));
		int u, b, l, r;
		Insets is = getMargin();
		u = is.top + 3;
		b = is.bottom + 3;
		l = is.left + 3;
		r = is.right + 3;
		setMargin(new Insets(u, l, b, r));
		addMouseListener(new MouseAdapter() {
			@Override
			public void mouseExited(MouseEvent e) {
				hover = false;
			}

			@Override
			public void mouseEntered(MouseEvent e) {
				hover = true;
			}
		});
	}

	@Override
	public void paintComponent(Graphics g) {
		Graphics2D g2 = (Graphics2D) g;
		g2.setFont(getFont());
		g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
		g2.setRenderingHint(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_QUALITY);
		g2.setColor(Color.black);

		g2.fillRect(0, 0, 2, getHeight());
		g2.fillRect(getWidth() - 2, 0, 2, getHeight());
		g2.fillRect(0, 0, getWidth(), 2);
		g2.fillRect(0, getHeight() - 2, getWidth(), 2);

		g2.setColor(new Color(Integer.parseInt(!isEnabled() ? "2d2d2d" : hover ? "bec7ff" : "ababab", 16)));
		g2.fillRect(2, 2, getWidth() - 4, 2);
		g2.fillRect(2, 2, 2, getHeight() - 4);

		g2.setColor(new Color(Integer.parseInt(!isEnabled() ? "2d2d2d" : hover ? "5f69a0" : "565656", 16)));
		g2.fillRect(2, getHeight() - 4, getWidth() - 4, 2);
		g2.fillRect(getWidth() - 4, 2, 2, getHeight() - 4);

		g2.setColor(new Color(Integer.parseInt(!isEnabled() ? "2d2d2d" : hover ? "7c86be" : "6f6f6f", 16)));
		g2.fillRect(4, 4, getWidth() - 8, getHeight() - 8);

		GlyphVector glyph = getFont().createGlyphVector(g2.getFontRenderContext(), getText());
		Shape bText = glyph.getOutline();
		float y = (float) ((getHeight() + (bText.getBounds2D().getHeight() * 2)) / 2);
		float x = (float) ((getWidth() - (bText.getBounds2D().getWidth())) / 2);

		if(isEnabled()) {
			g2.setColor(Color.DARK_GRAY);
			g2.fill(glyph.getOutline(x+2, y+2));
		}

		g2.setColor(isEnabled() ? Color.white : Color.lightGray);
		g2.fill(glyph.getOutline(x, y));
	}

}
