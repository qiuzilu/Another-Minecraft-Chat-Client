package net.defekt.mc.chatclient.ui.swing;

import java.awt.Component;

import javax.swing.BoxLayout;
import javax.swing.JComponent;
import javax.swing.JPanel;

@SuppressWarnings("serial")
public class JVBoxPanel extends JPanel {
	public JVBoxPanel() {
		setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));
	}

	public void alignAll() {
		for (Component ct : getComponents()) {
			if (ct instanceof JComponent) {
				JComponent jct = (JComponent) ct;
				jct.setAlignmentX(JComponent.LEFT_ALIGNMENT);
				jct.setOpaque(false);
			}
		}
	}
}
