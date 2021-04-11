package net.defekt.mc.chatclient.ui;

import java.awt.AWTException;
import java.awt.Color;
import java.awt.Component;
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
import net.defekt.mc.chatclient.protocol.MinecraftClient;
import net.defekt.mc.chatclient.protocol.MinecraftStat;
import net.defekt.mc.chatclient.protocol.ProtocolNumber;
import net.defekt.mc.chatclient.protocol.data.ChatMessage;
import net.defekt.mc.chatclient.protocol.data.PlayerInfo;
import net.defekt.mc.chatclient.protocol.data.PlayerSkinCache;
import net.defekt.mc.chatclient.protocol.data.TranslationUtils;
import net.defekt.mc.chatclient.protocol.io.IOUtils;
import net.defekt.mc.chatclient.protocol.io.ListenerHashMap.MapChangeListener;
import net.defekt.mc.chatclient.protocol.packets.general.clientbound.play.ServerChatMessagePacket.Position;
import net.defekt.mc.chatclient.protocol.packets.general.serverbound.play.ClientResourcePackStatusPacket.Status;
import net.defekt.mc.chatclient.ui.UserPreferences.ColorPreferences;
import net.defekt.mc.chatclient.ui.UserPreferences.Constants;
import net.defekt.mc.chatclient.ui.UserPreferences.SkinRule;
import net.defekt.mc.chatclient.ui.swing.JColorChooserButton;
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

	public static final BufferedImage bgImage = new BufferedImage(64, 64, BufferedImage.TYPE_INT_ARGB);
	private static BufferedImage logoImage = null;

	public static final String version = "1.0.0";
	private static final String changelogURL = "https://raw.githubusercontent.com/Defective4/Another-Minecraft-Chat-Client/master/Changes";

	public static Font mcFont = Font.decode(null);

	private static void checkForUpdates() {
		try (BufferedReader br = new BufferedReader(new InputStreamReader(new URL(changelogURL).openStream()))) {
			List<String> cgLines = new ArrayList<String>();
			String line;
			while ((line = br.readLine()) != null) {
				cgLines.add(line);
			}

			if (cgLines.size() > 1 && cgLines.get(0).equals("AMCC Change Log")) {
				String newVersionString = IOUtils.padString(cgLines.get(1).substring(1).replace(".", ""), 3, "0");
				String thisVersionString = IOUtils.padString(version.replace(".", ""), 3, "0");

				int newVersion = Integer.parseInt(newVersionString);
				int thisVersion = Integer.parseInt(thisVersionString);

				if (newVersion > thisVersion) {
					String newVersionSm = cgLines.get(1).substring(1);
					String oldVersionSm = version;

					if (newVersionSm.length() - newVersionSm.replace(".", "").length() < 2) {
						newVersionSm += ".0";
					}
					if (oldVersionSm.length() - oldVersionSm.replace(".", "").length() < 2) {
						oldVersionSm += ".0";
					}

					int nMajor = Integer.parseInt(newVersionSm.substring(0, newVersionSm.indexOf(".")));
					int nMinor = Integer.parseInt(
							newVersionSm.substring(newVersionSm.indexOf(".") + 1, newVersionSm.lastIndexOf(".")));
					int nFix = Integer.parseInt(newVersionSm.substring(newVersionSm.lastIndexOf(".") + 1));

					int oMajor = Integer.parseInt(oldVersionSm.substring(0, oldVersionSm.indexOf(".")));
					int oMinor = Integer.parseInt(
							oldVersionSm.substring(oldVersionSm.indexOf(".") + 1, oldVersionSm.lastIndexOf(".")));
					int oFix = Integer.parseInt(oldVersionSm.substring(oldVersionSm.lastIndexOf(".") + 1));

					int diff = 0;
					String vtype = "major";

					if (oFix != nFix) {
						diff = nFix - oFix;
						vtype = "fix";
					}
					if (oMinor != nMinor) {
						diff = nMinor - oMinor;
						vtype = "minor";
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
		}
	}

	public static void main(String[] args) {
		SwingUtils.setNativeLook();
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

		Graphics2D g2 = bgImage.createGraphics();
		try {
			BufferedImage dimg = ImageIO.read(Main.class.getResourceAsStream("/resources/dirt.png"));
			RescaleOp resc = new RescaleOp(0.3f, 15, null);
			resc.filter(dimg, dimg);
			g2.drawImage(dimg, 0, 0, 64, 64, null);
		} catch (Exception e) {
			g2.setColor(Color.white);
			g2.fillRect(0, 0, 64, 64);
		}

		new Main().init();
	}

	protected static final File serverFile = new File("mcc.prefs");
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
		JTextField mField = new JPlaceholderField("Enter message...");

		String label = cl.getHost() + ":" + cl.getPort();
		qmdShowing = true;
		int resp = JOptionPane.showOptionDialog(null, new Object[] { "Send quick message to " + label, mField
		}, "Quick message", JOptionPane.OK_CANCEL_OPTION, JOptionPane.QUESTION_MESSAGE, null,
				new Object[] { "Ok", "Cancel"
				}, 0);
		qmdShowing = false;
		if (resp == 0) {
			String msg = mField.getText();
			if (msg.replace(" ", "").isEmpty())
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

		MinecraftStat.listenOnLAN((sender, motd, port) -> {
			ServerEntry[] ets = lanListComponent.getListData() == null ? new ServerEntry[0]
					: lanListComponent.getListData();
			ServerEntry ent = new ServerEntry(sender.getHostAddress(), port,
					sender.getHostAddress() + ":" + Integer.toString(port), "Auto");
			for (ServerEntry et : ets)
				if (et.equals(ent))
					return;
			ServerEntry[] ets2 = new ServerEntry[ets.length + 1];
			for (int x = 0; x < ets.length; x++)
				ets2[x] = ets[x];
			ets2[ets2.length - 1] = ent;
			lanListComponent.setListData(ets2);

			ent.ping();
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
					diag.setTitle("Exit?");

					JButton ok = new JButton("Ok");
					JButton toTray = new JButton("Minimize to tray");
					JButton cancel = new JButton("Cancel");
					toTray.setEnabled(SystemTray.isSupported());
					JCheckBox rememberOp = new JCheckBox("Don't ask again.");

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
							trayIcon = new TrayIcon(IOUtils.scaleImage(logoImage, 0.5), "Another Chat Client");
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

								trayIcon.addActionListener(ev2 -> {
									switch (trayLastMessageType) {
										case 0: {
											showQuickMessageDialog(trayLastMessageSender);
											break;
										}
										case 1: {
											ml.mouseClicked(new MouseEvent(win, 0, System.currentTimeMillis(), 0, 0, 0,
													0, 0, 1, false, MouseEvent.BUTTON1));
											break;
										}
										default: {
											break;
										}
									}
								});

								PopupMenu menu = new PopupMenu();

								MenuItem quit = new MenuItem("Quit");
								quit.addActionListener(ev2 -> {
									System.exit(0);
								});

								MenuItem open = new MenuItem("Open");
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

								menu.add(open);
								menu.addSeparator();
								for (String label : labels.keySet()) {
									Menu srvMenu = new Menu(label);
									for (final MinecraftClient cl : labels.get(label)) {
										Menu pMenu = new Menu(cl.getUsername()) {
											{
												final MinecraftClient client = cl;
												MenuItem dcItem = new MenuItem("Disconnect");
												MenuItem qmItem = new MenuItem("Quick Message");

												dcItem.addActionListener(ev2 -> {
													client.close();
													srvMenu.remove(this);
													if (srvMenu.getItemCount() == 0) {
														menu.remove(srvMenu);
													}
													for (ClientListener ls : client.getClientListeners()) {
														ls.disconnected("Closed from tray");
													}
												});

												qmItem.addActionListener(ev2 -> {
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

					JOptionPane op = new JOptionPane(new Object[] { "Do you want to exit Minecraft Chat Client?\r\n"
							+ "You still have " + Integer.toString(clients.size()) + " clients connected!", rememberOp
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

		serverListPane.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);

		serverListBox.add(serverListPane);
		serverListComponent.setMinimumSize(serverListBox.getPreferredSize());

		Box controlsBox = Box.createHorizontalBox();

		JButton addServer = new JMinecraftButton("Add Server");
		addServer.addActionListener(ev -> {
			JTextField nameField = new JPlaceholderField("Server name");
			nameField.setText("A Minecraft Server");
			JTextField hostField = new JPlaceholderField("Server address");

			JComboBox<String> versionField = new JComboBox<>();
			versionField.addItem("Auto");
			versionField.addItem("Always Ask");
			for (ProtocolNumber num : ProtocolNumber.values()) {
				versionField.addItem(num.name);
			}

			final Box contents = Box.createVerticalBox();

			final JLabel errorLabel = new JLabel("");
			errorLabel.setForeground(Color.red);

			contents.add(errorLabel);
			contents.add(new JLabel("Basic server info:"));
			contents.add(new JLabel(" "));
			contents.add(nameField);
			contents.add(hostField);
			contents.add(versionField);

			for (Component c : contents.getComponents()) {
				if (c instanceof JComponent)
					((JComponent) c).setAlignmentX(Component.LEFT_ALIGNMENT);
			}

			do {
				try {
					int response = JOptionPane.showOptionDialog(win, contents, "Adding server...",
							JOptionPane.OK_CANCEL_OPTION, JOptionPane.QUESTION_MESSAGE, null, null, null);

					if (response == JOptionPane.OK_OPTION) {
						String server = hostField.getText();
						String name = nameField.getText();

						if (server.isEmpty() || name.isEmpty()) {
							errorLabel.setText("All fields are required");
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
			} while (true);
		});

		final JButton refresh = new JMinecraftButton("Refresh");
		refresh.addActionListener(ev -> {
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
		});

		final JButton removeServer = new JMinecraftButton("Remove Server");
		removeServer.addActionListener(ev -> {
			if (serverListComponent.getSelectedValue() != null) {
				removeFromList(serverListComponent.getSelectedValue());
				refresh.doClick();
			}
		});
		removeServer.setEnabled(false);

		final JButton connectServer = new JMinecraftButton("Connect");
		ActionListener alis = new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				ServerEntry et = sTypesPane.getSelectedIndex() == 0 ? serverListComponent.getSelectedValue()
						: lanListComponent.getSelectedValue();
				if (et == null)
					return;
				Box box = Box.createVerticalBox();
				box.add(new JLabel("Enter your username:"));

				JTextField unameField = new JPlaceholderField("User name");
				unameField.setAlignmentX(Component.LEFT_ALIGNMENT);
				box.add(unameField);

				do {
					int response = JOptionPane.showOptionDialog(win, box, "Enter your username",
							JOptionPane.OK_CANCEL_OPTION, JOptionPane.QUESTION_MESSAGE, null, null, null);
					if (response != JOptionPane.OK_OPTION)
						return;

					String uname = unameField.getText();
					if (!uname.isEmpty())
						break;
				} while (true);

				final JSplitPane b = createServerPane(et, unameField.getText());

				tabPane.addTab("", b);
				tabPane.setSelectedComponent(b);

				Box b2 = Box.createHorizontalBox();
				b2.setName(et.getHost() + "_" + et.getName() + "_" + unameField.getText());
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

				b2.add(new JLabel(" " + et.getName() + " (" + unameField.getText() + ")"));

				JButton close = new JButton("x");
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

		lanListComponent.getSelectionModel().addListSelectionListener(e -> {
			if (!e.getValueIsAdjusting()) {
				selectedServer = lanListComponent.getSelectedValue();
				if (selectedServer != null) {
					lanConnect.setEnabled(true);
				} else {
					lanConnect.setEnabled(false);
				}

			}
		});

		lanListBox.add(lanListPane);
		lanListBox.add(lanControlsBox);
		lanListComponent.setMinimumSize(lanListBox.getPreferredSize());

		sTypesPane.addTab("Internet", serverListBox);
		sTypesPane.addTab("LAN", lanListBox);

		tabPane.addTab("Server List", sTypesPane);

		JMenu fileMenu = new JMenu("File") {
			{
				add(new JMenuItem("Quit") {
					{
						addActionListener(ev -> {
							System.exit(0);
						});
					}
				});
			}
		};
		JMenu optionMenu = new JMenu("Options") {
			{
				add(new JMenuItem("Settings") {
					{
						addActionListener(ev -> {
							JDialog od = new JDialog(win);
							od.setModal(true);
							od.setResizable(false);
							od.setTitle("Minecraft Chat Client settings");

							Box b = Box.createVerticalBox();

							JTabbedPane jtp = new JTabbedPane();

							JVBoxPanel rsBox = new JVBoxPanel();

							JComboBox<Status> rPackBehaviorBox = new JComboBox<>(Status.values());
							rPackBehaviorBox.setSelectedItem(up.getResourcePackBehavior());
							rPackBehaviorBox.setRenderer(new DefaultListCellRenderer() {
								@Override
								public Component getListCellRendererComponent(JList<? extends Object> list,
										Object value, int index, boolean isSelected, boolean cellHasFocus) {
									JLabel lbl = new JLabel();
									String txt;
									switch ((Status) value) {
										case ACCEPTED: {
											txt = "Accept";
											break;
										}
										case DECLINED: {
											txt = "Decline";
											break;
										}
										default: {
											txt = "Simulate fail";
											break;
										}
										case LOADED: {
											txt = "Accept and load";
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

							JCheckBox rsPackShowCheck = new JCheckBox("Announce incoming resource packs",
									up.isShowResourcePackMessages());

							JPlaceholderField rsPackMsgText = new JPlaceholderField("Resource pack message");
							rsPackMsgText.setText(up.getResourcePackMessage());

							JComboBox<Position> rsPackMessagePosition = new JComboBox<>(Position.values());
							rsPackMessagePosition.setSelectedItem(up.getResourcePackMessagePosition());

							rsBox.add(new JLabel("Resource pack behavior"));
							rsBox.add(rPackBehaviorBox);
							rsBox.add(new JLabel(" "));
							rsBox.add(rsPackShowCheck);
							rsBox.add(new JLabel(" "));
							rsBox.add(new JLabel("Resource pack message. \"%res\" is replaced with pack's url."));
							rsBox.add(rsPackMsgText);
							rsBox.add(new JLabel(" "));
							rsBox.add(new JLabel("Resource pack message position"));
							rsBox.add(rsPackMessagePosition);
							rsBox.add(new JTextPane() {
								{
									setEditable(false);
									setOpaque(false);
								}
							});

							rsBox.alignAll();

							JVBoxPanel skBox = new JVBoxPanel();
							skBox.add(new JLabel("Skin fetch method"));
							JComboBox<SkinRule> ruleBox = new JComboBox<>(SkinRule.values());
							ruleBox.setSelectedItem(up.getSkinFetchRule());
							skBox.add(ruleBox);
							skBox.add(new JTextPane() {
								{
									setText("\r\n" + "SERVER - Use skins sent by server.\r\n\r\n"
											+ "MOJANG_API - Query Mojang API for every skin.\r\n"
											+ "Useful when there is no skin restoring plugin on the server's side.\r\n\t\n"
											+ "NONE - Do not display any skins at all.");
									setEditable(false);
								}
							});

							skBox.alignAll();

							JVBoxPanel pkBox = new JVBoxPanel();

							JCheckBox ignoreKAPackets = new JCheckBox("Ignore Keep-Alive Packets");
							ignoreKAPackets.setSelected(up.isIgnoreKeepAlive());

							JTextField brandField = new JPlaceholderField("Minecraft brand...");
							brandField.setText(up.getBrand());
							SwingUtilities.invokeLater(() -> {
								brandField.setOpaque(true);
							});

							JSpinner pingField = new JSpinner(new SpinnerNumberModel(0, 0, Integer.MAX_VALUE, 1));
							pingField.setValue(up.getAdditionalPing());
							SwingUtils.alignSpinner(pingField);

							pkBox.add(ignoreKAPackets);
							pkBox.add(new JLabel(" "));
							pkBox.add(new JLabel("Additional ping value"));
							pkBox.add(new JLabel("(Too high values may disconnect client with Timed Out reason)"));
							pkBox.add(pingField);
							pkBox.add(new JLabel(" "));
							pkBox.add(new JLabel("Minecraft Brand (empty = do not send brand info)"));
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
							trMessagesMode.setSelectedItem(up.getTrayMessageMode());
							JCheckBox showDMessages = new JCheckBox("Show disconnect messages");
							showDMessages.setSelected(up.isTrayShowDisconnectMessages());

							JButton clearRem = new JButton("Clear remembered dialogs");
							if (up.getCloseMode() == Constants.WINDOW_CLOSE_ALWAYS_ASK)
								clearRem.setEnabled(false);

							clearRem.addActionListener(ev2 -> {
								up.setCloseMode(0);
								clearRem.setEnabled(false);
							});

							trBox.add(new JLabel("Display messages when minimized to tray:"));
							trBox.add(trMessagesMode);
							trBox.add(showDMessages);
							trBox.add(new JLabel(" "));
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

							JColorChooserButton apButtonEnabled = new JColorChooserButton(cp.getColorEnabledButton(),
									od);
							JColorChooserButton apButtonEnabledHover = new JColorChooserButton(
									cp.getColorEnabledHoverButton(), od);
							JColorChooserButton apButtonDisabled = new JColorChooserButton(cp.getColorDisabledButton(),
									od);
							JColorChooserButton apButtonText = new JColorChooserButton(cp.getColorText(), od);
							JColorChooserButton apButtonTextDisabled = new JColorChooserButton(
									cp.getDisabledColorText(), od);

							JCheckBox apButtonLockColors = new JCheckBox("Automatical colors");
							apButtonLockColors.setSelected(true);
							JButton apButtonReset = new JButton("Reset to defaults");

							JMinecraftButton sampleButton = new JMinecraftButton("Test");
							JMinecraftButton sampleDisabledButton = new JMinecraftButton("Test");
							sampleButton.setCp(cprefCopy);
							sampleDisabledButton.setCp(cprefCopy);
							sampleDisabledButton.setEnabled(false);

							apButtonSettings.add(apButtonLockColors);
							apButtonSettings.add(new JLabel(" Button background color:"));
							apButtonSettings.add(apButtonEnabled);
							apButtonSettings.add(new JLabel(" Button hover color:"));
							apButtonSettings.add(apButtonEnabledHover);
							apButtonSettings.add(new JLabel(" Disabled button background color:"));
							apButtonSettings.add(apButtonDisabled);
							apButtonSettings.add(new JLabel(" Button text color:"));
							apButtonSettings.add(apButtonText);
							apButtonSettings.add(new JLabel(" Disabled text color:"));
							apButtonSettings.add(apButtonTextDisabled);
							apButtonSettings.add(new JLabel(" "));
							apButtonSettings.add(apButtonReset);
							apButtonSettings.add(new JLabel(" "));

							apButtonEnabled.addColorChangeListener(c -> {
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
								apButtonEnabled.setColor(new Color(Integer.parseInt(cp2.getColorEnabledButton(), 16)));
								apButtonEnabledHover
										.setColor(new Color(Integer.parseInt(cp2.getColorEnabledHoverButton(), 16)));
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

							apPane.addTab("Buttons", apButtonSettingsFull);

							jtp.add("Appearance", apPane);
							jtp.add("Tray", trBox);
							jtp.add("Resource Packs", rsBox);
							jtp.add("Skins", skBox);
							jtp.add("Protocol", pkBox);
							b.add(jtp);

							JButton sOk = new JButton("Ok");
							JButton sCancel = new JButton("Cancel");

							sOk.addActionListener(ev2 -> {
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
								up.setResourcePackMessage(resourcePackMessage.replace("&", "\u00A7"));
								up.setResourcePackMessagePosition(resourcePackMessagePosition);

								up.setSkinFetchRule(skinFetchRule);

								up.setIgnoreKeepAlive(ignoreKeepAlive);
								up.setAdditionalPing((int) pingField.getValue());
								up.setBrand(brand);
								up.setSendMCBrand(sendMCBrand);

								up.setTrayMessageMode((String) trMessagesMode.getSelectedItem());
								up.setTrayShowDisconnectMessages(showDMessages.isSelected());

								ColorPreferences cp2 = up.getColorPreferences();
								cp2.setColorDisabledButton(SwingUtils.getHexRGB(apButtonDisabled.getColor()));
								cp2.setColorEnabledButton(SwingUtils.getHexRGB(apButtonEnabled.getColor()));
								cp2.setColorEnabledHoverButton(SwingUtils.getHexRGB(apButtonEnabledHover.getColor()));
								cp2.setColorText(SwingUtils.getHexRGB(apButtonText.getColor()));
								cp2.setDisabledColorText(SwingUtils.getHexRGB(apButtonTextDisabled.getColor()));

								upSaveRunnable.run();
								PlayerSkinCache.getSkincache().clear();
								od.dispose();
								win.repaint();
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

		final JTextField chatInput = new JMinecraftField("Enter chat message...");
		chatInput.setEnabled(false);
		final JButton chatSend = new JMinecraftButton("Send");
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
		JScrollPane controlsScrollPane = new JScrollPane(controlsTabPane);

		win.addComponentListener(new ComponentAdapter() {
			@Override
			public void componentResized(ComponentEvent e) {
				fPane.setDividerLocation((double) 0.8);
			}
		});

		JVBoxPanel playerBox = new JVBoxPanel();
		JPanel statisticsContainer = new JPanel();

		final JCheckBox toggleSneak = new JCheckBox("Toggle sneak");
		toggleSneak.addActionListener(ev -> {
			try {
				MinecraftClient cl = clients.get(fPane);
				cl.toggleSneaking();
				toggleSneak.setSelected(cl.isSneaking());
			} catch (Exception e1) {
			}
		});

		playerBox.add(toggleSneak);

		final JCheckBox toggleSprint = new JCheckBox("Toggle sprint");
		toggleSprint.addActionListener(ev -> {
			try {
				MinecraftClient cl = clients.get(fPane);
				cl.toggleSprinting();
				toggleSprint.setSelected(cl.isSprinting());
			} catch (Exception e1) {
			}
		});

		final JProgressBar healthBar = new JProgressBar(0, 0);
		final JProgressBar foodBar = new JProgressBar(0, 0);

		healthBar.setStringPainted(true);
		foodBar.setStringPainted(true);

		healthBar.setString("Health");
		foodBar.setString("Food");

		JButton[] movementButtons = new JButton[] { new BasicArrowButton(SwingConstants.NORTH),
				new BasicArrowButton(SwingConstants.SOUTH_WEST), new BasicArrowButton(SwingConstants.WEST),
				new BasicArrowButton(SwingConstants.NORTH_WEST), new BasicArrowButton(SwingConstants.SOUTH),
				new BasicArrowButton(SwingConstants.NORTH_EAST), new BasicArrowButton(SwingConstants.EAST),
				new BasicArrowButton(SwingConstants.SOUTH_EAST)
		};

		JButton jumpButton = new BasicArrowButton(SwingConstants.NORTH_EAST);
		jumpButton.setEnabled(false);

		final JCheckBox lockPos = new JCheckBox("Lock position");
		final JSpinner speed = new JSpinner(new SpinnerNumberModel(0.3, 0.1, 1, 0.1));
		final JSpinner blocks = new JSpinner(new SpinnerNumberModel(1, 0, 1000, 0.1));

		SwingUtils.alignSpinner(speed);
		SwingUtils.alignSpinner(blocks);

		Box speedBox = Box.createHorizontalBox();
		Box blocksBox = Box.createHorizontalBox();

		speedBox.add(new JLabel("Movement speed: "));
		blocksBox.add(new JLabel("Distance to walk: "));
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
		playerBox.add(new JLabel("Player position"));
		playerBox.add(xLabel);
		playerBox.add(yLabel);
		playerBox.add(zLabel);
		playerBox.add(new JLabel(" "));
		playerBox.add(new JLabel("Player movement"));
		playerBox.add(lockPos);
		playerBox.add(new JLabel(" "));
		playerBox.add(speedBox);
		playerBox.add(blocksBox);
		playerBox.add(new JLabel(" "));
		playerBox.add(movementPanel);
		playerBox.alignAll();

		Box playerListBox = Box.createVerticalBox();

		JPlaceholderField filterField = new JMinecraftField("Filter...");
		filterField.setMaximumSize(new Dimension(SwingUtils.sSize.width, 0));

		final JMinecraftPlayerList playerList = new JMinecraftPlayerList(filterField, win, entry.getHost());
		JScrollPane playerListPane = new JScrollPane(playerList);

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

		JButton refreshStats = new JButton("Refresh");
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

		controlsTabPane.addTab("Player List", playerListBox);
		controlsTabPane.addTab("Player", playerBox);
		controlsTabPane.addTab("Statistics", statisticsPane);

		fPane.add(box);
		fPane.add(controlsScrollPane);
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
					case "Auto": {
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
							SwingUtils.appendColoredText("\u00A7cCould not connect to server: " + e.toString(), pane);
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

					bb.add(new JLabel("Choose a Minecraft version to connect to this server"));
					bb.add(pcBox);

					JOptionPane.showOptionDialog(win, bb, "Choose Minecraft Version", JOptionPane.DEFAULT_OPTION,
							JOptionPane.QUESTION_MESSAGE, null, new String[] { "Ok"
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
									hjtp.setText("");
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
								SwingUtils.appendColoredText("\u00A7cConnection Lost: \r\n" + reason + "\r\n", jtp);

								if (trayIcon != null && up.isTrayShowDisconnectMessages()) {
									trayLastMessageType = 1;
									String ttext = "Connection lost: " + ChatMessage.removeColors(reason);
									trayIcon.displayMessage(
											cl.getHost() + ":" + cl.getPort() + " (" + cl.getUsername() + ")", ttext,
											MessageType.ERROR);
									PopupMenu pm = trayIcon.getPopupMenu();
									for (int x = 0; x < pm.getItemCount(); x++) {
										MenuComponent ct = pm.getItem(x);
										if (ct instanceof Menu) {
											Menu cm = (Menu) ct;
											String lbl = cm.getLabel();
											if (lbl.equals(cl.getHost() + ":" + cl.getPort())) {
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

								for (Component ct : chatControls.getComponents())
									ct.setEnabled(false);
								for (int x = 0; x < tabPane.getTabCount(); x++) {
									Component ct = tabPane.getTabComponentAt(x);
									if (ct == null)
										continue;
									if (ct.getName().equals(entry.getHost() + "_" + entry.getName() + "_" + username)) {
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

								healthBar.setString("Health (" + Integer.toString((int) health) + "/"
										+ Integer.toString(healthBar.getMaximum()) + ")");
								foodBar.setString("Food (" + Integer.toString(food) + "/"
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
								// TODO
							}

						});
						cl.connect(username);
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

						chatSend.addActionListener(ev -> {
							String message = chatInput.getText();
							if (!message.isEmpty()) {
								try {
									cl.sendChatMessage(message);
								} catch (IOException e1) {
									SwingUtils.appendColoredText("\u00A7cConnection lost: \r\n " + e1.toString(), pane);
									e1.printStackTrace();
									for (Component ct : chatControls.getComponents())
										ct.setEnabled(false);
								}
								chatInput.setText("");
							}
						});

						for (Component ct : chatControls.getComponents())
							ct.setEnabled(true);

					} catch (

					IOException e) {
						SwingUtils.appendColoredText("\u00A7cCould not connect to server \r\n\r\n " + e.toString(),
								pane);
					}
				}
			}
		}).start();

		return fPane;
	}

}
