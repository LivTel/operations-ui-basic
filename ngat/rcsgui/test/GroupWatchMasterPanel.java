/**
 * 
 */
package ngat.rcsgui.test;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Vector;

import javax.swing.DefaultListModel;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextField;
import javax.swing.ListCellRenderer;
import javax.swing.ListSelectionModel;
import javax.swing.ScrollPaneConstants;

import ngat.phase2.XGroup;
import ngat.sms.GroupItem;

/**
 * @author eng
 * 
 */
public class GroupWatchMasterPanel extends JPanel {

	/** Displays the list of watched groups. */
	JList watchList;

	/** Watch list data. */
	DefaultListModel watchListModel;

	/** Displays the watch history of a specific group. */
	JList groupHistoryList;

	/** History list data. */
	DefaultListModel groupHistoryListModel;

	/** Text field to enter a new watch group. */
	JTextField addWatchField;

	/** Watch display. */
	GroupWatchDisplayPanel display;

	Map<String, List<WatchEntry>> watchMap;

	/** The group we are specifically watching atm. */
	GroupItem selectedGroup;

	/**
	 * 
	 */
	public GroupWatchMasterPanel() {
		super(true);
		createPanel();
		watchMap = new HashMap<String, List<WatchEntry>>();
	}

	private void createPanel() {

		// watch list
		watchListModel = new DefaultListModel();
		watchList = new JList(watchListModel);

		watchList.setSelectionMode(ListSelectionModel.SINGLE_INTERVAL_SELECTION);
		watchList.setVisibleRowCount(15);

		JScrollPane jspWatch = new JScrollPane(watchList, ScrollPaneConstants.VERTICAL_SCROLLBAR_ALWAYS,
				ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);
		jspWatch.setPreferredSize(new Dimension(150, 300));
		WatchCellRenderer wcr = new WatchCellRenderer();
		watchList.setCellRenderer(new WatchCellRenderer());

		// history list
		groupHistoryListModel = new DefaultListModel();
		groupHistoryList = new JList(groupHistoryListModel);

		groupHistoryList.setSelectionMode(ListSelectionModel.SINGLE_INTERVAL_SELECTION);
		groupHistoryList.setVisibleRowCount(15);

		JScrollPane jspHistory = new JScrollPane(groupHistoryList, ScrollPaneConstants.VERTICAL_SCROLLBAR_ALWAYS,
				ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);
		jspHistory.setPreferredSize(new Dimension(150, 300));
		HistoryCellRenderer hcr = new HistoryCellRenderer();
		groupHistoryList.setCellRenderer(hcr);

		// watch add
		addWatchField = new JTextField(7);

		JButton addWatchBtn = new JButton("Add");
		addWatchBtn.addActionListener(new AddListener());

		JButton selectBtn = new JButton(">>");
		selectBtn.addActionListener(new SelectListener());

		long start = System.currentTimeMillis();
		long end = start + 15 * 3600 * 1000L;
		display = new GroupWatchDisplayPanel(null, start, end);

		// top left
		JPanel ltp = new JPanel(true);
		ltp.setLayout(new FlowLayout(FlowLayout.LEFT));
		ltp.add(jspWatch);
		ltp.add(selectBtn);
		ltp.add(jspHistory);

		// btm left
		JPanel lbp = new JPanel(true);
		lbp.setLayout(new FlowLayout(FlowLayout.LEFT));
		lbp.add(addWatchField);
		lbp.add(addWatchBtn);

		// all left
		JPanel left = new JPanel(true);
		left.setLayout(new BorderLayout());
		left.add(ltp, BorderLayout.CENTER);
		left.add(lbp, BorderLayout.SOUTH);

		JPanel all = new JPanel(true);
		all.setLayout(new BorderLayout());
		all.add(left, BorderLayout.CENTER);
		all.add(display, BorderLayout.EAST);

		add(all);
	}

	public void candidateUpdate(GroupItem group, boolean selected, double score) {
		List<WatchEntry> list = watchMap.get(group.getName());
		if (list == null)
			return;
		
		WatchEntry w = new WatchEntry(System.currentTimeMillis(), selected, score, null, score);
		list.add(w);
		// TODO should use ID
		if (selectedGroup != null && selectedGroup.getName().equals(group.getName())) {
			display.addWatchEntry(System.currentTimeMillis(), selected, score, null, score + Math.random()*0.5);
			groupHistoryListModel.addElement(w);
		}
	}

	public void addWatchGroup(GroupItem g) {
		watchListModel.addElement(g);
		// create a watch entry list for this group and map it to group name (or
		// better ID)
		List<WatchEntry> list = new Vector<WatchEntry>();
		// TODO should use GID
		watchMap.put(g.getName(), list);
	}

	private class AddListener implements ActionListener {

		@Override
		public void actionPerformed(ActionEvent e) {
			String addGroupText = addWatchField.getText();
			if (addGroupText != null && !addGroupText.equals("")) {
				XGroup g = new XGroup();
				g.setName(addGroupText);
				GroupItem gi = new GroupItem(g, null);
				addWatchGroup(gi);
				addWatchField.setText("");
			}

		}

	}

	private class SelectListener implements ActionListener {

		@Override
		public void actionPerformed(ActionEvent e) {

			GroupItem g = (GroupItem) watchList.getSelectedValue();
			System.err.println("You have selected " + g);
			selectedGroup = g;
			// populate the history list with content from group's watch list

			List<WatchEntry> list = watchMap.get(g.getName());
			groupHistoryListModel.clear();
			display.clearWatchEntries();
			for (int i = 0; i < list.size(); i++) {
				WatchEntry w = list.get(i);
				groupHistoryListModel.addElement(w);
				display.addWatchEntry(w.time, w.selected, w.score, null, w.score + Math.random()*0.5);
			}

		}

	}

	private class HistoryCellRenderer extends JLabel implements ListCellRenderer {

		@Override
		public Component getListCellRendererComponent(JList list, // the list
				Object value, // value to display
				int index, // cell index
				boolean isSelected, // is the cell selected
				boolean cellHasFocus) // does the cell have focus
		{
			WatchEntry w = (WatchEntry) value;
			if (w.selected) {
				setBackground(list.getSelectionBackground());
				setForeground(list.getSelectionForeground());
			} else {
				setBackground(list.getBackground());
				setForeground(list.getForeground());
			}
			setText(String.format("%tH:%tM  =  %2.2f", w.time, w.time, w.score));
			setEnabled(list.isEnabled());
			setFont(list.getFont());
			setOpaque(true);
			return this;
		}
	}

	private class WatchCellRenderer extends JLabel implements ListCellRenderer {

		@Override
		public Component getListCellRendererComponent(JList list, // the list
				Object value, // value to display
				int index, // cell index
				boolean isSelected, // is the cell selected
				boolean cellHasFocus) // does the cell have focus
		{

			GroupItem gi = (GroupItem) value;
			String name = gi.getName();
			setText(name);
			if (isSelected) {
				setBackground(list.getSelectionBackground());
				setForeground(list.getSelectionForeground());
			} else {
				setBackground(list.getBackground());
				setForeground(list.getForeground());
			}

			setEnabled(list.isEnabled());
			setFont(list.getFont());
			setOpaque(true);
			return this;
		}
	}

}
