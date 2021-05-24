package net.defekt.mc.chatclient.ui.swing;

import java.awt.Component;

import javax.swing.BoxLayout;
import javax.swing.JComponent;
import javax.swing.JPanel;

/**
 * Simple {@link JPanel} extension with vertical {@link BoxLayout}
 * 
 * @author Defective4
 *
 */
@SuppressWarnings("serial")
public class JVBoxPanel extends JPanel {
	/**
	 * Default constructor
	 */
	public JVBoxPanel() {
		setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));
	}

	/**
	 * Aligns all contained components to the left
	 */
	public void alignAll() {
		for (Component ct : getComponents()) {
			if (ct instanceof JComponent) {
				JComponent jct = (JComponent) ct;
				jct.setAlignmentX(Component.LEFT_ALIGNMENT);
				jct.setOpaque(false);
			}
		}
	}
}
