package net.defekt.mc.chatclient.ui.swing;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Window;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.List;

import javax.swing.JButton;
import javax.swing.JColorChooser;

import net.defekt.mc.chatclient.ui.Messages;

/**
 * A special button allowing you to choose a color when clicking on it.<br>
 * If you click on button, it will bring up a color chooser dialog.
 * 
 * @author Defective4
 *
 */
@SuppressWarnings("serial")
public class JColorChooserButton extends JButton {

	private Color currentColor = Color.black;
	private List<ColorChangeListener> colorChangeListeners = new ArrayList<JColorChooserButton.ColorChangeListener>();

	/**
	 * Adds a color change listener, fired when user is done choosing a color.
	 * 
	 * @param listener color change listener
	 */
	public void addColorChangeListener(ColorChangeListener listener) {
		colorChangeListeners.add(listener);
	}

	/**
	 * Removes a color change listener
	 * 
	 * @param listener color change listener
	 */
	public void removeColorChangeListener(ColorChangeListener listener) {
		colorChangeListeners.remove(listener);
	}

	/**
	 * Initializes color chooser button with black color and a null parent.
	 */
	public JColorChooserButton() {
		this(Color.black, null);
	}

	/**
	 * Initializes color chooser button with black color and specified parent.
	 * 
	 * @param parent dialog opened with this button will belong to this parent.
	 */
	public JColorChooserButton(Window parent) {
		this(Color.black, parent);
	}

	/**
	 * Initializes color chooser button with specified color and null parent.
	 * 
	 * @param initialColor initial color
	 */
	public JColorChooserButton(Color initialColor) {
		this(initialColor, null);
	}

	/**
	 * Initializes color chooser button with specified color and null parent.
	 * 
	 * @param initialColor initial color as HEX.
	 */
	public JColorChooserButton(String initialColor) {
		this(new Color(Integer.parseInt(initialColor, 16)), null);
	}

	/**
	 * Initializes color chooser button with specified color and parent.
	 * 
	 * @param initialColor initial color as HEX.
	 * @param parent       dialog opened with this button will belong to this
	 *                     parent.
	 */
	public JColorChooserButton(String initialColor, Window parent) {
		this(new Color(Integer.parseInt(initialColor, 16)), parent);
	}

	/**
	 * Initializes color chooser button with specified color nad parent.
	 * 
	 * @param initialColor initial color.
	 * @param parent       dialog opened with this button will belong to this
	 *                     parent.
	 */
	public JColorChooserButton(Color initialColor, Window parent) {
		setText(" "); //$NON-NLS-1$
		currentColor = initialColor;
		addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				{
					Color tc = JColorChooser.showDialog(parent,
							Messages.getString("JColorChooserButton.colorChooserDialogTitle"), //$NON-NLS-1$
							JColorChooserButton.this.currentColor);
					if (tc == null)
						return;
					currentColor = tc;
					for (ColorChangeListener ccl : colorChangeListeners)
						ccl.colorChanged(currentColor);
				}
			}
		});
	}

	/**
	 * Get current selected color
	 * 
	 * @return selected color
	 */
	public Color getColor() {
		return currentColor;
	}

	/**
	 * Set button's current color
	 * 
	 * @param c color
	 */
	public void setColor(Color c) {
		currentColor = c;
		repaint();
	}

	@Override
	public void paintComponent(Graphics g) {
		super.paintComponent(g);
		Graphics2D g2 = (Graphics2D) g;
		int w = getWidth() - 10;
		int h = getHeight() - 10;
		g2.setColor(currentColor);
		g2.fillRect(5, 5, w, h);
	}

	/**
	 * A listener for receiving color updates from button
	 * 
	 * @author Defective4
	 *
	 */
	@FunctionalInterface
	public static interface ColorChangeListener {
		/**
		 * Invoked when button's color was changed by user.<br>
		 * It does NOT fire when the color is changed by setColor()
		 * 
		 * @param color button's current color
		 */
		public void colorChanged(Color color);
	}
}
