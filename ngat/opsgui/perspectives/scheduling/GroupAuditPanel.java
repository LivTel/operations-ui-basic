/**
 * 
 */
package ngat.opsgui.perspectives.scheduling;

import java.awt.BorderLayout;
import java.awt.FlowLayout;
import java.util.List;
import java.util.Vector;

import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.ListSelectionModel;
import javax.swing.ScrollPaneConstants;
import ngat.opsgui.base.ComponentFactory;
import ngat.sms.GroupItem;
import ngat.sms.ScheduleItem;

/**
 * @author eng
 * 
 */
public class GroupAuditPanel extends JPanel implements TagListUpdateListener {

	private List<SweepEntry> sweeps;

	private GroupAuditTableModel groupAuditTableModel;

	private JTable groupAuditTable;
	
	private GroupAuditTableRenderer renderer;
	
	private GroupItem auditGroup;
	
	private JTextField groupNameField;
	
	/**
	 * 
	 */
	public GroupAuditPanel(List<SweepEntry> sweeps) {
		super(true);
		this.sweeps = sweeps;
		setLayout(new BorderLayout());

		groupAuditTableModel = new GroupAuditTableModel();

		// renderer = new RejectsTableRenderer();
		renderer = new GroupAuditTableRenderer();
		groupAuditTable = new JTable(groupAuditTableModel);

		JScrollPane jspSweep = new JScrollPane(groupAuditTable);
		jspSweep.setVerticalScrollBarPolicy(ScrollPaneConstants.VERTICAL_SCROLLBAR_AS_NEEDED);

		groupAuditTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
		groupAuditTable.setRowSelectionAllowed(true);
		ListSelectionModel clm = groupAuditTable.getSelectionModel();
		clm.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
		// clm.addListSelectionListener(new
		// GroupAuditTableSelectionModel(groupAuditTableModel));
		groupAuditTable.setSelectionModel(clm);

		int nc = groupAuditTable.getColumnModel().getColumnCount();
		 for (int ic = 0; ic < nc; ic++) {
		 groupAuditTable.getColumnModel().getColumn(ic).setCellRenderer(renderer);
		 }

		add(jspSweep, BorderLayout.CENTER);
		
		groupNameField = ComponentFactory.makeEntryField(24);
		JPanel top = new JPanel();
		top.setLayout(new FlowLayout(FlowLayout.LEADING));
		top.add(ComponentFactory.makeLabel("Group"));
		top.add(groupNameField);
		add(top, BorderLayout.NORTH);

	}

	public void displayGroup(GroupItem group) {

		if (group == null)
			return;

		long gid = group.getID();

		// note this one
		auditGroup = group;
		
		groupNameField.setText(group.getName());
		
		groupAuditTableModel.clearTable();

		List<GroupAuditRow> rows = new Vector<GroupAuditRow>();

		for (int is = 0; is < sweeps.size(); is++) {

			SweepEntry entry = sweeps.get(is);

			// find the named group in the sweep
			System.err.println("GAP: check sweep: "+is);
			CandidateEntry candidate = entry.findCandidate(gid);
			if (candidate != null) {
				// its a candidate
				GroupAuditEntry gad = null;
				if (candidate.getRank() == 1) 
					gad = new GroupAuditEntry(is+1, 
							entry.getTime(), 
							GroupAuditEntry.SELECTED, 
							"Selected "+candidate.getScore());
				else 
					gad = new GroupAuditEntry(is+1, 
							entry.getTime(), 
							GroupAuditEntry.CANDIDATE, 
							"Rank: "+ candidate.getRank()+ " "+candidate.getScore());
				GroupAuditRow grow = new GroupAuditRow(gad);
				rows.add(grow);
				System.err.println("GAP: candidate");
			} else {
				// maybe its a reject
				RejectEntry reject = entry.findReject(gid);
				if (reject != null) {
					GroupAuditEntry gad = new GroupAuditEntry(is+1, entry.getTime(), GroupAuditEntry.REJECT, reject.getReason());
					GroupAuditRow grow = new GroupAuditRow(gad);
					rows.add(grow);
					System.err.println("GAP: rejected");
				}
			}
			
		}
	
		groupAuditTableModel.setRows(rows);

	}
	
	public void updateSweep(SweepEntry latestSweep, ScheduleItem sched) {
		System.err.println("GAP: new sweep");
		
		if (auditGroup == null)
			return;
		
		int ns = groupAuditTableModel.getRowCount();
		
		CandidateEntry candidate = latestSweep.findCandidate(auditGroup.getID());
		if (candidate != null) {
			// its a candidate
			GroupAuditEntry gad = new GroupAuditEntry(sweeps.size(), latestSweep.getTime(), GroupAuditEntry.CANDIDATE, "Rank: "
					+ candidate.getRank()+" : "+candidate.getScore());
			GroupAuditRow grow = new GroupAuditRow(gad);
			groupAuditTableModel.addRow(grow);
			System.err.println("GAP: candidate");
		} else {
			// maybe its a reject
			RejectEntry reject = latestSweep.findReject(auditGroup.getID());
			if (reject != null) {
				GroupAuditEntry gad = new GroupAuditEntry(sweeps.size(), latestSweep.getTime(), GroupAuditEntry.REJECT, reject.getReason());
				GroupAuditRow grow = new GroupAuditRow(gad);
				groupAuditTableModel.addRow(grow);
				System.err.println("GAP: rejected");
			}
		}
		
	}
	
	@Override
	public void groupTagged(final GroupItem group) throws Exception {		
				displayGroup(group);
	}

	@Override
	public void groupUntagged(GroupItem group) throws Exception {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void tagsCleared() throws Exception {
		// TODO Auto-generated method stub
		
	}

}
