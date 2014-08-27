/**
 * 
 */
package ngat.opsgui.perspectives.scheduling;

import java.util.List;
import java.util.Vector;

import javax.swing.SwingUtilities;
import javax.swing.table.AbstractTableModel;

import ngat.phase2.IProgram;
import ngat.phase2.IProposal;
import ngat.phase2.ITag;
import ngat.phase2.ITimingConstraint;
import ngat.phase2.IUser;
import ngat.phase2.XEphemerisTimingConstraint;
import ngat.phase2.XFixedTimingConstraint;
import ngat.phase2.XFlexibleTimingConstraint;
import ngat.phase2.XMinimumIntervalTimingConstraint;
import ngat.phase2.XMonitorTimingConstraint;
import ngat.sms.ChargeAccountingModel;
import ngat.sms.GroupItem;

/** Displays scheduler sweep candidates.
 * 
 * @author eng
 * 
 */
public class CandidateTableModel extends AbstractTableModel {

	final String[] columnNames = new String[] { "Rank", "GID", "Name", "Timing", "Exec", "Cat", "Priority", "PI", "Prog", "TAG",
			"Score" };

	private List<CandidateRow> rows;

	private ChargeAccountingModel cam;
	
	/**
	 * @param xrm the xrm to set
	 */
	public void setCam(ChargeAccountingModel cam) {
		this.cam = cam;
	}
	/**
	 * 
	 */
	public CandidateTableModel() {
		super();	
		rows = new Vector<CandidateRow>();
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

	public CandidateRow getRow(int rowIndex) {
		if (rows == null)
			return null;
		
		if (rowIndex > rows.size())
			return null;
		
		CandidateRow row = rows.get(rowIndex);
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
		// {"Rank", "GID", "Name", "Timing", "Exec", "Cat", "Priority", "PI", "Prog", "TAG",
		// "Score"}

		CandidateRow row = rows.get(rowIndex);

		GroupItem group = row.group;
		ITimingConstraint timing = group.getTimingConstraint();
		IProposal proposal = group.getProposal();
		IProgram program = group.getProgram();
		IUser user = group.getUser();
		ITag tag = group.getTag();

		int tp = 0;
		String timingName = "unknown";
		if (timing instanceof XFlexibleTimingConstraint) {
			timingName = "Flex";
		} else if (timing instanceof XMonitorTimingConstraint) {
			timingName = "Mon";
			tp = 1;
		} else if (timing instanceof XEphemerisTimingConstraint) {
			timingName = "Phase";
		} else if (timing instanceof XMinimumIntervalTimingConstraint) {
			timingName = "MinInt";
			tp = 1;
		} else if (timing instanceof XFixedTimingConstraint) {
			timingName = "Fixed";
		}

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
		
		switch (columnIndex) {
		case 0:
			return row.rank;
		case 1:
			return group.getID();
		case 2:
			return group.getName();
		case 3:
			return timingName;
		case 4:
			if (cam == null)
				return "UNK";
			double cost = cam.calculateCost(group.getSequence());
			return new Double(cost/60000); // cost in minutes
		case 5:
			return priorityName;
		case 6:
			return String.format("%2.2f", priority);
		case 7:
			return user.getName();
		case 8:
			//return proposal.getName();
			return program.getName();
		case 9:
			return tag.getName();
		case 10:
			return String.format("%3.2f", row.score);
		default:
			return "";
		}

	}

	/**
	 * @param rows the rows to set
	 */
	public void setRows(final List<CandidateRow> trows) {
		SwingUtilities.invokeLater(new Runnable() {
			@Override
			public void run() {
				rows = trows;	
				System.err.println("New data received");
				fireTableDataChanged();
		}});		
	}
	
	public void addRow(final CandidateRow crow) {
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
