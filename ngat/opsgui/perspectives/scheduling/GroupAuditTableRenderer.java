/**
 * 
 */
package ngat.opsgui.perspectives.scheduling;

import java.awt.Color;
import java.awt.Component;

import javax.swing.JLabel;
import javax.swing.JTable;
import javax.swing.table.DefaultTableCellRenderer;

/**
 * @author eng
 *
 */
public class GroupAuditTableRenderer extends DefaultTableCellRenderer {
	  private static Color NORMAL_BACKGROUND = Color.white;
		private static Color NORMAL_FOREGROUND = Color.blue;
	    
	/* (non-Javadoc)
	 * @see javax.swing.table.DefaultTableCellRenderer#getTableCellRendererComponent(javax.swing.JTable, java.lang.Object, boolean, boolean, int, int)
	 */
	@Override
	public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus,
			int row, int column) {
		System.err.println("GAR: col"+column+" Value isa: "+value.getClass().getName());
		
		if (column == 2)
			return getCategoryRendererComponent(table, value, isSelected, hasFocus, row, column);
		
		Component c = super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
	  
	    	c.setBackground(NORMAL_BACKGROUND);
	    	c.setForeground(NORMAL_FOREGROUND);
	    

	    return c;
	    	
	}
		private Component getCategoryRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus,
				int row, int column) {
			
			//Component c = super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
		
			JLabel label = new JLabel();
			label.setOpaque(true);
			int is = ((Integer)value).intValue();
			
			switch (is) {
			case GroupAuditEntry.CANDIDATE:
				label.setBackground(Color.green);
				label.setForeground(Color.blue);
				label.setText("Candidate");			
				break;
			case GroupAuditEntry.REJECT:
				label.setBackground(Color.red);
				label.setForeground(Color.blue);
				label.setText("Rejected");
				break;
			}
			
			return label;
		}
	
}
