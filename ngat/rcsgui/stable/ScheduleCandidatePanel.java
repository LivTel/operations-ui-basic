/**
 * 
 */
package ngat.rcsgui.stable;

import java.awt.BorderLayout;
import java.awt.FlowLayout;

import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextField;
import javax.swing.ListSelectionModel;
import javax.swing.ScrollPaneConstants;

import ngat.opsgui.perspectives.scheduling.CandidateRow;
import ngat.opsgui.perspectives.scheduling.CandidateTable;
import ngat.opsgui.perspectives.scheduling.CandidateTableModel;
import ngat.opsgui.perspectives.scheduling.CandidateTableSelectionModel;
import ngat.sms.ChargeAccountingModel;
import ngat.sms.GroupItem;
import ngat.sms.models.standard.StandardChargeAccountingModel;

/**
 * @author eng
 *
 */
public class ScheduleCandidatePanel extends JPanel {

	JTextField sweepCountField;
	CandidateTableModel ctm;
	/**
	 * 
	 */
	public ScheduleCandidatePanel() {
		super(true);
		
		sweepCountField = new JTextField(5);
		JPanel top = new JPanel(true);
		top.setLayout(new FlowLayout(FlowLayout.LEADING));
		top.add(new JLabel("Sweep"));
		top.add(sweepCountField);
		
		ChargeAccountingModel cam = new StandardChargeAccountingModel();

		ctm = new CandidateTableModel();
		ctm.setCam(cam);
		
		CandidateTable ctab = new CandidateTable(ctm);
		JScrollPane jspSweep = new JScrollPane(ctab);
		jspSweep.setVerticalScrollBarPolicy(ScrollPaneConstants.VERTICAL_SCROLLBAR_AS_NEEDED);
		
		ctab.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
		ctab.setRowSelectionAllowed(true);
		ListSelectionModel clm = ctab.getSelectionModel();
		clm.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
		clm.addListSelectionListener(new CandidateTableSelectionModel(ctm));
		ctab.setSelectionModel(clm);
		
		
		setLayout(new BorderLayout());
		add(jspSweep, BorderLayout.CENTER);
		add(top, BorderLayout.NORTH);
		
	}
	
	public void candidateAdded(GroupItem group, double score) {
		CandidateRow crow = new CandidateRow();
		crow.group = group;
		crow.rank = 1;
		crow.score = score;
		ctm.addRow(crow);
	}
	
	public void sweepStarted(int sweep) {
		sweepCountField.setText(String.format("%6d",sweep));
		ctm.clearTable();
	}
	
	public void candidateSelected() {
		ctm.fireTableDataChanged();
	}
	

}
