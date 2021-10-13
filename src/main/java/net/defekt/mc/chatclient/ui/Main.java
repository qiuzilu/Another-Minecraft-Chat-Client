package net.defekt.mc.chatclient.ui;

import java.awt.AWTException;
import java.awt.Color;
import java.awt.Component;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Graphics;
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
import javax.swing.ButtonGroup;
import javax.swing.DefaultListCellRenderer;
import javax.swing.ImageIcon;
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
import javax.swing.JRadioButton;
import javax.swing.JScrollPane;
import javax.swing.JSeparator;
import javax.swing.JSpinner;
import javax.swing.JSplitPane;
import javax.swing.JTabbedPane;
import javax.swing.JTextField;
import javax.swing.JTextPane;
import javax.swing.ListSelectionModel;
import javax.swing.ScrollPaneConstants;
import javax.swing.SpinnerNumberModel;
import javax.swing.SwingConstants;
import javax.swing.SwingUtilities;
import javax.swing.WindowConstants;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import javax.swing.plaf.basic.BasicArrowButton;

import net.defekt.mc.chatclient.protocol.ClientListener;
import net.defekt.mc.chatclient.protocol.LANListener;
import net.defekt.mc.chatclient.protocol.MinecraftClient;
import net.defekt.mc.chatclient.protocol.MinecraftStat;
import net.defekt.mc.chatclient.protocol.ProtocolNumber;
import net.defekt.mc.chatclient.protocol.data.ChatMessages;
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
import net.defekt.mc.chatclient.ui.swing.JMemList;
import net.defekt.mc.chatclient.ui.swing.JMinecraftButton;
import net.defekt.mc.chatclient.ui.swing.JMinecraftField;
import net.defekt.mc.chatclient.ui.swing.JMinecraftPlayerList;
import net.defekt.mc.chatclient.ui.swing.JMinecraftServerList;
import net.defekt.mc.chatclient.ui.swing.JPlaceholderField;
import net.defekt.mc.chatclient.ui.swing.JVBoxPanel;
import net.defekt.mc.chatclient.ui.swing.SwingUtils;

@SuppressWarnings({ "serial", "javadoc" })
public class Main {

	private Main() {

	}

	private static BufferedImage logoImage = null;

	public static final String version = "1.4.1";
	private static final String changelogURL = "https://raw.githubusercontent.com/Defective4/Another-Minecraft-Chat-Client/master/Changes";

	public static Font mcFont = Font.decode(null);

	private static void checkForUpdates() {
		try (BufferedReader br = new BufferedReader(new InputStreamReader(new URL(changelogURL).openStream()))) {
			List<String> cgLines = new ArrayList<String>();
			String line;
			while ((line = br.readLine()) != null)
				cgLines.add(line);

			if (cgLines.size() > 1 && cgLines.get(0).equals("AMCC Change Log")) {
				String newVersionString = IOUtils.padString(cgLines.get(1).substring(1).replace(".", ""), 3, "0", 0);
				String thisVersionString = IOUtils.padString(version.replace(".", ""), 3, "0", 0);

				int newVersion = Integer.parseInt(newVersionString);
				int thisVersion = Integer.parseInt(thisVersionString);

				if (newVersion > thisVersion) {
					String newVersionSm = cgLines.get(1).substring(1);
					String oldVersionSm = version;

					if (newVersionSm.length() - newVersionSm.replace(".", "").length() < 2)
						newVersionSm += ".0";
					if (oldVersionSm.length() - oldVersionSm.replace(".", "").length() < 2)
						oldVersionSm += ".0";

					int nMajor = Integer.parseInt(newVersionSm.substring(0, newVersionSm.indexOf(".")));
					int nMinor = Integer.parseInt(
							newVersionSm.substring(newVersionSm.indexOf(".") + 1, newVersionSm.lastIndexOf(".")));
					int nFix = Integer.parseInt(newVersionSm.substring(newVersionSm.lastIndexOf(".") + 1));

					int oMajor = Integer.parseInt(oldVersionSm.substring(0, oldVersionSm.indexOf(".")));
					int oMinor = Integer.parseInt(
							oldVersionSm.substring(oldVersionSm.indexOf(".") + 1, oldVersionSm.lastIndexOf(".")));
					int oFix = Integer.parseInt(oldVersionSm.substring(oldVersionSm.lastIndexOf(".") + 1));

					int diff = 0;
					String vtype = "";

					if (oFix != nFix) {
						diff = nFix - oFix;
						vtype = "minor";
					}
					if (oMinor != nMinor) {
						diff = nMinor - oMinor;
						vtype = "major";
					}
					if (oMajor != nMajor) {
						diff = nMajor - oMajor;
						vtype = "major";
					}

					cgLines.remove(0);
					cgLines.remove(0);

					SwingUtils.showVersionDialog("v" + version, "v" + newVersionSm, diff, vtype, cgLines);
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

			final JFrame win = new JFrame("Choose your language");
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

			JOptionPane cp = new JOptionPane(new Object[] { "Choose your language", languages },
					JOptionPane.QUESTION_MESSAGE, JOptionPane.OK_OPTION, null, new Object[] { ct });

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
							Main.class.getResourceAsStream("/resources/Minecraftia-Regular.ttf"))
					.deriveFont((float) 14);
		} catch (Exception e) {
			e.printStackTrace();
		}

		try {
			logoImage = ImageIO.read(Main.class.getResourceAsStream("/resources/logo.png"));
		} catch (Exception e) {
			e.printStackTrace();
		}
		if (up.isEnableInventoryHandling() && up.isLoadInventoryTextures())
			ItemsWindow.initTextures(new Main(), true);
		new Main().init();
	}

	protected static final File serverFile = new File("mcc.prefs");
	public static final UserPreferences up = UserPreferences.load();
	private List<ServerEntry> servers = Collections.synchronizedList(new ArrayList<ServerEntry>());
	private final JMinecraftServerList serverListComponent = new JMinecraftServerList(this, true);
	private final JMinecraftServerList lanListComponent = new JMinecraftServerList(this, false);
	private final JTabbedPane tabPane = new JTabbedPane();
	private final Map<JSplitPane, MinecraftClient> clients = new HashMap<JSplitPane, MinecraftClient>();
	private final JFrame win = new JFrame();
	private TrayIcon trayIcon = null;

	private MinecraftClient trayLastMessageSender = null;
	private int trayLastMessageType = 0;

	private ServerEntry selectedServer = null;

	private ActionListener alis;

	public void moveServer(int index, int direction) {
		int targetIndex = -1;
		if (index > 0 && direction == 0)
			targetIndex = index - 1;
		else if (index < servers.size() - 1 && direction == 1)
			targetIndex = index + 1;

		if (targetIndex == -1)
			return;

		ServerEntry s1 = servers.get(index);
		ServerEntry s2 = servers.get(targetIndex);

		synchronized (servers) {
			servers.set(targetIndex, s1);
			servers.set(index, s2);
		}

		ServerEntry[] entries = new ServerEntry[servers.size()];
		entries = servers.toArray(entries);

		serverListComponent.setListData(entries);

		final int tgIndex = targetIndex;

		SwingUtilities.invokeLater(() -> {
			serverListComponent.setSelectedIndex(tgIndex);
		});
	}

	private void addToList(String host, int port, String name, String version) {
		ServerEntry entry = new ServerEntry(host, port, name, version);
		for (ServerEntry se : servers)
			if (se.equals(entry))
				return;
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
		JTextField mField = new JPlaceholderField(Messages.getString("Main.quickMessageDialog"));

		String label = cl.getHost() + ":" + cl.getPort();
		qmdShowing = true;
		int resp = JOptionPane.showOptionDialog(null,
				new Object[] { Messages.getString("Main.quickMessageRecipient") + label, mField },
				Messages.getString("Main.quickMesage"), JOptionPane.OK_CANCEL_OPTION, JOptionPane.QUESTION_MESSAGE,
				null, new Object[] { Messages.getString("Main.qmOkOption"), Messages.getString("Main.qmCancelOption") },
				0);
		qmdShowing = false;
		if (resp == 0) {
			String msg = mField.getText();
			if (msg.replace(" ", "").isEmpty())
				return;
			try {
				cl.sendChatMessage(msg);
			} catch (IOException e) {
				for (ClientListener ls : cl.getClientListeners())
					ls.disconnected(e.toString());
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
						sender.getHostAddress() + ":" + Integer.toString(port), Messages.getString("Main.Auto"));
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

		win.setDefaultCloseOperation(WindowConstants.DO_NOTHING_ON_CLOSE);

		win.addWindowListener(new WindowAdapter() {
			@Override
			public void windowClosing(WindowEvent e) {
				if (clients.size() > 0) {
					JDialog diag = new JDialog(win);
					diag.setModal(true);
					diag.setTitle(Messages.getString("Main.exitDialogTitle"));

					JButton ok = new JButton(Messages.getString("Main.exitOkOption"));
					JButton toTray = new JButton(Messages.getString("Main.exitMinimizeOption"));
					JButton cancel = new JButton(Messages.getString("Main.exitCancelOption"));
					toTray.setEnabled(SystemTray.isSupported());
					JCheckBox rememberOp = new JCheckBox(Messages.getString("Main.exitRememberChoice"));

					ok.addActionListener(ev -> {
						if (rememberOp.isSelected())
							up.setCloseMode(Constants.WINDOW_CLOSE_EXIT);
						System.exit(0);
					});

					cancel.addActionListener(ev -> {
						diag.dispose();
					});

					toTray.addActionListener(new ActionListener() {
						@Override
						public void actionPerformed(ActionEvent ev) {
							if (rememberOp.isSelected())
								up.setCloseMode(Constants.WINDOW_CLOSE_TO_TRAY);
							if (trayIcon != null)
								return;
							diag.dispose();
							SystemTray tray = SystemTray.getSystemTray();
							trayIcon = new TrayIcon(IOUtils.scaleImage(logoImage, 0.5),
									"Another Minecraft Chat Client");
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

								MenuItem quit = new MenuItem(Messages.getString("Main.trayQuitItem"));
								quit.addActionListener(ev2 -> {
									System.exit(0);
								});

								MenuItem open = new MenuItem(Messages.getString("Main.trayOpenGUIItem"));
								open.addActionListener(ev2 -> {
									ml.mouseClicked(new MouseEvent(win, 0, System.currentTimeMillis(), 0, 0, 0, 0, 0, 1,
											false, MouseEvent.BUTTON1));
								});
								open.setFont(win.getFont().deriveFont(Font.BOLD));

								Map<String, List<MinecraftClient>> labels = new HashMap<>();

								for (MinecraftClient cl : clients.values()) {
									String srvLabel = cl.getHost() + ":" + cl.getPort();
									if (!labels.containsKey(srvLabel))
										labels.put(srvLabel, new ArrayList<>());
									labels.get(srvLabel).add(cl);
								}

								MenuItem options = new MenuItem(Messages.getString("Main.optionsMenu") + "...");
								options.addActionListener(ev2 -> {
									showOptionsDialog();
								});

								menu.add(open);
								menu.addSeparator();
								for (String label : labels.keySet()) {
									Menu srvMenu = new Menu(label);
									for (final MinecraftClient cl : labels.get(label)) {
										Menu pMenu = new Menu(cl.getUsername()) {
											{
												final MinecraftClient client = cl;
												MenuItem dcItem = new MenuItem(
														Messages.getString("Main.trayDisconnectItem"));
												MenuItem qmItem = new MenuItem(
														Messages.getString("Main.trayQuickMessageItem"));
												MenuItem invItem = new MenuItem(
														Messages.getString("Main.showInventoryButton"));
												final Menu ins = this;

												dcItem.addActionListener(new ActionListener() {

													@Override
													public void actionPerformed(ActionEvent e) {
														client.close();
														srvMenu.remove(ins);
														if (srvMenu.getItemCount() == 0)
															menu.remove(srvMenu);
														for (ClientListener ls : client.getClientListeners())
															ls.disconnected(
																	Messages.getString("Main.trayClosedReason"));
													}
												});

												qmItem.addActionListener(ev -> {
													showQuickMessageDialog(client);
												});

												invItem.addActionListener(ev -> {
													if (!up.isEnableInventoryHandling())
														return;
													client.getInventory().openWindow(win,
															up.isSendWindowClosePackets());
												});

												add(qmItem);
												add(dcItem);
												add(invItem);
											}
										};

										srvMenu.add(pMenu);
									}
									menu.add(srvMenu);
								}

								menu.addSeparator();
								menu.add(options);
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

					JOptionPane op = new JOptionPane(new Object[] { Messages.getString("Main.trayExitQuestion")
							+ Messages.getString("Main.trayExitQuestionLine2") + Integer.toString(clients.size())
							+ Messages.getString("Main.trayExitQuestionLine2Append"), rememberOp },
							JOptionPane.QUESTION_MESSAGE, JOptionPane.OK_CANCEL_OPTION, null,
							new JButton[] { ok, toTray, cancel });

					diag.setContentPane(op);
					diag.pack();
					SwingUtils.centerWindow(diag);
					diag.setVisible(true);
				} else
					System.exit(0);
			}
		});

		win.setTitle("Another Minecraft Chat Client v" + version);
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

		serverListPane.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);

		serverListBox.add(serverListPane);
		serverListComponent.setMinimumSize(serverListBox.getPreferredSize());

		Box controlsBox = Box.createHorizontalBox();

		JButton addServer = new JMinecraftButton(Messages.getString("Main.addServerOption"));
		addServer.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				JTextField nameField = new JPlaceholderField(Messages.getString("Main.serveNameField"));
				nameField.setText(Messages.getString("Main.defaultServerName"));
				JTextField hostField = new JPlaceholderField(Messages.getString("Main.serverAddressField"));

				JComboBox<String> versionField = new JComboBox<>();
				versionField.addItem("Auto");
				versionField.addItem("Always Ask");
				for (ProtocolNumber num : ProtocolNumber.values())
					versionField.addItem(num.name);

				final Box contents = Box.createVerticalBox();

				final JLabel errorLabel = new JLabel("");
				errorLabel.setForeground(Color.red);

				contents.add(errorLabel);
				contents.add(new JLabel(Messages.getString("Main.basicServerInfoLabel")));
				contents.add(new JLabel(" "));
				contents.add(nameField);
				contents.add(hostField);
				contents.add(versionField);

				for (Component c : contents.getComponents())
					if (c instanceof JComponent)
						((JComponent) c).setAlignmentX(Component.LEFT_ALIGNMENT);

				do
					try {
						int response = JOptionPane.showOptionDialog(win, contents,
								Messages.getString("Main.addServerDialogTitle"), JOptionPane.OK_CANCEL_OPTION,
								JOptionPane.QUESTION_MESSAGE, null, null, null);

						if (response == JOptionPane.OK_OPTION) {
							String server = hostField.getText();
							String name = nameField.getText();

							if (server.isEmpty() || name.isEmpty()) {
								errorLabel.setText(Messages.getString("Main.addServerDialogEmptyFieldsWarning"));
								continue;
							}

							String host = server;
							int port = 25565;
							if (server.contains(":") && server.split(":").length > 1) {
								String[] ag = server.split(":");
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
				while (true);
			}
		});

		final JButton refresh = new JMinecraftButton(Messages.getString("Main.refreshOption"));
		refresh.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				if (serverListComponent.getListData() != null)
					serverListComponent.setListData(serverListComponent.getListData());
				if (servers != null)
					for (ServerEntry entry : servers)
						try {
							if (!entry.refreshing)
								entry.ping();
						} catch (Exception e2) {
							e2.printStackTrace();
						}
			}
		});

		final JButton removeServer = new JMinecraftButton(Messages.getString("Main.removeServerOption"));
		removeServer.addActionListener(ev -> {
			if (serverListComponent.getSelectedValue() != null) {
				removeFromList(serverListComponent.getSelectedValue());
				refresh.doClick();
			}
		});
		removeServer.setEnabled(false);

		final JButton connectServer = new JMinecraftButton(Messages.getString("Main.connectServerOption"));
		alis = new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				ServerEntry et = sTypesPane.getSelectedIndex() == 0 ? serverListComponent.getSelectedValue()
						: lanListComponent.getSelectedValue();
				if (et == null)
					return;

				if (et.isRefreshing() || et.isError()) {
					String haltReason = et.isRefreshing() ? Messages.getString("Main.haltReasonRefreshing")
							: Messages.getString("Main.haltReasonError");

					int haltResponse = JOptionPane.showOptionDialog(win,
							new String[] { haltReason, Messages.getString("Main.haltQuestion") },
							Messages.getString("Main.haltTitle"), JOptionPane.DEFAULT_OPTION,
							JOptionPane.WARNING_MESSAGE, null,
							new String[] { Messages.getString("Main.haltResponseCancel"),
									Messages.getString("Main.haltResponseJoin") },
							-1);

					switch (haltResponse) {
						case 1: {
							break;
						}

						case 0: {
							return;
						}

						default: {
							return;
						}
					}

				}

				JVBoxPanel box = new JVBoxPanel();
				box.add(new JLabel(Messages.getString("Main.enterUsernameLabel")));

				Box uCtl = Box.createHorizontalBox();

				BufferedImage x = null;
				try {
					x = ImageIO.read(getClass().getResourceAsStream("/resources/x.png"));
					x = IOUtils.resizeImageProp(x, 12);
				} catch (Exception e2) {
					e2.printStackTrace();
				}
				JButton unameClear = x == null ? new JButton("C") : new JButton(new ImageIcon(x));
				unameClear.setToolTipText(Messages.getString("Main.clearUnames"));

				JComboBox<String> unameField = new JComboBox<>();
				unameClear.addActionListener(new ActionListener() {

					@Override
					public void actionPerformed(ActionEvent e) {
						up.clearLastUserNames();
						unameField.removeAllItems();
					}
				});
				unameField.setEditable(true);
				unameField.setAlignmentX(Component.LEFT_ALIGNMENT);
				for (String uname : up.getLastUserNames())
					unameField.addItem(uname);

				uCtl.add(unameField);
				uCtl.add(unameClear);

				box.add(uCtl);
				box.alignAll();

				do {
					int response = JOptionPane.showOptionDialog(win, box, Messages.getString("Main.enterUsernameTitle"),
							JOptionPane.OK_CANCEL_OPTION, JOptionPane.QUESTION_MESSAGE, null, null, null);
					if (response != JOptionPane.OK_OPTION)
						return;

					String uname = (String) unameField.getSelectedItem();
					if (uname == null)
						continue;
					if (!up.isUsernameAlertSeen() && !uname.replaceAll("[^a-zA-Z0-9]", "").equals(uname)) {
						int alResp = JOptionPane.showOptionDialog(win,
								Messages.getString("Main.nickIllegalCharsWarning1") + uname
										+ Messages.getString("Main.nickIllegalCharsWarning2")
										+ Messages.getString("Main.nickIllegalCharsWarningQuestion"),
								Messages.getString("Main.nickIllegalCharsWarningTitle"), JOptionPane.YES_NO_OPTION,
								JOptionPane.WARNING_MESSAGE, null,
								new Object[] { Messages.getString("Main.nickIllegalCharsWarningOptionYes"),
										Messages.getString("Main.nickIllegalCharsWarningOptionNo") },
								0);
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

				tabPane.addTab("", b);
				tabPane.setSelectedComponent(b);

				Box b2 = Box.createHorizontalBox();
				b2.setName(et.getHost() + "_" + et.getName() + "_" + uname);
				int pxh = et.getIcon() == null ? 0 : 16;

				BufferedImage bicon = null;
				if (et.getIcon() != null)
					try {
						bicon = ImageIO
								.read(new ByteArrayInputStream(Base64.getDecoder().decode(et.getIcon().getBytes())));
					} catch (IOException e1) {
						e1.printStackTrace();
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

				b2.add(new JLabel(" " + et.getName() + " (" + (String) unameField.getSelectedItem() + ")"));

				JButton close = new JButton("x");
				close.setMargin(new Insets(0, 5, 0, 5));
				close.addActionListener(new ActionListener() {
					private final JSplitPane box = b;

					@Override
					public void actionPerformed(ActionEvent e) {
						for (int x = 0; x < tabPane.getTabCount(); x++)
							if (tabPane.getComponentAt(x).equals(box)) {
								if (clients.containsKey(box))
									clients.get(box).close();
								tabPane.removeTabAt(x);
								clients.remove(b);
								break;
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
				if (e.getButton() == MouseEvent.BUTTON1 && e.getClickCount() >= 2 && connectServer.isEnabled())
					connectServer.doClick();
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

		lanListPane.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);

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
					if (selectedServer != null)
						lanConnect.setEnabled(true);
					else
						lanConnect.setEnabled(false);

				}
			}
		});

		lanListBox.add(lanListPane);
		lanListBox.add(lanControlsBox);
		lanListComponent.setMinimumSize(lanListBox.getPreferredSize());

		sTypesPane.addTab(Messages.getString("Main.serversTabInternet"), serverListBox);
		sTypesPane.addTab(Messages.getString("Main.serversTabLAN"), lanListBox);

		tabPane.addTab(Messages.getString("Main.serversListTab"), sTypesPane);

		JMenu fileMenu = new JMenu(Messages.getString("Main.fileMenu")) {
			{
				setMnemonic(getText().charAt(0));
				add(new JMenuItem(Messages.getString("Main.fileMenuQuit")) {
					{
						addActionListener(ev -> {
							System.exit(0);
						});
					}
				});
			}
		};
		JMenu optionMenu = new JMenu(Messages.getString("Main.optionsMenu")) {
			{
				setMnemonic(getText().charAt(0));
				add(new JMenuItem(Messages.getString("Main.optionsMenuSettings")) {
					{
						addActionListener(e -> {
							showOptionsDialog();
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

	private void showOptionsDialog() {
		JDialog od = new JDialog(win);
		od.setModal(true);
		od.setResizable(false);
		od.setTitle(Messages.getString("Main.settingsTitle"));

		Box b = Box.createVerticalBox();

		JTabbedPane jtp = new JTabbedPane();

		JVBoxPanel rsBox = new JVBoxPanel();

		JComboBox<Status> rPackBehaviorBox = new JComboBox<>(Status.values());
		rPackBehaviorBox.setToolTipText(Messages.getString("Main.rsBehaviorToolTip"));
		rPackBehaviorBox.setSelectedItem(up.getResourcePackBehavior());
		rPackBehaviorBox.setRenderer(new DefaultListCellRenderer() {
			@Override
			public Component getListCellRendererComponent(JList<? extends Object> list, Object value, int index,
					boolean isSelected, boolean cellHasFocus) {
				JLabel lbl = new JLabel();
				String txt;
				switch ((Status) value) {
					case ACCEPTED: {
						txt = Messages.getString("Main.rsBehaviorAccept");
						break;
					}
					case DECLINED: {
						txt = Messages.getString("Main.rsBehaviorDecline");
						break;
					}
					case LOADED: {
						txt = Messages.getString("Main.rsBehaviorAcceptLoad");
						break;
					}
					default: {
						txt = Messages.getString("Main.rsBehaviorFail");
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

		JCheckBox rsPackShowCheck = new JCheckBox(Messages.getString("Main.rsPackShowCheck"),
				up.isShowResourcePackMessages());
		rsPackShowCheck.setToolTipText(Messages.getString("Main.rsPackShowToolTip"));

		JPlaceholderField rsPackMsgText = new JPlaceholderField(Messages.getString("Main.rsPackMessageField"));
		rsPackMsgText.setToolTipText(Messages.getString("Main.rsPackMessageToolTip"));
		rsPackMsgText.setText(up.getResourcePackMessage());

		JComboBox<Position> rsPackMessagePosition = new JComboBox<>(Position.values());
		rsPackMessagePosition.setSelectedItem(up.getResourcePackMessagePosition());

		rsBox.add(new JLabel(Messages.getString("Main.rsPackBehaviorLabel")));
		rsBox.add(rPackBehaviorBox);
		rsBox.add(new JLabel(" "));
		rsBox.add(rsPackShowCheck);
		rsBox.add(new JLabel(" "));
		rsBox.add(new JLabel(Messages.getString("Main.rsPackMessageLabel")));
		rsBox.add(rsPackMsgText);
		rsBox.add(new JLabel(" "));
		rsBox.add(new JLabel(Messages.getString("Main.rsPackPositionLabel")));
		rsBox.add(rsPackMessagePosition);
		rsBox.add(new JTextPane() {
			{
				setEditable(false);
				setOpaque(false);
			}
		});

		rsBox.alignAll();

		JVBoxPanel skBox = new JVBoxPanel();
		skBox.add(new JLabel(Messages.getString("Main.skinFetchMetchodLabel")));
		JComboBox<SkinRule> ruleBox = new JComboBox<>(SkinRule.values());
		ruleBox.setToolTipText(Messages.getString("Main.skinFetchToolTip"));
		ruleBox.setSelectedItem(up.getSkinFetchRule());
		skBox.add(ruleBox);
		skBox.add(new JTextPane() {
			{
				setText("\r\n" + Messages.getString("Main.skinFetchTipLine1")
						+ Messages.getString("Main.skinFetchTipLine2") + Messages.getString("Main.skinFetchTipLine3")
						+ Messages.getString("Main.skinFetchTipLine4"));
				setEditable(false);
			}
		});

		skBox.alignAll();

		JVBoxPanel pkBox = new JVBoxPanel();

		JCheckBox ignoreKAPackets = new JCheckBox(Messages.getString("Main.ignoreKAPackets"));
		ignoreKAPackets.setToolTipText(Messages.getString("Main.ignoreKAPacketsToolTop"));
		ignoreKAPackets.setSelected(up.isIgnoreKeepAlive());

		JTextField brandField = new JPlaceholderField(Messages.getString("Main.brandField"));
		brandField.setToolTipText(Messages.getString("Main.brandToolTop"));
		brandField.setText(up.getBrand());
		SwingUtilities.invokeLater(() -> {
			brandField.setOpaque(true);
		});

		JSpinner pingField = new JSpinner(new SpinnerNumberModel(0, 0, Integer.MAX_VALUE, 1));
		pingField.setToolTipText(Messages.getString("Main.pingToolTop"));
		pingField.setValue(up.getAdditionalPing());
		SwingUtils.alignSpinner(pingField);

		pkBox.add(ignoreKAPackets);
		pkBox.add(new JLabel(" "));
		pkBox.add(new JLabel(Messages.getString("Main.pingLabel")));
		pkBox.add(new JLabel(Messages.getString("Main.pingLabel2")));
		pkBox.add(pingField);
		pkBox.add(new JLabel(" "));
		pkBox.add(new JLabel(Messages.getString("Main.brandLabel")));
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
				new String[] { Constants.TRAY_MESSAGES_KEY_ALWAYS, Constants.TRAY_MESSAGES_KEY_MENTION,
						Constants.TRAY_MESSAGES_KEY_KEYWORD, Constants.TRAY_MESSAGES_KEY_NEVER });
		trMessagesMode.setToolTipText(Messages.getString("Main.trMessagesModeToolTop"));
		trMessagesMode.setSelectedItem(up.getTrayMessageMode());
		JCheckBox showDMessages = new JCheckBox(Messages.getString("Main.showDMessages"));
		showDMessages.setToolTipText(Messages.getString("Main.showDMessagesToolTop"));
		showDMessages.setSelected(up.isTrayShowDisconnectMessages());

		JMemList<String> trMessagesKeywords = new JMemList<>();
		trMessagesKeywords.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
		trMessagesKeywords.setListData(up.getTrayKeyWords() == null ? new String[0] : up.getTrayKeyWords());

		Box trKwControls = Box.createHorizontalBox();

		JButton addKeyword = new JButton(Messages.getString("Main.keywordAdd"));
		JButton removeKeyword = new JButton(Messages.getString("Main.keywordRemove"));

		removeKeyword.setEnabled(trMessagesKeywords.getSelectedIndex() != -1);

		trMessagesKeywords.addListSelectionListener(e -> {
			if (!e.getValueIsAdjusting())
				removeKeyword.setEnabled(e.getFirstIndex() != -1);
		});

		removeKeyword.addActionListener(e -> {
			if (trMessagesKeywords.getSelectedIndex() == -1)
				return;

			String selected = trMessagesKeywords.getSelectedValue();
			int index = trMessagesKeywords.getSelectedIndex();

			List<String> ld = new ArrayList<>();
			Collections.addAll(ld, trMessagesKeywords.getListData());

			ld.remove(selected);

			String[] ss = new String[ld.size()];
			ss = ld.toArray(ss);

			trMessagesKeywords.setListData(ss);

			trMessagesKeywords.setSelectedIndex(index > 0 ? index - 1 : 0);

			removeKeyword.setEnabled(ss.length > 0);
		});

		addKeyword.addActionListener(e -> {

			JTextField kwField = new JPlaceholderField(Messages.getString("Main.kewyordField"));

			int response = JOptionPane.showOptionDialog(od,
					new Object[] { Messages.getString("Main.keywordDialogLabel"), kwField },
					Messages.getString("Main.keywordDialogTitle"), JOptionPane.OK_CANCEL_OPTION,
					JOptionPane.QUESTION_MESSAGE, null,
					new String[] { Messages.getString("Main.qmOkOption"), Messages.getString("Main.qmCancelOption") },
					0);

			if (response == 0 && !kwField.getText().isEmpty()) {
				List<String> ld = new ArrayList<>();
				Collections.addAll(ld, trMessagesKeywords.getListData());
				if (ld.contains(kwField.getText()))
					return;
				ld.add(kwField.getText());

				String[] ss = new String[ld.size()];
				ss = ld.toArray(ss);

				trMessagesKeywords.setListData(ss);

				removeKeyword.setEnabled(ss.length > 0);
			}
		});

		trKwControls.add(addKeyword);
		trKwControls.add(removeKeyword);

		JScrollPane trKeywordsScroll = new JScrollPane(trMessagesKeywords);

		JButton clearRem = new JButton(Messages.getString("Main.clearRem"));
		if (up.getCloseMode() == Constants.WINDOW_CLOSE_ALWAYS_ASK)
			clearRem.setEnabled(false);

		clearRem.addActionListener(ev2 -> {
			up.setCloseMode(0);
			clearRem.setEnabled(false);
		});

		trBox.add(new JLabel(Messages.getString("Main.trMessagesModeLabel")));
		trBox.add(trMessagesMode);
		trBox.add(showDMessages);
		trBox.add(new JLabel(" "));
		trBox.add(clearRem);
		trBox.add(new JLabel(" "));
		trBox.add(new JLabel(Messages.getString("Main.keywordLabel")));
		trBox.add(trKeywordsScroll);
		trBox.add(trKwControls);
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

		JColorChooserButton apButtonEnabled = new JColorChooserButton(cp.getColorEnabledButton(), od);
		JColorChooserButton apButtonEnabledHover = new JColorChooserButton(cp.getColorEnabledHoverButton(), od);
		JColorChooserButton apButtonDisabled = new JColorChooserButton(cp.getColorDisabledButton(), od);
		JColorChooserButton apButtonText = new JColorChooserButton(cp.getColorText(), od);
		JColorChooserButton apButtonTextDisabled = new JColorChooserButton(cp.getDisabledColorText(), od);

		JCheckBox apButtonLockColors = new JCheckBox(Messages.getString("Main.apButtonLockColors"));
		apButtonLockColors.setSelected(true);
		JButton apButtonReset = new JButton(Messages.getString("Main.apButtonReset"));

		JMinecraftButton sampleButton = new JMinecraftButton("Test");
		JMinecraftButton sampleDisabledButton = new JMinecraftButton("Test");
		sampleButton.setCp(cprefCopy);
		sampleDisabledButton.setCp(cprefCopy);
		sampleDisabledButton.setEnabled(false);

		apButtonSettings.add(apButtonLockColors);
		apButtonSettings.add(new JLabel(Messages.getString("Main.apButtonSettingsBGLabel")));
		apButtonSettings.add(apButtonEnabled);
		apButtonSettings.add(new JLabel(Messages.getString("Main.apButtonSettingsHoverLabel")));
		apButtonSettings.add(apButtonEnabledHover);
		apButtonSettings.add(new JLabel(Messages.getString("Main.apButtonSettingsDisabledLabel")));
		apButtonSettings.add(apButtonDisabled);
		apButtonSettings.add(new JLabel(Messages.getString("Main.apButtonSettingsTextColor")));
		apButtonSettings.add(apButtonText);
		apButtonSettings.add(new JLabel(Messages.getString("Main.apButtonSettingsDTexTColor")));
		apButtonSettings.add(apButtonTextDisabled);
		apButtonSettings.add(new JLabel(" "));
		apButtonSettings.add(apButtonReset);
		apButtonSettings.add(new JLabel(" "));

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

			apButtonDisabled.setColor(new Color(Integer.parseInt(cp2.getColorDisabledButton(), 16)));
			apButtonEnabled.setColor(new Color(Integer.parseInt(cp2.getColorEnabledButton(), 16)));
			apButtonEnabledHover.setColor(new Color(Integer.parseInt(cp2.getColorEnabledHoverButton(), 16)));
			apButtonText.setColor(new Color(Integer.parseInt(cp2.getColorText(), 16)));
			apButtonTextDisabled.setColor(new Color(Integer.parseInt(cp2.getDisabledColorText(), 16)));
			sampleButton.repaint();
			sampleDisabledButton.repaint();
		});

		Box apButtonSettingsSamples = Box.createHorizontalBox();
		apButtonSettingsSamples.add(sampleButton);
		apButtonSettingsSamples.add(sampleDisabledButton);

		apButtonSettingsFull.add(apButtonSettingsSP);
		apButtonSettingsFull.add(apButtonSettingsSamples);

		apButtonSettings.alignAll();

		apPane.addTab(Messages.getString("Main.appearancePaneButtons"), apButtonSettingsFull);

		JVBoxPanel ivBox = new JVBoxPanel();

		final JCheckBox enableIVHandling = new JCheckBox(Messages.getString("Main.enableIVHandling"));
		final JCheckBox hideIncomingWindows = new JCheckBox(Messages.getString("Main.hideIncomingWindows"));
		final JCheckBox hiddenWindowsResponse = new JCheckBox(Messages.getString("Main.hiddenWindowsResponse"));
		final JCheckBox loadTextures = new JCheckBox(Messages.getString("Main.loadItemTextures"));
		final JCheckBox showWhenInTray = new JCheckBox(Messages.getString("Main.showWindowsInTray"));
		final JCheckBox sendClosePackets = new JCheckBox(Messages.getString("Main.sendClosePackets"));

		enableIVHandling.setToolTipText(Messages.getString("Main.enableIVHandlingToolTop"));
		loadTextures.setToolTipText(Messages.getString("Main.loadItemTexturesToolTop"));
		showWhenInTray.setToolTipText(Messages.getString("Main.showWindowsInTrayToolTop"));
		sendClosePackets.setToolTipText(Messages.getString("Main.sendClosePacketsToolTop"));
		hideIncomingWindows.setToolTipText(Messages.getString("Main.hideIncomingWindowsToolTop"));
		hiddenWindowsResponse.setToolTipText(Messages.getString("Main.hiddenWindowsResponseToolTop"));

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
				} else
					setEb(ct);
			}

			private void setEb(Component ct) {
				if ((ct instanceof JCheckBox) && !ct.equals(enableIVHandling)) {
					ct.setEnabled(enableIVHandling.isSelected());
					if (ct.equals(hiddenWindowsResponse))
						ct.setEnabled(hideIncomingWindows.isSelected() && hideIncomingWindows.isEnabled());
				}
			}
		});

		ivBox.add(new JPanel() {
			{
				setLayout(new BoxLayout(this, BoxLayout.X_AXIS));
				add(enableIVHandling);
				add(new JButton("?") {
					{
						addActionListener(new ActionListener() {

							@Override
							public void actionPerformed(ActionEvent e) {
								JOptionPane.showOptionDialog(od,
										Messages.getString("Main.inventoryHandlingHelpLine1")
												+ Messages.getString("Main.inventoryHandlingHelpLine2")
												+ Messages.getString("Main.inventoryHandlingHelpLine3")
												+ Messages.getString("Main.inventoryHandlingHelpLine4")
												+ Messages.getString("Main.inventoryHandlingHelpLine5")
												+ Messages.getString("Main.inventoryHandlingHelpLine6")
												+ Messages.getString("Main.inventoryHandlingHelpLine7")
												+ Messages.getString("Main.inventoryHandlingHelpLine8")
												+ Messages.getString("Main.inventoryHandlingHelpLine9"),
										Messages.getString("Main.inventoryHandlingHelpTitle"),
										JOptionPane.YES_NO_OPTION, JOptionPane.INFORMATION_MESSAGE, null,
										new Object[] { Messages.getString("Main.inventoryHandlingHelpOk") }, 0);
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

		for (Component ct : ivBox.getComponents())
			if (!(ct instanceof JTextPane) && !ct.equals(enableIVHandling))
				ct.setEnabled(enableIVHandling.isSelected());

		ivBox.alignAll();

		JVBoxPanel gnBox = new JVBoxPanel();

		JComboBox<Language> languages = new JComboBox<>(Language.values());
		languages.setSelectedItem(up.getAppLanguage());

		gnBox.add(new JLabel(Messages.getString("Main.settingsLangChangeLabel")));
		gnBox.add(languages);
		gnBox.add(new JTextPane() {
			{
				setEditable(false);
				setOpaque(false);
			}
		});

		gnBox.alignAll();

		jtp.add(Messages.getString("Main.settingsTabGeneral"), gnBox);
		jtp.add(Messages.getString("Main.settingsTabAppearance"), apPane);
		jtp.add(Messages.getString("Main.settingsTabTray"), trBox);
		jtp.add(Messages.getString("Main.settingsTabResourcePacks"), rsBox);
		jtp.add(Messages.getString("Main.settingsTabSkins"), skBox);
		jtp.add(Messages.getString("Main.settingsTabProtocol"), pkBox);
		jtp.add(Messages.getString("Main.settingsTabInventory"), ivBox);
		b.add(jtp);

		JButton sOk = new JButton(Messages.getString("Main.settingsOk"));
		JButton sCancel = new JButton(Messages.getString("Main.settingsCancel"));

		sOk.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				Status rsBehavior = (Status) rPackBehaviorBox.getSelectedItem();
				boolean showResourcePackMessages = rsPackShowCheck.isSelected();
				String resourcePackMessage = rsPackMsgText.getText();
				Position resourcePackMessagePosition = (Position) rsPackMessagePosition.getSelectedItem();

				SkinRule skinFetchRule = (SkinRule) ruleBox.getSelectedItem();

				boolean ignoreKeepAlive = ignoreKAPackets.isSelected();
				String brand = brandField.getText();
				boolean sendMCBrand = !brand.isEmpty();

				up.setResourcePackBehavior(rsBehavior);
				up.setShowResourcePackMessages(showResourcePackMessages);
				up.setResourcePackMessage(resourcePackMessage.replace("&", "\u00A7"));
				up.setResourcePackMessagePosition(resourcePackMessagePosition);

				up.setSkinFetchRule(skinFetchRule);

				up.setIgnoreKeepAlive(ignoreKeepAlive);
				up.setAdditionalPing((int) pingField.getValue());
				up.setBrand(brand);
				up.setSendMCBrand(sendMCBrand);

				up.setTrayMessageMode((String) trMessagesMode.getSelectedItem());
				up.setTrayShowDisconnectMessages(showDMessages.isSelected());
				up.setTrayKeyWords(trMessagesKeywords.getListData());

				if (!enableIVHandling.isSelected())
					for (MinecraftClient cl : clients.values()) {
						for (ItemsWindow iw : cl.getOpenWindows().values())
							iw.closeWindow();
						cl.getInventory().closeWindow();
					}

				if (!enableIVHandling.isSelected() || !loadTextures.isSelected())
					if ((up.isEnableInventoryHandling() != enableIVHandling.isSelected())
							|| (up.isLoadInventoryTextures() != loadTextures.isSelected()))
						if (ItemsWindow.getTexturesSize() > 0) {
							int response = JOptionPane.showOptionDialog(od,
									Messages.getString("Main.inventoryHandlingDisabledLine1")
											+ Messages.getString("Main.inventoryHandlingDisabledLine2"),
									Messages.getString("Main.inventoryHandlingDisabledTitle"),
									JOptionPane.YES_NO_OPTION, JOptionPane.QUESTION_MESSAGE, null,
									new Object[] { Messages.getString("Main.inventoryHandlingDisabledYes"),
											Messages.getString("Main.inventoryHandlingDisabledNo") },
									0);
							if (response == 0)
								ItemsWindow.clearTextures(Main.this);
						}

				if (enableIVHandling.isSelected() && loadTextures.isSelected())
					if ((up.isEnableInventoryHandling() != enableIVHandling.isSelected())
							|| (up.isLoadInventoryTextures() != loadTextures.isSelected())) {
						int response = JOptionPane.showOptionDialog(od,
								Messages.getString("Main.itemLoadingEnabledLine1")
										+ Messages.getString("Main.itemLoadingEnabledLine2"),
								Messages.getString("Main.itemLoadingEnabledTitle"), JOptionPane.YES_NO_OPTION,
								JOptionPane.QUESTION_MESSAGE, null,
								new Object[] { Messages.getString("Main.itemLoadingEnabledYes"),
										Messages.getString("Main.itemLoadingEnabledNo") },
								0);
						if (response == 0) {
							od.dispose();
							ItemsWindow.initTextures(Main.this, false);
							if (clients.size() > 0)
								JOptionPane.showOptionDialog(od,
										Messages.getString("Main.itemTexturesLoadedLine1")
												+ Messages.getString("Main.itemTexturesLoadedLine2")
												+ Messages.getString("Main.itemTexturesLoadedLine3"),
										Messages.getString("Main.itemTexturesLoadedTitle"),
										JOptionPane.OK_CANCEL_OPTION, JOptionPane.INFORMATION_MESSAGE, null,
										new Object[] { Messages.getString("Main.itemTexturesLoadedOk") }, 0);
						}
					}

				if (enableIVHandling.isSelected() && !up.isEnableInventoryHandling())
					if (clients.size() > 0)
						JOptionPane.showOptionDialog(od,
								Messages.getString("Main.inventoryHandlingEnabledLine1")
										+ Messages.getString("Main.inventoryHandlingEnabledLine2")
										+ Messages.getString("Main.inventoryHandlingEnabledLine3"),
								Messages.getString("Main.inventoryHandlingEnabledTitle"), JOptionPane.OK_CANCEL_OPTION,
								JOptionPane.INFORMATION_MESSAGE, null,
								new Object[] { Messages.getString("Main.inventoryHandlingEnabledOk") }, 0);

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
				cp2.setColorEnabledHoverButton(SwingUtils.getHexRGB(apButtonEnabledHover.getColor()));
				cp2.setColorText(SwingUtils.getHexRGB(apButtonText.getColor()));
				cp2.setDisabledColorText(SwingUtils.getHexRGB(apButtonTextDisabled.getColor()));

				upSaveRunnable.run();
				PlayerSkinCache.getSkincache().clear();

				if (langChanged) {
					int response = JOptionPane.showOptionDialog(od,
							Messages.getString("Main.langChangedLabelLine1")
									+ Messages.getString("Main.langChangedLabelLine2"),
							Messages.getString("Main.langChangedDialogTitle"), JOptionPane.OK_OPTION,
							JOptionPane.INFORMATION_MESSAGE, null,
							new Object[] { Messages.getString("Main.langChangedLabelDialogOptionRestart"),
									Messages.getString("Main.langChangedLabelDialogOptionContinue") },
							0);
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

	private JSplitPane createServerPane(final ServerEntry entry, final String username) {

		final JSplitPane fPane = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT);

		final Box box = Box.createVerticalBox();

		final JTextPane pane = new JTextPane();
		pane.setBackground(new Color(35, 35, 35));
		pane.setForeground(Color.white);
		pane.setEditable(false);
		pane.setFont(mcFont.deriveFont(13.5f));

		final JScrollPane jsc = new JScrollPane(pane);
		jsc.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_AS_NEEDED);

		final Box chatControls = Box.createHorizontalBox();

		final JTextField chatInput = new JMinecraftField(Messages.getString("Main.chatField"));
		chatInput.setEnabled(false);

		final JButton chatSend = new JMinecraftButton(Messages.getString("Main.chatSendButton"));
		chatSend.setEnabled(false);
		chatSend.setMargin(new Insets(5, 5, 5, 5));

		for (Component ct : chatControls.getComponents())
			ct.setFont(ct.getFont().deriveFont(13.5f));

		chatControls.add(chatInput);
		chatControls.add(chatSend);
		chatControls.setMaximumSize(new Dimension(SwingUtils.sSize.width, 0));

		final JTextPane hotbar = new JTextPane();
		hotbar.setBackground(new Color(35, 35, 35));
		hotbar.setForeground(Color.white);
		hotbar.setEditable(false);
		hotbar.setFont(mcFont.deriveFont(13.5f));

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
						fPane.setDividerLocation(0.8);
					}
				});
			}
		});

		JVBoxPanel playerBox = new JVBoxPanel();
		JPanel statisticsContainer = new JPanel();

		final JCheckBox toggleSneak = new JCheckBox(Messages.getString("Main.toggleSneak"));
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

		final JCheckBox toggleSprint = new JCheckBox(Messages.getString("Main.toggleSprint"));
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

		healthBar.setString(Messages.getString("Main.healthBar"));
		foodBar.setString(Messages.getString("Main.foodBar"));

		JButton[] movementButtons = new JButton[] { new BasicArrowButton(SwingConstants.NORTH),
				new BasicArrowButton(SwingConstants.SOUTH_WEST), new BasicArrowButton(SwingConstants.WEST),
				new BasicArrowButton(SwingConstants.NORTH_WEST), new BasicArrowButton(SwingConstants.SOUTH),
				new BasicArrowButton(SwingConstants.NORTH_EAST), new BasicArrowButton(SwingConstants.EAST),
				new BasicArrowButton(SwingConstants.SOUTH_EAST) };

		JButton jumpButton = new BasicArrowButton(SwingConstants.NORTH_EAST);
		jumpButton.setEnabled(false);

		final JCheckBox lockPos = new JCheckBox(Messages.getString("Main.lockPlayerPosition"));
		final JSpinner speed = new JSpinner(new SpinnerNumberModel(0.3, 0.1, 1, 0.1));
		final JSpinner blocks = new JSpinner(new SpinnerNumberModel(1, 0, 1000, 0.1));

		SwingUtils.alignSpinner(speed);
		SwingUtils.alignSpinner(blocks);

		Box speedBox = Box.createHorizontalBox();
		Box blocksBox = Box.createHorizontalBox();

		speedBox.add(new JLabel(Messages.getString("Main.movementSpeed")));
		blocksBox.add(new JLabel(Messages.getString("Main.distanceToWalk")));
		speedBox.add(speed);
		blocksBox.add(blocks);

		for (final Component ct : speedBox.getComponents())
			SwingUtilities.invokeLater(new Runnable() {

				@Override
				public void run() {
					ct.setMaximumSize(new Dimension(ct.getWidth(), 20));
				}
			});
		for (final Component ct : blocksBox.getComponents())
			SwingUtilities.invokeLater(new Runnable() {

				@Override
				public void run() {
					ct.setMaximumSize(new Dimension(ct.getWidth(), 20));
				}
			});

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

		final JLabel xLabel = new JLabel("X: 0");
		final JLabel yLabel = new JLabel("Y: 0");
		final JLabel zLabel = new JLabel("Z: 0");

		playerBox.add(toggleSneak);
		playerBox.add(toggleSprint);
		playerBox.add(healthBar);
		playerBox.add(foodBar);
		playerBox.add(new JLabel(" "));
		playerBox.add(new JLabel(Messages.getString("Main.playerPosition")));
		playerBox.add(xLabel);
		playerBox.add(yLabel);
		playerBox.add(zLabel);
		playerBox.add(new JLabel(" "));
		playerBox.add(new JLabel(Messages.getString("Main.playerMovement")));
		playerBox.add(lockPos);
		playerBox.add(new JLabel(" "));
		playerBox.add(speedBox);
		playerBox.add(blocksBox);
		playerBox.add(new JLabel(" "));
		playerBox.add(movementPanel);
		playerBox.alignAll();

		Box playerListBox = Box.createVerticalBox();

		JPlaceholderField filterField = new JMinecraftField(Messages.getString("Main.playerNamesFilter"));
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
						boolean prependSpace = !(ct.isEmpty() || (ct.substring(ct.length() - 1).equals(" ")));
						if (prependSpace)
							ct += " ";
						chatInput.setText(ct + uName + " ");
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

		JButton refreshStats = new JButton(Messages.getString("Main.refreshStatsButton"));
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

		final JButton showInventory = new JButton(Messages.getString("Main.showInventoryButton"));
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

		JVBoxPanel worldBox = new JVBoxPanel();

		Box timeBox = Box.createHorizontalBox();

		JLabel timeLabel = new JLabel(Messages.getString("Main.worldTimeLabel"));
		final JLabel timeValueLabel = new JLabel("-:-");

		timeBox.add(timeLabel);
		timeBox.add(timeValueLabel);

		worldBox.add(timeBox);
		worldBox.alignAll();

		JVBoxPanel autoMsgBox = new JVBoxPanel();

		JCheckBox autoMsgEnable = new JCheckBox(Messages.getString("Main.enabled"));

		JSpinner autoMsgDelay = new JSpinner(new SpinnerNumberModel(1, 0, Integer.MAX_VALUE, 1));
		SwingUtils.alignSpinner(autoMsgDelay);
		JSpinner autoMsgInterval = new JSpinner(new SpinnerNumberModel(1, 0, Integer.MAX_VALUE, 1));
		SwingUtils.alignSpinner(autoMsgInterval);

		JRadioButton intSeconds = new JRadioButton(Messages.getString("Main.seconds"));
		intSeconds.setSelected(true);
		JRadioButton intMinutes = new JRadioButton(Messages.getString("Main.minutes"));
		ButtonGroup gp = new ButtonGroup();
		gp.add(intMinutes);
		gp.add(intSeconds);

		JMemList<String> autoMessages = new JMemList<String>();
		JScrollPane autoMsgPane = new JScrollPane(autoMessages);

		JButton addMsg = new JButton("+");
		JButton removeMsg = new JButton("-");
		JButton downMsg = new JButton("v");
		JButton upMsg = new JButton("^");

		removeMsg.setEnabled(false);
		upMsg.setEnabled(false);
		downMsg.setEnabled(false);

		autoMessages.addListSelectionListener(e -> {
			if (!e.getValueIsAdjusting()) {
				@SuppressWarnings("unchecked")
				JMemList<String> ls = (JMemList<String>) e.getSource();

				boolean eb = ls.getSelectedIndex() != -1 && ls.getSelectedValue() != null;
				removeMsg.setEnabled(eb);
				upMsg.setEnabled(eb && ls.getSelectedIndex() > 0);
				downMsg.setEnabled(eb && ls.getSelectedIndex() < ls.getListData().length - 1);
			}
		});

		upMsg.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				if (autoMessages.getSelectedIndex() > 0) {
					String[] ld = autoMessages.getListData();
					int x = autoMessages.getSelectedIndex();
					String bf = ld[x - 1];
					ld[x - 1] = ld[x];
					ld[x] = bf;
					autoMessages.setListData(ld);
					autoMessages.setSelectedIndex(x - 1);
				}
			}
		});

		downMsg.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				if (autoMessages.getSelectedIndex() < autoMessages.getListData().length - 1) {
					String[] ld = autoMessages.getListData();
					int x = autoMessages.getSelectedIndex();
					String bf = ld[x + 1];
					ld[x + 1] = ld[x];
					ld[x] = bf;
					autoMessages.setListData(ld);
					autoMessages.setSelectedIndex(x + 1);
				}
			}
		});

		removeMsg.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				if (autoMessages.getSelectedIndex() != -1) {
					int z = autoMessages.getSelectedIndex();
					String[] ld = autoMessages.getListData();
					List<String> ls = new ArrayList<>();
					for (int x = 0; x < ld.length; x++)
						if (x != z)
							ls.add(ld[x]);
					autoMessages.setListData((String[]) ls.toArray(new String[ls.size()]));
					autoMessages.setSelectedIndex(
							z >= autoMessages.getListData().length ? autoMessages.getListData().length - 1 : z);
				}
			}
		});

		addMsg.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				JVBoxPanel autoMsgAddPanel = new JVBoxPanel();
				autoMsgAddPanel.add(new JLabel(Messages.getString("Main.autoMsgAddLabel")));

				JPlaceholderField jpf = new JPlaceholderField(Messages.getString("Main.message"));
				autoMsgAddPanel.add(jpf);

				autoMsgAddPanel.alignAll();

				int resp = JOptionPane.showOptionDialog(win, autoMsgAddPanel,
						Messages.getString("Main.autoMsgAddTitle"), JOptionPane.OK_CANCEL_OPTION,
						JOptionPane.QUESTION_MESSAGE, null, null, 0);

				if (resp == 0 && !jpf.getText().replace(" ", "").isEmpty()) {
					String[] ld = autoMessages.getListData();
					ld = ld == null ? new String[0] : ld;
					String[] nld = new String[ld.length + 1];
					for (int x = 0; x < ld.length; x++)
						nld[x] = ld[x];
					nld[nld.length - 1] = jpf.getText();
					autoMessages.setListData(nld);
				}
			}
		});

		Box msgCtlBox = Box.createHorizontalBox();
		msgCtlBox.add(addMsg);
		msgCtlBox.add(removeMsg);
		msgCtlBox.add(upMsg);
		msgCtlBox.add(downMsg);

		ActionListener ac = new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				boolean eb = autoMsgEnable.isSelected();
				for (Component ct : autoMsgBox.getComponents()) {
					if (ct instanceof JRadioButton || ct instanceof JSpinner)
						ct.setEnabled(eb);
				}
			}
		};
		autoMsgEnable.addActionListener(ac);

		autoMsgBox.add(autoMsgEnable);
		autoMsgBox.add(new JLabel(" "));
		autoMsgBox.add(new JLabel(Messages.getString("Main.autoMsgDelay")));
		autoMsgBox.add(autoMsgDelay);
		autoMsgBox.add(intSeconds);
		autoMsgBox.add(intMinutes);
		autoMsgBox.add(new JLabel(" "));
		autoMsgBox.add(new JLabel(Messages.getString("Main.autoMsgInterval")));
		autoMsgBox.add(autoMsgInterval);
		autoMsgBox.add(new JLabel(" "));
		autoMsgBox.add(new JLabel(Messages.getString("Main.autoMsgLabel")));
		autoMsgBox.add(msgCtlBox);
		autoMsgBox.add(autoMsgPane);

		autoMsgBox.alignAll();

		SwingUtilities.invokeLater(new Runnable() {

			@Override
			public void run() {
				autoMsgInterval.setMaximumSize(new Dimension(autoMsgInterval.getWidth(), 20));
				autoMsgDelay.setMaximumSize(new Dimension(autoMsgDelay.getWidth(), 20));
				ac.actionPerformed(null);
			}
		});

		controlsTabPane.addTab(Messages.getString("Main.playerListTab"), playerListBox);
		controlsTabPane.addTab(Messages.getString("Main.playerTab"), new JScrollPane(playerBox));
		controlsTabPane.addTab(Messages.getString("Main.statisticsTab"), statisticsPane);
		controlsTabPane.addTab(Messages.getString("Main.inventoryTab"), inventoryBox);
		controlsTabPane.addTab(Messages.getString("Main.worldTab"), worldBox);
		controlsTabPane.addTab(Messages.getString("Main.autoMessagesTab"), autoMsgBox);

		fPane.add(box);
		fPane.add(controlsTabPane);
		SwingUtilities.invokeLater(new Runnable() {
			@Override
			public void run() {
				fPane.setDividerLocation(0.8);
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
					case "Auto": {
						try {
							protocol = MinecraftStat.serverListPing(host, port).getProtocol();
							boolean contains = false;
							for (ProtocolNumber num : ProtocolNumber.values())
								if (num.protocol == protocol) {
									contains = true;
									break;
								}
							if (!contains)
								protocol = -2;
						} catch (Exception e) {
							SwingUtils.appendColoredText(
									Messages.getString("Main.connectionFailedChatMessage") + e.toString(), pane);
							e.printStackTrace();
						}
						break;
					}
					case "Always Ask": {
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

					bb.add(new JLabel(Messages.getString("Main.chooseMinecraftVersionLabel")));
					bb.add(pcBox);

					JOptionPane.showOptionDialog(win, bb, Messages.getString("Main.chooseMinecraftVersionTitle"),
							JOptionPane.DEFAULT_OPTION, JOptionPane.QUESTION_MESSAGE, null,
							new String[] { Messages.getString("Main.chooseMinecraftVersionOptionOl") }, null);

					protocol = ProtocolNumber.getForName((String) pcBox.getSelectedItem()).protocol;
				}

				if (protocol != -1) {
					final int iprotocol = protocol;
					try {
						if (iprotocol == 755)
							showInventory.setEnabled(false);
						if (iprotocol >= 393)
							controlsTabPane.setEnabledAt(2, false);
						final MinecraftClient cl = new MinecraftClient(host, port, iprotocol);
						clients.put(fPane, cl);

						final Thread autoMessagesThread = new Thread(new Runnable() {
							@Override
							public void run() {
								while (cl.isConnected()) {
									try {
										int sleepVal = (int) (autoMsgDelay.getValue()) * 1000;
										if (intMinutes.isSelected())
											sleepVal *= 60;
										Thread.sleep(sleepVal);
										if (autoMsgEnable.isSelected() && autoMessages.getListData() != null) {
											List<String> msgs = new ArrayList<String>();
											for (String s : autoMessages.getListData())
												msgs.add(s);
											for (int x = 0; x < msgs.size(); x++) {
												if (!cl.isConnected())
													return;
												cl.sendChatMessage(msgs.get(x));
												if (x < msgs.size() - 1)
													try {
														Thread.sleep((int) autoMsgInterval.getValue() * 1000);
													} catch (Exception e) {
														e.printStackTrace();
													}
												if (!autoMsgEnable.isSelected())
													break;
											}
										}
									} catch (Exception e2) {
										e2.printStackTrace();
									}
								}
							}
						});

						cl.getPlayersTabList().addChangeListener(new MapChangeListener<UUID, PlayerInfo>() {
							@Override
							public void itemRemoved(Object key, PlayerInfo value, HashMap<UUID, PlayerInfo> map) {
								List<PlayerInfo> pl = new ArrayList<PlayerInfo>();
								for (UUID ukey : map.keySet())
									pl.add(map.get(ukey));
								if (pl.size() <= 0)
									return;
								PlayerInfo[] infs = new PlayerInfo[pl.size()];
								infs = pl.toArray(infs);
								playerList.setListData(infs);
							}

							@Override
							public void itemAdded(UUID key, PlayerInfo value, HashMap<UUID, PlayerInfo> map) {
								List<PlayerInfo> pl = new ArrayList<PlayerInfo>();
								for (UUID ukey : map.keySet())
									pl.add(map.get(ukey));
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
									hjtp.setText("");
									SwingUtils.appendColoredText(message, hjtp);
								} else {
									if (trayIcon != null) {
										boolean shouldDisplay = (up.getTrayMessageMode()
												.equals(Constants.TRAY_MESSAGES_KEY_ALWAYS))
												|| (up.getTrayMessageMode().equals(Constants.TRAY_MESSAGES_KEY_MENTION)
														&& message.toLowerCase()
																.contains(cl.getUsername().toLowerCase()));
										if (!shouldDisplay && up.getTrayMessageMode()
												.equals(Constants.TRAY_MESSAGES_KEY_KEYWORD)) {
											String[] keyWords = up.getTrayKeyWords();
											if (keyWords != null)
												for (String keyWord : keyWords)
													if (message.toLowerCase().contains(keyWord.toLowerCase()))
														shouldDisplay = true;
										}

										if (shouldDisplay) {
											trayLastMessageType = 0;
											trayLastMessageSender = cl;
											String ttext = ChatMessages.removeColors(message);
											trayIcon.displayMessage(
													cl.getHost() + ":" + cl.getPort() + " (" + cl.getUsername() + ")",
													ttext, MessageType.NONE);
										}
									}

									SwingUtils.appendColoredText(message + "\r\n", jtp);
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
								autoMessagesThread.interrupt();
								SwingUtils.appendColoredText(
										Messages.getString("Main.connectionLostChatMessage") + reason + "\r\n", jtp);

								if (trayIcon != null && up.isTrayShowDisconnectMessages()) {
									trayLastMessageType = 1;
									String ttext = Messages.getString("Main.connectionLostTrayMessage")
											+ ChatMessages.removeColors(reason);
									trayIcon.displayMessage(
											cl.getHost() + ":" + cl.getPort() + " (" + cl.getUsername() + ")", ttext,
											MessageType.ERROR);
									PopupMenu pm = trayIcon.getPopupMenu();
									for (int x = 0; x < pm.getItemCount(); x++) {
										MenuComponent ct = pm.getItem(x);
										if (ct instanceof Menu) {
											Menu cm = (Menu) ct;
											String lbl = cm.getLabel();
											if (lbl.equals(cl.getHost() + ":" + cl.getPort()))
												for (int y = 0; y < cm.getItemCount(); x++) {
													Menu pmenu = (Menu) cm.getItem(y);
													if (pmenu.getLabel().equals(cl.getUsername()))
														cm.remove(y);
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
									if (ct.getName().equals(entry.getHost() + "_" + entry.getName() + "_" + username))
										if (ct instanceof Box) {
											Box ctb = (Box) ct;
											for (Component ctt : ctb.getComponents())
												if (ctt instanceof JLabel)
													ctt.setForeground(Color.gray);
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
										Messages.getString("Main.healthBarText") + Integer.toString((int) health) + "/"
												+ Integer.toString(healthBar.getMaximum()) + ")");
								foodBar.setString(Messages.getString("Main.foodBarText") + Integer.toString(food) + "/"
										+ Integer.toString(foodBar.getMaximum()) + ")");
							}

							@Override
							public void positionChanged(double x, double y, double z) {
								String sx = Double.toString(x);
								String sy = Double.toString(y);
								String sz = Double.toString(z);
								if (sx.contains("."))
									sx = sx.substring(0, sx.lastIndexOf(".") + 2);
								if (sy.contains("."))
									sy = sy.substring(0, sy.lastIndexOf(".") + 2);
								if (sz.contains("."))
									sz = sz.substring(0, sz.lastIndexOf(".") + 2);

								xLabel.setText("X: " + sx);
								yLabel.setText("Y: " + sy);
								zLabel.setText("Z: " + sz);
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
									statisticsContainer.add(new JLabel("   " + Integer.toString(trueValues.get(key))));
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
													PacketFactory.constructPacket(reg, "ClientCloseWindowPacket", id));
										} catch (Exception e) {
											e.printStackTrace();
										}
									return;
								}
								if (!up.isShowWindowsInTray() && trayIcon != null)
									return;
								win.openWindow(Main.this.win, up.isSendWindowClosePackets());
							}

							@Override
							public void timeUpdated(long time, long worldAge) {
								if (time < 0)
									time = time * -1;

								time = time % 24000;

								int hours = 6 + (int) (time / 1000);
								if (hours >= 24)
									hours -= 24;
								double minutesDouble = time % 1000;
								minutesDouble = (minutesDouble / 1000) * 60;
								int minutes = (int) Math.round(minutesDouble);
								if (minutes == 60) {
									minutes = 0;
									hours++;
								}

								String timeString = IOUtils.padString(Integer.toString(hours), 2, "0", 1) + ":"
										+ IOUtils.padString(Integer.toString(minutes), 2, "0", 1);

								timeValueLabel.setText(timeString);

							}

						});
						cl.connect(username);
						autoMessagesThread.start();

						SwingUtilities.invokeLater(new Runnable() {

							@Override
							public void run() {
								fPane.setDividerLocation(0.8);
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
												Messages.getString("Main.connectionLostChatMessage2") + e1.toString(),
												pane);
										e1.printStackTrace();
										for (Component ct : chatControls.getComponents())
											ct.setEnabled(false);
									}
									chatInput.setText("");
								}
							}
						});

						for (Component ct : chatControls.getComponents())
							ct.setEnabled(true);

					} catch (

					IOException e) {
						SwingUtils.appendColoredText(
								Messages.getString("Main.connectionFailedChatMessage2") + e.toString(), pane);
					}
				}
			}
		}).start();

		return fPane;
	}

	public ActionListener getConnectionACL() {
		return alis;
	}
}
