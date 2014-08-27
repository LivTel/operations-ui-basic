/**
 * 
 */
package ngat.smsgui;

import java.util.List;
import java.util.Vector;

import javax.swing.table.AbstractTableModel;

/**
 * @author eng
 * 
 */
public class SequenceTableModel extends AbstractTableModel {

	final String[] columnNames = new String[] { "N", "Sequence", "Score" };

	private List<SequenceRow> rows;

	/**
	 * @param rows
	 */
	public SequenceTableModel() {
		super();
		rows = new Vector<SequenceRow>();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see javax.swing.table.TableModel#getColumnCount()
	 */
	@Override
	public int getColumnCount() {
		// TODO Auto-generated method stub
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
		// TODO Auto-generated method stub
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

		SequenceRow row = rows.get(rowIndex);

		switch (columnIndex) {
		case 0:
			return new Integer(rowIndex);
		case 1:
			return row.sequence;
		case 2:
			return new Double(row.score);
		default:
			return "";
		}

	}

}
