/**
 * 
 */
package ngat.smsgui;

import javax.swing.JTable;

/**
 * @author eng
 *
 */
public class HistoryTable extends JTable {


	/**
	 * @param dm
	 */
	public HistoryTable(HistoryTableModel htm) {
		super(htm);
		getColumnModel().getColumn(7).setCellRenderer(new HistoryTableRenderer());
		//getColumnModel().getColumn(3).setHeaderRenderer(new HistoryTableRenderer());
		
	}

}
