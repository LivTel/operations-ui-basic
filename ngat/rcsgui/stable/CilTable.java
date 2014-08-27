/**
 * 
 */
package ngat.rcsgui.stable;

import javax.swing.JTable;

/**
 * @author eng
 *
 */
public class CilTable extends JTable {


	/**
	 * @param dm
	 */
	public CilTable(CilTableModel ctm) {
		super(ctm);
		getColumnModel().getColumn(3).setCellRenderer(new CilTableRenderer());
	}

}
