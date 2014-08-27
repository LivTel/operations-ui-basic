/**
 * 
 */
package ngat.rcsgui.stable;

import java.util.List;
import java.util.Vector;

import javax.swing.SwingUtilities;
import javax.swing.table.AbstractTableModel;

/** Displays execution selection history.
 * 
 * @author eng
 *
 */
public class CilTableModel extends AbstractTableModel {
	
	// what else do we want here ?
	// e.g. g.seeing/g.moon requirements, astro and env info at time (eg actual seeing)
	
	final String[] columnNames = new String[] {"Command", "Sent", "Elapsed", "Status"};

	private List<CilRow> rows;
	
	CilRow currentRow;
	
	/**
	 * 
	 */
	public CilTableModel() {
		super();
		rows = new Vector<CilRow>();
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
		
	
		CilRow row = rows.get(rowIndex);
		// message, sentAt, elapsed, status
	
		switch (columnIndex) {
		case 0:
			return row.message;
		case 1:
			return String.format("%tT",row.sendTime);
		case 2:
			return String.format("%6.2f", (double)(row.elapsedTime/1000));
		case 3:
			return new Integer(row.status);		
		default:
			return "";
		}
	}
	
	/** Add a new row - this represents the current latest selected group.
	 * @param rows the rows to set
	 */
	public void addRow(CilRow row) {
		final CilRow frow = row;
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
	public void modifyRow() {	
	/*	if (currentRow.group.getID() != group.getID()) {
			System.err.println("This group does not appear to be current ?");
			return;
		}*/
	/*	SwingUtilities.invokeLater(new Runnable() {
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
		*/
	}
	
	
}
