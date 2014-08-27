/**
 * 
 */
package ngat.opsgui.perspectives.meteorology;

import java.util.List;
import java.util.Vector;

import javax.swing.table.AbstractTableModel;

import ngat.ems.SkyModelSeeingUpdate;

/**
 * @author eng
 * 
 */
public class SeeingHistoryTableModel extends AbstractTableModel {

	final String[] columnNames = new String[] { "Time", "Raw", "Corr", "Elev", "Wave", "Cat", "Source", "Target" };

	List<SkyModelSeeingUpdate> rows;

	/**
	 * 
	 */
	public SeeingHistoryTableModel() {
		super();
		rows = new Vector<SkyModelSeeingUpdate>();
	}

	public void setRows(List<SkyModelSeeingUpdate> rows) {
		this.rows = rows;
		// OR this.rows.addAll(rows);
	}
	
	public void addRow(SkyModelSeeingUpdate sm) {
		int rc = rows.size();
		rows.add(sm);
		fireTableRowsInserted(rc, rc);
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
	 * @see javax.swing.table.TableModel#getColumnCount()
	 */
	@Override
	public int getColumnCount() {
		return columnNames.length;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see javax.swing.table.TableModel#getValueAt(int, int)
	 */
	@Override
	public Object getValueAt(int row, int column) {

		if (rows == null)
			return null;

		if (row > rows.size())
			return null;

		SkyModelSeeingUpdate sm = rows.get(row);
		
		// "Time", "Raw", "Corrected", "Target", "Wave", "Cat", "Source"
		
		switch (column) {
		case 0:
			return new Long(sm.getStatusTimeStamp());
		case 1:
			return new Double(sm.getRawSeeing());
		case 2:
			return new Double(sm.getCorrectedSeeing());
		case 5:
			double corrsee = sm.getCorrectedSeeing();
			if (corrsee > 2.0)
				return "USABLE";
			else if (corrsee > 1.5)
				return "POOR";
			else if (corrsee > 0.8)
				return "AVERAGE";
			else 
				return "GOOD";		
		case 3:
			double elev = sm.getElevation();
			double azm  = sm.getAzimuth();
			return String.format("%4.2f", Math.toDegrees(elev));
		case 4:
			return new Double(sm.getWavelength());
		case 6:
			return sm.getSource()+"("+(sm.isStandard() ? "STD":"SCI")+")";
		case 7:
			return sm.getTargetName();
		default:
			return "";
		}
	}

}
