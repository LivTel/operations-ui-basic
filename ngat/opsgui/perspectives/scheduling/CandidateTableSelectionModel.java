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
public class CandidateTableSelectionModel implements ListSelectionListener {

	private CandidateTableModel ctabmodel;
	
	public CandidateTableSelectionModel(CandidateTableModel ctabmodel) {
		this.ctabmodel = ctabmodel;
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
			CandidateRow crow = ctabmodel.getRow(n1);
			JOptionPane.showMessageDialog(null, "You have selected rows ["+n0+","+n1+"] containing: "+
			crow.group.getName()+" <"+crow.group.getID()+">");
			// extract the GID and update the watch master panel
		}
		
	}

}
