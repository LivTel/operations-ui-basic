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

/**
 * @author eng
 * 
 */
public class GroupAuditTableModel extends AbstractTableModel {
	

	static SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
	static SimpleTimeZone UTC = new SimpleTimeZone(0, "UTC");
	
	
	final String[] columnNames = new String[] { "Sweep", "Time", "Status", "Context"};

	private List<GroupAuditRow> rows;


	public GroupAuditTableModel() {
		super();
		sdf.setTimeZone(UTC);
		rows = new Vector<GroupAuditRow>();
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

	public GroupAuditRow getRow(int rowIndex) {
		if (rows == null)
			return null;

		if (rowIndex > rows.size())
			return null;

		GroupAuditRow row = rows.get(rowIndex);
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
	

		GroupAuditRow row = rows.get(rowIndex);

		switch (columnIndex) {	
		case 0:
			return row.sweepNumber;
		case 1:
			return sdf.format(row.sweepTime);
		case 2:
			return row.status;
		case 3:
			return row.extra;
		default:
			return "";
		}

	}

	/**
	 * @param rows
	 *            the rows to set
	 */
	public void setRows(final List<GroupAuditRow> trows) {
		SwingUtilities.invokeLater(new Runnable() {
			@Override
			public void run() {
				rows = trows;
				System.err.println("New data received");
				fireTableDataChanged();
			}
		});
	}

	public void addRow(final GroupAuditRow crow) {
		SwingUtilities.invokeLater(new Runnable() {
			@Override
			public void run() {
		rows.add(crow);
		fireTableDataChanged();
			}
			});
	}

	public void clearTable() {
		SwingUtilities.invokeLater(new Runnable() {
			@Override
			public void run() {
				rows.clear();
				fireTableDataChanged();
			}
		});
	}

}
