/**
 * 
 */
package ngat.opsgui.perspectives.scheduling;

import java.awt.BorderLayout;

import javax.swing.ListSelectionModel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.ScrollPaneConstants;

import ngat.sms.ScheduleItem;

/** Displays a summary table of despatcher sweeps.
 * @author eng
 *
 */
public class SchedulingSummaryPanel extends JPanel {

	private SummaryTableModel summaryTableModel;
	
	private JTable summaryTable;

	/**
	 * @param sweeps
	 */
    public SchedulingSummaryPanel() {
	super(true);
	
	setLayout(new BorderLayout());
	
	summaryTableModel = new SummaryTableModel();
	
	summaryTable = new JTable(summaryTableModel);
	
	JScrollPane jspSweep = new JScrollPane(summaryTable);
	jspSweep.setVerticalScrollBarPolicy(ScrollPaneConstants.VERTICAL_SCROLLBAR_AS_NEEDED);
	
	summaryTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
	summaryTable.setRowSelectionAllowed(true);
        ListSelectionModel clm = summaryTable.getSelectionModel();
	clm.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
	//clm.addListSelectionListener(new RejectTableSelectionModel(rejectTableModel));
	summaryTable.setSelectionModel(clm);
	
	add(jspSweep, BorderLayout.CENTER);
	
    }

    public void updateSweep(int sweepNumber, SweepEntry sweep, ScheduleItem sched) {
	
	// create a sweep row using this entry
	SummaryEntry summary = new SummaryEntry(sweepNumber, sweep, sched);	
	SummaryRow row = new SummaryRow(summary);
	summaryTableModel.addRow(row);
    }
    
}
