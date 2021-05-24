package net.defekt.mc.chatclient.ui.swing;

import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileOutputStream;
import java.io.PrintWriter;
import java.util.UUID;

import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JMenuItem;
import javax.swing.JPanel;
import javax.swing.JPopupMenu;
import javax.swing.JSeparator;
import javax.swing.JTextField;
import javax.swing.JTextPane;
import javax.swing.SwingUtilities;
import javax.swing.WindowConstants;
import javax.swing.filechooser.FileNameExtensionFilter;

import net.defekt.mc.chatclient.protocol.MinecraftClient;
import net.defekt.mc.chatclient.protocol.data.ChatMessage;
import net.defekt.mc.chatclient.protocol.data.PlayerInfo;
import net.defekt.mc.chatclient.protocol.data.PlayerSkinCache;
import net.defekt.mc.chatclient.protocol.io.IOUtils;
import net.defekt.mc.chatclient.ui.Messages;

/**
 * Custom list component used to display players list.<br>
 * By default it uses custom cell renderer - {@link MinecraftPlayerListRenderer}
 * 
 * @see MinecraftPlayerListRenderer
 * @see PlayerInfo
 * @see MinecraftClient
 * @author Defective4
 *
 */
public class JMinecraftPlayerList extends JMemList<PlayerInfo> {
	private static final long serialVersionUID = 1L;
	private MinecraftClient mcl = null;

	/**
	 * Default constructor
	 * 
	 * @param filterField text field used as player filter
	 * @param win         parent window containing this list
	 * @param addr        associated server's address
	 */
	public JMinecraftPlayerList(JTextField filterField, final JFrame win, final String addr) {
		setCellRenderer(new MinecraftPlayerListRenderer(filterField, this));
		setBackground(new Color(35, 35, 35));

		addMouseListener(new MouseAdapter() {
			@Override
			public void mouseClicked(MouseEvent e) {
				if (e.getButton() == MouseEvent.BUTTON3) {
					setSelectedIndex(locationToIndex(e.getPoint()));
					PlayerInfo inf = getSelectedValue();

					JPopupMenu jp = new JPopupMenu();
					JMenuItem placeholder;
					try {
						placeholder = new JMenuItem(inf.getName(),
								new ImageIcon(IOUtils.scaleImage(PlayerSkinCache.getHead(inf.getUUID()), 2)));
					} catch (Exception e2) {
						placeholder = new JMenuItem(inf.getName());
					}
					placeholder.setEnabled(false);

					JMenuItem playerInfo = new JMenuItem(
							Messages.getString("JMinecraftPlayerList.playerListOptionPlayerInfo")); //$NON-NLS-1$
					playerInfo.addActionListener(ev -> {
						showUserInfo(inf);
					});

					JMenuItem resetSkin = new JMenuItem(
							Messages.getString("JMinecraftPlayerList.playerListOptionResetSkin")); //$NON-NLS-1$
					resetSkin.addActionListener(ev -> {
						if (PlayerSkinCache.getSkincache().containsKey(inf.getUUID()))
							PlayerSkinCache.getSkincache().remove(inf.getUUID());
					});

					JMenuItem resetAllSkins = new JMenuItem(
							Messages.getString("JMinecraftPlayerList.playerListOptionClearSkinCache")); //$NON-NLS-1$
					resetAllSkins.addActionListener(ev -> {

						PlayerSkinCache.getSkincache().clear();
						SwingUtilities.invokeLater(() -> {
							repaint();
						});
					});

					JMenuItem exportList = new JMenuItem(
							Messages.getString("JMinecraftPlayerList.playerListOptionExportPlayerList")); //$NON-NLS-1$
					exportList.addActionListener(new ActionListener() {

						@Override
						public void actionPerformed(ActionEvent e) {

							if (mcl == null)
								return;
							String fname = addr.replace(".", "_") + " player list"; //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
							JFileChooser jfc = new JFileChooser();
							jfc.setDialogTitle(Messages.getString("JMinecraftPlayerList.exportDialogTitle")); //$NON-NLS-1$
							jfc.setAcceptAllFileFilterUsed(false);
							jfc.addChoosableFileFilter(new FileNameExtensionFilter(
									Messages.getString("JMinecraftPlayerList.exportDialogFileTypeCSV"), "csv")); //$NON-NLS-1$ //$NON-NLS-2$
							jfc.setSelectedFile(new File(fname));
							int ret = jfc.showSaveDialog(win);
							if (ret == JFileChooser.APPROVE_OPTION) {
								File sel = jfc.getSelectedFile();
								String ext = "csv"; //$NON-NLS-1$
								if (!sel.getName().contains(".") //$NON-NLS-1$
										|| !sel.getName().substring(sel.getName().lastIndexOf(".")).equals("." + ext)) //$NON-NLS-1$ //$NON-NLS-2$

									sel = new File(sel.getPath() + "." + ext); //$NON-NLS-1$

								try {
									switch (ext) {
									default: {
										PrintWriter pw = new PrintWriter(new FileOutputStream(sel));
										pw.println(Messages.getString("JMinecraftPlayerList.exportFileColumns")); //$NON-NLS-1$
										for (UUID uid : mcl.getPlayersTabList().keySet()) {
											PlayerInfo pinf = mcl.getPlayersTabList().get(uid);
											String name = pinf.getName();
											String dname = pinf.getDisplayName() == null ? "N/A" //$NON-NLS-1$
													: ChatMessage
															.removeColors(ChatMessage.parse(pinf.getDisplayName()));
											String uuid = pinf.getUUID().toString();
											String ping = pinf.getPing() > 0 ? Integer.toString(pinf.getPing()) : "?"; //$NON-NLS-1$
											String skurl = PlayerSkinCache.getSkincache().containsKey(pinf.getUUID())
													? PlayerSkinCache.getSkincache().get(pinf.getUUID()).getUrl()
													: "N/A"; //$NON-NLS-1$

											pw.println(name + "; " + dname + "; " + uuid + "; " + ping + "; " + skurl); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$
										}
										pw.close();
										break;
									}
									}
								} catch (Exception e2) {
									SwingUtils.showErrorDialog(win,
											Messages.getString("JMinecraftPlayerList.exportErrorDialogTitle"), e2, //$NON-NLS-1$
											Messages.getString("JMinecraftPlayerList.exportErrorDialogMessage") //$NON-NLS-1$
													+ e2.toString());
								}
							}
						}
					});

					jp.add(placeholder);
					jp.add(playerInfo);
					jp.add(resetSkin);
					jp.add(new JSeparator());
					jp.add(resetAllSkins);
					jp.add(exportList);

					jp.show(JMinecraftPlayerList.this, e.getX(), e.getY());
				}
			}
		});

	}

	@SuppressWarnings("serial")
	private void showUserInfo(PlayerInfo info) {
		JFrame diag = new JFrame(Messages.getString("JMinecraftPlayerList.userInfoDialogTitle") + info.getName()); //$NON-NLS-1$
		diag.setAlwaysOnTop(true);
		diag.setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);

		Box box = Box.createVerticalBox();

		String name = info.getName();
		String dname = info.getDisplayName() == null ? "N/A" : ChatMessage.parse(info.getDisplayName()); //$NON-NLS-1$
		int ping = info.getPing();
		String pingI = Integer.toString(ping);
		String uuid = info.getUUID().toString();

		box.add(new JLabel(Messages.getString("JMinecraftPlayerList.userInfoDialogName") + name)); //$NON-NLS-1$
		box.add(new JPanel() {
			private static final long serialVersionUID = 1L;
			{
				setLayout(new BoxLayout(this, BoxLayout.X_AXIS));
				add(new JLabel(Messages.getString("JMinecraftPlayerList.userInfoDialogDisplayName"))); //$NON-NLS-1$
				JTextPane jtp = new JTextPane();
				jtp.setMaximumSize(new Dimension(SwingUtils.sSize.width, 20));
				jtp.setEditable(false);
				jtp.setOpaque(false);
				SwingUtils.appendColoredText(
						dname.replace("\u00A77", "\u00A78").replace("\u00A7f", "\u00A70").replace("\u00A7r", "\u00A70"), //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$ //$NON-NLS-5$ //$NON-NLS-6$
						jtp);
				add(jtp);
			}
		});
		box.add(new JPanel() {
			private static final long serialVersionUID = 1L;
			{
				setLayout(new BoxLayout(this, BoxLayout.X_AXIS));
				add(new JLabel(Messages.getString("JMinecraftPlayerList.userInfoDialogPing"))); //$NON-NLS-1$
				BufferedImage bb = MinecraftPlayerListRenderer.bar5;
				Color c = Color.green;

				if (ping >= 1000) {
					bb = MinecraftPlayerListRenderer.bar1;
					c = new Color(255, 150, 0);
				} else if (ping >= 600) {
					bb = MinecraftPlayerListRenderer.bar2;
					c = new Color(200, 150, 0);
				} else if (ping >= 300) {
					bb = MinecraftPlayerListRenderer.bar3;
					c = new Color(150, 150, 0);
				} else if (ping >= 150) {
					bb = MinecraftPlayerListRenderer.bar4;
					c = new Color(0, 150, 0);
				} else if (ping > 0) {
					bb = MinecraftPlayerListRenderer.bar5;
					c = Color.green;
				}

				final Color fc = c;
				final BufferedImage bbf = bb;
				add(new JPanel() {
					private static final long serialVersionUID = 1L;
					{
						setMaximumSize(new Dimension(16, 16));
					}

					@Override
					public void paintComponent(Graphics g) {
						super.paintComponent(g);
						g.drawImage(bbf, 0, 0, 16, 16, null);
					}
				});
				add(new JLabel(" " + pingI + "ms") { //$NON-NLS-1$ //$NON-NLS-2$
					private static final long serialVersionUID = 1L;
					{
						setForeground(fc);
					}
				});

			}
		});
		box.add(new JPanel() {
			private static final long serialVersionUID = 1L;
			{
				setLayout(new BoxLayout(this, BoxLayout.X_AXIS));
				add(new JLabel(Messages.getString("JMinecraftPlayerList.userInfoDialogUUID"))); //$NON-NLS-1$
				JTextPane jtp = new JTextPane();
				jtp.setMaximumSize(new Dimension(SwingUtils.sSize.width, 20));
				jtp.setEditable(false);
				jtp.setOpaque(false);
				jtp.setText(uuid);
				add(jtp);
			}
		});

		box.add(new JButton(Messages.getString("JMinecraftPlayerList.userInfoDialogViewSkin")) { //$NON-NLS-1$
			private static final long serialVersionUID = 1L;
			{
				addActionListener(new ActionListener() {

					@Override
					public void actionPerformed(ActionEvent e) {
						diag.dispose();
						BufferedImage skin = PlayerSkinCache.getSkincache().get(info.getUUID()).getImg();
						BufferedImage p1 = IOUtils.renderPlayerSkin(skin, 0);
						BufferedImage p2 = IOUtils.renderPlayerSkin(skin, 1);

						BufferedImage[] igs = new BufferedImage[] { p1, p2, skin };

						final int[] inRef = new int[] { 0 };

						JFrame win = new JFrame(
								Messages.getString("JMinecraftPlayerList.userSkinDialogTitle") + info.getName()); //$NON-NLS-1$
						win.setAlwaysOnTop(true);
						win.setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);

						Box box = Box.createVerticalBox();
						JPanel jp = new JPanel() {
							private static final long serialVersionUID = 1L;
							{
								setPreferredSize(new Dimension(
										(SwingUtils.sSize.height / 2) * (skin.getWidth() / skin.getHeight()),
										SwingUtils.sSize.height / 2));
							}

							@Override
							public void paintComponent(Graphics g) {
								super.paintComponent(g);
								g.drawImage(IOUtils.resizeImageProp(igs[inRef[0]], getHeight()), 0, 0, null);
							}
						};
						box.add(jp);

						box.add(new JPanel() {
							private static final long serialVersionUID = 1L;
							{
								setLayout(new BoxLayout(this, BoxLayout.X_AXIS));
								add(new JButton(Messages.getString("JMinecraftPlayerList.42")) { //$NON-NLS-1$
									{
										addActionListener(ev -> {
											inRef[0]--;
											if (inRef[0] < 0)
												inRef[0] = igs.length - 1;
											SwingUtilities.invokeLater(() -> {
												jp.repaint();
											});
										});
									}
								});
								add(new JLabel(Messages.getString("JMinecraftPlayerList.userSkinDialogChangeView"))); //$NON-NLS-1$
								add(new JButton(Messages.getString("JMinecraftPlayerList.44")) { //$NON-NLS-1$
									{
										addActionListener(ev -> {
											inRef[0]++;
											if (inRef[0] >= igs.length)
												inRef[0] = 0;
											SwingUtilities.invokeLater(() -> {
												jp.repaint();
											});
										});
									}
								});
							}
						});

						win.setContentPane(box);
						win.pack();
						SwingUtils.centerWindow(win);
						win.setVisible(true);
					}
				});
			}
		});

		for (Component ct : box.getComponents()) {
			if (ct instanceof JComponent)
				((JComponent) ct).setAlignmentX(Component.LEFT_ALIGNMENT);
		}

		diag.setContentPane(box);
		diag.pack();
		SwingUtils.centerWindow(diag);
		diag.setResizable(false);
		diag.setVisible(true);
	}

	/**
	 * Get Minecraft client instance
	 * 
	 * @return Minecraft client instance
	 */
	public MinecraftClient getMcl() {
		return mcl;
	}

	/**
	 * Set current Minecraft client instance
	 * 
	 * @param mcl Minecraft client instance
	 */
	public void setMcl(MinecraftClient mcl) {
		this.mcl = mcl;
	}
}
