/**
 * 
 */
package ngat.opsgui.perspectives.scheduling;

import java.text.SimpleDateFormat;
import java.util.List;
import java.util.SimpleTimeZone;
import java.util.Vector;

import javax.swing.SwingUtilities;
import javax.swing.table.AbstractTableModel;

import ngat.sms.GroupItem;

/** Model for a scheduler sweep summary table.
 * @author eng
 *
 */
public class SummaryTableModel extends AbstractTableModel {

	static SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
	static SimpleTimeZone UTC = new SimpleTimeZone(0, "UTC");
	
    final String[] columnNames = new String[] {"Sweep", "Time", "Duration", "Nc", "Nr", "Winner", "Score"};

	private List<SummaryRow> rows;
	
	/**
	 * 
	 */
	public SummaryTableModel() {
		super();	
		sdf.setTimeZone(UTC);
		rows = new Vector<SummaryRow>();
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

	public SummaryRow getRow(int rowIndex) {
		if (rows == null)
			return null;
		
		if (rowIndex > rows.size())
			return null;
		
		SummaryRow row = rows.get(rowIndex);
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
		
		// "Sweep", "Time", "Duration", "Nc", "Nr", "Winner", "Score"

		SummaryRow row = rows.get(rowIndex);

		GroupItem group = row.winningGroup;

		// TODO we may want this info - or not		
		// priority stuff calculate this before here.

		switch (columnIndex) {		
		case 0:
		    return row.sweepNumber;
		case 1:
		    return sdf.format(row.sweepTime);
		case 2:
		    return row.sweepDuration;
		case 3:
		    return row.countCandidates;
		case 4:
		    return row.countRejects;
		case 5:
		    return (group != null ? group.getName() : "");
		case 6:
		    return row.winningScore;
		default:
		    return "";
		}

	}
    
    /**
     * @param rows the rows to set
	 */
    public void setRows(final List<SummaryRow> trows) {
	SwingUtilities.invokeLater(new Runnable() {
		@Override
		public void run() {
		    rows = trows;	
		    System.err.println("New data received");
		    fireTableDataChanged();
		}});		
    }
	
    public void addRow(final SummaryRow crow) {
	SwingUtilities.invokeLater(new Runnable() {
                @Override
				public void run() {
		    rows.add(crow);
		    System.err.println("New data received");
                    fireTableDataChanged();
                }});

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
