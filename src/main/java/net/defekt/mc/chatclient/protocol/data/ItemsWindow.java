package net.defekt.mc.chatclient.protocol.data;

import java.awt.Component;
import java.awt.Font;
import java.awt.Graphics2D;
import java.awt.GridLayout;
import java.awt.Window;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

import javax.imageio.ImageIO;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JPopupMenu;
import javax.swing.JProgressBar;
import javax.swing.WindowConstants;

import com.flowpowered.nbt.CompoundMap;
import com.flowpowered.nbt.StringTag;

import net.defekt.mc.chatclient.protocol.MinecraftClient;
import net.defekt.mc.chatclient.protocol.io.IOUtils;
import net.defekt.mc.chatclient.protocol.packets.PacketFactory;
import net.defekt.mc.chatclient.protocol.packets.PacketRegistry;
import net.defekt.mc.chatclient.protocol.packets.general.serverbound.play.ClientPlayerDiggingPacket.Status;
import net.defekt.mc.chatclient.ui.Main;
import net.defekt.mc.chatclient.ui.Messages;
import net.defekt.mc.chatclient.ui.swing.JVBoxPanel;
import net.defekt.mc.chatclient.ui.swing.MinecraftToolTip;
import net.defekt.mc.chatclient.ui.swing.SwingUtils;

/**
 * Class used to wrap a Minecraft inventory in a GUI window
 * 
 * @author Defective4
 *
 */
public class ItemsWindow {

	private static final Map<String, BufferedImage> itemTextures = new HashMap<String, BufferedImage>();
	private static final String[] armorNames = new String[] { "helmet", "chestplate", "leggings", "boots", "pumpkin" };

	private final Map<Short, Runnable> pendingTransactions = new HashMap<Short, Runnable>();

	private final String title;
	private final int size;
	private final int windowID;
	private final MinecraftClient client;
	private final PacketRegistry registry;
	private final boolean isInventory;
	private boolean isShowing = false;

	private boolean closeSilently = false;

	private final JButton[] bts;
	private final ItemStack[] items;
	private JDialog dialog = null;

	private final Random rand = new Random();
	private final ItemStack emptyItem = new ItemStack((short) -1, -1, (short) -1, null);

	/**
	 * Creates new item window
	 * 
	 * @param title    inventory title
	 * @param size     inventory size
	 * @param windowID window ID
	 * @param client   Minecraft client instance
	 * @param registry Packet registry instance
	 */
	public ItemsWindow(String title, int size, final int windowID, final MinecraftClient client,
			final PacketRegistry registry) {

		isInventory = size == 46;

		this.title = title;
		this.size = size;
		this.windowID = windowID;
		this.client = client;
		this.registry = registry;
		bts = new JButton[size];
		items = new ItemStack[size];
		for (int x1 = 0; x1 < size; x1++) {
			final int xIndexLocal = x1;
			items[x1] = null;
			bts[x1] = new JButton(" ");

			BufferedImage picon = null;
			if (isInventory)
				picon = getPlaceholderIcon(x1);
			bts[x1].setIcon(picon == null ? null : new ImageIcon(picon));

			bts[x1].addMouseListener(new MouseAdapter() {
				final int xIndex = xIndexLocal;
				@SuppressWarnings("serial")
				final JPopupMenu pm = new JPopupMenu() {
					{
						final boolean isHotbar = isInventory && (xIndex >= 36 && xIndex <= 44);
						add(new JMenuItem(Messages.getString("ItemsWindow.itemWindowOptionShiftClick")) {
							{
								addActionListener(new ActionListener() {

									@Override
									public void actionPerformed(ActionEvent arg0) {
										try {
											short actionID = (short) rand.nextInt(Short.MAX_VALUE);
											client.sendPacket(PacketFactory.constructPacket(registry,
													"ClientWindowClickPacket", windowID, (short) xIndex, (byte) 0,
													actionID, 1, emptyItem));
											int newPosition = 0;
											String itemName = items[xIndex] == null ? ""
													: TranslationUtils
															.getItemForID(items[xIndex].getId(),
																	PacketFactory.getProtocolFor(registry))
															.getFileName();

											if (isInventory) {
												int armorPlace = -1;
												for (int x = 0; x < armorNames.length; x++) {
													String armorName = armorNames[x];
													if (itemName.contains(armorName))
														armorPlace = x;
													if (armorPlace > 3)
														armorPlace = 3;

													if (!(items[armorPlace + 5] == null
															|| items[armorPlace + 5].getId() == 0))
														armorPlace = -1;
												}

												if (itemName.contains("elytra") && xIndex != 6)
													newPosition = 6;
												else if (itemName.contains("shield") && xIndex != 45)
													newPosition = 45;
												else if (armorPlace != -1 && !(xIndex < 9 && xIndex >= 5))
													newPosition = 5 + armorPlace;
												else if (!isHotbar && !(xIndex < 9 && xIndex >= 5) && xIndex != 45)
													for (int x = 36; x <= 44; x++) {
														newPosition = x;
														if (items[x] == null || items[x].getId() == 0)
															break;
													}
												else
													for (int x = 9; x < 36; x++) {
														newPosition = x;
														if (items[x] == null || items[x].getId() == 0)
															break;
													}
											} else
												newPosition = -1;

											final int newPositionLocal = newPosition;
											pendingTransactions.put(actionID, new Runnable() {
												final int newPositionI = newPositionLocal;
												final int xIndexI = xIndex;

												@Override
												public void run() {
													if (items[xIndexI] != null)
														putItem(newPositionI, items[xIndexI]);
													putItem(xIndexI, new ItemStack((short) 0, 1, (short) -1, null));
												}
											});

										} catch (IOException e) {
											e.printStackTrace();
										}
									}
								});
							}
						});

						add(new JMenuItem(Messages.getString("ItemsWindow.itemWindowOptionDrop")) {
							{
								addActionListener(new ActionListener() {

									@Override
									public void actionPerformed(ActionEvent arg0) {
										try {
											short actionID = (short) rand.nextInt(Short.MAX_VALUE);
											client.sendPacket(PacketFactory.constructPacket(registry,
													"ClientWindowClickPacket", windowID, (short) xIndex, (byte) 1,
													actionID, 4, emptyItem));
											pendingTransactions.put(actionID, new Runnable() {

												@Override
												public void run() {
													putItem(xIndex, new ItemStack((short) 0, 1, (short) -1, null));
												}
											});
										} catch (IOException e) {
											e.printStackTrace();
										}
									}
								});
							}
						});

						if (isHotbar) {
							add(new JMenuItem(Messages.getString("ItemsWindow.itemWindowOptionSetSlot")) {
								{
									addActionListener(new ActionListener() {

										@Override
										public void actionPerformed(ActionEvent e) {
											try {
												client.sendPacket(PacketFactory.constructPacket(registry,
														"ClientHeldItemChangePacket", (short) (xIndex - 36)));
											} catch (Exception e2) {
												e2.printStackTrace();
											}
										}
									});
								}
							});
							if (PacketFactory.getProtocolFor(registry) > 47) {
								add(new JMenuItem(Messages.getString("ItemsWindow.itemWindowOptionUse")) {
									{
										setFont(getFont().deriveFont(Font.BOLD));
										addActionListener(new ActionListener() {

											@Override
											public void actionPerformed(ActionEvent e) {
												try {
													client.sendPacket(PacketFactory.constructPacket(registry,
															"ClientHeldItemChangePacket", (short) (xIndex - 36)));
													client.sendPacket(PacketFactory.constructPacket(registry,
															"ClientUseItemPacket"));
												} catch (Exception e2) {
													e2.printStackTrace();
												}
											}
										});
									}
								});

								add(new JMenuItem(Messages.getString("ItemsWindow.itemWindowOptionStopUsing")) {
									{
										addActionListener(new ActionListener() {

											@Override
											public void actionPerformed(ActionEvent e) {
												try {
													client.sendPacket(PacketFactory.constructPacket(registry,
															"ClientPlayerDiggingPacket", Status.FINISH_ACTION, 0, 0, 0,
															(byte) 0));
												} catch (Exception e2) {
													e2.printStackTrace();
												}
											}
										});
									}
								});

								add(new JMenuItem(Messages.getString("ItemsWindow.itemWindowOptionSwapItems")) {
									{
										addActionListener(new ActionListener() {

											@Override
											public void actionPerformed(ActionEvent e) {
												try {
													client.sendPacket(PacketFactory.constructPacket(registry,
															"ClientHeldItemChangePacket", (short) (xIndex - 36)));
													client.sendPacket(PacketFactory.constructPacket(registry,
															"ClientPlayerDiggingPacket", Status.SWAP_ITEMS, 0, 0, 0,
															(byte) 0));
												} catch (Exception e3) {
													e3.printStackTrace();
												}
											}
										});
									}
								});
							}
						}
					}
				};

				@Override
				public void mouseClicked(MouseEvent e) {
					if (e.getButton() == MouseEvent.BUTTON3)
						pm.show((Component) e.getSource(), e.getX(), e.getY());
				}
			});

			bts[x1].addActionListener(new ActionListener() {
				final int xIndex = xIndexLocal;

				@Override
				public void actionPerformed(ActionEvent e) {
					try {
						boolean isHotbar = isInventory && (xIndex >= 36 && xIndex <= 44);

						if (isHotbar && PacketFactory.getProtocolFor(registry) > 47)
							try {
								client.sendPacket(PacketFactory.constructPacket(registry, "ClientHeldItemChangePacket",
										(short) (xIndex - 36)));
								client.sendPacket(PacketFactory.constructPacket(registry, "ClientUseItemPacket"));
							} catch (Exception e2) {
								e2.printStackTrace();
							}
						else if (!isInventory)
							client.sendPacket(PacketFactory.constructPacket(registry, "ClientWindowClickPacket",
									windowID, (short) xIndex, (byte) 0, (short) rand.nextInt(Short.MAX_VALUE), 0,
									emptyItem));
					} catch (IOException e1) {
						e1.printStackTrace();
					}
				}
			});
		}
	}

	/**
	 * Init textures from textures archive file
	 * 
	 * @param main     Main class instance
	 * @param preStart is this method invoked before application was launched
	 */
	public static void initTextures(Main main, boolean preStart) {
		JFrame win = new JFrame(Messages.getString("ItemsWindow.itemWindowItemLoadDialogTitle"));
		win.setDefaultCloseOperation(preStart ? JFrame.EXIT_ON_CLOSE : WindowConstants.DO_NOTHING_ON_CLOSE);

		JVBoxPanel message = new JVBoxPanel();
		message.add(new JLabel(Messages.getString("ItemsWindow.itemWindowItemLoadDialogLabel")));

		JLabel pLabel = new JLabel(" ");
		JProgressBar jpb = new JProgressBar(0, 0);
		message.add(pLabel);
		message.alignAll();

		JOptionPane ppane = new JOptionPane(message, JOptionPane.PLAIN_MESSAGE, JOptionPane.DEFAULT_OPTION, null,
				new Component[] { jpb });

		win.setContentPane(ppane);
		win.pack();
		win.setResizable(false);
		SwingUtils.centerWindow(win);
		win.setVisible(true);
		win.toFront();
		win.setAlwaysOnTop(true);
		try {

			ZipInputStream zis = new ZipInputStream(ItemsWindow.class.getResourceAsStream("/resources/textures.jar"));
			ZipEntry ze;
			while ((ze = zis.getNextEntry()) != null) {
				jpb.setMaximum(jpb.getMaximum() + 1);
				zis.closeEntry();
			}
			zis.close();

			zis = new ZipInputStream(ItemsWindow.class.getResourceAsStream("/resources/textures.jar"));
			while ((ze = zis.getNextEntry()) != null) {
				if (!ze.getName().contains(".png"))
					continue;
				byte[] entryData = IOUtils.readFully(zis, false);
				String name = ze.getName().substring(ze.getName().lastIndexOf("/") + 1);
				name = name.substring(0, name.lastIndexOf("."));
				pLabel.setText(name);
				try {
					BufferedImage raw = ImageIO.read(new ByteArrayInputStream(entryData));
					BufferedImage target = new BufferedImage(32, 32, BufferedImage.TYPE_INT_ARGB);
					Graphics2D g2 = target.createGraphics();
					g2.drawImage(raw, 0, 0, 32, 32, null);
					itemTextures.put(name, target);
				} catch (Exception e2) {
					e2.printStackTrace();
				}
				jpb.setValue(jpb.getValue() + 1);
				zis.closeEntry();
			}
			zis.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
		win.dispose();
	}

	/**
	 * Clear loaded textures
	 * 
	 * @param main Main class instance
	 */
	public static void clearTextures(Main main) {
		itemTextures.clear();
	}

	/**
	 * Count loaded textures
	 * 
	 * @return how many textures are loaded
	 */
	public static int getTexturesSize() {
		return itemTextures.size();
	}

	/**
	 * Get an icon for armor placeholder
	 * 
	 * @param index slot index
	 * @return placeholder image
	 */
	private BufferedImage getPlaceholderIcon(int index) {
		switch (index) {
			case 5: {
				return itemTextures.get("empty_armor_slot_helmet");
			}
			case 6: {
				return itemTextures.get("empty_armor_slot_chestplate");
			}
			case 7: {
				return itemTextures.get("empty_armor_slot_leggings");
			}
			case 8: {
				return itemTextures.get("empty_armor_slot_boots");
			}
			case 45: {
				return itemTextures.get("empty_armor_slot_shield");
			}
			default: {
				return null;
			}
		}
	}

	/**
	 * Put an item in specified slot
	 * 
	 * @param index slot to put item to
	 * @param item  item stack for this slot
	 */
	@SuppressWarnings("unchecked")
	public void putItem(int index, ItemStack item) {
		if (index >= items.length || index < 0)
			return;

		ItemInfo itemInfo = TranslationUtils.getItemForID(item.getId(), PacketFactory.getProtocolFor(registry));

		items[index] = item.getId() == 0 ? null : item;
		if (itemTextures.containsKey(itemInfo.getFileName())) {
			bts[index].setText(" ");
			bts[index].setIcon(new ImageIcon(itemTextures.get(itemInfo.getFileName())));
		} else {
			bts[index].setText(item.getId() == 0 ? " " : "" + item.getId());
			if (bts[index].getText().replace(" ", "").isEmpty()) {
				BufferedImage pimg = null;
				if (isInventory)
					pimg = getPlaceholderIcon(index);
				bts[index].setIcon(pimg == null ? null : new ImageIcon(pimg));
			} else
				bts[index].setIcon(null);
		}
		JButton btn = bts[index];

		for (MouseListener ml : btn.getMouseListeners())
			if (ml instanceof TooltipMouseListener) {
				ml.mouseExited(new MouseEvent(btn, 0, System.currentTimeMillis(), 0, 0, 0, 0, 0, 0, false, 0));
				btn.removeMouseListener(ml);
			}
		if (item.getId() == 0)
			return;
		String label = "\u00A7f" + itemInfo.getName();
		try {
			if (item.getNbt() != null) {
				CompoundMap map = (CompoundMap) item.getNbt().getValue();
				if (map.containsKey("display")) {
					map = (CompoundMap) map.get("display").getValue();
					if (map.containsKey("Name"))
						label = "\u00A7f" + ChatMessages.parse((String) map.get("Name").getValue());
					if (map.containsKey("Lore"))
						for (StringTag lore : (List<StringTag>) map.get("Lore").getValue())
							label += "\r\n\u00A75" + ChatMessages.parse(lore.getValue());
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}

		btn.addMouseListener(new TooltipMouseListener(label, btn));
		btn.revalidate();
		if (dialog != null) {
			for (JButton b : bts) {
				b.revalidate();
				b.repaint();
			}

			dialog.revalidate();
			dialog.pack();
			SwingUtils.centerWindow(dialog);
		}
	}

	private class TooltipMouseListener extends MouseAdapter {
		private final MinecraftToolTip tp;
		private final JButton btn;

		private TooltipMouseListener(String label, JButton btn) {
			tp = new MinecraftToolTip(label);
			this.btn = btn;
		}

		@Override
		public void mouseExited(MouseEvent e) {
			tp.hide();
		}

		@Override
		public void mouseEntered(MouseEvent e) {
			tp.show(btn.getX() + (int) (btn.getWidth() * 1.1) + dialog.getX(),
					btn.getY() + btn.getHeight() + dialog.getY());
			dialog.requestFocus();
		}
	}

	/**
	 * Get inventory's title
	 * 
	 * @return inventory's title
	 */
	public String getTitle() {
		return title;
	}

	/**
	 * Get inventory's size
	 * 
	 * @return inventory size
	 */
	public int getSize() {
		return size;
	}

	/**
	 * Closes this inventory
	 */
	public void closeWindow() {
		closeWindow(false);
	}

	/**
	 * Closes this inventory
	 * 
	 * @param silently if set to true, no window close packet will be sent when
	 *                 closing window
	 */
	public void closeWindow(boolean silently) {
		closeSilently = true;
		if (dialog != null)
			dialog.dispose();
		dialog = null;
	}

	/**
	 * Completes a pending transaction in this inventory
	 * 
	 * @param actionID transaction ID
	 */
	public void finishTransaction(short actionID) {
		if (pendingTransactions.containsKey(actionID)) {
			pendingTransactions.get(actionID).run();
			pendingTransactions.remove(actionID);
		}
	}

	/**
	 * Cancels pending transaction
	 * 
	 * @param actionID transaction ID
	 */
	public void cancelTransaction(short actionID) {
		if (pendingTransactions.containsKey(actionID))
			pendingTransactions.remove(actionID);
	}

	/**
	 * Opens this window to user
	 * 
	 * @param parent           parent component
	 * @param sendClosePackets send a packet when this window is closed
	 */
	public void openWindow(Window parent, final boolean sendClosePackets) {
		if (isShowing)
			return;
		dialog = new JDialog(parent);
		dialog.setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
		dialog.addWindowListener(new WindowAdapter() {
			final boolean sendClosePacketsL = sendClosePackets;

			@Override
			public void windowClosed(WindowEvent e) {
				for (JButton bt : bts)
					for (MouseListener ml : bt.getMouseListeners())
						ml.mouseExited(new MouseEvent(bt, 0, System.currentTimeMillis(), 0, 0, 0, 0, 0, 1, false, 0));
				if (sendClosePacketsL && !closeSilently)
					try {
						client.sendPacket(PacketFactory.constructPacket(registry, "ClientCloseWindowPacket", windowID));
					} catch (Exception e2) {
						return;
					}
				else
					closeSilently = false;
				isShowing = false;
			}
		});

		dialog.setTitle(title);

		JPanel panel = new JPanel(new GridLayout((size / 9) - (isInventory ? 5 : 0), 9));
		boolean hasShield = PacketFactory.getProtocolFor(registry) >= 107;

		for (int x = isInventory ? 5 : 0; x < bts.length - (isInventory ? 1 : 0); x++) {
			JButton bt = bts[x];
			panel.add(bt);
			if (x == 8 && isInventory) {
				if (hasShield) {
					panel.add(new JLabel(" "));
					panel.add(bts[45]);
				}
				for (int y = 0; y < (hasShield ? 3 : 5); y++)
					panel.add(new JLabel(" "));
			}
		}

		dialog.setContentPane(panel);
		dialog.pack();
		dialog.setResizable(false);
		SwingUtils.centerWindow(dialog);
		dialog.setVisible(true);
		isShowing = true;
	}
}
