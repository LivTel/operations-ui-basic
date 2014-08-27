/**
 * 
 */
package ngat.opsgui.perspectives.scheduling;

import javax.swing.JOptionPane;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;

/**
 * @author eng
 *
 */
public class RejectTableSelectionModel implements ListSelectionListener {
	
private RejectTableModel rtabmodel;
	
	public RejectTableSelectionModel(RejectTableModel ctabmodel) {
		this.rtabmodel = rtabmodel;
	}

	/* (non-Javadoc)
	 * @see javax.swing.event.ListSelectionListener#valueChanged(javax.swing.event.ListSelectionEvent)
	 */
	@Override
	public void valueChanged(ListSelectionEvent e) {
		
		e.getSource();// should be ctab
		
		int n0 = e.getFirstIndex();
		int n1 = e.getLastIndex();
		
		if (!e.getValueIsAdjusting()) {
			RejectRow crow = rtabmodel.getRow(n1);
			JOptionPane.showMessageDialog(null, "You have selected rows ["+n0+","+n1+"] containing: "+
			crow.group.getName()+" <"+crow.group.getID()+">");
			// extract the GID and do stuff
		}
		
	}
}
