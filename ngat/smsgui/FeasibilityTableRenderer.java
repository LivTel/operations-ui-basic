/**
 * 
 */
package ngat.smsgui;

import java.awt.Color;
import java.awt.Component;

import javax.swing.JLabel;
import javax.swing.JTable;
import javax.swing.table.DefaultTableCellRenderer;

import ngat.phase2.IProposal;
import ngat.sms.GroupItem;
import ngat.sms.util.PrescanEntry;

/**
 * @author eng
 *
 */
public class FeasibilityTableRenderer extends DefaultTableCellRenderer {

	static Color BG4 = new Color(152,125,206);
	
	/* (non-Javadoc)
	 * @see javax.swing.table.DefaultTableCellRenderer#getTableCellRendererComponent(javax.swing.JTable, java.lang.Object, boolean, boolean, int, int)
	 */
	@Override
	public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus,
			int row, int column) {
		JLabel clabel; 
		switch (column) {
		case 2:
			clabel = (JLabel)super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
			GroupItem group = (GroupItem)value;
			clabel.setText(group.getName());
			switch (group.getProposal().getPriority()) {
			case IProposal.PRIORITY_A:
				clabel.setForeground(Color.blue);
				return clabel;
			case IProposal.PRIORITY_B:
				clabel.setForeground(Color.green);
				return clabel;
			case IProposal.PRIORITY_C:
				clabel.setForeground(Color.red);
				return clabel;
			case IProposal.PRIORITY_Z:
				clabel.setForeground(Color.cyan);
				return clabel;
			}
			return clabel;
		case 5:
					
			PrescanEntry pse = (PrescanEntry)value;
			return new FeasibilityPanel(pse);
//			JLabel label = (JLabel)super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
//			label.setText(pse.display());
//			
//			if (isSelected) {
//				label.setBackground(Color.pink);
//				label.setForeground(Color.blue);
//			} else {			
//				label.setBackground(BG4);
//				label.setForeground(Color.cyan);				
//			}
//				return label;
		default:	
			clabel = (JLabel)super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
			if (isSelected) {			
				clabel.setBackground(Color.pink);
				clabel.setForeground(Color.blue);
			}
			return clabel;
		}

	}	
	
}
