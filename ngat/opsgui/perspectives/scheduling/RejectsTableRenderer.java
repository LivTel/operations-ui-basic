/**
 * 
 */
package ngat.opsgui.perspectives.scheduling;

import java.awt.Color;
import java.awt.Component;
import java.util.List;
import javax.swing.JTable;
import javax.swing.table.DefaultTableCellRenderer;


/**
 * @author eng
 *
 */
public class RejectsTableRenderer extends DefaultTableCellRenderer {

    private static Color HIGHLIGHT_BACKGROUND = Color.cyan;
    private static Color HIGHLIGHT_FOREGROUND = Color.black;
    private static Color NORMAL_BACKGROUND = Color.white;
	private static Color NORMAL_FOREGROUND = Color.blue;
    
	private List<Integer> highlightedRows;
    
	/**
	 * 
	 */
	public RejectsTableRenderer() {
	    super();
	}

    public void highlightRows(List<Integer> highlightedRows) {
	this.highlightedRows = highlightedRows;
    }

	/* (non-Javadoc)
	 * @see javax.swing.table.DefaultTableCellRenderer#getTableCellRendererComponent(javax.swing.JTable, java.lang.Object, boolean, boolean, int, int)
	 */
	@Override
	public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus,
			int row, int column) {

		Component c = super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
	    if (highlightedRows.contains(row)) {
	    	c.setBackground(HIGHLIGHT_BACKGROUND);
	    	c.setForeground(HIGHLIGHT_FOREGROUND);
	    	System.err.println("CTR:Highlighting row: "+row+" : "+value);
	    	return this;
	    } else {
	    	c.setBackground(NORMAL_BACKGROUND);
	    	c.setForeground(NORMAL_FOREGROUND);
	    }

	    return c;
	    	
	}	
	
}
