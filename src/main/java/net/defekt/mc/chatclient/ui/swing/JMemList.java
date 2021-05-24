package net.defekt.mc.chatclient.ui.swing;

import javax.swing.JList;

/**
 * An extension of {@link JList} allowing to get list data that was set before
 * 
 * @author Defective4
 *
 * @param <E> the type of the elements of this list
 */
public class JMemList<E> extends JList<E> {
	private static final long serialVersionUID = 1L;

	private E[] listData = null;

	@Override
	public void setListData(E[] entries) {
		if (entries == null)
			return;
		super.setListData(entries);
		listData = entries;
	}

	/**
	 * Get stored list data
	 * 
	 * @return list data contained in this object
	 */
	public E[] getListData() {
		return listData;
	}
}
