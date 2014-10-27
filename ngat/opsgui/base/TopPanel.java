/**
 * 
 */
package ngat.opsgui.base;

import java.awt.FlowLayout;
import java.rmi.RemoteException;

import javax.swing.BorderFactory;
import javax.swing.JPanel;

import ngat.astrometry.ISite;
import ngat.opsgui.components.AuxSystemsSummaryPanel;
import ngat.opsgui.components.ObservationSummaryPanel;
import ngat.opsgui.components.TestPanel1;
import ngat.opsgui.components.TestPanel2;
import ngat.opsgui.components.TrackingSummaryPanel;
import ngat.phase2.IExecutionFailureContext;
import ngat.rcs.telemetry.GroupOperationsListener;
import ngat.sms.GroupItem;
import ngat.tcm.TelescopeStatus;
import ngat.tcm.TelescopeStatusUpdateListener;

/**
 * @author eng
 *
 */
public class TopPanel extends JPanel implements GroupOperationsListener, TelescopeStatusUpdateListener {


	private ISite site;
	
    private ObservationSummaryPanel observationSummaryPanel;
	private AuxSystemsSummaryPanel auxSystemsSummaryPanel;
	
    /**
	 * 
	 */
	public TopPanel(ISite site) {
		super(true);
		this.site = site;
		
		setBorder(BorderFactory.createLoweredBevelBorder());		
		setLayout(new FlowLayout(FlowLayout.LEADING));
		
		observationSummaryPanel = new ObservationSummaryPanel("Observation");
		observationSummaryPanel.createPanel();
		add(observationSummaryPanel);
		
		auxSystemsSummaryPanel = new AuxSystemsSummaryPanel("Auxilliary Systems");
		auxSystemsSummaryPanel.createPanel();
		add(auxSystemsSummaryPanel);
		
		// TEST PANELS TO WIDEN THE TOPPANEL
		
		// add(new TrackingSummaryPanel(site));
		
		TestPanel1 tp1 = new TestPanel1("Disk Monitoring");
		tp1.createPanel();
		add(tp1);
		
		TestPanel2 tp2 = new TestPanel2("Test2");
		tp2.createPanel();
		add(tp2);
		
	}
	
	public ObservationSummaryPanel getObservationSummaryPanel() { return observationSummaryPanel;}
	
	public AuxSystemsSummaryPanel getAuxSystemsSummaryPanel() { return auxSystemsSummaryPanel;}
	
	/* (non-Javadoc)
	 * @see ngat.rcs.telemetry.GroupOperationsListener#groupCompleted(ngat.sms.GroupItem, ngat.phase2.IExecutionFailureContext)
	 */
	@Override
	public void groupCompleted(GroupItem arg0, IExecutionFailureContext arg1) throws RemoteException {
		// TODO Auto-generated method stub

	}

	/* (non-Javadoc)
	 * @see ngat.rcs.telemetry.GroupOperationsListener#groupSelected(ngat.sms.GroupItem)
	 */
	@Override
	public void groupSelected(GroupItem group) throws RemoteException {
		observationSummaryPanel.updateGroup(group);
	}

	@Override
	public void telescopeNetworkFailure(long arg0, String arg1)
			throws RemoteException {
		// do nothing here
	}

	@Override
	public void telescopeStatusUpdate(TelescopeStatus arg0)
			throws RemoteException {
		// TODO update tracking panel
		
	}

}
