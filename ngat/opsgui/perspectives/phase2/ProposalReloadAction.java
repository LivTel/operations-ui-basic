/**
 * 
 */
package ngat.opsgui.perspectives.phase2;

import java.rmi.Naming;

import javax.swing.JOptionPane;
import ngat.phase2.IProposal;
import ngat.sms.Phase2LoadController;

/**
 * @author eng
 *
 */
public class ProposalReloadAction implements Runnable {

	private IProposal proposal;
	
	/**
	 * @param proposal
	 */
	public ProposalReloadAction(IProposal proposal) {
		this.proposal = proposal;
	}

	/* (non-Javadoc)
	 * @see java.awt.event.ActionListener#actionPerformed(java.awt.event.ActionEvent)
	 */
	@Override
	public void run() {
		long start = System.currentTimeMillis();
		Phase2LoadController plc = null;
		try {
			plc = (Phase2LoadController)Naming.lookup("rmi://localhost/Phase2LoadController");			
		} catch (Exception e) {
			e.printStackTrace();
			JOptionPane.showMessageDialog(null, 
					"Error locating load-controller: "+e.getMessage(), 
					"Proposal re-load", 
					JOptionPane.ERROR_MESSAGE);
		}
		try {
			plc.loadProposal(proposal.getID());
			long end = System.currentTimeMillis();
			JOptionPane.showMessageDialog(null, 
					"Reload of proposal: "+proposal.getName()+" completed in "+((end-start)/1000)+"s",
					"Proposal re-load", 
					JOptionPane.INFORMATION_MESSAGE);
			
		} catch (Exception e) {
			e.printStackTrace();
			JOptionPane.showMessageDialog(null, 
					"Error reloading proposal: "+e.getMessage(),
					"Proposal re-load", 
					JOptionPane.ERROR_MESSAGE);
		}

	}

}
