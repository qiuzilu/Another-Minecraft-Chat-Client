package net.defekt.mc.chatclient.ui.swing;

import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.Graphics;
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
import javax.swing.filechooser.FileNameExtensionFilter;

import net.defekt.mc.chatclient.protocol.MinecraftClient;
import net.defekt.mc.chatclient.protocol.data.ChatMessage;
import net.defekt.mc.chatclient.protocol.data.PlayerInfo;
import net.defekt.mc.chatclient.protocol.data.PlayerSkinCache;
import net.defekt.mc.chatclient.protocol.io.IOUtils;

public class JMinecraftPlayerList extends JMemList<PlayerInfo> {
	private static final long serialVersionUID = 1L;
	private MinecraftClient mcl = null;

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
								new ImageIcon(IOUtils.rescaleImage(PlayerSkinCache.getHead(inf.getUuid()), 2)));
					} catch (Exception e2) {
						placeholder = new JMenuItem(inf.getName());
					}
					placeholder.setEnabled(false);

					JMenuItem playerInfo = new JMenuItem("Player Info");
					playerInfo.addActionListener(ev -> {
						showUserInfo(inf);
					});

					JMenuItem resetSkin = new JMenuItem("Reset skin");
					resetSkin.addActionListener(ev -> {
						if (PlayerSkinCache.getSkincache().containsKey(inf.getUuid()))
							PlayerSkinCache.getSkincache().remove(inf.getUuid());
					});

					JMenuItem resetAllSkins = new JMenuItem("Clear skin cache");
					resetAllSkins.addActionListener(ev -> {
						PlayerSkinCache.getSkincache().clear();
						SwingUtilities.invokeLater(() -> {
							repaint();
						});
					});

					JMenuItem exportList = new JMenuItem("Export player list");
					exportList.addActionListener(ev -> {
						if (mcl == null)
							return;
						String fname = addr.replace(".", "_") + " player list";
						JFileChooser jfc = new JFileChooser();
						jfc.setDialogTitle("Choose output file");
						jfc.setAcceptAllFileFilterUsed(false);
						jfc.addChoosableFileFilter(new FileNameExtensionFilter("CSV File", "csv"));
						jfc.setSelectedFile(new File(fname));
						int ret = jfc.showSaveDialog(win);
						if (ret == JFileChooser.APPROVE_OPTION) {
							File sel = jfc.getSelectedFile();
							String ext = "csv";
							if (!sel.getName().contains(".")
									|| !sel.getName().substring(sel.getName().lastIndexOf(".")).equals("." + ext))

								sel = new File(sel.getPath() + "." + ext);

							try {
								switch (ext) {
									case "csv": {
										PrintWriter pw = new PrintWriter(new FileOutputStream(sel));
										pw.println("Player name; Display name; UUID; Ping (ms); Skin URL");
										for (UUID uid : mcl.getPlayersTabList().keySet()) {
											PlayerInfo pinf = mcl.getPlayersTabList().get(uid);
											String name = pinf.getName();
											String dname = pinf.getDisplayName() == null ? "N/A"
													: ChatMessage
															.removeColors(ChatMessage.parse(pinf.getDisplayName()));
											String uuid = pinf.getUuid().toString();
											String ping = pinf.getPing() > 0 ? Integer.toString(pinf.getPing()) : "?";
											String skurl = PlayerSkinCache.getSkincache().containsKey(pinf.getUuid())
													? PlayerSkinCache.getSkincache().get(pinf.getUuid()).getUrl()
													: "N/A";

											pw.println(name + "; " + dname + "; " + uuid + "; " + ping + "; " + skurl);
										}
										pw.close();
										break;
									}
								}
							} catch (Exception e2) {
								SwingUtils.showErrorDialog(win, "Error saving file...", e2,
										"Error exporting player list: " + e2.toString());
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
		JFrame diag = new JFrame("Information about " + info.getName());
		diag.setAlwaysOnTop(true);
		diag.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);

		Box box = Box.createVerticalBox();

		String name = info.getName();
		String dname = info.getDisplayName() == null ? "N/A" : ChatMessage.parse(info.getDisplayName());
		int ping = info.getPing();
		String pingI = Integer.toString(ping);
		String uuid = info.getUuid().toString();

		box.add(new JLabel("Name: " + name));
		box.add(new JPanel() {
			private static final long serialVersionUID = 1L;
			{
				setLayout(new BoxLayout(this, BoxLayout.X_AXIS));
				add(new JLabel("Display name: "));
				JTextPane jtp = new JTextPane();
				jtp.setMaximumSize(new Dimension(SwingUtils.sSize.width, 20));
				jtp.setEditable(false);
				jtp.setOpaque(false);
				SwingUtils.appendColoredText(
						dname.replace("\u00A77", "\u00A78").replace("\u00A7f", "\u00A70").replace("\u00A7r", "\u00A70"),
						jtp);
				add(jtp);
			}
		});
		box.add(new JPanel() {
			private static final long serialVersionUID = 1L;
			{
				setLayout(new BoxLayout(this, BoxLayout.X_AXIS));
				add(new JLabel("Ping: "));
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
				add(new JLabel(" " + pingI + "ms") {
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
				add(new JLabel("UUID: "));
				JTextPane jtp = new JTextPane();
				jtp.setMaximumSize(new Dimension(SwingUtils.sSize.width, 20));
				jtp.setEditable(false);
				jtp.setOpaque(false);
				jtp.setText(uuid);
				add(jtp);
			}
		});

		box.add(new JButton("View skin") {
			private static final long serialVersionUID = 1L;
			{
				addActionListener(ev -> {
					diag.dispose();
					BufferedImage skin = PlayerSkinCache.getSkincache().get(info.getUuid()).getImg();
					BufferedImage p1 = IOUtils.renderPlayerSkin(skin, 0);
					BufferedImage p2 = IOUtils.renderPlayerSkin(skin, 1);

					BufferedImage[] igs = new BufferedImage[] { p1, p2, skin
					};

					final int[] inRef = new int[] { 0
					};

					JFrame win = new JFrame("Skin of " + info.getName());
					win.setAlwaysOnTop(true);
					win.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);

					Box box = Box.createVerticalBox();
					JPanel jp = new JPanel() {
						private static final long serialVersionUID = 1L;
						{
							setPreferredSize(
									new Dimension((SwingUtils.sSize.height / 2) * (skin.getWidth() / skin.getHeight()),
											SwingUtils.sSize.height / 2));
						}

						@Override
						public void paintComponent(Graphics g) {
							super.paintComponent(g);
							g.drawImage(IOUtils.rescaleImageProp(igs[inRef[0]], getHeight()), 0, 0, null);
						}
					};
					box.add(jp);

					box.add(new JPanel() {
						private static final long serialVersionUID = 1L;
						{
							setLayout(new BoxLayout(this, BoxLayout.X_AXIS));
							add(new JButton("<") {
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
							add(new JLabel("Change View"));
							add(new JButton(">") {
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
				});
			}
		});

		for (Component ct : box.getComponents()) {
			if (ct instanceof JComponent)
				((JComponent) ct).setAlignmentX(JComponent.LEFT_ALIGNMENT);
		}

		diag.setContentPane(box);
		diag.pack();
		SwingUtils.centerWindow(diag);
		diag.setResizable(false);
		diag.setVisible(true);
	}

	public MinecraftClient getMcl() {
		return mcl;
	}

	public void setMcl(MinecraftClient mcl) {
		this.mcl = mcl;
	}
}
