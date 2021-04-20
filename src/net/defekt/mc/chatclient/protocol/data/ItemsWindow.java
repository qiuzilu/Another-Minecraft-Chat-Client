package net.defekt.mc.chatclient.protocol.data;

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
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JProgressBar;

import com.flowpowered.nbt.CompoundMap;
import com.flowpowered.nbt.StringTag;

import net.defekt.mc.chatclient.protocol.MinecraftClient;
import net.defekt.mc.chatclient.protocol.io.IOUtils;
import net.defekt.mc.chatclient.protocol.packets.PacketFactory;
import net.defekt.mc.chatclient.protocol.packets.PacketRegistry;
import net.defekt.mc.chatclient.ui.Main;
import net.defekt.mc.chatclient.ui.swing.MinecraftToolTip;
import net.defekt.mc.chatclient.ui.swing.SwingUtils;

public class ItemsWindow {

	private static final Map<String, BufferedImage> itemTextures = new HashMap<String, BufferedImage>();

	private final String title;
	private final int size;
	private final int windowID;
	private final MinecraftClient client;
	private final PacketRegistry registry;

	private final JButton[] bts;
	private final ItemStack[] items;
	private JDialog dialog = null;

	private final Random rand = new Random();

	public ItemsWindow(String title, int size, int windowID, MinecraftClient client, PacketRegistry registry) {
		this.title = title;
		this.size = size;
		this.windowID = windowID;
		this.client = client;
		this.registry = registry;
		bts = new JButton[size];
		items = new ItemStack[size];
		for (int x = 0; x < size; x++) {
			final int xIndexLocal = x;
			items[x] = null;
			bts[x] = new JButton(" ");
			bts[x].addActionListener(new ActionListener() {
				final int xIndex = xIndexLocal;

				@Override
				public void actionPerformed(ActionEvent e) {
					try {
						client.sendPacket(PacketFactory.constructPacket(registry, "ClientWindowClickPacket", windowID,
								(short) xIndex, (byte) 0, (short) rand.nextInt(Short.MAX_VALUE), 0,
								new ItemStack((short) -1, -1, (short) -1, null)));
					} catch (IOException e1) {
						e1.printStackTrace();
					}
				}
			});
		}
	}

	public static void initTextures(Main main, JLabel pLabel, JProgressBar jpb) {
		try {
			jpb.setIndeterminate(false);
			ZipInputStream zis = new ZipInputStream(ItemsWindow.class.getResourceAsStream("/resources/textures.jar"));
			ZipEntry ze;
			while ((ze = zis.getNextEntry()) != null) {
				jpb.setMaximum(jpb.getMaximum()+1);
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
	}

	@SuppressWarnings("unchecked")
	public void putItem(int index, ItemStack item) {
		if (index >= items.length)
			return;

		ItemInfo itemInfo = TranslationUtils.getItemForID(item.getId(), PacketFactory.getProtocolFor(registry));

		items[index] = item.getId() == 0 ? null : item;
		if (itemTextures.containsKey(itemInfo.getFileName())) {
			bts[index].setText(" ");
			bts[index].setIcon(new ImageIcon(itemTextures.get(itemInfo.getFileName())));
		} else {
			bts[index].setText(item.getId() == 0 ? " " : "" + item.getId());
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
						label = "\u00A7f" + ChatMessage.parse((String) map.get("Name").getValue());
					if (map.containsKey("Lore")) {
						for (StringTag lore : (List<StringTag>) map.get("Lore").getValue()) {
							label += "\r\n\u00A75" + ChatMessage.parse(lore.getValue());
						}
					}
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}

		btn.addMouseListener(new TooltipMouseListener(label, btn));
		if (dialog != null) {
			for (JButton b : bts) {
				b.revalidate();
				b.repaint();
			}

			dialog.revalidate();
			dialog.pack();
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

	public String getTitle() {
		return title;
	}

	public int getSize() {
		return size;
	}

	public void closeWindow() {
		if (dialog != null)
			dialog.dispose();
		dialog = null;
	}

	public void openWindow(Window parent) {
		dialog = new JDialog(parent);
		dialog.setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);
		dialog.addWindowListener(new WindowAdapter() {
			@Override
			public void windowClosed(WindowEvent e) {
				for (JButton bt : bts)
					for (MouseListener ml : bt.getMouseListeners())
						ml.mouseExited(new MouseEvent(bt, 0, System.currentTimeMillis(), 0, 0, 0, 0, 0, 1, false, 0));
				try {
					client.sendPacket(PacketFactory.constructPacket(registry, "ClientCloseWindowPacket", windowID));
				} catch (Exception e2) {
				}
			}
		});

//		dialog.setModal(true);
		dialog.setTitle(title);

		JPanel panel = new JPanel(new GridLayout(size / 9, 9));
		for (JButton bt : bts)
			panel.add(bt);

		dialog.setContentPane(panel);
		dialog.pack();
		dialog.setResizable(false);
		SwingUtils.centerWindow(dialog);
		dialog.setVisible(true);
	}
}
