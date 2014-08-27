/**
 * 
 */
package ngat.opsgui.perspectives.scheduling;

import java.awt.BorderLayout;
import java.util.List;
import java.util.Vector;

import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.ListSelectionModel;
import javax.swing.ScrollPaneConstants;

/**
 * Displays rejects during a despatcher sweep.
 * 
 * @author eng
 * 
 */
public class SchedulingRejectsPanel extends SchedulingSweepDataDisplayPanel {

	private RejectTableModel rejectTableModel;

	private JTable rejectTable;
	
	private RejectsTableRenderer renderer;
	
	private TagList tagList;
	
	/**
	 * @param sweeps
	 */
	public SchedulingRejectsPanel(List<SweepEntry> sweeps,	TagList tagList) {
		super(sweeps);
		this.tagList = tagList;
		setLayout(new BorderLayout());

		rejectTableModel = new RejectTableModel();

		renderer = new RejectsTableRenderer();
		rejectTable = new JTable(rejectTableModel);

		JScrollPane jspSweep = new JScrollPane(rejectTable);
		jspSweep.setVerticalScrollBarPolicy(ScrollPaneConstants.VERTICAL_SCROLLBAR_AS_NEEDED);

		rejectTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
		rejectTable.setRowSelectionAllowed(true);
		ListSelectionModel clm = rejectTable.getSelectionModel();
		clm.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
		clm.addListSelectionListener(new RejectTableSelectionModel(rejectTableModel));
		rejectTable.setSelectionModel(clm);

		int nc = rejectTable.getColumnModel().getColumnCount();
		for (int ic = 0; ic < nc; ic++) {
			rejectTable.getColumnModel().getColumn(ic).setCellRenderer(renderer);
		}
		
		add(jspSweep, BorderLayout.CENTER);

	}

	@Override
	public void displaySweep(int d, int l, boolean s) {
		
		if (s) {

			SweepEntry entry = sweeps.get(l);
			if (entry != null) {

				rejectTableModel.clearTable();

				List<RejectRow> rows = new Vector<RejectRow>();

				List<RejectEntry> rejects = entry.getRejects();
				if (rejects != null) {
					for (int is = 0; is < rejects.size(); is++) {
						RejectEntry c = rejects.get(is);
						RejectRow crow = new RejectRow(c);
						rows.add(crow);
					}
				}

			
				List<Integer> hrows = new Vector<Integer>();

				for (int ir = 0; ir < rows.size(); ir++) {
					RejectRow row = rows.get(ir);				
					// we can add it to the trows list if its tagged
					if (tagList.isTagged(row.group))
						hrows.add(ir);
				}
			
				renderer.highlightRows(hrows);

				rejectTableModel.setRows(rows);

			}

		} else {

			// if we are not already showing d then show it.

			SweepEntry entry = sweeps.get(d);
			if (entry != null) {

				rejectTableModel.clearTable();

				List<RejectRow> rows = new Vector<RejectRow>();

				List<RejectEntry> rejects = entry.getRejects();
				if (rejects != null) {
					for (int is = 0; is < rejects.size(); is++) {
						RejectEntry c = rejects.get(is);
						RejectRow crow = new RejectRow(c);
						rows.add(crow);
					}
				}


				List<Integer> hrows = new Vector<Integer>();
				for (int ir = 0; ir < rows.size(); ir++) {
					RejectRow row = rows.get(ir);					
					// we can add it to the trows list if its tagged
					//if (tagList.isTagged(row.group))
						//hrows.add(ir);
				}
				// TODO check each row to see if its a tagged group
				//renderer.highlightRows(hrows);

				rejectTableModel.setRows(rows);

			}

		}
	}

}
