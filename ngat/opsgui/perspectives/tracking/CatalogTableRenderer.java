/**
 * 
 */
package ngat.opsgui.perspectives.tracking;

import java.awt.Component;

import javax.swing.JLabel;
import javax.swing.JTable;
import javax.swing.table.DefaultTableCellRenderer;

import ngat.astrometry.AstroFormatter;

/**
 * @author eng
 *
 */
public class CatalogTableRenderer extends DefaultTableCellRenderer {
	
	

	/* (non-Javadoc)
	 * @see javax.swing.table.DefaultTableCellRenderer#getTableCellRendererComponent(javax.swing.JTable, java.lang.Object, boolean, boolean, int, int)
	 */
	/**
	 * 
	 */
	public CatalogTableRenderer() {
		super();
		// TODO Auto-generated constructor stub
	}

	@Override
	public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus,
			int row, int column) {

		System.err.println("CTR::Column renderer: col="+column+" value isa "+value.getClass().getName());
		
		Component c = super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
		
		if (column == 1) {
			JLabel l = (JLabel)c;
			Double d = (Double)value;
			l.setText(AstroFormatter.formatHMS(d.doubleValue(), ":"));
		} else if
		(column == 2) {
			JLabel l = (JLabel)c;
			Double d = (Double)value;
			l.setText(AstroFormatter.formatDMS(d.doubleValue(), ":"));
		}
		
		return c;
		
	}
}
