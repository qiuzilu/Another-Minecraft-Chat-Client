package net.defekt.mc.chatclient.ui.swing;

import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.Toolkit;
import java.awt.Window;

import javax.swing.Box;
import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JTextPane;
import javax.swing.SwingUtilities;
import javax.swing.UIManager;
import javax.swing.UIManager.LookAndFeelInfo;
import javax.swing.UnsupportedLookAndFeelException;
import javax.swing.text.Style;
import javax.swing.text.StyleConstants;
import javax.swing.text.StyleContext;
import javax.swing.text.StyledDocument;

import net.defekt.mc.chatclient.protocol.data.ChatColor;

@SuppressWarnings("serial")
public class SwingUtils {
	public static void setNativeLook() {
		LookAndFeelInfo[] infs = UIManager.getInstalledLookAndFeels();
		try {
			UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
		} catch (ClassNotFoundException | InstantiationException | IllegalAccessException
				| UnsupportedLookAndFeelException e) {
			e.printStackTrace();
		}
		for (LookAndFeelInfo inf : infs) {
			if (inf.getName().toLowerCase().contains("gtk") || inf.getName().toLowerCase().contains("ux")
					|| inf.getName().toLowerCase().contains("ix"))
				try {
					UIManager.setLookAndFeel(inf.getClassName());
				} catch (ClassNotFoundException | InstantiationException | IllegalAccessException
						| UnsupportedLookAndFeelException e) {
					e.printStackTrace();
				}
		}
	}

	public static final Dimension sSize = Toolkit.getDefaultToolkit().getScreenSize();

	public static void centerWindow(Window win) {
		int x = (sSize.width - win.getWidth()) / 2;
		int y = (sSize.height - win.getHeight()) / 2;

		win.setLocation(x, y);
	}

	public static void appendColoredText(String text, JTextPane pane) {
		StyledDocument doc = pane.getStyledDocument();
		StyleContext ctx = new StyleContext();
		Style style = ctx.addStyle("style", null);

		String[] split = text.split("\u00A7");
		for (String part : split) {
			try {
				if (text.startsWith(part))
					doc.insertString(doc.getLength(), part, null);
				else {
					String code = part.substring(0, 1);
					Color c = ChatColor.translateColorCode(code);
					StyleConstants.setForeground(style, c);

					String rest = part.substring(1);
					doc.insertString(doc.getLength(), rest, style);
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}

	public static void showErrorDialog(Window parent, String title, Exception ex, String message) {
		final JDialog errDial = new JDialog(parent);
		errDial.setModal(true);
		errDial.setTitle(title);

		JOptionPane jop = new JOptionPane(message, JOptionPane.ERROR_MESSAGE, JOptionPane.DEFAULT_OPTION, null,
				new Object[] { new JButton("Ok") {
					{
						addActionListener(ev -> {
							errDial.dispose();
						});
					}
				}, new JButton("Details") {
					{
						addActionListener(ev -> {
							showExceptionDetails(parent, ex);
						});
					}
				}
				});

		errDial.setContentPane(jop);
		errDial.pack();
		SwingUtils.centerWindow(errDial);
		errDial.setVisible(true);
	}

	private static void showExceptionDetails(Window parent, Exception ex) {

		Box box = Box.createVerticalBox();
		box.add(new JLabel("Exception details:"));
		box.add(new JScrollPane(new JTextArea() {
			{
				append(ex.toString() + "\r\n");
				for (StackTraceElement ste : ex.getStackTrace()) {
					append(ste.toString() + "\r\n");
				}
				setForeground(new Color(150, 0, 0));
				setFont(getFont().deriveFont(13f));
			}
		}) {
			{
				setPreferredSize(new Dimension((int) (sSize.getWidth() / 3), (int) (sSize.getHeight() / 2)));
			}
		});

		for (Component ct : box.getComponents()) {
			if (ct instanceof JComponent) {
				((JComponent) ct).setAlignmentX(JComponent.LEFT_ALIGNMENT);
			}
			if (ct instanceof JScrollPane)
				SwingUtilities.invokeLater(new Runnable() {

					@Override
					public void run() {
						((JScrollPane) ct).getVerticalScrollBar().setValue(0);
					}
				});
		}

		JOptionPane.showOptionDialog(parent, box, "Exception details", JOptionPane.DEFAULT_OPTION,
				JOptionPane.PLAIN_MESSAGE, null, new Object[] { "Ok"
				}, 0);
	}
}
