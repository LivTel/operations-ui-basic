/**
 * 
 */
package ngat.smsgui;

import java.util.List;
import java.util.Vector;

import javax.swing.SwingUtilities;
import javax.swing.table.AbstractTableModel;

import ngat.sms.util.PrescanEntry;

/**
 * @author eng
 * 
 */
public class FeasibilityTableModel extends AbstractTableModel {

	final String[] columnNames = new String[] { "Gen", "ID", "Name", "nx", "Exec", "Feasibility" };

	private List<PrescanEntry> rows;

	/**
	 * 
	 */
	public FeasibilityTableModel() {
		rows = new Vector<PrescanEntry>();
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

	/*Search Bookmarks and History
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

		PrescanEntry pse = rows.get(rowIndex);

		// "Gen", "ID", "Name", "Exec", "Feasibility"

		switch (columnIndex) {
		case 0:
			return pse.gname;
		case 1:
			return pse.group.getID();
		case 2:
			return pse.group;
		case 3:
			return new Integer(pse.nx);
		case 4:
			return String.format("%4.1f", (pse.execTime / 60000));
		case 5:
			// return pse.feasible;
			return pse;
		default:
			return "";
		}

	}

	public void clear() {
		SwingUtilities.invokeLater(new Runnable() {
			@Override
			public void run() {
				rows.clear();
				fireTableDataChanged();
			}
		});
	}

	public void addEntry(PrescanEntry pse) {
		final PrescanEntry fpse = pse;
		SwingUtilities.invokeLater(new Runnable() {
			@Override
			public void run() {
				rows.add(fpse);
				fireTableDataChanged();
			}
		});			
	}

}
