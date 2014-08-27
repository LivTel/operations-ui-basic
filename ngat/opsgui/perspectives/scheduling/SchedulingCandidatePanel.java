package ngat.opsgui.perspectives.scheduling;

import java.awt.Point;
import java.awt.BorderLayout;
import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;
import java.awt.event.MouseEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseListener;
import java.util.Iterator;
import java.util.List;
import java.util.Vector;

import javax.swing.JOptionPane;
import javax.swing.JPopupMenu;
import javax.swing.JMenuItem;
import javax.swing.JScrollPane;
import javax.swing.ListSelectionModel;
import javax.swing.ScrollPaneConstants;
import ngat.phase2.ISequenceComponent;
import ngat.rcs.sciops.DisplaySeq;
import ngat.sms.GroupItem;
import ngat.sms.ScoreMetric;
import ngat.sms.ScoreMetricsSet;

/**
 * Displays candidates during a despatcher sweep.
 * 
 * @author eng
 * 
 */
public class SchedulingCandidatePanel extends SchedulingSweepDataDisplayPanel {

	private CandidateTableModel candidateTableModel;

	private CandidateTable candidateTable;

	private CandidateTableRenderer renderer;

	private TagList tagList;

	private TagList auditList;
	
	private CandidatePopupMenu popup;

	/**
	 * @param sweeps
	 */
	public SchedulingCandidatePanel(List<SweepEntry> sweeps, TagList tagList,  TagList auditList) {
		super(sweeps);

		this.tagList = tagList;
		this.auditList = auditList;
		
		setLayout(new BorderLayout());

		candidateTableModel = new CandidateTableModel();

		renderer = new CandidateTableRenderer();
		candidateTable = new CandidateTable(candidateTableModel);

		JScrollPane jspSweep = new JScrollPane(candidateTable);
		jspSweep.setVerticalScrollBarPolicy(ScrollPaneConstants.VERTICAL_SCROLLBAR_AS_NEEDED);

		// Selection model - do we need this ?
		candidateTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
		candidateTable.setRowSelectionAllowed(true);
		ListSelectionModel clm = candidateTable.getSelectionModel();
		clm.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
		//clm.addListSelectionListener(new CandidateTableSelectionModel(candidateTableModel));
		//candidateTable.setSelectionModel(clm);

		int nc = candidateTable.getColumnModel().getColumnCount();
		for (int ic = 0; ic < nc; ic++) {
			candidateTable.getColumnModel().getColumn(ic).setCellRenderer(renderer);
		}

		popup = createPopupMenu(candidateTableModel, tagList);

		MouseListener popupListener = new PopupListener(popup);
		candidateTable.addMouseListener(popupListener);
		
		add(jspSweep, BorderLayout.CENTER);

	}

	private CandidatePopupMenu createPopupMenu(final CandidateTableModel candidateTableModel,
			final TagList tagList) {

		final CandidatePopupMenu popup = new CandidatePopupMenu();

		// Follow this group item
		JMenuItem followItem = new JMenuItem("Follow");
		followItem.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent ae) {

				// what row are we in.
				int irow = popup.getRow();

				// what CandiateRow is this
				CandidateRow row = candidateTableModel.getRow(irow);

				// what group is it
				GroupItem group = row.group;

				// add the group to the taglist
				tagList.tagGroup(group);
			}
		});
		popup.add(followItem);
		
		JMenuItem auditItem = new JMenuItem("Audit");
		auditItem.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent e) {	
				
				int irow = popup.getRow();

				// what CandiateRow is this
				CandidateRow row = candidateTableModel.getRow(irow);

				// what group is it
				GroupItem group = row.group;
				
				auditList.clearTags();
				auditList.tagGroup(group);
			}
		});
		popup.add(auditItem);
		
		JMenuItem metricsItem = new JMenuItem("Metrics...");
		metricsItem.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent e) {	
				
				int irow = popup.getRow();

				// what CandiateRow is this
				CandidateRow row = candidateTableModel.getRow(irow);

				// what group is it
				GroupItem group = row.group;
				
				ScoreMetricsSet metrics = row.metrics;
				
				StringBuffer b = new StringBuffer("Group: "+group.getName()+"\n");
				
				Iterator<ScoreMetric> im = metrics.listMetrics();
				while (im.hasNext()) {
					ScoreMetric metric = im.next();
					System.err.println("SCP: Displaying metric: "+metric.getMetricName()+" : "+metric.getMetricValue());
					b.append(String.format("%10.10s : %4.2f \n", metric.getMetricName(), metric.getMetricValue()));
					// MAYBE add the scoring weight..... metric.getWeight();
				}
				
				JOptionPane.showMessageDialog(null, b.toString(), "Group Metrics", JOptionPane.INFORMATION_MESSAGE);
				
			}
		});
		popup.add(metricsItem);
		
		JMenuItem sequenceItem = new JMenuItem("Show...");
		sequenceItem.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent e) {	
				
				int irow = popup.getRow();

				// what CandiateRow is this
				CandidateRow row = candidateTableModel.getRow(irow);

				// what group is it
				GroupItem group = row.group;
				ISequenceComponent seq = group.getSequence();
				
				StringBuffer b = new StringBuffer("Group: "+group.getName()+"\n");
				
				DisplaySeq d = new DisplaySeq();
				b.append(DisplaySeq.display(0, seq));
				
				JOptionPane.showMessageDialog(null, b.toString(), "Group Observation Sequence", JOptionPane.INFORMATION_MESSAGE);
				
			}
		});
		popup.add(sequenceItem);
		
		return popup;

	}

	@Override
	public void displaySweep(int d, int l, boolean s) {
				
		if (s) {

			SweepEntry entry = sweeps.get(l);
			if (entry != null) {

				candidateTableModel.clearTable();

				List<CandidateRow> rows = new Vector<CandidateRow>();

				List<CandidateEntry> candidates = entry.getCandidates();
				if (candidates != null) {
					for (int is = 0; is < candidates.size(); is++) {
						CandidateEntry c = candidates.get(is);
						CandidateRow crow = new CandidateRow(c);
						rows.add(crow);
					}
				}

				// TWEAKED Collections.sort(rows);

				List<Integer> hrows = new Vector<Integer>();

				for (int ir = 0; ir < rows.size(); ir++) {
					CandidateRow row = rows.get(ir);
					row.rank = ir + 1;
					// we can add it to the trows list if its tagged
					if (tagList.isTagged(row.group))
						hrows.add(ir);
				}
			
				renderer.highlightRows(hrows);

				candidateTableModel.setRows(rows);

			}

		} else {

			// if we are not already showing d then show it.

			SweepEntry entry = sweeps.get(d);
			if (entry != null) {

				candidateTableModel.clearTable();

				List<CandidateRow> rows = new Vector<CandidateRow>();

				List<CandidateEntry> candidates = entry.getCandidates();
				if (candidates != null) {
					for (int is = 0; is < candidates.size(); is++) {
						CandidateEntry c = candidates.get(is);
						CandidateRow crow = new CandidateRow(c);
						rows.add(crow);
					}
				}

				// TWEAKED Collections.sort(rows);

				List<Integer> hrows = new Vector<Integer>();
				for (int ir = 0; ir < rows.size(); ir++) {
					CandidateRow row = rows.get(ir);
					row.rank = ir + 1;
					// we can add it to the trows list if its tagged
					if (tagList.isTagged(row.group))
						hrows.add(ir);
				}
				// TODO check each row to see if its a tagged group
				renderer.highlightRows(hrows);

				candidateTableModel.setRows(rows);

			}

		}
	}

	private class CandidatePopupMenu extends JPopupMenu {

		int row = -1;

		public CandidatePopupMenu() {
			super();
		}

		public void setRow(int row) {
			this.row = row;
		}

		public int getRow() {
			return row;
		}
	}

	private class PopupListener extends MouseAdapter {

		private CandidatePopupMenu popup;

		PopupListener(CandidatePopupMenu popup) {
			this.popup = popup;
		}

		@Override
		public void mousePressed(MouseEvent e) {
			maybeShowPopup(e);
		}

		@Override
		public void mouseReleased(MouseEvent e) {
			maybeShowPopup(e);
		}

		private void maybeShowPopup(MouseEvent e) {
			if (e.isPopupTrigger()) {
				int x = e.getX();
				int y = e.getY();
				int row = candidateTable.rowAtPoint(new Point(x, y));
				popup.setRow(row);
				popup.show(e.getComponent(), x, y);
			}
		}
	}

	
	
}
