package net.defekt.mc.chatclient.ui.swing;

import java.awt.Component;
import java.util.ArrayList;
import java.util.List;

import javax.swing.DefaultListCellRenderer;
import javax.swing.JLabel;
import javax.swing.JList;

import net.defekt.mc.chatclient.ui.AutoResponseRule;

public class JAutoResponseList extends JMemList<AutoResponseRule> {
	private static final long serialVersionUID = 1L;

	private class ResponseRuleCellRenderer extends DefaultListCellRenderer {
		private static final long serialVersionUID = 1L;

		public Component getListCellRendererComponent(JList<? extends Object> list, Object value, int index,
				boolean isSelected, boolean cellHasFocus) {

			JLabel ct = (JLabel) super.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus);
			AutoResponseRule rule = (AutoResponseRule) value;
			ct.setText(rule.getName());

			return ct;
		}
	}

	public JAutoResponseList() {
		setCellRenderer(new ResponseRuleCellRenderer());
	}

	public void addRule(AutoResponseRule rule) {
		List<AutoResponseRule> list = new ArrayList<AutoResponseRule>();
		if (getListData() != null)
			for (AutoResponseRule rl : getListData())
				list.add(rl);
		list.add(rule);
		setListData((AutoResponseRule[]) list.toArray(new AutoResponseRule[list.size()]));
	}

	public void removeRule(int index) {
		if (index <= -1 || getListData() == null)
			return;
		List<AutoResponseRule> list = new ArrayList<AutoResponseRule>();
		AutoResponseRule[] d = getListData();
		for (int x = 0; x < d.length; x++)
			if (x != index)
				list.add(d[x]);
		setListData((AutoResponseRule[]) list.toArray(new AutoResponseRule[list.size()]));
		setSelectedIndex(index >= list.size() ? index - 1 : index);
	}

	public void setRule(AutoResponseRule rule, int index) {
		if (getListData() == null)
			return;
		AutoResponseRule[] rs = getListData();
		rs[index] = rule;
		setListData(rs);
	}
}
