package net.defekt.mc.chatclient.ui;

import java.awt.AWTException;
import java.awt.Color;
import java.awt.Component;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.GridLayout;
import java.awt.Insets;
import java.awt.Menu;
import java.awt.MenuComponent;
import java.awt.MenuItem;
import java.awt.PopupMenu;
import java.awt.SystemTray;
import java.awt.TrayIcon;
import java.awt.TrayIcon.MessageType;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.awt.image.BufferedImage;
import java.awt.image.RescaleOp;
import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.ObjectOutputStream;
import java.net.InetAddress;
import java.net.URL;
import java.util.ArrayList;
import java.util.Base64;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import javax.imageio.ImageIO;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.DefaultListCellRenderer;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JComponent;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JProgressBar;
import javax.swing.JScrollPane;
import javax.swing.JSeparator;
import javax.swing.JSpinner;
import javax.swing.JSplitPane;
import javax.swing.JTabbedPane;
import javax.swing.JTextField;
import javax.swing.JTextPane;
import javax.swing.SpinnerNumberModel;
import javax.swing.SwingConstants;
import javax.swing.SwingUtilities;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import javax.swing.plaf.basic.BasicArrowButton;

import net.defekt.mc.chatclient.protocol.ClientListener;
import net.defekt.mc.chatclient.protocol.LANListener;
import net.defekt.mc.chatclient.protocol.MinecraftClient;
import net.defekt.mc.chatclient.protocol.MinecraftStat;
import net.defekt.mc.chatclient.protocol.ProtocolNumber;
import net.defekt.mc.chatclient.protocol.data.ChatMessage;
import net.defekt.mc.chatclient.protocol.data.ItemsWindow;
import net.defekt.mc.chatclient.protocol.data.PlayerInfo;
import net.defekt.mc.chatclient.protocol.data.PlayerSkinCache;
import net.defekt.mc.chatclient.protocol.data.TranslationUtils;
import net.defekt.mc.chatclient.protocol.io.IOUtils;
import net.defekt.mc.chatclient.protocol.io.ListenerHashMap.MapChangeListener;
import net.defekt.mc.chatclient.protocol.packets.PacketFactory;
import net.defekt.mc.chatclient.protocol.packets.PacketRegistry;
import net.defekt.mc.chatclient.protocol.packets.general.clientbound.play.ServerChatMessagePacket.Position;
import net.defekt.mc.chatclient.protocol.packets.general.serverbound.play.ClientResourcePackStatusPacket.Status;
import net.defekt.mc.chatclient.ui.UserPreferences.ColorPreferences;
import net.defekt.mc.chatclient.ui.UserPreferences.Constants;
import net.defekt.mc.chatclient.ui.UserPreferences.Language;
import net.defekt.mc.chatclient.ui.UserPreferences.SkinRule;
import net.defekt.mc.chatclient.ui.swing.JColorChooserButton;
import net.defekt.mc.chatclient.ui.swing.JColorChooserButton.ColorChangeListener;
import net.defekt.mc.chatclient.ui.swing.JMinecraftButton;
import net.defekt.mc.chatclient.ui.swing.JMinecraftField;
import net.defekt.mc.chatclient.ui.swing.JMinecraftPlayerList;
import net.defekt.mc.chatclient.ui.swing.JMinecraftServerList;
import net.defekt.mc.chatclient.ui.swing.JPlaceholderField;
import net.defekt.mc.chatclient.ui.swing.JVBoxPanel;
import net.defekt.mc.chatclient.ui.swing.SwingUtils;

@SuppressWarnings({ "serial", "javadoc"
})
public class Main {

	private Main() {

	}

	public static final BufferedImage bgImage = new BufferedImage(64, 64, BufferedImage.TYPE_INT_ARGB);
	private static BufferedImage logoImage = null;

	public static final String version = "1.2.1"; //$NON-NLS-1$
	private static final String changelogURL = "https://raw.githubusercontent.com/Defective4/Another-Minecraft-Chat-Client/master/Changes"; //$NON-NLS-1$

	public static Font mcFont = Font.decode(null);

	private static void checkForUpdates() {
		try (BufferedReader br = new BufferedReader(new InputStreamReader(new URL(changelogURL).openStream()))) {
			List<String> cgLines = new ArrayList<String>();
			String line;
			while ((line = br.readLine()) != null) {
				cgLines.add(line);
			}

			if (cgLines.size() > 1 && cgLines.get(0).equals("AMCC Change Log")) { //$NON-NLS-1$
				String newVersionString = IOUtils.padString(cgLines.get(1).substring(1).replace(".", ""), 3, "0"); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
				String thisVersionString = IOUtils.padString(version.replace(".", ""), 3, "0"); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$

				int newVersion = Integer.parseInt(newVersionString);
				int thisVersion = Integer.parseInt(thisVersionString);

				if (newVersion > thisVersion) {
					String newVersionSm = cgLines.get(1).substring(1);
					String oldVersionSm = version;

					if (newVersionSm.length() - newVersionSm.replace(".", "").length() < 2) { //$NON-NLS-1$ //$NON-NLS-2$
						newVersionSm += ".0"; //$NON-NLS-1$
					}
					if (oldVersionSm.length() - oldVersionSm.replace(".", "").length() < 2) { //$NON-NLS-1$ //$NON-NLS-2$
						oldVersionSm += ".0"; //$NON-NLS-1$
					}

					int nMajor = Integer.parseInt(newVersionSm.substring(0, newVersionSm.indexOf("."))); //$NON-NLS-1$
					int nMinor = Integer.parseInt(
							newVersionSm.substring(newVersionSm.indexOf(".") + 1, newVersionSm.lastIndexOf("."))); //$NON-NLS-1$ //$NON-NLS-2$
					int nFix = Integer.parseInt(newVersionSm.substring(newVersionSm.lastIndexOf(".") + 1)); //$NON-NLS-1$

					int oMajor = Integer.parseInt(oldVersionSm.substring(0, oldVersionSm.indexOf("."))); //$NON-NLS-1$
					int oMinor = Integer.parseInt(
							oldVersionSm.substring(oldVersionSm.indexOf(".") + 1, oldVersionSm.lastIndexOf("."))); //$NON-NLS-1$ //$NON-NLS-2$
					int oFix = Integer.parseInt(oldVersionSm.substring(oldVersionSm.lastIndexOf(".") + 1)); //$NON-NLS-1$

					int diff = 0;
					String vtype = ""; //$NON-NLS-1$

					if (oFix != nFix) {
						diff = nFix - oFix;
						vtype = "minor"; //$NON-NLS-1$
					}
					if (oMinor != nMinor) {
						diff = nMinor - oMinor;
						vtype = "major"; //$NON-NLS-1$
					}
					if (oMajor != nMajor) {
						diff = nMajor - oMajor;
						vtype = "major"; //$NON-NLS-1$
					}

					cgLines.remove(0);
					cgLines.remove(0);

					SwingUtils.showVersionDialog("v" + version, "v" + newVersionSm, diff, vtype, cgLines); //$NON-NLS-1$ //$NON-NLS-2$
				}
			}

		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public static void main(String[] args) {
		SwingUtils.setNativeLook();

		if (!up.isWasLangSet()) {

			JComboBox<Language> languages = new JComboBox<>(Language.values());
			languages.setSelectedItem(up.getAppLanguage() == null ? Language.English : up.getAppLanguage());

			JFrame win = new JFrame("Choose your language");
			win.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

			JButton ct = new JButton("Ok");
			ct.addActionListener(new ActionListener() {

				@Override
				public void actionPerformed(ActionEvent e) {
					synchronized (win) {
						win.notify();
					}
				}
			});

			JOptionPane cp = new JOptionPane(new Object[] { "Choose your language", languages
			}, JOptionPane.QUESTION_MESSAGE, JOptionPane.OK_OPTION, null, new Object[] { ct
			});

			win.setContentPane(cp);
			win.pack();
			win.setResizable(false);
			SwingUtils.centerWindow(win);
			win.setVisible(true);
			win.setAlwaysOnTop(true);
			synchronized (win) {
				try {
					win.wait();
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
			}

			win.dispose();
			up.setAppLanguage((Language) languages.getSelectedItem());
		}

		checkForUpdates();
		try {
			mcFont = Font
					.createFont(Font.TRUETYPE_FONT,
							Main.class.getResourceAsStream("/resources/Minecraftia-Regular.ttf")) //$NON-NLS-1$
					.deriveFont((float) 14);
		} catch (Exception e) {
			e.printStackTrace();
		}

		try {
			logoImage = ImageIO.read(Main.class.getResourceAsStream("/resources/logo.png")); //$NON-NLS-1$
		} catch (Exception e) {
			e.printStackTrace();
		}

		Graphics2D g2 = bgImage.createGraphics();
		try {
			BufferedImage dimg = ImageIO.read(Main.class.getResourceAsStream("/resources/dirt.png")); //$NON-NLS-1$
			RescaleOp resc = new RescaleOp(0.3f, 15, null);
			resc.filter(dimg, dimg);
			g2.drawImage(dimg, 0, 0, 64, 64, null);
		} catch (Exception e) {
			g2.setColor(Color.white);
			g2.fillRect(0, 0, 64, 64);
		}
		if (up.isEnableInventoryHandling() && up.isLoadInventoryTextures())
			ItemsWindow.initTextures(new Main(), true);
		new Main().init();
	}

	protected static final File serverFile = new File("mcc.prefs"); //$NON-NLS-1$
	public static final UserPreferences up = UserPreferences.load();
	private List<ServerEntry> servers = Collections.synchronizedList(new ArrayList<ServerEntry>());
	private final JMinecraftServerList serverListComponent = new JMinecraftServerList();
	private final JMinecraftServerList lanListComponent = new JMinecraftServerList();
	private final JTabbedPane tabPane = new JTabbedPane();
	private final Map<JSplitPane, MinecraftClient> clients = new HashMap<JSplitPane, MinecraftClient>();
	private final JFrame win = new JFrame();
	private TrayIcon trayIcon = null;

	private MinecraftClient trayLastMessageSender = null;
	private int trayLastMessageType = 0;

	private ServerEntry selectedServer = null;

	private void addToList(String host, int port, String name, String version) {
		ServerEntry entry = new ServerEntry(host, port, name, version);
		for (ServerEntry se : servers) {
			if (se.equals(entry)) {
				return;
			}
		}
		synchronized (servers) {
			servers.add(entry);
		}
		entry.ping();

		ServerEntry[] entries = new ServerEntry[servers.size()];
		entries = servers.toArray(entries);

		serverListComponent.setListData(entries);

	}

	private void removeFromList(ServerEntry entry) {
		synchronized (servers) {
			servers.remove(entry);
		}
		ServerEntry[] entries = new ServerEntry[servers.size()];
		entries = servers.toArray(entries);
		serverListComponent.setListData(entries);
	}

	private Runnable upSaveRunnable = new Runnable() {

		@Override
		public void run() {
			try (ObjectOutputStream os = new ObjectOutputStream(new FileOutputStream(serverFile))) {
				os.writeObject(up);
				os.close();
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	};

	private static boolean qmdShowing = false;

	private static void showQuickMessageDialog(MinecraftClient cl) {
		if (qmdShowing)
			return;
		JTextField mField = new JPlaceholderField(Messages.getString("Main.quickMessageDialog")); //$NON-NLS-1$

		String label = cl.getHost() + ":" + cl.getPort(); //$NON-NLS-1$
		qmdShowing = true;
		int resp = JOptionPane.showOptionDialog(null,
				new Object[] { Messages.getString("Main.quickMessageRecipient") + label, mField //$NON-NLS-1$
				}, Messages.getString("Main.quickMesage"), JOptionPane.OK_CANCEL_OPTION, JOptionPane.QUESTION_MESSAGE, //$NON-NLS-1$
				null, new Object[] { Messages.getString("Main.qmOkOption"), Messages.getString("Main.qmCancelOption") //$NON-NLS-1$ //$NON-NLS-2$
				}, 0);
		qmdShowing = false;
		if (resp == 0) {
			String msg = mField.getText();
			if (msg.replace(" ", "").isEmpty()) //$NON-NLS-1$ //$NON-NLS-2$
				return;
			try {
				cl.sendChatMessage(msg);
			} catch (IOException e) {
				for (ClientListener ls : cl.getClientListeners()) {
					ls.disconnected(e.toString());
				}
			}
		}
	}

	private void init() {

		for (ServerEntry ent : up.getServers()) {
			addToList(ent.getHost(), ent.getPort(), ent.getName(), ent.getVersion());
			ent.ping();
		}
		servers = up.getServers();
		ServerEntry[] entries = new ServerEntry[servers.size()];
		entries = servers.toArray(entries);

		MinecraftStat.listenOnLAN(new LANListener() {

			@Override
			public void serverDiscovered(InetAddress sender, String motd, int port) {
				ServerEntry[] ets = lanListComponent.getListData() == null ? new ServerEntry[0]
						: lanListComponent.getListData();
				ServerEntry ent = new ServerEntry(sender.getHostAddress(), port,
						sender.getHostAddress() + ":" + Integer.toString(port), Messages.getString("Main.Auto")); //$NON-NLS-1$ //$NON-NLS-2$
				for (ServerEntry et : ets)
					if (et.equals(ent))
						return;
				ServerEntry[] ets2 = new ServerEntry[ets.length + 1];
				for (int x = 0; x < ets.length; x++)
					ets2[x] = ets[x];
				ets2[ets2.length - 1] = ent;
				lanListComponent.setListData(ets2);

				ent.ping();
			}
		});

		serverListComponent.setListData(entries);
		Runtime.getRuntime().addShutdownHook(new Thread(upSaveRunnable));

		win.setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);

		win.addWindowListener(new WindowAdapter() {
			@Override
			public void windowClosing(WindowEvent e) {
				if (clients.size() > 0) {
					JDialog diag = new JDialog(win);
					diag.setModal(true);
					diag.setTitle(Messages.getString("Main.exitDialogTitle")); //$NON-NLS-1$

					JButton ok = new JButton(Messages.getString("Main.exitOkOption")); //$NON-NLS-1$
					JButton toTray = new JButton(Messages.getString("Main.exitMinimizeOption")); //$NON-NLS-1$
					JButton cancel = new JButton(Messages.getString("Main.exitCancelOption")); //$NON-NLS-1$
					toTray.setEnabled(SystemTray.isSupported());
					JCheckBox rememberOp = new JCheckBox(Messages.getString("Main.exitRememberChoice")); //$NON-NLS-1$

					ok.addActionListener(ev -> {
						if (rememberOp.isSelected())
							up.setCloseMode(Constants.WINDOW_CLOSE_EXIT);
						System.exit(0);
					});

					cancel.addActionListener(ev -> {
						diag.dispose();
					});

					toTray.addActionListener(new ActionListener() {
						public void actionPerformed(ActionEvent ev) {
							if (rememberOp.isSelected())
								up.setCloseMode(Constants.WINDOW_CLOSE_TO_TRAY);
							if (trayIcon != null)
								return;
							diag.dispose();
							SystemTray tray = SystemTray.getSystemTray();
							trayIcon = new TrayIcon(IOUtils.scaleImage(logoImage, 0.5), "Another Chat Client"); //$NON-NLS-1$
							try {
								MouseListener ml = new MouseAdapter() {
									@Override
									public void mouseClicked(MouseEvent e) {
										if (e.getButton() != MouseEvent.BUTTON1)
											return;
										tray.remove(trayIcon);
										trayIcon = null;
										win.setVisible(true);
									}
								};
								trayIcon.addMouseListener(ml);

								trayIcon.addActionListener(new ActionListener() {

									@Override
									public void actionPerformed(ActionEvent e) {
										switch (trayLastMessageType) {
											case 0: {
												showQuickMessageDialog(trayLastMessageSender);
												break;
											}
											case 1: {
												ml.mouseClicked(new MouseEvent(win, 0, System.currentTimeMillis(), 0, 0,
														0, 0, 0, 1, false, MouseEvent.BUTTON1));
												break;
											}
											default: {
												break;
											}
										}
									}
								});

								PopupMenu menu = new PopupMenu();

								MenuItem quit = new MenuItem(Messages.getString("Main.trayQuitItem")); //$NON-NLS-1$
								quit.addActionListener(ev2 -> {
									System.exit(0);
								});

								MenuItem open = new MenuItem(Messages.getString("Main.trayOpenGUIItem")); //$NON-NLS-1$
								open.addActionListener(ev2 -> {
									ml.mouseClicked(new MouseEvent(win, 0, System.currentTimeMillis(), 0, 0, 0, 0, 0, 1,
											false, MouseEvent.BUTTON1));
								});
								open.setFont(win.getFont().deriveFont(Font.BOLD));

								Map<String, List<MinecraftClient>> labels = new HashMap<>();

								for (MinecraftClient cl : clients.values()) {
									String srvLabel = cl.getHost() + ":" + cl.getPort(); //$NON-NLS-1$
									if (!labels.containsKey(srvLabel))
										labels.put(srvLabel, new ArrayList<>());
									labels.get(srvLabel).add(cl);
								}

								menu.add(open);
								menu.addSeparator();
								for (String label : labels.keySet()) {
									Menu srvMenu = new Menu(label);
									for (final MinecraftClient cl : labels.get(label)) {
										Menu pMenu = new Menu(cl.getUsername()) {
											{
												final MinecraftClient client = cl;
												MenuItem dcItem = new MenuItem(
														Messages.getString("Main.trayDisconnectItem")); //$NON-NLS-1$
												MenuItem qmItem = new MenuItem(
														Messages.getString("Main.trayQuickMessageItem")); //$NON-NLS-1$
												final Menu ins = this;

												dcItem.addActionListener(new ActionListener() {

													@Override
													public void actionPerformed(ActionEvent e) {
														client.close();
														srvMenu.remove(ins);
														if (srvMenu.getItemCount() == 0) {
															menu.remove(srvMenu);
														}
														for (ClientListener ls : client.getClientListeners()) {
															ls.disconnected(
																	Messages.getString("Main.trayClosedReason")); //$NON-NLS-1$
														}
													}
												});

												qmItem.addActionListener(ev -> {
													showQuickMessageDialog(client);
												});

												add(qmItem);
												add(dcItem);
											}
										};

										srvMenu.add(pMenu);
									}
									menu.add(srvMenu);
								}

								menu.addSeparator();
								menu.add(quit);
								trayIcon.setPopupMenu(menu);

								tray.add(trayIcon);
								win.setVisible(false);
							} catch (AWTException e1) {
								e1.printStackTrace();
							}
						}

					});

					switch (up.getCloseMode()) {
						default: {
							break;
						}
						case 1: {
							if (toTray.isEnabled()) {
								toTray.doClick();
								return;
							}
							break;
						}
						case 2: {
							ok.doClick();
							return;
						}
					}

					JOptionPane op = new JOptionPane(new Object[] { Messages.getString("Main.trayExitQuestion") //$NON-NLS-1$
							+ Messages.getString("Main.trayExitQuestionLine2") + Integer.toString(clients.size()) //$NON-NLS-1$
							+ Messages.getString("Main.trayExitQuestionLine2Append"), rememberOp //$NON-NLS-1$
					}, JOptionPane.QUESTION_MESSAGE, JOptionPane.OK_CANCEL_OPTION, null,
							new JButton[] { ok, toTray, cancel
					});

					diag.setContentPane(op);
					diag.pack();
					SwingUtils.centerWindow(diag);
					diag.setVisible(true);
				} else
					System.exit(0);
			}
		});

		win.setTitle("Another Minecraft Chat Client v" + version); //$NON-NLS-1$
		if (logoImage != null)
			win.setIconImage(logoImage);

		JTabbedPane sTypesPane = new JTabbedPane();

		final JPanel serverListBox = new JPanel();
		serverListBox.setLayout(new BoxLayout(serverListBox, BoxLayout.Y_AXIS));
		serverListBox.setPreferredSize(
				new Dimension((int) (SwingUtils.sSize.width / 1.5), (int) (SwingUtils.sSize.height / 1.5)));

		JScrollPane serverListPane = new JScrollPane(serverListComponent);
		serverListPane.setOpaque(false);
		serverListBox.setBackground(new Color(60, 47, 74));

		serverListPane.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);

		serverListBox.add(serverListPane);
		serverListComponent.setMinimumSize(serverListBox.getPreferredSize());

		Box controlsBox = Box.createHorizontalBox();

		JButton addServer = new JMinecraftButton(Messages.getString("Main.addServerOption")); //$NON-NLS-1$
		addServer.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				JTextField nameField = new JPlaceholderField(Messages.getString("Main.serveNameField")); //$NON-NLS-1$
				nameField.setText(Messages.getString("Main.defaultServerName")); //$NON-NLS-1$
				JTextField hostField = new JPlaceholderField(Messages.getString("Main.serverAddressField")); //$NON-NLS-1$

				JComboBox<String> versionField = new JComboBox<>();
				versionField.addItem("Auto"); //$NON-NLS-1$
				versionField.addItem("Always Ask"); //$NON-NLS-1$
				for (ProtocolNumber num : ProtocolNumber.values()) {
					versionField.addItem(num.name);
				}

				final Box contents = Box.createVerticalBox();

				final JLabel errorLabel = new JLabel(""); //$NON-NLS-1$
				errorLabel.setForeground(Color.red);

				contents.add(errorLabel);
				contents.add(new JLabel(Messages.getString("Main.basicServerInfoLabel"))); //$NON-NLS-1$
				contents.add(new JLabel(" ")); //$NON-NLS-1$
				contents.add(nameField);
				contents.add(hostField);
				contents.add(versionField);

				for (Component c : contents.getComponents()) {
					if (c instanceof JComponent)
						((JComponent) c).setAlignmentX(Component.LEFT_ALIGNMENT);
				}

				do {
					try {
						int response = JOptionPane.showOptionDialog(win, contents,
								Messages.getString("Main.addServerDialogTitle"), //$NON-NLS-1$
								JOptionPane.OK_CANCEL_OPTION, JOptionPane.QUESTION_MESSAGE, null, null, null);

						if (response == JOptionPane.OK_OPTION) {
							String server = hostField.getText();
							String name = nameField.getText();

							if (server.isEmpty() || name.isEmpty()) {
								errorLabel.setText(Messages.getString("Main.addServerDialogEmptyFieldsWarning")); //$NON-NLS-1$
								continue;
							}

							String host = server;
							int port = 25565;
							if (server.contains(":") && server.split(":").length > 1) { //$NON-NLS-1$ //$NON-NLS-2$
								String[] ag = server.split(":"); //$NON-NLS-1$
								host = ag[0];
								port = Integer.parseInt(ag[1]);
							}

							addToList(host, port, name, (String) versionField.getSelectedItem());

							break;
						} else
							break;

					} catch (Exception e1) {
						e1.printStackTrace();
						errorLabel.setText(e1.toString());
					}
				} while (true);
			}
		});

		final JButton refresh = new JMinecraftButton(Messages.getString("Main.refreshOption")); //$NON-NLS-1$
		refresh.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				if (serverListComponent.getListData() != null)
					serverListComponent.setListData(serverListComponent.getListData());
				if (servers != null)
					for (ServerEntry entry : servers)
						try {
							if (!entry.refreshing) {
								entry.ping();
							}
						} catch (Exception e2) {
							e2.printStackTrace();
						}
			}
		});

		final JButton removeServer = new JMinecraftButton(Messages.getString("Main.removeServerOption")); //$NON-NLS-1$
		removeServer.addActionListener(ev -> {
			if (serverListComponent.getSelectedValue() != null) {
				removeFromList(serverListComponent.getSelectedValue());
				refresh.doClick();
			}
		});
		removeServer.setEnabled(false);

		final JButton connectServer = new JMinecraftButton(Messages.getString("Main.connectServerOption")); //$NON-NLS-1$
		ActionListener alis = new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				ServerEntry et = sTypesPane.getSelectedIndex() == 0 ? serverListComponent.getSelectedValue()
						: lanListComponent.getSelectedValue();
				if (et == null)
					return;
				Box box = Box.createVerticalBox();
				box.add(new JLabel(Messages.getString("Main.enterUsernameLabel"))); //$NON-NLS-1$

				JComboBox<String> unameField = new JComboBox<>();
				unameField.setEditable(true);
				unameField.setAlignmentX(Component.LEFT_ALIGNMENT);
				for (String uname : up.getLastUserNames())
					unameField.addItem(uname);
				box.add(unameField);

				do {
					int response = JOptionPane.showOptionDialog(win, box, Messages.getString("Main.enterUsernameTitle"), //$NON-NLS-1$
							JOptionPane.OK_CANCEL_OPTION, JOptionPane.QUESTION_MESSAGE, null, null, null);
					if (response != JOptionPane.OK_OPTION)
						return;

					String uname = (String) unameField.getSelectedItem();
					if (uname == null)
						continue;
					if (!up.isUsernameAlertSeen() && !uname.replaceAll("[^a-zA-Z0-9]", "").equals(uname)) { //$NON-NLS-1$ //$NON-NLS-2$
						int alResp = JOptionPane.showOptionDialog(win,
								Messages.getString("Main.nickIllegalCharsWarning1") + uname //$NON-NLS-1$
										+ Messages.getString("Main.nickIllegalCharsWarning2") //$NON-NLS-1$
										+ Messages.getString("Main.nickIllegalCharsWarningQuestion"), //$NON-NLS-1$
								Messages.getString("Main.nickIllegalCharsWarningTitle"), JOptionPane.YES_NO_OPTION, //$NON-NLS-1$
								JOptionPane.WARNING_MESSAGE, null,
								new Object[] { Messages.getString("Main.nickIllegalCharsWarningOptionYes"), //$NON-NLS-1$
										Messages.getString("Main.nickIllegalCharsWarningOptionNo") //$NON-NLS-1$
						}, 0);
						if (alResp == 0) {
							up.setUsernameAlertSeen(true);
							break;
						} else
							continue;
					}
					if (!uname.isEmpty())
						break;
				} while (true);

				final String uname = (String) unameField.getSelectedItem();
				final JSplitPane b = createServerPane(et, uname);

				tabPane.addTab("", b); //$NON-NLS-1$
				tabPane.setSelectedComponent(b);

				Box b2 = Box.createHorizontalBox();
				b2.setName(et.getHost() + "_" + et.getName() + "_" + uname); //$NON-NLS-1$ //$NON-NLS-2$
				int pxh = et.getIcon() == null ? 0 : 16;

				BufferedImage bicon = null;
				if (et.getIcon() != null) {
					try {
						bicon = ImageIO
								.read(new ByteArrayInputStream(Base64.getDecoder().decode(et.getIcon().getBytes())));
					} catch (IOException e1) {
						e1.printStackTrace();
					}
				}

				final BufferedImage bicon2 = bicon;

				b2.add(new JPanel() {
					{
						setPreferredSize(new Dimension(pxh, 16));
					}

					final BufferedImage bico = bicon2;

					@Override
					public void paintComponent(Graphics g) {
						g.drawImage(bico, 0, 0, 16, 16, null);
					}
				});

				b2.add(new JLabel(" " + et.getName() + " (" + (String) unameField.getSelectedItem() + ")")); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$

				JButton close = new JButton("x"); //$NON-NLS-1$
				close.setMargin(new Insets(0, 5, 0, 5));
				close.addActionListener(new ActionListener() {
					private final JSplitPane box = b;

					@Override
					public void actionPerformed(ActionEvent e) {
						for (int x = 0; x < tabPane.getTabCount(); x++) {
							if (tabPane.getComponentAt(x).equals(box)) {
								if (clients.containsKey(box))
									clients.get(box).close();
								tabPane.removeTabAt(x);
								clients.remove(b);
								break;
							}

						}
					}
				});

				b2.add(close);
				up.putUserName(uname);
				tabPane.setTabComponentAt(tabPane.getSelectedIndex(), b2);
			}
		};
		connectServer.addActionListener(alis);
		connectServer.setEnabled(false);

		serverListComponent.getSelectionModel().addListSelectionListener(new ListSelectionListener() {

			@Override
			public void valueChanged(ListSelectionEvent e) {
				if (!e.getValueIsAdjusting()) {
					selectedServer = serverListComponent.getSelectedValue();
					if (selectedServer != null) {
						removeServer.setEnabled(true);
						connectServer.setEnabled(true);
					} else {
						removeServer.setEnabled(false);
						connectServer.setEnabled(false);
					}

				}
			}
		});

		MouseListener doubleClickListener = new MouseAdapter() {
			@Override
			public void mouseClicked(MouseEvent e) {
				if (e.getButton() == MouseEvent.BUTTON1 && e.getClickCount() >= 2 && connectServer.isEnabled()) {
					connectServer.doClick();
				}
			}
		};

		serverListComponent.addMouseListener(doubleClickListener);
		controlsBox.add(connectServer);
		controlsBox.add(addServer);
		controlsBox.add(removeServer);
		controlsBox.add(refresh);

		serverListBox.add(controlsBox);

		final JPanel lanListBox = new JPanel();
		lanListBox.setBackground(serverListBox.getBackground());
		lanListBox.setLayout(new BoxLayout(lanListBox, BoxLayout.Y_AXIS));
		lanListBox.setPreferredSize(
				new Dimension((int) (SwingUtils.sSize.width / 1.5), (int) (SwingUtils.sSize.height / 1.5)));

		JScrollPane lanListPane = new JScrollPane(lanListComponent);
		lanListPane.setOpaque(false);

		lanListPane.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);

		Box lanControlsBox = Box.createHorizontalBox();

		JButton lanConnect = new JMinecraftButton(connectServer.getText());
		JButton lanAdd = new JMinecraftButton(addServer.getText());
		JButton lanRemove = new JMinecraftButton(removeServer.getText());
		JButton lanRefresh = new JMinecraftButton(refresh.getText());

		lanConnect.setEnabled(false);
		lanAdd.setEnabled(false);
		lanRemove.setEnabled(false);

		lanConnect.addActionListener(alis);
		lanRefresh.addActionListener(ev -> {
			lanListComponent.setListData(new ServerEntry[0]);
		});

		lanControlsBox.add(lanConnect);
		lanControlsBox.add(lanAdd);
		lanControlsBox.add(lanRemove);
		lanControlsBox.add(lanRefresh);

		lanListComponent.getSelectionModel().addListSelectionListener(new ListSelectionListener() {

			@Override
			public void valueChanged(ListSelectionEvent e) {
				if (!e.getValueIsAdjusting()) {
					selectedServer = lanListComponent.getSelectedValue();
					if (selectedServer != null) {
						lanConnect.setEnabled(true);
					} else {
						lanConnect.setEnabled(false);
					}

				}
			}
		});

		lanListBox.add(lanListPane);
		lanListBox.add(lanControlsBox);
		lanListComponent.setMinimumSize(lanListBox.getPreferredSize());

		sTypesPane.addTab(Messages.getString("Main.serversTabInternet"), serverListBox); //$NON-NLS-1$
		sTypesPane.addTab(Messages.getString("Main.serversTabLAN"), lanListBox); //$NON-NLS-1$

		tabPane.addTab(Messages.getString("Main.serversListTab"), sTypesPane); //$NON-NLS-1$

		JMenu fileMenu = new JMenu(Messages.getString("Main.fileMenu")) { //$NON-NLS-1$
			{
				setMnemonic(getText().charAt(0));
				add(new JMenuItem(Messages.getString("Main.fileMenuQuit")) { //$NON-NLS-1$
					{
						addActionListener(ev -> {
							System.exit(0);
						});
					}
				});
			}
		};
		JMenu optionMenu = new JMenu(Messages.getString("Main.optionsMenu")) { //$NON-NLS-1$
			{
				setMnemonic(getText().charAt(0));
				add(new JMenuItem(Messages.getString("Main.optionsMenuSettings")) { //$NON-NLS-1$
					{
						addActionListener(new ActionListener() {

							@Override
							public void actionPerformed(ActionEvent ev) {
								JDialog od = new JDialog(win);
								od.setModal(true);
								od.setResizable(false);
								od.setTitle(Messages.getString("Main.settingsTitle")); //$NON-NLS-1$

								Box b = Box.createVerticalBox();

								JTabbedPane jtp = new JTabbedPane();

								JVBoxPanel rsBox = new JVBoxPanel();

								JComboBox<Status> rPackBehaviorBox = new JComboBox<>(Status.values());
								rPackBehaviorBox.setToolTipText(Messages.getString("Main.rsBehaviorToolTip")); //$NON-NLS-1$
								rPackBehaviorBox.setSelectedItem(up.getResourcePackBehavior());
								rPackBehaviorBox.setRenderer(new DefaultListCellRenderer() {
									@Override
									public Component getListCellRendererComponent(JList<? extends Object> list,
											Object value, int index, boolean isSelected, boolean cellHasFocus) {
										JLabel lbl = new JLabel();
										String txt;
										switch ((Status) value) {
											case ACCEPTED: {
												txt = Messages.getString("Main.rsBehaviorAccept"); //$NON-NLS-1$
												break;
											}
											case DECLINED: {
												txt = Messages.getString("Main.rsBehaviorDecline"); //$NON-NLS-1$
												break;
											}
											case LOADED: {
												txt = Messages.getString("Main.rsBehaviorAcceptLoad"); //$NON-NLS-1$
												break;
											}
											default: {
												txt = Messages.getString("Main.rsBehaviorFail"); //$NON-NLS-1$
												break;
											}
										}
										lbl.setText(txt);
										lbl.setOpaque(true);
										if (isSelected) {
											lbl.setBackground(Color.blue);
											lbl.setForeground(Color.white);
										}
										return lbl;
									}
								});

								JCheckBox rsPackShowCheck = new JCheckBox(Messages.getString("Main.rsPackShowCheck"), //$NON-NLS-1$
										up.isShowResourcePackMessages());
								rsPackShowCheck.setToolTipText(Messages.getString("Main.rsPackShowToolTip")); //$NON-NLS-1$

								JPlaceholderField rsPackMsgText = new JPlaceholderField(
										Messages.getString("Main.rsPackMessageField")); //$NON-NLS-1$
								rsPackMsgText.setToolTipText(Messages.getString("Main.rsPackMessageToolTip")); //$NON-NLS-1$
								rsPackMsgText.setText(up.getResourcePackMessage());

								JComboBox<Position> rsPackMessagePosition = new JComboBox<>(Position.values());
								rsPackMessagePosition.setSelectedItem(up.getResourcePackMessagePosition());

								rsBox.add(new JLabel(Messages.getString("Main.rsPackBehaviorLabel"))); //$NON-NLS-1$
								rsBox.add(rPackBehaviorBox);
								rsBox.add(new JLabel(" ")); //$NON-NLS-1$
								rsBox.add(rsPackShowCheck);
								rsBox.add(new JLabel(" ")); //$NON-NLS-1$
								rsBox.add(new JLabel(Messages.getString("Main.rsPackMessageLabel"))); //$NON-NLS-1$
								rsBox.add(rsPackMsgText);
								rsBox.add(new JLabel(" ")); //$NON-NLS-1$
								rsBox.add(new JLabel(Messages.getString("Main.rsPackPositionLabel"))); //$NON-NLS-1$
								rsBox.add(rsPackMessagePosition);
								rsBox.add(new JTextPane() {
									{
										setEditable(false);
										setOpaque(false);
									}
								});

								rsBox.alignAll();

								JVBoxPanel skBox = new JVBoxPanel();
								skBox.add(new JLabel(Messages.getString("Main.skinFetchMetchodLabel"))); //$NON-NLS-1$
								JComboBox<SkinRule> ruleBox = new JComboBox<>(SkinRule.values());
								ruleBox.setToolTipText(Messages.getString("Main.skinFetchToolTip")); //$NON-NLS-1$
								ruleBox.setSelectedItem(up.getSkinFetchRule());
								skBox.add(ruleBox);
								skBox.add(new JTextPane() {
									{
										setText("\r\n" + Messages.getString("Main.skinFetchTipLine1") //$NON-NLS-1$ //$NON-NLS-2$
												+ Messages.getString("Main.skinFetchTipLine2") //$NON-NLS-1$
												+ Messages.getString("Main.skinFetchTipLine3") //$NON-NLS-1$
												+ Messages.getString("Main.skinFetchTipLine4")); //$NON-NLS-1$
										setEditable(false);
									}
								});

								skBox.alignAll();

								JVBoxPanel pkBox = new JVBoxPanel();

								JCheckBox ignoreKAPackets = new JCheckBox(Messages.getString("Main.ignoreKAPackets")); //$NON-NLS-1$
								ignoreKAPackets.setToolTipText(Messages.getString("Main.ignoreKAPacketsToolTop")); //$NON-NLS-1$
								ignoreKAPackets.setSelected(up.isIgnoreKeepAlive());

								JTextField brandField = new JPlaceholderField(Messages.getString("Main.brandField")); //$NON-NLS-1$
								brandField.setToolTipText(Messages.getString("Main.brandToolTop")); //$NON-NLS-1$
								brandField.setText(up.getBrand());
								SwingUtilities.invokeLater(() -> {
									brandField.setOpaque(true);
								});

								JSpinner pingField = new JSpinner(new SpinnerNumberModel(0, 0, Integer.MAX_VALUE, 1));
								pingField.setToolTipText(Messages.getString("Main.pingToolTop")); //$NON-NLS-1$
								pingField.setValue(up.getAdditionalPing());
								SwingUtils.alignSpinner(pingField);

								pkBox.add(ignoreKAPackets);
								pkBox.add(new JLabel(" ")); //$NON-NLS-1$
								pkBox.add(new JLabel(Messages.getString("Main.pingLabel"))); //$NON-NLS-1$
								pkBox.add(new JLabel(Messages.getString("Main.pingLabel2"))); //$NON-NLS-1$
								pkBox.add(pingField);
								pkBox.add(new JLabel(" ")); //$NON-NLS-1$
								pkBox.add(new JLabel(Messages.getString("Main.brandLabel"))); //$NON-NLS-1$
								pkBox.add(brandField);

								pkBox.add(new JTextPane() {
									{
										setEditable(false);
										setOpaque(false);
									}
								});
								pkBox.alignAll();

								JVBoxPanel trBox = new JVBoxPanel();

								JComboBox<String> trMessagesMode = new JComboBox<>(
										new String[] { Constants.TRAY_MESSAGES_KEY_ALWAYS,
												Constants.TRAY_MESSAGES_KEY_MENTION, Constants.TRAY_MESSAGES_KEY_NEVER
								});
								trMessagesMode.setToolTipText(Messages.getString("Main.trMessagesModeToolTop")); //$NON-NLS-1$
								trMessagesMode.setSelectedItem(up.getTrayMessageMode());
								JCheckBox showDMessages = new JCheckBox(Messages.getString("Main.showDMessages")); //$NON-NLS-1$
								showDMessages.setToolTipText(Messages.getString("Main.showDMessagesToolTop")); //$NON-NLS-1$
								showDMessages.setSelected(up.isTrayShowDisconnectMessages());

								JButton clearRem = new JButton(Messages.getString("Main.clearRem")); //$NON-NLS-1$
								if (up.getCloseMode() == Constants.WINDOW_CLOSE_ALWAYS_ASK)
									clearRem.setEnabled(false);

								clearRem.addActionListener(ev2 -> {
									up.setCloseMode(0);
									clearRem.setEnabled(false);
								});

								trBox.add(new JLabel(Messages.getString("Main.trMessagesModeLabel"))); //$NON-NLS-1$
								trBox.add(trMessagesMode);
								trBox.add(showDMessages);
								trBox.add(new JLabel(" ")); //$NON-NLS-1$
								trBox.add(clearRem);
								trBox.add(new JTextPane() {
									{
										setEditable(false);
										setOpaque(false);
									}
								});

								trBox.alignAll();

								JTabbedPane apPane = new JTabbedPane();

								JVBoxPanel apButtonSettings = new JVBoxPanel();
								JScrollPane apButtonSettingsSP = new JScrollPane(apButtonSettings);
								JVBoxPanel apButtonSettingsFull = new JVBoxPanel();
								apButtonSettingsSP.setPreferredSize(new Dimension(0, 0));

								ColorPreferences cp = Main.up.getColorPreferences();
								ColorPreferences cprefCopy = new UserPreferences.ColorPreferences();
								cprefCopy.setColorDisabledButton(cp.getColorDisabledButton());
								cprefCopy.setColorEnabledButton(cp.getColorEnabledButton());
								cprefCopy.setColorEnabledHoverButton(cp.getColorEnabledHoverButton());
								cprefCopy.setColorText(cp.getColorText());
								cprefCopy.setDisabledColorText(cp.getDisabledColorText());

								JColorChooserButton apButtonEnabled = new JColorChooserButton(
										cp.getColorEnabledButton(), od);
								JColorChooserButton apButtonEnabledHover = new JColorChooserButton(
										cp.getColorEnabledHoverButton(), od);
								JColorChooserButton apButtonDisabled = new JColorChooserButton(
										cp.getColorDisabledButton(), od);
								JColorChooserButton apButtonText = new JColorChooserButton(cp.getColorText(), od);
								JColorChooserButton apButtonTextDisabled = new JColorChooserButton(
										cp.getDisabledColorText(), od);

								JCheckBox apButtonLockColors = new JCheckBox(
										Messages.getString("Main.apButtonLockColors")); //$NON-NLS-1$
								apButtonLockColors.setSelected(true);
								JButton apButtonReset = new JButton(Messages.getString("Main.apButtonReset")); //$NON-NLS-1$

								JMinecraftButton sampleButton = new JMinecraftButton("Test"); //$NON-NLS-1$
								JMinecraftButton sampleDisabledButton = new JMinecraftButton("Test"); //$NON-NLS-1$
								sampleButton.setCp(cprefCopy);
								sampleDisabledButton.setCp(cprefCopy);
								sampleDisabledButton.setEnabled(false);

								apButtonSettings.add(apButtonLockColors);
								apButtonSettings.add(new JLabel(Messages.getString("Main.apButtonSettingsBGLabel"))); //$NON-NLS-1$
								apButtonSettings.add(apButtonEnabled);
								apButtonSettings.add(new JLabel(Messages.getString("Main.apButtonSettingsHoverLabel"))); //$NON-NLS-1$
								apButtonSettings.add(apButtonEnabledHover);
								apButtonSettings
										.add(new JLabel(Messages.getString("Main.apButtonSettingsDisabledLabel"))); //$NON-NLS-1$
								apButtonSettings.add(apButtonDisabled);
								apButtonSettings.add(new JLabel(Messages.getString("Main.apButtonSettingsTextColor"))); //$NON-NLS-1$
								apButtonSettings.add(apButtonText);
								apButtonSettings.add(new JLabel(Messages.getString("Main.apButtonSettingsDTexTColor"))); //$NON-NLS-1$
								apButtonSettings.add(apButtonTextDisabled);
								apButtonSettings.add(new JLabel(" ")); //$NON-NLS-1$
								apButtonSettings.add(apButtonReset);
								apButtonSettings.add(new JLabel(" ")); //$NON-NLS-1$

								apButtonEnabled.addColorChangeListener(new ColorChangeListener() {

									@Override
									public void colorChanged(Color c) {
										cprefCopy.setColorEnabledButton(SwingUtils.getHexRGB(c));
										if (apButtonLockColors.isSelected()) {
											Color hover = SwingUtils.brighten(c, 51);
											Color disabled = SwingUtils.brighten(c,
													(int) -(((c.getRed() + c.getGreen() + c.getBlue()) / 3) / 1.3));
											cprefCopy.setColorEnabledHoverButton(SwingUtils.getHexRGB(hover));
											cprefCopy.setColorDisabledButton(SwingUtils.getHexRGB(disabled));
											apButtonEnabledHover.setColor(hover);
											apButtonDisabled.setColor(disabled);
										}
										sampleButton.repaint();
										sampleDisabledButton.repaint();
									}
								});
								apButtonEnabledHover.addColorChangeListener(c -> {
									cprefCopy.setColorEnabledHoverButton(SwingUtils.getHexRGB(c));
									sampleButton.repaint();
									sampleDisabledButton.repaint();
								});

								apButtonDisabled.addColorChangeListener(c -> {
									cprefCopy.setColorDisabledButton(SwingUtils.getHexRGB(c));
									sampleButton.repaint();
									sampleDisabledButton.repaint();
								});
								apButtonText.addColorChangeListener(c -> {
									cprefCopy.setColorText(SwingUtils.getHexRGB(c));
									sampleButton.repaint();
									sampleDisabledButton.repaint();
								});
								apButtonTextDisabled.addColorChangeListener(c -> {
									cprefCopy.setDisabledColorText(SwingUtils.getHexRGB(c));
									sampleButton.repaint();
									sampleDisabledButton.repaint();
								});
								apButtonReset.addActionListener(ev2 -> {
									ColorPreferences cp2 = UserPreferences.defaultColorPreferences;
									cprefCopy.setColorDisabledButton(cp2.getColorDisabledButton());
									cprefCopy.setColorEnabledButton(cp2.getColorEnabledButton());
									cprefCopy.setColorEnabledHoverButton(cp2.getColorEnabledHoverButton());
									cprefCopy.setColorText(cp2.getColorText());
									cprefCopy.setDisabledColorText(cp2.getDisabledColorText());

									apButtonDisabled
											.setColor(new Color(Integer.parseInt(cp2.getColorDisabledButton(), 16)));
									apButtonEnabled
											.setColor(new Color(Integer.parseInt(cp2.getColorEnabledButton(), 16)));
									apButtonEnabledHover.setColor(
											new Color(Integer.parseInt(cp2.getColorEnabledHoverButton(), 16)));
									apButtonText.setColor(new Color(Integer.parseInt(cp2.getColorText(), 16)));
									apButtonTextDisabled
											.setColor(new Color(Integer.parseInt(cp2.getDisabledColorText(), 16)));
									sampleButton.repaint();
									sampleDisabledButton.repaint();
								});

								Box apButtonSettingsSamples = Box.createHorizontalBox();
								apButtonSettingsSamples.add(sampleButton);
								apButtonSettingsSamples.add(sampleDisabledButton);

								apButtonSettingsFull.add(apButtonSettingsSP);
								apButtonSettingsFull.add(apButtonSettingsSamples);

								apButtonSettings.alignAll();

								apPane.addTab(Messages.getString("Main.appearancePaneButtons"), apButtonSettingsFull); //$NON-NLS-1$

								JVBoxPanel ivBox = new JVBoxPanel();

								final JCheckBox enableIVHandling = new JCheckBox(
										Messages.getString("Main.enableIVHandling")); //$NON-NLS-1$
								final JCheckBox hideIncomingWindows = new JCheckBox(
										Messages.getString("Main.hideIncomingWindows")); //$NON-NLS-1$
								final JCheckBox hiddenWindowsResponse = new JCheckBox(
										Messages.getString("Main.hiddenWindowsResponse")); //$NON-NLS-1$
								final JCheckBox loadTextures = new JCheckBox(
										Messages.getString("Main.loadItemTextures")); //$NON-NLS-1$
								final JCheckBox showWhenInTray = new JCheckBox(
										Messages.getString("Main.showWindowsInTray")); //$NON-NLS-1$
								final JCheckBox sendClosePackets = new JCheckBox(
										Messages.getString("Main.sendClosePackets")); //$NON-NLS-1$

								enableIVHandling.setToolTipText(Messages.getString("Main.enableIVHandlingToolTop")); //$NON-NLS-1$
								loadTextures.setToolTipText(Messages.getString("Main.loadItemTexturesToolTop")); //$NON-NLS-1$
								showWhenInTray.setToolTipText(Messages.getString("Main.showWindowsInTrayToolTop")); //$NON-NLS-1$
								sendClosePackets.setToolTipText(Messages.getString("Main.sendClosePacketsToolTop")); //$NON-NLS-1$
								hideIncomingWindows
										.setToolTipText(Messages.getString("Main.hideIncomingWindowsToolTop")); //$NON-NLS-1$
								hiddenWindowsResponse
										.setToolTipText(Messages.getString("Main.hiddenWindowsResponseToolTop")); //$NON-NLS-1$

								enableIVHandling.setSelected(up.isEnableInventoryHandling());
								loadTextures.setSelected(up.isLoadInventoryTextures());
								showWhenInTray.setSelected(up.isShowWindowsInTray());
								sendClosePackets.setSelected(up.isSendWindowClosePackets());
								hideIncomingWindows.setSelected(up.isHideIncomingWindows());
								hiddenWindowsResponse.setSelected(up.isHiddenWindowsResponse());
								hiddenWindowsResponse.setEnabled(hideIncomingWindows.isSelected());

								hideIncomingWindows.addActionListener(ev2 -> {
									hiddenWindowsResponse.setEnabled(hideIncomingWindows.isSelected());
								});

								enableIVHandling.addActionListener(new ActionListener() {

									@Override
									public void actionPerformed(ActionEvent e) {
										recDisable(ivBox);
									}

									private void recDisable(Component ct) {
										if (ct instanceof Container) {
											setEb(ct);
											for (Component cpt : ((Container) ct).getComponents())
												recDisable(cpt);
										} else {
											setEb(ct);
										}
									}

									private void setEb(Component ct) {
										if ((ct instanceof JCheckBox) && !ct.equals(enableIVHandling)) {
											ct.setEnabled(enableIVHandling.isSelected());
											if (ct.equals(hiddenWindowsResponse))
												ct.setEnabled(hideIncomingWindows.isSelected()
														&& hideIncomingWindows.isEnabled());
										}
									}
								});

								ivBox.add(new JPanel() {
									{
										setLayout(new BoxLayout(this, BoxLayout.X_AXIS));
										add(enableIVHandling);
										add(new JButton("?") { //$NON-NLS-1$
											{
												addActionListener(new ActionListener() {

													@Override
													public void actionPerformed(ActionEvent e) {
														JOptionPane.showOptionDialog(od, Messages
																.getString("Main.inventoryHandlingHelpLine1") //$NON-NLS-1$
																+ Messages.getString("Main.inventoryHandlingHelpLine2") //$NON-NLS-1$
																+ Messages.getString("Main.inventoryHandlingHelpLine3") //$NON-NLS-1$
																+ Messages.getString("Main.inventoryHandlingHelpLine4") //$NON-NLS-1$
																+ Messages.getString("Main.inventoryHandlingHelpLine5") //$NON-NLS-1$
																+ Messages.getString("Main.inventoryHandlingHelpLine6") //$NON-NLS-1$
																+ Messages.getString("Main.inventoryHandlingHelpLine7") //$NON-NLS-1$
																+ Messages.getString("Main.inventoryHandlingHelpLine8") //$NON-NLS-1$
																+ Messages.getString("Main.inventoryHandlingHelpLine9"), //$NON-NLS-1$
																Messages.getString("Main.inventoryHandlingHelpTitle"), //$NON-NLS-1$
																JOptionPane.YES_NO_OPTION,
																JOptionPane.INFORMATION_MESSAGE, null,
																new Object[] { Messages
																		.getString("Main.inventoryHandlingHelpOk") //$NON-NLS-1$
														}, 0);
													}
												});
											}
										});
									}
								});
								ivBox.add(loadTextures);
								ivBox.add(showWhenInTray);
								ivBox.add(sendClosePackets);
								ivBox.add(new JSeparator());
								ivBox.add(hideIncomingWindows);
								ivBox.add(new JPanel() {
									{
										setLayout(new BoxLayout(this, BoxLayout.X_AXIS));
										add(Box.createHorizontalStrut(10));
										add(hiddenWindowsResponse);
									}
								});
								ivBox.add(new JSeparator());
								ivBox.add(new JTextPane() {
									{
										setEditable(false);
										setOpaque(false);
									}
								});

								for (Component ct : ivBox.getComponents()) {
									if (!(ct instanceof JTextPane) && !ct.equals(enableIVHandling)) {
										ct.setEnabled(enableIVHandling.isSelected());
									}
								}

								ivBox.alignAll();

								JVBoxPanel gnBox = new JVBoxPanel();

								JComboBox<Language> languages = new JComboBox<>(Language.values());
								languages.setSelectedItem(up.getAppLanguage());

								gnBox.add(new JLabel(Messages.getString("Main.settingsLangChangeLabel"))); //$NON-NLS-1$
								gnBox.add(languages);
								gnBox.add(new JTextPane() {
									{
										setEditable(false);
										setOpaque(false);
									}
								});

								gnBox.alignAll();

								jtp.add(Messages.getString("Main.settingsTabGeneral"), gnBox); //$NON-NLS-1$
								jtp.add(Messages.getString("Main.settingsTabAppearance"), apPane); //$NON-NLS-1$
								jtp.add(Messages.getString("Main.settingsTabTray"), trBox); //$NON-NLS-1$
								jtp.add(Messages.getString("Main.settingsTabResourcePacks"), rsBox); //$NON-NLS-1$
								jtp.add(Messages.getString("Main.settingsTabSkins"), skBox); //$NON-NLS-1$
								jtp.add(Messages.getString("Main.settingsTabProtocol"), pkBox); //$NON-NLS-1$
								jtp.add(Messages.getString("Main.settingsTabInventory"), ivBox); //$NON-NLS-1$
								b.add(jtp);

								JButton sOk = new JButton(Messages.getString("Main.settingsOk")); //$NON-NLS-1$
								JButton sCancel = new JButton(Messages.getString("Main.settingsCancel")); //$NON-NLS-1$

								sOk.addActionListener(new ActionListener() {

									@Override
									public void actionPerformed(ActionEvent e) {
										Status rsBehavior = (Status) rPackBehaviorBox.getSelectedItem();
										boolean showResourcePackMessages = rsPackShowCheck.isSelected();
										String resourcePackMessage = rsPackMsgText.getText();
										Position resourcePackMessagePosition = (Position) rsPackMessagePosition
												.getSelectedItem();

										SkinRule skinFetchRule = (SkinRule) ruleBox.getSelectedItem();

										boolean ignoreKeepAlive = ignoreKAPackets.isSelected();
										String brand = brandField.getText();
										boolean sendMCBrand = !brand.isEmpty();

										up.setResourcePackBehavior(rsBehavior);
										up.setShowResourcePackMessages(showResourcePackMessages);
										up.setResourcePackMessage(resourcePackMessage.replace("&", "\u00A7")); //$NON-NLS-1$ //$NON-NLS-2$
										up.setResourcePackMessagePosition(resourcePackMessagePosition);

										up.setSkinFetchRule(skinFetchRule);

										up.setIgnoreKeepAlive(ignoreKeepAlive);
										up.setAdditionalPing((int) pingField.getValue());
										up.setBrand(brand);
										up.setSendMCBrand(sendMCBrand);

										up.setTrayMessageMode((String) trMessagesMode.getSelectedItem());
										up.setTrayShowDisconnectMessages(showDMessages.isSelected());

										if (!enableIVHandling.isSelected()) {
											for (MinecraftClient cl : clients.values()) {
												for (ItemsWindow iw : cl.getOpenWindows().values())
													iw.closeWindow();
												cl.getInventory().closeWindow();
											}
										}

										if (!enableIVHandling.isSelected() || !loadTextures.isSelected()) {
											if ((up.isEnableInventoryHandling() != enableIVHandling.isSelected())
													|| (up.isLoadInventoryTextures() != loadTextures.isSelected()))
												if (ItemsWindow.getTexturesSize() > 0) {
													int response = JOptionPane.showOptionDialog(od, Messages
															.getString("Main.inventoryHandlingDisabledLine1") //$NON-NLS-1$
															+ Messages.getString("Main.inventoryHandlingDisabledLine2"), //$NON-NLS-1$
															Messages.getString("Main.inventoryHandlingDisabledTitle"), //$NON-NLS-1$
															JOptionPane.YES_NO_OPTION, JOptionPane.QUESTION_MESSAGE,
															null,
															new Object[] {
																	Messages.getString(
																			"Main.inventoryHandlingDisabledYes"), //$NON-NLS-1$
																	Messages.getString(
																			"Main.inventoryHandlingDisabledNo") //$NON-NLS-1$
													}, 0);
													if (response == 0)
														ItemsWindow.clearTextures(Main.this);
												}
										}

										if (enableIVHandling.isSelected() && loadTextures.isSelected())
											if ((up.isEnableInventoryHandling() != enableIVHandling.isSelected())
													|| (up.isLoadInventoryTextures() != loadTextures.isSelected())) {
												int response = JOptionPane.showOptionDialog(od,
														Messages.getString("Main.itemLoadingEnabledLine1") //$NON-NLS-1$
																+ Messages.getString("Main.itemLoadingEnabledLine2"), //$NON-NLS-1$
														Messages.getString("Main.itemLoadingEnabledTitle"), //$NON-NLS-1$
														JOptionPane.YES_NO_OPTION, JOptionPane.QUESTION_MESSAGE, null,
														new Object[] { Messages.getString("Main.itemLoadingEnabledYes"), //$NON-NLS-1$
																Messages.getString("Main.itemLoadingEnabledNo") //$NON-NLS-1$
												}, 0);
												if (response == 0) {
													od.dispose();
													ItemsWindow.initTextures(Main.this, false);
													if (clients.size() > 0)
														JOptionPane.showOptionDialog(
																od, Messages.getString("Main.itemTexturesLoadedLine1") //$NON-NLS-1$
																		+ Messages.getString(
																				"Main.itemTexturesLoadedLine2") //$NON-NLS-1$
																		+ Messages.getString(
																				"Main.itemTexturesLoadedLine3"), //$NON-NLS-1$
																Messages.getString("Main.itemTexturesLoadedTitle"), //$NON-NLS-1$
																JOptionPane.OK_CANCEL_OPTION,
																JOptionPane.INFORMATION_MESSAGE, null, new Object[] {
																		Messages.getString("Main.itemTexturesLoadedOk") //$NON-NLS-1$
														}, 0);
												}
											}

										if (enableIVHandling.isSelected() && !up.isEnableInventoryHandling())
											if (clients.size() > 0)
												JOptionPane.showOptionDialog(
														od, Messages.getString("Main.inventoryHandlingEnabledLine1") //$NON-NLS-1$
																+ Messages
																		.getString("Main.inventoryHandlingEnabledLine2") //$NON-NLS-1$
																+ Messages.getString(
																		"Main.inventoryHandlingEnabledLine3"), //$NON-NLS-1$
														Messages.getString("Main.inventoryHandlingEnabledTitle"), //$NON-NLS-1$
														JOptionPane.OK_CANCEL_OPTION, JOptionPane.INFORMATION_MESSAGE,
														null, new Object[] {
																Messages.getString("Main.inventoryHandlingEnabledOk") //$NON-NLS-1$
												}, 0);

										up.setEnableInventoryHandling(enableIVHandling.isSelected());
										up.setHideIncomingWindows(hideIncomingWindows.isSelected());
										up.setHiddenWindowsResponse(hiddenWindowsResponse.isSelected());
										up.setLoadInventoryTextures(loadTextures.isSelected());
										up.setShowWindowsInTray(showWhenInTray.isSelected());
										up.setSendWindowClosePackets(sendClosePackets.isSelected());

										boolean langChanged = up.getAppLanguage() != languages.getSelectedItem();
										up.setAppLanguage((Language) languages.getSelectedItem());

										ColorPreferences cp2 = up.getColorPreferences();
										cp2.setColorDisabledButton(SwingUtils.getHexRGB(apButtonDisabled.getColor()));
										cp2.setColorEnabledButton(SwingUtils.getHexRGB(apButtonEnabled.getColor()));
										cp2.setColorEnabledHoverButton(
												SwingUtils.getHexRGB(apButtonEnabledHover.getColor()));
										cp2.setColorText(SwingUtils.getHexRGB(apButtonText.getColor()));
										cp2.setDisabledColorText(SwingUtils.getHexRGB(apButtonTextDisabled.getColor()));

										upSaveRunnable.run();
										PlayerSkinCache.getSkincache().clear();

										if (langChanged) {
											int response = JOptionPane.showOptionDialog(od,
													Messages.getString("Main.langChangedLabelLine1") //$NON-NLS-1$
															+ Messages.getString("Main.langChangedLabelLine2"), //$NON-NLS-1$
													Messages.getString("Main.langChangedDialogTitle"), //$NON-NLS-1$
													JOptionPane.OK_OPTION, JOptionPane.INFORMATION_MESSAGE, null,
													new Object[] {
															Messages.getString(
																	"Main.langChangedLabelDialogOptionRestart"), //$NON-NLS-1$
															Messages.getString(
																	"Main.langChangedLabelDialogOptionContinue") //$NON-NLS-1$
											}, 0);
											if (response == 0)
												System.exit(0);
										}

										od.dispose();
										win.repaint();
									}
								});

								sCancel.addActionListener(ev2 -> {
									od.dispose();
								});

								Box sControls = Box.createHorizontalBox();

								sControls.add(sOk);
								sControls.add(sCancel);

								b.add(sControls);
								od.setContentPane(b);
								od.pack();
								SwingUtils.centerWindow(od);
								od.setVisible(true);
							}
						});
					}
				});
			}
		};

		win.setJMenuBar(new JMenuBar() {
			{
				add(fileMenu);
				add(optionMenu);
			}

		});
		win.setContentPane(tabPane);
		win.pack();
		SwingUtils.centerWindow(win);
		win.setVisible(true);

	}

	private JSplitPane createServerPane(final ServerEntry entry, final String username) {

		final JSplitPane fPane = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT);

		final Box box = Box.createVerticalBox();

		final JTextPane pane = new JTextPane();
		pane.setBackground(new Color(35, 35, 35));
		pane.setForeground(Color.white);
		pane.setEditable(false);
		pane.setFont(mcFont.deriveFont((float) 13.5f));

		final JScrollPane jsc = new JScrollPane(pane);
		jsc.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);

		final Box chatControls = Box.createHorizontalBox();

		final JTextField chatInput = new JMinecraftField(Messages.getString("Main.chatField")); //$NON-NLS-1$
		chatInput.setEnabled(false);

		final JButton chatSend = new JMinecraftButton(Messages.getString("Main.chatSendButton")); //$NON-NLS-1$
		chatSend.setEnabled(false);
		chatSend.setMargin(new Insets(5, 5, 5, 5));

		for (Component ct : chatControls.getComponents())
			ct.setFont(ct.getFont().deriveFont((float) 13.5f));

		chatControls.add(chatInput);
		chatControls.add(chatSend);
		chatControls.setMaximumSize(new Dimension(SwingUtils.sSize.width, 0));

		final JTextPane hotbar = new JTextPane();
		hotbar.setBackground(new Color(35, 35, 35));
		hotbar.setForeground(Color.white);
		hotbar.setEditable(false);
		hotbar.setFont(mcFont.deriveFont((float) 13.5f));

		JScrollPane hjsc = new JScrollPane(hotbar);
		hjsc.setMaximumSize(chatControls.getMaximumSize());

		JTabbedPane controlsTabPane = new JTabbedPane();
//		JScrollPane controlsScrollPane = new JScrollPane(controlsTabPane);

		win.addComponentListener(new ComponentAdapter() {
			@Override
			public void componentResized(ComponentEvent e) {
				SwingUtilities.invokeLater(new Runnable() {

					@Override
					public void run() {
						fPane.setDividerLocation((double) 0.8);
					}
				});
			}
		});

		JVBoxPanel playerBox = new JVBoxPanel();
		JPanel statisticsContainer = new JPanel();

		final JCheckBox toggleSneak = new JCheckBox(Messages.getString("Main.toggleSneak")); //$NON-NLS-1$
		toggleSneak.addActionListener(ev -> {
			try {
				MinecraftClient cl = clients.get(fPane);
				cl.toggleSneaking();
				toggleSneak.setSelected(cl.isSneaking());
			} catch (Exception e1) {
				e1.printStackTrace();
			}
		});

		playerBox.add(toggleSneak);

		final JCheckBox toggleSprint = new JCheckBox(Messages.getString("Main.toggleSprint")); //$NON-NLS-1$
		toggleSprint.addActionListener(ev -> {
			try {
				MinecraftClient cl = clients.get(fPane);
				cl.toggleSprinting();
				toggleSprint.setSelected(cl.isSprinting());
			} catch (Exception e1) {
				e1.printStackTrace();
			}
		});

		final JProgressBar healthBar = new JProgressBar(0, 0);
		final JProgressBar foodBar = new JProgressBar(0, 0);

		healthBar.setStringPainted(true);
		foodBar.setStringPainted(true);

		healthBar.setString(Messages.getString("Main.healthBar")); //$NON-NLS-1$
		foodBar.setString(Messages.getString("Main.foodBar")); //$NON-NLS-1$

		JButton[] movementButtons = new JButton[] { new BasicArrowButton(SwingConstants.NORTH),
				new BasicArrowButton(SwingConstants.SOUTH_WEST), new BasicArrowButton(SwingConstants.WEST),
				new BasicArrowButton(SwingConstants.NORTH_WEST), new BasicArrowButton(SwingConstants.SOUTH),
				new BasicArrowButton(SwingConstants.NORTH_EAST), new BasicArrowButton(SwingConstants.EAST),
				new BasicArrowButton(SwingConstants.SOUTH_EAST)
		};

		JButton jumpButton = new BasicArrowButton(SwingConstants.NORTH_EAST);
		jumpButton.setEnabled(false);

		final JCheckBox lockPos = new JCheckBox(Messages.getString("Main.lockPlayerPosition")); //$NON-NLS-1$
		final JSpinner speed = new JSpinner(new SpinnerNumberModel(0.3, 0.1, 1, 0.1));
		final JSpinner blocks = new JSpinner(new SpinnerNumberModel(1, 0, 1000, 0.1));

		SwingUtils.alignSpinner(speed);
		SwingUtils.alignSpinner(blocks);

		Box speedBox = Box.createHorizontalBox();
		Box blocksBox = Box.createHorizontalBox();

		speedBox.add(new JLabel(Messages.getString("Main.movementSpeed"))); //$NON-NLS-1$
		blocksBox.add(new JLabel(Messages.getString("Main.distanceToWalk"))); //$NON-NLS-1$
		speedBox.add(speed);
		blocksBox.add(blocks);

		for (final Component ct : speedBox.getComponents()) {
			SwingUtilities.invokeLater(new Runnable() {

				@Override
				public void run() {
					ct.setMaximumSize(new Dimension(ct.getWidth(), 20));
				}
			});
		}
		for (final Component ct : blocksBox.getComponents()) {
			SwingUtilities.invokeLater(new Runnable() {

				@Override
				public void run() {
					ct.setMaximumSize(new Dimension(ct.getWidth(), 20));
				}
			});
		}

		for (int x = 0; x < movementButtons.length; x++) {
			final int direction = x;
			movementButtons[x].addActionListener(new ActionListener() {
				final int directionL = direction;

				@Override
				public void actionPerformed(ActionEvent arg0) {
					try {
						MinecraftClient cl = clients.get(fPane);
						cl.move(directionL, (double) speed.getValue(), (double) blocks.getValue(),
								lockPos.isSelected());
					} catch (Exception e1) {
						e1.printStackTrace();
					}
				}
			});
		}

		JPanel movementPanel = new JPanel(new GridLayout(3, 3));
		movementPanel.add(movementButtons[1]);
		movementPanel.add(movementButtons[0]);
		movementPanel.add(movementButtons[7]);
		movementPanel.add(movementButtons[2]);
		movementPanel.add(jumpButton);
		movementPanel.add(movementButtons[6]);
		movementPanel.add(movementButtons[3]);
		movementPanel.add(movementButtons[4]);
		movementPanel.add(movementButtons[5]);

		movementPanel.setMaximumSize(new Dimension(180, 180));

		final JLabel xLabel = new JLabel("X: 0"); //$NON-NLS-1$
		final JLabel yLabel = new JLabel("Y: 0"); //$NON-NLS-1$
		final JLabel zLabel = new JLabel("Z: 0"); //$NON-NLS-1$

		playerBox.add(toggleSneak);
		playerBox.add(toggleSprint);
		playerBox.add(healthBar);
		playerBox.add(foodBar);
		playerBox.add(new JLabel(" ")); //$NON-NLS-1$
		playerBox.add(new JLabel(Messages.getString("Main.playerPosition"))); //$NON-NLS-1$
		playerBox.add(xLabel);
		playerBox.add(yLabel);
		playerBox.add(zLabel);
		playerBox.add(new JLabel(" ")); //$NON-NLS-1$
		playerBox.add(new JLabel(Messages.getString("Main.playerMovement"))); //$NON-NLS-1$
		playerBox.add(lockPos);
		playerBox.add(new JLabel(" ")); //$NON-NLS-1$
		playerBox.add(speedBox);
		playerBox.add(blocksBox);
		playerBox.add(new JLabel(" ")); //$NON-NLS-1$
		playerBox.add(movementPanel);
		playerBox.alignAll();

		Box playerListBox = Box.createVerticalBox();

		JPlaceholderField filterField = new JMinecraftField(Messages.getString("Main.playerNamesFilter")); //$NON-NLS-1$
		filterField.setMaximumSize(new Dimension(SwingUtils.sSize.width, 0));

		final JMinecraftPlayerList playerList = new JMinecraftPlayerList(filterField, win, entry.getHost());
		JScrollPane playerListPane = new JScrollPane(playerList);

		playerList.addMouseListener(new MouseAdapter() {

			@Override
			public void mouseClicked(MouseEvent e) {
				if (e.getButton() == MouseEvent.BUTTON1 && e.getClickCount() == 2) {
					JMinecraftPlayerList pl = (JMinecraftPlayerList) e.getSource();
					PlayerInfo pInfo = pl.getSelectedValue();
					if (pInfo != null && chatInput.isEnabled()) {
						String uName = pInfo.getName();
						String ct = chatInput.getText();
						boolean prependSpace = !(ct.isEmpty() || (ct.substring(ct.length() - 1).equals(" "))); //$NON-NLS-1$
						if (prependSpace)
							ct += " "; //$NON-NLS-1$
						chatInput.setText(ct + uName + " "); //$NON-NLS-1$
						chatInput.requestFocus();
					}
				}
			}
		});

		filterField.addKeyListener(new KeyAdapter() {
			@Override
			public void keyPressed(KeyEvent e) {
				playerListPane.getVerticalScrollBar().setValue(0);
				playerList.setListData(playerList.getListData());
				SwingUtilities.invokeLater(() -> {
					playerList.repaint();
				});
			}
		});

		playerListBox.add(filterField);
		playerListBox.add(playerListPane);

		JVBoxPanel statisticsBox = new JVBoxPanel();
		JScrollPane statisticsPane = new JScrollPane(statisticsBox);

		JButton refreshStats = new JButton(Messages.getString("Main.refreshStatsButton")); //$NON-NLS-1$
		refreshStats.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				MinecraftClient cl = clients.get(fPane);
				if (cl != null)
					try {
						cl.refreshStatistics();
					} catch (IOException e1) {
						e1.printStackTrace();
					}
			}
		});

		statisticsBox.add(refreshStats);
		statisticsBox.add(statisticsContainer);
		statisticsBox.alignAll();

		JVBoxPanel inventoryBox = new JVBoxPanel();

		final JButton showInventory = new JButton(Messages.getString("Main.showInventoryButton")); //$NON-NLS-1$
		showInventory.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				if (!up.isEnableInventoryHandling())
					return;

				MinecraftClient cl = clients.get(fPane);
				cl.getInventory().openWindow(win, up.isSendWindowClosePackets());
			}
		});

		inventoryBox.add(showInventory);
		inventoryBox.alignAll();

		controlsTabPane.addTab(Messages.getString("Main.playerListTab"), playerListBox); //$NON-NLS-1$
		controlsTabPane.addTab(Messages.getString("Main.playerTab"), new JScrollPane(playerBox)); //$NON-NLS-1$
		controlsTabPane.addTab(Messages.getString("Main.statisticsTab"), statisticsPane); //$NON-NLS-1$
		controlsTabPane.addTab(Messages.getString("Main.inventoryTab"), inventoryBox); //$NON-NLS-1$

		fPane.add(box);
		fPane.add(controlsTabPane);
		SwingUtilities.invokeLater(new Runnable() {
			@Override
			public void run() {
				fPane.setDividerLocation((double) 0.8);
			}
		});

		box.add(hjsc);
		box.add(jsc);
		box.add(chatControls);
		new Thread(new Runnable() {
			@Override
			public void run() {

				final String host = entry.getHost();
				final int port = entry.getPort();
				int protocol = -1;
				switch (entry.getVersion()) {
					case "Auto": { //$NON-NLS-1$
						try {
							protocol = MinecraftStat.serverListPing(host, port).getProtocol();
							boolean contains = false;
							for (ProtocolNumber num : ProtocolNumber.values()) {
								if (num.protocol == protocol)
									contains = true;
							}
							if (!contains)
								protocol = -2;
						} catch (Exception e) {
							SwingUtils.appendColoredText(
									Messages.getString("Main.connectionFailedChatMessage") + e.toString(), pane); //$NON-NLS-1$
							e.printStackTrace();
						}
						break;
					}
					case "Always Ask": { //$NON-NLS-1$
						protocol = -2;
						break;
					}
					default: {
						protocol = ProtocolNumber.getForName(entry.getVersion()).protocol;
						break;
					}
				}

				if (protocol == -2) {

					Box bb = Box.createVerticalBox();

					JComboBox<String> pcBox = new JComboBox<>();
					pcBox.setAlignmentX(Component.LEFT_ALIGNMENT);
					for (ProtocolNumber num : ProtocolNumber.values())
						pcBox.addItem(num.name);

					bb.add(new JLabel(Messages.getString("Main.chooseMinecraftVersionLabel"))); //$NON-NLS-1$
					bb.add(pcBox);

					JOptionPane.showOptionDialog(win, bb, Messages.getString("Main.chooseMinecraftVersionTitle"), //$NON-NLS-1$
							JOptionPane.DEFAULT_OPTION, JOptionPane.QUESTION_MESSAGE, null,
							new String[] { Messages.getString("Main.chooseMinecraftVersionOptionOl") //$NON-NLS-1$
					}, null);

					protocol = ProtocolNumber.getForName((String) pcBox.getSelectedItem()).protocol;
				}

				if (protocol != -1) {
					final int iprotocol = protocol;
					try {
						if (iprotocol >= 393)
							controlsTabPane.setEnabledAt(2, false);
						final MinecraftClient cl = new MinecraftClient(host, port, iprotocol);
						clients.put(fPane, cl);
						cl.getPlayersTabList().addChangeListener(new MapChangeListener<UUID, PlayerInfo>() {

							@Override
							public void itemRemoved(Object key, PlayerInfo value, HashMap<UUID, PlayerInfo> map) {
								List<PlayerInfo> pl = new ArrayList<PlayerInfo>();
								for (UUID ukey : map.keySet()) {
									pl.add(map.get(ukey));
								}
								if (pl.size() <= 0)
									return;
								PlayerInfo[] infs = new PlayerInfo[pl.size()];
								infs = pl.toArray(infs);
								playerList.setListData(infs);
							}

							@Override
							public void itemAdded(UUID key, PlayerInfo value, HashMap<UUID, PlayerInfo> map) {
								List<PlayerInfo> pl = new ArrayList<PlayerInfo>();
								for (UUID ukey : map.keySet()) {
									pl.add(map.get(ukey));
								}
								if (pl.size() <= 0)
									return;
								PlayerInfo[] infs = new PlayerInfo[pl.size()];
								infs = pl.toArray(infs);
								playerList.setListData(infs);
								SwingUtilities.invokeLater(() -> {
									playerList.repaint();
								});
							}
						});
						cl.addClientListener(new ClientListener() {

							final JTextPane jtp = pane;
							final JTextPane hjtp = hotbar;

							@Override
							public void messageReceived(final String message, Position pos) {
								if (pos == Position.HOTBAR) {
									hjtp.setText(""); //$NON-NLS-1$
									SwingUtils.appendColoredText(message, hjtp);
								} else {
									if (trayIcon != null) {
										boolean shouldDisplay = (up.getTrayMessageMode()
												.equals(Constants.TRAY_MESSAGES_KEY_ALWAYS))
												|| (up.getTrayMessageMode()
														.contentEquals(Constants.TRAY_MESSAGES_KEY_MENTION)
														&& message.toLowerCase()
																.contains(cl.getUsername().toLowerCase()));
										if (shouldDisplay) {
											trayLastMessageType = 0;
											trayLastMessageSender = cl;
											String ttext = ChatMessage.removeColors(message);
											trayIcon.displayMessage(
													cl.getHost() + ":" + cl.getPort() + " (" + cl.getUsername() + ")", //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
													ttext, MessageType.NONE);
										}
									}

									SwingUtils.appendColoredText(message + "\r\n", jtp); //$NON-NLS-1$
									try {
										Thread.sleep(10);
									} catch (InterruptedException e) {
										e.printStackTrace();
									}
									SwingUtilities.invokeLater(() -> {
										jsc.repaint();
									});

									if (!jsc.getVerticalScrollBar().getValueIsAdjusting())
										jsc.getVerticalScrollBar()
												.setValue(jsc.getVerticalScrollBar().getMaximum() * 2);
								}
							}

							@Override
							public void disconnected(String reason) {
								SwingUtils.appendColoredText(
										Messages.getString("Main.connectionLostChatMessage") + reason + "\r\n", jtp); //$NON-NLS-1$ //$NON-NLS-2$

								if (trayIcon != null && up.isTrayShowDisconnectMessages()) {
									trayLastMessageType = 1;
									String ttext = Messages.getString("Main.connectionLostTrayMessage") //$NON-NLS-1$
											+ ChatMessage.removeColors(reason);
									trayIcon.displayMessage(
											cl.getHost() + ":" + cl.getPort() + " (" + cl.getUsername() + ")", ttext, //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
											MessageType.ERROR);
									PopupMenu pm = trayIcon.getPopupMenu();
									for (int x = 0; x < pm.getItemCount(); x++) {
										MenuComponent ct = pm.getItem(x);
										if (ct instanceof Menu) {
											Menu cm = (Menu) ct;
											String lbl = cm.getLabel();
											if (lbl.equals(cl.getHost() + ":" + cl.getPort())) { //$NON-NLS-1$
												for (int y = 0; y < cm.getItemCount(); x++) {
													Menu pmenu = (Menu) cm.getItem(y);
													if (pmenu.getLabel().equals(cl.getUsername()))
														cm.remove(y);
												}
											}
											if (cm.getItemCount() == 0)
												pm.remove(cm);
										}
									}
								}

								showInventory.setEnabled(false);
								for (Component ct : chatControls.getComponents())
									ct.setEnabled(false);
								for (int x = 0; x < tabPane.getTabCount(); x++) {
									Component ct = tabPane.getTabComponentAt(x);
									if (ct == null)
										continue;
									if (ct.getName().equals(entry.getHost() + "_" + entry.getName() + "_" + username)) { //$NON-NLS-1$ //$NON-NLS-2$
										if (ct instanceof Box) {
											Box ctb = (Box) ct;
											for (Component ctt : ctb.getComponents())
												if (ctt instanceof JLabel)
													ctt.setForeground(Color.gray);
										}
									}
								}
								cl.close();
								clients.remove(fPane);
							}

							@Override
							public void healthUpdate(float health, int food) {
								if (health > healthBar.getMaximum())
									healthBar.setMaximum((int) health);
								if (food > foodBar.getMaximum())
									foodBar.setMaximum(food);

								healthBar.setValue((int) health);
								foodBar.setValue(food);

								healthBar.setString(
										Messages.getString("Main.healthBarText") + Integer.toString((int) health) + "/" //$NON-NLS-1$ //$NON-NLS-2$
												+ Integer.toString(healthBar.getMaximum()) + ")"); //$NON-NLS-1$
								foodBar.setString(Messages.getString("Main.foodBarText") + Integer.toString(food) + "/" //$NON-NLS-1$ //$NON-NLS-2$
										+ Integer.toString(foodBar.getMaximum()) + ")"); //$NON-NLS-1$
							}

							@Override
							public void positionChanged(double x, double y, double z) {
								String sx = Double.toString(x);
								String sy = Double.toString(y);
								String sz = Double.toString(z);
								if (sx.contains(".")) //$NON-NLS-1$
									sx = sx.substring(0, sx.lastIndexOf(".") + 2); //$NON-NLS-1$
								if (sy.contains(".")) //$NON-NLS-1$
									sy = sy.substring(0, sy.lastIndexOf(".") + 2); //$NON-NLS-1$
								if (sz.contains(".")) //$NON-NLS-1$
									sz = sz.substring(0, sz.lastIndexOf(".") + 2); //$NON-NLS-1$

								xLabel.setText("X: " + sx); //$NON-NLS-1$
								yLabel.setText("Y: " + sy); //$NON-NLS-1$
								zLabel.setText("Z: " + sz); //$NON-NLS-1$
							}

							Map<String, Integer> trueValues = new HashMap<String, Integer>();

							@Override
							public void statisticsReceived(Map<String, Integer> values) {
								if (values.size() == 0)
									return;
								statisticsContainer.removeAll();
								for (String key : values.keySet()) {
									String tkey = TranslationUtils.translateKey(key);
									if (tkey == key)
										continue;
									trueValues.put(tkey, values.get(key));
								}

								statisticsContainer.setLayout(new GridLayout(trueValues.size(), 2));
								for (String key : trueValues.keySet()) {
									statisticsContainer.add(new JLabel(key));
									statisticsContainer.add(new JLabel("   " + Integer.toString(trueValues.get(key)))); //$NON-NLS-1$
								}
								statisticsContainer.revalidate();
								statisticsContainer.repaint();
							}

							@Override
							public void windowOpened(int id, ItemsWindow win, PacketRegistry reg) {
								if (up.isHideIncomingWindows()) {
									if (up.isHiddenWindowsResponse())
										try {
											cl.sendPacket(
													PacketFactory.constructPacket(reg, "ClientCloseWindowPacket", id)); //$NON-NLS-1$
										} catch (Exception e) {
											e.printStackTrace();
										}
									return;
								}
								if (!up.isShowWindowsInTray() && trayIcon != null)
									return;
								win.openWindow(Main.this.win, up.isSendWindowClosePackets());
							}

						});
						cl.connect(username);

						SwingUtilities.invokeLater(new Runnable() {

							@Override
							public void run() {
								fPane.setDividerLocation((double) 0.8);
							}
						});

						playerList.setMcl(cl);
						PlayerSkinCache.getSkincache().clear();

						chatInput.addKeyListener(new KeyAdapter() {

							@Override
							public void keyPressed(KeyEvent e) {

								if (e.getKeyCode() == KeyEvent.VK_ENTER) {
									chatSend.requestFocusInWindow();
									chatSend.doClick();
									chatInput.requestFocusInWindow();
								}
							}
						});

						chatSend.addActionListener(new ActionListener() {

							@Override
							public void actionPerformed(ActionEvent e) {
								String message = chatInput.getText();
								if (!message.isEmpty()) {
									try {
										cl.sendChatMessage(message);
									} catch (IOException e1) {
										SwingUtils.appendColoredText(
												Messages.getString("Main.connectionLostChatMessage2") + e1.toString(), //$NON-NLS-1$
												pane);
										e1.printStackTrace();
										for (Component ct : chatControls.getComponents())
											ct.setEnabled(false);
									}
									chatInput.setText(""); //$NON-NLS-1$
								}
							}
						});

						for (Component ct : chatControls.getComponents())
							ct.setEnabled(true);

					} catch (

					IOException e) {
						SwingUtils.appendColoredText(
								Messages.getString("Main.connectionFailedChatMessage2") + e.toString(), //$NON-NLS-1$
								pane);
					}
				}
			}
		}).start();

		return fPane;
	}
}
