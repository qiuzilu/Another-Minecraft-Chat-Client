package net.defekt.mc.chatclient.ui.swing;

import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;

import javax.imageio.ImageIO;
import javax.swing.Box;
import javax.swing.DefaultListCellRenderer;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.JTextPane;
import javax.swing.SwingUtilities;

import net.defekt.mc.chatclient.protocol.data.ChatMessages;
import net.defekt.mc.chatclient.protocol.data.PlayerInfo;
import net.defekt.mc.chatclient.protocol.data.PlayerSkinCache;
import net.defekt.mc.chatclient.ui.Main;

/**
 * Custom cell rendered used in {@link JMinecraftPlayerList}.<br>
 * It is used to render players list along with their custom names, ping, and
 * their skins.
 * 
 * @author Defective4
 *
 */
public class MinecraftPlayerListRenderer extends DefaultListCellRenderer {
	private static final long serialVersionUID = 1L;

	/**
	 * <img src="doc-files/0.png" height="16" alt="0"> No connection indicator image
	 */
	public final static BufferedImage bar0 = new BufferedImage(40, 28, BufferedImage.TYPE_INT_ARGB) {
		{
			try {
				Graphics2D g2 = createGraphics();
				BufferedImage img = ImageIO
						.read(MinecraftPlayerListRenderer.class.getResourceAsStream("/resources/ping/0.png"));
				g2.drawImage(img, 0, 0, getWidth(), getHeight(), null);
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	};

	/**
	 * <img src="doc-files/5.png" height="16" alt="5"> Full connection indicator
	 * image
	 */
	public final static BufferedImage bar5 = new BufferedImage(40, 28, BufferedImage.TYPE_INT_ARGB) {
		{
			try {
				Graphics2D g2 = createGraphics();
				BufferedImage img = ImageIO
						.read(MinecraftPlayerListRenderer.class.getResourceAsStream("/resources/ping/5.png"));
				g2.drawImage(img, 0, 0, getWidth(), getHeight(), null);
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	};

	/**
	 * <img src="doc-files/1.png" height="16" alt="1"> One bar connection indicator
	 * image
	 */
	public final static BufferedImage bar1 = new BufferedImage(40, 28, BufferedImage.TYPE_INT_ARGB) {
		{
			try {
				Graphics2D g2 = createGraphics();
				BufferedImage img = ImageIO
						.read(MinecraftPlayerListRenderer.class.getResourceAsStream("/resources/ping/1.png"));
				g2.drawImage(img, 0, 0, getWidth(), getHeight(), null);
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	};

	/**
	 * <img src="doc-files/2.png" height="16" alt="2"> Two bar connection indicator
	 * image
	 */
	public final static BufferedImage bar2 = new BufferedImage(40, 28, BufferedImage.TYPE_INT_ARGB) {
		{
			try {
				Graphics2D g2 = createGraphics();
				BufferedImage img = ImageIO
						.read(MinecraftPlayerListRenderer.class.getResourceAsStream("/resources/ping/2.png"));
				g2.drawImage(img, 0, 0, getWidth(), getHeight(), null);
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	};

	/**
	 * <img src="doc-files/3.png" height="16" alt="3"> Three bar connection
	 * indicator image
	 */
	public final static BufferedImage bar3 = new BufferedImage(40, 28, BufferedImage.TYPE_INT_ARGB) {
		{
			try {
				Graphics2D g2 = createGraphics();
				BufferedImage img = ImageIO
						.read(MinecraftPlayerListRenderer.class.getResourceAsStream("/resources/ping/3.png"));
				g2.drawImage(img, 0, 0, getWidth(), getHeight(), null);
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	};

	/**
	 * <img src="doc-files/4.png" height="16" alt="4"> Four bar connection indicator
	 * image
	 */
	public final static BufferedImage bar4 = new BufferedImage(40, 28, BufferedImage.TYPE_INT_ARGB) {
		{
			try {
				Graphics2D g2 = createGraphics();
				BufferedImage img = ImageIO
						.read(MinecraftPlayerListRenderer.class.getResourceAsStream("/resources/ping/4.png"));
				g2.drawImage(img, 0, 0, getWidth(), getHeight(), null);
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	};

	private final JTextField filter;

	/**
	 * Default constructor
	 * 
	 * @param filter     text field used to filter player names
	 * @param playerList a player list component associated with this renderer
	 */
	protected MinecraftPlayerListRenderer(JTextField filter, final JMemList<PlayerInfo> playerList) {
		this.filter = filter;
		new Thread(new Runnable() {

			@Override
			public void run() {
				try {
					while (true) {
						Thread.sleep(3000);
						synchronized (MinecraftPlayerListRenderer.this) {
							SwingUtilities.invokeLater(new Runnable() {

								@Override
								public void run() {
									int ind = playerList.getSelectedIndex();
									playerList.setListData(playerList.getListData());
									playerList.setSelectedIndex(ind);
									playerList.repaint();
								}
							});
						}
					}
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		}).start();
	}

	@Override
	public Component getListCellRendererComponent(JList<? extends Object> list, Object value, int index,
			boolean isSelected, boolean cellHasFocus) {
		PlayerInfo info = (PlayerInfo) value;
		String dname = info.getDisplayName() != null ? ChatMessages.parse(info.getDisplayName()) : info.getName();

		if (!filter.getText().isEmpty()
				&& !ChatMessages.removeColors(dname).toLowerCase().contains(filter.getText().toLowerCase()))
			return new JLabel();

		Box playerLine = Box.createHorizontalBox();

		if (info.getTexture() != null)
			try {
				PlayerSkinCache.putSkin(info.getUUID(), info.getTexture(), info.getName());
				if (true)
					playerLine.add(new JPanel() {
						BufferedImage img = PlayerSkinCache.getHead(info.getUUID());
						private static final long serialVersionUID = 1L;
						{
							setPreferredSize(new Dimension(32, 40));
							setOpaque(false);
						}

						@Override
						public void paintComponent(Graphics g) {
							super.paintComponent(g);
							g.drawImage(img, 0, 0, 32, 32, null);
						}
					});
			} catch (Exception e) {

			}

		JTextPane nameField = new JTextPane();
		nameField.setEditable(false);
		nameField.setFont(Main.mcFont.deriveFont(13.5f));
		nameField.setOpaque(false);
		nameField.setForeground(Color.white);

		SwingUtils.appendColoredText(dname, nameField);

		playerLine.add(nameField);

		playerLine.add(new JPanel() {
			private static final long serialVersionUID = 1L;
			{
				setPreferredSize(new Dimension(bar0.getWidth(), bar0.getHeight()));
				setOpaque(false);
			}

			@Override
			public void paintComponent(Graphics g) {
				super.paintComponent(g);
				BufferedImage bb = bar5;

				int ping = info.getPing();

				if (ping >= 1000)
					bb = bar1;
				else if (ping >= 600)
					bb = bar2;
				else if (ping >= 300)
					bb = bar3;
				else if (ping >= 150)
					bb = bar4;
				else if (ping > 0)
					bb = bar5;
				g.drawImage(bb, 0, 0, null);
			}
		});

		playerLine.setOpaque(true);
		playerLine.setBackground(new Color(0, 0, 0, isSelected ? 255 / 3 : 0));

		return playerLine;
	}

}
