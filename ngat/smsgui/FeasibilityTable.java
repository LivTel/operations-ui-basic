/**
 * 
 */
package ngat.smsgui;

import java.awt.Component;

import javax.swing.JTable;
import javax.swing.table.TableColumn;

/**
 * @author eng
 *
 */
public class FeasibilityTable extends JTable {

	private FeasibilityTableModel ftm;
	
	 public static final Object[] lv = {"12345", 
		 "12345", 
         "12345678901234567890123456789012345", 
         "123",
         "1234", 
         "12345678901234567890234567890"};

	/**
	 * @param ftm
	 */
	public FeasibilityTable(FeasibilityTableModel ftm) {
		super(ftm);
		
		FeasibilityTableRenderer ftr = new FeasibilityTableRenderer();
		getColumnModel().getColumn(2).setCellRenderer(ftr);
		getColumnModel().getColumn(5).setCellRenderer(ftr);
		
		// reset the column widths to sensible ones	
		 setAutoResizeMode(JTable.AUTO_RESIZE_OFF);

	        // Try to set the fecking column sizes..
	        TableColumn col = null;
	        Component comp  = null;
	        int cellWidth   = 0;
	        int cellHeight  = 0;
	        int totalWidth  = 0;

	        for (int icol = 0; icol < ftm.getColumnCount(); icol++) {
	            col = getColumnModel().getColumn(icol);
	          
	            comp = getDefaultRenderer(ftm.getColumnClass(icol)).
	                getTableCellRendererComponent(this, lv[icol], false, false, 0, icol);
	            
	            cellWidth = comp.getPreferredSize().width;

	            col.setPreferredWidth(cellWidth);
	            totalWidth += cellWidth;
	            cellHeight = comp.getPreferredSize().height;
	        }

	}
	
}
