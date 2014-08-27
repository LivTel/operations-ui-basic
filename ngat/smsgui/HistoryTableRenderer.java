/**
 * 
 */
package ngat.smsgui;

import java.awt.Color;
import java.awt.Component;
import javax.swing.JLabel;
import javax.swing.JTable;
import javax.swing.table.DefaultTableCellRenderer;

/**
 * @author eng
 *
 */
public class HistoryTableRenderer extends DefaultTableCellRenderer {

	/**
	 * 
	 */
	public HistoryTableRenderer() {
	
	}
	/* (non-Javadoc)
	 * @see javax.swing.table.DefaultTableCellRenderer#getTableCellRendererComponent(javax.swing.JTable, java.lang.Object, boolean, boolean, int, int)
	 */
	@Override
	public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus,
			int row, int column) {
		JLabel label = (JLabel)super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
		
		switch (column) {
		case 7:
			int status = ((Integer)value).intValue();
			switch (status) {
			case HistoryRow.COMPLETED:
				label.setBackground(Color.cyan);
				label.setForeground(Color.blue);
				label.setText("Completed");
				break;
			case HistoryRow.FAILED:
				label.setBackground(Color.red);
				label.setForeground(Color.cyan);		
				label.setText("Failed");
				//label.setToolTipText(row.errmsg);
				break;
			case HistoryRow.RUNNING:
				label.setBackground(Color.green);
				label.setForeground(Color.red);
				label.setText("Executing");
				break;
			}
						
			break;	
		}
		return label;
	}
	
	private Component getSunRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus,
			int row, int column) {
		Component c = super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
		//System.err.println("Sunrenderer for: "+value.getClass().getName());
		
		double elev = ((Double)value).doubleValue();
		if (elev < Math.toRadians(-18)) {
			c.setBackground(Color.black);
			c.setForeground(Color.white);
			((JLabel)c).setText("NIGHT");
		} else if 
		(elev < Math.toRadians(-12)) {
			c.setBackground(Color.magenta);
			c.setForeground(Color.white);
			((JLabel)c).setText("ASTRO");
		} else if 
		(elev < Math.toRadians(-6)) {
			c.setBackground(Color.cyan);
			c.setForeground(Color.black);
			((JLabel)c).setText("NAUTI");
		} else if 
		(elev < Math.toRadians(0.0)) {
			c.setBackground(Color.cyan);
			c.setForeground(Color.black);
			((JLabel)c).setText("CIVIL");
		} else {
			c.setBackground(Color.yellow);
			c.setForeground(Color.blue);
			((JLabel)c).setText("DAY");
		}
		
		if (isSelected)
			c.setBackground(Color.pink);
		return c;
	}
	
			
}
