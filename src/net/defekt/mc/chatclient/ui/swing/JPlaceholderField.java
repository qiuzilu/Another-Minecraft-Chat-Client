package net.defekt.mc.chatclient.ui.swing;

import java.awt.Color;
import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;

import javax.swing.JTextField;

public class JPlaceholderField extends JTextField {
	private static final long serialVersionUID = 1L;

	private String placeholder = "";

	public JPlaceholderField(String placeholder) {
		this.placeholder = placeholder;

		addFocusListener(new FocusListener() {

			@Override
			public void focusLost(FocusEvent e) {
				if (getText().isEmpty()) {
					JPlaceholderField.super.setForeground(Color.gray);
					JPlaceholderField.super.setText(JPlaceholderField.this.placeholder);
				}
			}

			@Override
			public void focusGained(FocusEvent e) {
				if (getForeground().equals(Color.gray)) {
					JPlaceholderField.super.setForeground(fg);
					JPlaceholderField.super.setText("");
				}
			}
		});
		
		for (FocusListener listener : getFocusListeners()) {
			listener.focusLost(new FocusEvent(this, 1));
		}
	}

	@Override
	public String getText() {
		return getForeground().equals(Color.gray) ? "" : super.getText();
	}
	
	private Color fg = getForeground();
	
	@Override
	public void setForeground(Color color) {
		super.setForeground(color);
		this.fg = color;
	}
	
	@Override
	public void setText(String text) {
		super.setText(text);
		super.setForeground(fg);
		for (FocusListener listener : getFocusListeners()) {
			listener.focusLost(new FocusEvent(this, 1));
		}
	}
	
	@Override
	public void setEnabled(boolean enabled) {
		if(enabled)
			setText("");
		super.setEnabled(enabled);
	}
	
	public String getPlaceholder() {
		return placeholder;
	}

	public void setPlaceholder(String placeholder) {
		this.placeholder = placeholder;
	}
}
