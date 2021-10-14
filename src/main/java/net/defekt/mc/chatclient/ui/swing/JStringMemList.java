package net.defekt.mc.chatclient.ui.swing;

import java.util.ArrayList;
import java.util.List;

public class JStringMemList extends JMemList<String> {
	private static final long serialVersionUID = 1L;

	public void addString(String value) {
		List<String> list = new ArrayList<String>();
		for (String s : getListData() == null ? new String[0] : getListData())
			list.add(s);
		list.add(value);
		setListData((String[]) list.toArray(new String[list.size()]));
	}

	public void removeString(int index) {
		if (index != -1) {
			List<String> list = new ArrayList<String>();
			for (String s : getListData() == null ? new String[0] : getListData())
				list.add(s);
			list.remove(index);
			setListData((String[]) list.toArray(new String[list.size()]));
			setSelectedIndex(index >= list.size() ? index - 1 : index);
		}
	}
}