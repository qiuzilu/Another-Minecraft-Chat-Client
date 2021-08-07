package net.defekt.mc.chatclient.ui.swing;

import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

import javax.swing.JMenuItem;
import javax.swing.JPopupMenu;
import javax.swing.ListSelectionModel;

import net.defekt.mc.chatclient.ui.Main;
import net.defekt.mc.chatclient.ui.Messages;
import net.defekt.mc.chatclient.ui.ServerEntry;

/**
 * Minecraft-like server list<br>
 * It shows informations about all servers it contains (their names, motd,
 * version, players and even an icon).<br>
 * by default it uses custom cell renderer -
 * {@link MinecraftServerListRenderer}.
 * 
 * @see ServerEntry
 * @see MinecraftServerListRenderer
 * @author Defective4
 *
 */
public class JMinecraftServerList extends JMemList<ServerEntry> {
	private static final long serialVersionUID = 1L;

	/**
	 * Default constructor
	 * 
	 * @param main      instance of main application class
	 * @param popupMenu if pop-up menu with options should be accessible for this
	 *                  list
	 */
	public JMinecraftServerList(final Main main, boolean popupMenu) {
		setOpaque(true);
		setBackground(new Color(0, 0, 0, 0));
		setCellRenderer(new MinecraftServerListRenderer());
		getSelectionModel().setSelectionMode(ListSelectionModel.SINGLE_SELECTION);

		if (popupMenu)
			addMouseListener(new MouseAdapter() {
				@SuppressWarnings("serial")
				@Override
				public void mouseClicked(MouseEvent e) {
					if (e.getButton() == MouseEvent.BUTTON3) {
						setSelectedIndex(locationToIndex(e.getPoint()));
						ServerEntry et = getSelectedValue();
						final int selIndex = getSelectedIndex();

						if (et != null) {
							JPopupMenu pm = new JPopupMenu();

							JMenuItem conItem = new JMenuItem(Messages.getString("Main.connectServerOption")) {
								{
									addActionListener(main.getConnectionACL());
									setFont(getFont().deriveFont(Font.BOLD));
								}
							};

							JMenuItem mupItem = new JMenuItem(
									Messages.getString("JMinecraftServerList.serverUpLabel")) {
								{
									addActionListener(e -> {
										main.moveServer(selIndex, 0);
									});
								}
							};

							JMenuItem mdownItem = new JMenuItem(
									Messages.getString("JMinecraftServerList.serverDownLabel")) {
								{
									addActionListener(e -> {
										main.moveServer(selIndex, 1);
									});
								}
							};

							mupItem.setEnabled(selIndex > 0);
							mdownItem.setEnabled(selIndex < getListData().length - 1);

							pm.add(conItem);
							pm.add(mupItem);
							pm.add(mdownItem);
							pm.show(JMinecraftServerList.this, e.getX(), e.getY());
						}
					}
				}
			});
	}

	@Override
	public void paintComponent(Graphics g) {
		for (int x = 0; x <= getWidth() / 64; x++)
			for (int y = 0; y <= getHeight() / 64; y++)
				g.drawImage(Main.bgImage, x * 64, y * 64, 64, 64, null);
		super.paintComponent(g);
	}
}
