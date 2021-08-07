package net.defekt.mc.chatclient.ui.swing;

import java.awt.Color;
import java.awt.Component;
import java.awt.Desktop;
import java.awt.Desktop.Action;
import java.awt.Dimension;
import java.awt.Toolkit;
import java.awt.Window;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.List;

import javax.swing.Box;
import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JScrollPane;
import javax.swing.JSpinner;
import javax.swing.JSpinner.DefaultEditor;
import javax.swing.JTextArea;
import javax.swing.JTextPane;
import javax.swing.SwingConstants;
import javax.swing.SwingUtilities;
import javax.swing.UIManager;
import javax.swing.UnsupportedLookAndFeelException;
import javax.swing.text.Style;
import javax.swing.text.StyleConstants;
import javax.swing.text.StyleContext;
import javax.swing.text.StyledDocument;

import net.defekt.mc.chatclient.protocol.data.ChatColor;
import net.defekt.mc.chatclient.ui.Messages;

/**
 * Various UI utilities used internally
 * 
 * @see ChatColor
 * @author Defective4
 *
 */
@SuppressWarnings("serial")
public class SwingUtils {
	/**
	 * Set system look and feel.<br>
	 * It does work fine on Windows, but as far as I know it does NOT work with GTK.
	 */
	public static void setNativeLook() {
		try {
			UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
		} catch (ClassNotFoundException | InstantiationException | IllegalAccessException
				| UnsupportedLookAndFeelException e) {
			e.printStackTrace();
		}
	}

	/**
	 * Aligns spinner's text field to the left side
	 * 
	 * @param spinner spinner to align
	 */
	public static void alignSpinner(JSpinner spinner) {
		DefaultEditor editor = (DefaultEditor) spinner.getEditor();
		editor.getTextField().setHorizontalAlignment(SwingConstants.LEFT);
	}

	/**
	 * User's screen size
	 */
	public static final Dimension sSize = Toolkit.getDefaultToolkit().getScreenSize();

	/**
	 * Sets window's position to center
	 * 
	 * @param win window to center
	 */
	public static void centerWindow(Window win) {
		int x = (sSize.width - win.getWidth()) / 2;
		int y = (sSize.height - win.getHeight()) / 2;

		win.setLocation(x, y);
	}

	/**
	 * Appends colored text to text pane.<br>
	 * It follows Minecraft colors defined in {@link ChatColor}
	 * 
	 * @param text text to append.<br>
	 *             It follows the same rules defined in Minecraft, for example,
	 *             §4Hello §9World would be
	 *             "<font style="color: aa0000;">Hello</font>
	 *             <font style="color: 5555ff;">World</font>""
	 * @param pane pane to append text to
	 */
	public static void appendColoredText(String text, JTextPane pane) {
		StyledDocument doc = pane.getStyledDocument();
		StyleContext ctx = new StyleContext();
		Style style = ctx.addStyle("style", null);

		String[] split = text.split("\u00A7");
		for (String part : split)
			try {
				if (text.startsWith(part))
					doc.insertString(doc.getLength(), part, null);
				else {
					String code = part.substring(0, 1);
					Color c;
					boolean isHex = false;
					if (code.equals("#") && part.length() > 7)
						try {
							String hex = part.substring(1, 7);
							int rgb = Integer.parseInt(hex, 16);
							c = new Color(rgb);
							isHex = true;
						} catch (Exception e) {
							c = ChatColor.translateColorCode(code);
						}
					else
						c = ChatColor.translateColorCode(code);
					StyleConstants.setForeground(style, c);

					String rest = part.substring(isHex ? 7 : 1);
					doc.insertString(doc.getLength(), rest, style);
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
	}

	/**
	 * Shows error dialog, allowing to display exception details
	 * 
	 * @param parent  parent of this dialog
	 * @param title   dialog title
	 * @param ex      exception to display details of
	 * @param message custom dialog message
	 */
	public static void showErrorDialog(Window parent, String title, Exception ex, String message) {
		final JDialog errDial = new JDialog(parent);
		errDial.setModal(true);
		errDial.setTitle(title);

		JOptionPane jop = new JOptionPane(message, JOptionPane.ERROR_MESSAGE, JOptionPane.DEFAULT_OPTION, null,
				new Object[] { new JButton(Messages.getString("SwingUtils.errorDialogOptionOk")) {
					{
						addActionListener(ev -> {
							errDial.dispose();
						});
					}
				}, new JButton(Messages.getString("SwingUtils.errorDialogOptionDetails")) {
					{
						addActionListener(ev -> {
							showExceptionDetails(parent, ex);
						});
					}
				} });

		errDial.setContentPane(jop);
		errDial.pack();
		SwingUtils.centerWindow(errDial);
		errDial.setVisible(true);
	}

	/**
	 * Get color as RGB in HEX
	 * 
	 * @param c color to convert
	 * @return HEX string
	 */
	public static String getHexRGB(Color c) {
		return Integer.toHexString(c.getRGB()).substring(2);
	}

	/**
	 * Change each of RGB values by index
	 * 
	 * @param c     color to modify
	 * @param index color modifier
	 * @return modified color
	 */
	public static Color brighten(Color c, int index) {
		int r = c.getRed();
		int g = c.getGreen();
		int b = c.getBlue();
		for (int x = 0; x < Math.abs(index); x++)
			if (index < 0) {
				if (r > 1)
					r--;
				if (g > 1)
					g--;
				if (b > 1)
					b--;
			} else {
				if (r < 255)
					r++;
				if (g < 255)
					g++;
				if (b < 255)
					b++;
			}
		return new Color(r, g, b);
	}

	/**
	 * Creates an version information dialog.<br>
	 * Used when there is an update available
	 * 
	 * @param oldVersion  current version of application
	 * @param newVersion  new version
	 * @param difference  version difference
	 * @param versionType version difference type.<br>
	 *                    Options used:<br>
	 *                    "major"<br>
	 *                    "minor"<br>
	 *                    "fix"
	 * @param changesList list of changes
	 */
	public static void showVersionDialog(String oldVersion, String newVersion, int difference, String versionType,
			List<String> changesList) {
		JFrame win = new JFrame();
		win.setTitle("New version available!");
		win.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

		Box message = Box.createVerticalBox();
		message.add(new JLabel("<html><font style=\"font-weight:bold;\">A new update is available!</font><br><br>"
				+ "An update is available to download!<br><br>" + "Current version: <font style=\"font-weight:bold;\">"
				+ oldVersion + "</font><br>" + "New version: <font style=\"font-weight:bold;\">" + newVersion
				+ "</font><br><br>" + "You are <font style=\"font-weight:bold;\">" + Integer.toString(difference)
				+ "</font> " + versionType + " versions behind!</html>"));

		for (Component ct : message.getComponents())
			if (ct instanceof JComponent)
				((JComponent) ct).setAlignmentX(Component.LEFT_ALIGNMENT);

		JButton ok = new JButton(Messages.getString("SwingUtils.updateDialogOptionOk"));
		ok.addActionListener(ev -> {
			synchronized (win) {
				win.notify();
			}
		});

		JButton changes = new JButton(Messages.getString("SwingUtils.updateDialogOptionShowChanges"));
		changes.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {

				StringBuilder msg = new StringBuilder();
				for (String line : changesList) {
					if (line.startsWith("["))
						msg.append("<font style=\"font-weight: bold;\">");
					msg.append(line).append("<br>");
					if (line.startsWith("["))
						msg.append("</font>");
				}

				JLabel message = new JLabel("<html>" + msg.append("</html>").toString());
				JScrollPane jsp = new JScrollPane(message);
				jsp.setPreferredSize(win.getSize());
				JOptionPane.showOptionDialog(win, jsp,
						Messages.getString("SwingUtils.updateDialogChangesTitle") + newVersion,
						JOptionPane.DEFAULT_OPTION, JOptionPane.PLAIN_MESSAGE, null,
						new String[] { Messages.getString("SwingUtils.updateDialogChangesOptionOk") }, 0);
			}
		});
		JButton update = new JButton(Messages.getString("SwingUtils.updateDialogOptionUpdate"));
		update.setEnabled(Desktop.isDesktopSupported() && Desktop.getDesktop().isSupported(Action.BROWSE));
		update.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				try {
					Desktop.getDesktop()
							.browse(new URI("https://github.com/Defective4/Another-Minecraft-Chat-Client/releases"));
				} catch (IOException | URISyntaxException e1) {
					e1.printStackTrace();
				}
			}
		});

		JButton exit = new JButton(Messages.getString("SwingUtils.updateDialogOptionExit"));
		exit.addActionListener(ev -> {
			System.exit(0);
		});

		JOptionPane pane = new JOptionPane(message, JOptionPane.INFORMATION_MESSAGE, JOptionPane.DEFAULT_OPTION, null,
				new Object[] { ok, changes, update, exit });

		win.setContentPane(pane);
		win.pack();
		win.setResizable(false);
		centerWindow(win);
		win.setVisible(true);
		win.toFront();

		synchronized (win) {
			try {
				win.wait();
			} catch (InterruptedException e) {
				e.printStackTrace();
			}

		}

		win.dispose();
	}

	private static void showExceptionDetails(Window parent, Exception ex) {

		Box box = Box.createVerticalBox();
		box.add(new JLabel(Messages.getString("SwingUtils.exceptionDetailsDialogLabel")));
		box.add(new JScrollPane(new JTextArea() {
			{
				append(ex.toString() + "\r\n");
				for (StackTraceElement ste : ex.getStackTrace())
					append(ste.toString() + "\r\n");
				setForeground(new Color(150, 0, 0));
				setFont(getFont().deriveFont(13f));
			}
		}) {
			{
				setPreferredSize(new Dimension((int) (sSize.getWidth() / 3), (int) (sSize.getHeight() / 2)));
			}
		});

		for (Component ct : box.getComponents()) {
			if (ct instanceof JComponent)
				((JComponent) ct).setAlignmentX(Component.LEFT_ALIGNMENT);
			if (ct instanceof JScrollPane)
				SwingUtilities.invokeLater(new Runnable() {

					@Override
					public void run() {
						((JScrollPane) ct).getVerticalScrollBar().setValue(0);
					}
				});
		}

		JOptionPane.showOptionDialog(parent, box, Messages.getString("SwingUtils.exceptionDetailsDialogTitle"),
				JOptionPane.DEFAULT_OPTION, JOptionPane.PLAIN_MESSAGE, null,
				new Object[] { Messages.getString("SwingUtils.exceptionDetailsDialogOptionOk") }, 0);
	}
}
