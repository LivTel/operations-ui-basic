/**
 * 
 */
package ngat.smsgui;

import java.util.List;
import java.util.Vector;

import javax.swing.SwingUtilities;
import javax.swing.table.AbstractTableModel;

import ngat.astrometry.BasicSite;
import ngat.astrometry.ISite;
import ngat.phase2.IExecutionFailureContext;
import ngat.phase2.IProposal;
import ngat.phase2.ITag;
import ngat.phase2.ITimingConstraint;
import ngat.phase2.IUser;
import ngat.phase2.XEphemerisTimingConstraint;
import ngat.phase2.XFixedTimingConstraint;
import ngat.phase2.XFlexibleTimingConstraint;
import ngat.phase2.XMinimumIntervalTimingConstraint;
import ngat.phase2.XMonitorTimingConstraint;
import ngat.sms.GroupItem;

/** Displays execution selection history.
 * 
 * @author eng
 *
 */
public class HistoryTableModel extends AbstractTableModel {
	
	// what else do we want here ?
	// e.g. g.seeing/g.moon requirements, astro and env info at time (eg actual seeing)
	
	final String[] columnNames = new String[] {"Sweep", "Start", "Name", "Timing", "Cat", "Score", "Duration", "Status"};

	private List<HistoryRow> rows;
	
	final ISite site = new BasicSite("obs", Math.toRadians(28.0), Math.toRadians(-3));

	HistoryRow currentRow;
	
	/**
	 * 
	 */
	public HistoryTableModel() {
		super();
		rows = new Vector<HistoryRow>();
	}

	/* (non-Javadoc)
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
	
	/* (non-Javadoc)
	 * @see javax.swing.table.TableModel#getRowCount()
	 */
	@Override
	public int getRowCount() {
		return (rows != null ? rows.size() : 0);
	}

	/* (non-Javadoc)
	 * @see javax.swing.table.TableModel#getValueAt(int, int)
	 */
	@Override
	public Object getValueAt(int rowIndex, int columnIndex) {
		if (rows == null)
			return null;
		
		if (rowIndex > rows.size())
			return null;
		
		//Sweep", "Start", "Name", "Timing", "Cat", "Score", "Duration", "Status"
		
		HistoryRow row = rows.get(rowIndex);

		GroupItem group = row.group;
		ITimingConstraint timing = group.getTimingConstraint();
		IProposal proposal = group.getProposal();
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
		
		//String path = (group.getTag() != null ? group.getTag().getName() : "TAG?") + "/"
		//+ (group.getUser() != null ? group.getUser().getName() : "USR?") + "/"
		//+ (group.getProposal() != null ? group.getProposal().getName() : "PROP?");

		

		//Sweep", "Start", "Name", "Timing", "Cat", "Score", "Duration", "Status"		
		switch (columnIndex) {
		case 0:
			return String.format("%3d", row.sweep);
		case 1:
			return String.format("%tF %tT",row.time, row.time);
		case 2:
			return group.getName();
		case 3:
			return timingName;
		case 4:
			return priorityName;
		case 5:
			return String.format("%4.2f", row.score);
		case 6:
//			// work out sun level
//			try {
//			SolarCalculator sun = new SolarCalculator();
//			BasicAstrometryCalculator astro = new BasicAstrometryCalculator();
//			double elev = astro.getAltitude(sun.getCoordinates(row.time), site, row.time);
//			//return String.format("%3.2f",Math.toDegrees(elev));
//			return new Double(elev);
//			} catch (AstrometryException ax) {
//				ax.printStackTrace();
//				return null;
//			}
			long duration = row.completion - row.time;
			return new Long(duration);
		case 7:
			return new Integer(row.status);
			//switch (row.status) {
			//case HistoryRow.RUNNING:
				//return "Running";
			//case HistoryRow.COMPLETED:
				//return "Completed";
			//case HistoryRow.FAILED:
				//return "Failed";
			//}
		default:
			return "";
		}
	}
	
	/** Add a new row - this represents the current latest selected group.
	 * @param rows the rows to set
	 */
	public void addRow(HistoryRow row) {
		final HistoryRow frow = row;
		// TODO make it repaint somehow 
		SwingUtilities.invokeLater(new Runnable() {
			@Override
			public void run() {
				System.err.println("New htm row received");
				rows.add(frow);
				currentRow = frow;
				fireTableDataChanged();
			}
		});		
	}
	
	/** Modify the current row with the supplied info.*/
	public void modifyRow(final GroupItem group, final long time, final IExecutionFailureContext efc) {	
		if (currentRow.group.getID() != group.getID()) {
			System.err.println("This group does not appear to be current ?");
			return;
		}
		SwingUtilities.invokeLater(new Runnable() {
			@Override
			public void run() {
				currentRow.completion = time;
				
				if (efc == null) {
					currentRow.status = HistoryRow.COMPLETED;			
				} else {
					currentRow.status = HistoryRow.FAILED;
					currentRow.errmsg = efc.getErrorMessage();
				}
				fireTableDataChanged();
			}
		});		
		
	}
	
	
}
