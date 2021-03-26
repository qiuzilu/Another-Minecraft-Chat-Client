package net.defekt.mc.chatclient.ui.swing;

import javax.swing.JList;

public class JMemList<K> extends JList<K> {
	private static final long serialVersionUID = 1L;

	private K[] listData = null;

	@Override
	public void setListData(K[] entries) {
		if (entries == null)
			return;
		super.setListData(entries);
		listData = entries;
	}

	public void setSilentListData(K[] entries) {
		listData = entries;
	}

	public K[] getListData() {
		return listData;
	}
}
