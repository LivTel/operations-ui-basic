/**
 * 
 */
package ngat.opsgui.perspectives.tracking;

import java.util.List;
import java.util.Vector;

import javax.swing.table.AbstractTableModel;

import ngat.astrometry.AstroCatalog;
import ngat.phase2.XExtraSolarTarget;

/**
 * @author eng
 *
 */
public class CatalogTableModel extends AbstractTableModel {
	
	final String[] columnNames = new String[] { "Name", "RA", "Dec"};
	
	List<XExtraSolarTarget> rows;
	
	
	
	/**
	 * 
	 */
	public CatalogTableModel() {
		super();
		rows = new Vector<XExtraSolarTarget>();
	}
	
	public void setRows(AstroCatalog catalog) {
		rows.addAll(catalog.listTargets());
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
	 * @see javax.swing.table.TableModel#getColumnCount()
	 */
	@Override
	public int getColumnCount() {
		return columnNames.length;
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
		

		XExtraSolarTarget target = rows.get(rowIndex);
		
		switch (columnIndex) {
		case 0:
			return target.getName();
		case 1:
			return new Double(target.getRa());
		case 2:
			return new Double(target.getDec());
			default:
				return "";
		}
	}

}
