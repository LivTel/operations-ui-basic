/**
 * 
 */
package ngat.rcsgui.stable;

import java.util.List;
import java.util.Vector;

import javax.swing.SwingUtilities;
import javax.swing.table.AbstractTableModel;

import ngat.phase2.IProposal;
import ngat.phase2.ITag;
import ngat.phase2.IUser;
import ngat.sms.GroupItem;

/**
 * @author eng
 * 
 */
public class OpsTableModel extends AbstractTableModel {

    final String[] columnNames = new String[] {"Mode", "Started", "TAG", "PI", "Proposal", "Group", "Inst", "Status", "Details"};

	private List<OpsTableRow> rows;

	/**
* 
*/
	public OpsTableModel() {
		super();
		rows = new Vector<OpsTableRow>();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see javax.swing.table.TableModel#getColumnCount()
	 */
	@Override
	public int getColumnCount() {
		return columnNames.length;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see javax.swing.table.AbstractTableModel#getColumnName(int)
	 */
	@Override
	public String getColumnName(int column) {
		return columnNames[column];
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see javax.swing.table.TableModel#getRowCount()
	 */
	@Override
	public int getRowCount() {
		return (rows != null ? rows.size() : 0);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see javax.swing.table.TableModel#getValueAt(int, int)
	 */
	@Override
	public Object getValueAt(int rowIndex, int columnIndex) {

		if (rows == null)
			return null;

		if (rowIndex > rows.size())
			return null;
		
		OpsTableRow row = rows.get(rowIndex);

		GroupItem group = row.group;
		IProposal proposal = group.getProposal();
		IUser user = group.getUser();
		ITag tag = group.getTag();

		switch (columnIndex) {
		case 0:
			// TODO this is a temporary fudge...		
			String proposalName = proposal.getName();
			if (proposalName.equals("SkyFlats"))
				return new Integer(3);
			else if
				(proposalName.equals("Standards"))
				return new Integer(1);
			else
				return new Integer(2);
			
			// TODO no current way to distinguish a TO "group"
			
		case 1:
			return String.format("%tT", row.startTime);
		case 2:
			return tag.getName();
		case 3:
			return user.getName();
		case 4:
			return proposal.getName();
		case 5:
			return group.getName();
		case 7:
			if (row.hasCompleted) {
				if (row.error == null)
					return "Completed";
				else
					return "Failed";
			} else
				return "Running...";
		case 8:
			if (row.hasCompleted) {
				if (row.error != null) {			
					return row.error.getErrorCode()+" "+ row.error.getErrorMessage();	
				} else {
					return "";
					// later we might log details like how many images were generated or QOS stats
				}
			} else {
				return ""; // not done yet
			}
		case 6:
		    return row.details;
		default:
			return "";
		}

	}
	
	public void addEntry(OpsTableRow row) {
		final OpsTableRow frow = row;
		SwingUtilities.invokeLater(new Runnable() {
			@Override
			public void run() {
				rows.add(frow);				
				//fireTableDataChanged();
				fireTableRowsInserted(rows.size()-1, rows.size()-1);
			}
		});			
	}
}
