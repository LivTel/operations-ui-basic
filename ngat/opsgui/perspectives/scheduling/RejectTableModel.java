/**
 * 
 */
package ngat.opsgui.perspectives.scheduling;

import java.util.List;
import java.util.Vector;

import javax.swing.SwingUtilities;
import javax.swing.table.AbstractTableModel;

import ngat.phase2.IProposal;
import ngat.phase2.ITag;
import ngat.phase2.ITimingConstraint;
import ngat.phase2.IUser;
import ngat.phase2.XMinimumIntervalTimingConstraint;
import ngat.phase2.XMonitorTimingConstraint;
import ngat.sms.GroupItem;

/** Model for a scheduler sweep rejection table.
 * @author eng
 *
 */
public class RejectTableModel extends AbstractTableModel {

	final String[] columnNames = new String[] {"Name", "Proposal", "Reason"};

	private List<RejectRow> rows;
	
	/**
	 * 
	 */
	public RejectTableModel() {
		super();	
		rows = new Vector<RejectRow>();
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

	public RejectRow getRow(int rowIndex) {
		if (rows == null)
			return null;
		
		if (rowIndex > rows.size())
			return null;
		
		RejectRow row = rows.get(rowIndex);
		return row;
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
	
		RejectRow row = rows.get(rowIndex);

		GroupItem group = row.group;
		IProposal proposal = group.getProposal();
		IUser user = group.getUser();
		ITag tag = group.getTag();
		
		ITimingConstraint timing = group.getTimingConstraint();
		int tp = 0;
		if (timing instanceof XMonitorTimingConstraint ||
			timing instanceof XMinimumIntervalTimingConstraint) 
			tp = 1;
		
		String priorityName = "";
		double priority = tp;
		if (group.isUrgent()) {
			priorityName += "*";
			priority += 2;
		}
		
		switch (proposal.getPriority()) {
		case IProposal.PRIORITY_A:
			priorityName += "A";
			priority += 4;
			break;
		case IProposal.PRIORITY_B:
			priorityName += "B";
			priority += 2;
			break;
		case IProposal.PRIORITY_C:
			priorityName += "C";
			break;
		case IProposal.PRIORITY_Z:
			priorityName += "Z";
			priority -= 10;
			break;
		}
		double po = proposal.getPriorityOffset();
		if (po < 0.0)
			priorityName += "-";
		else if (po > 0.0)
			priorityName += "+";
		
		priority += po;
		
		String error = row.error;

		switch (columnIndex) {		
		case 0:		
			return group.getName();
		case 1:
			return proposal.getName();
		case 2:
			return error;
		default:
			return "";
		}

	}

	/**
	 * @param rows the rows to set
	 */
	public void setRows(final List<RejectRow> trows) {
		SwingUtilities.invokeLater(new Runnable() {
			@Override
			public void run() {
				rows = trows;	
				System.err.println("New data received");
				fireTableDataChanged();
		}});		
	}
	
	public void addRow(final RejectRow crow) {
		rows.add(crow);
	}

	public void clearTable() {
		SwingUtilities.invokeLater(new Runnable() {
			@Override
			public void run() {
				rows.clear();
				fireTableDataChanged();
			}});		
	}
	
	
}
