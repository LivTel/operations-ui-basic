/**
 * 
 */
package ngat.opsgui.perspectives.operations;

import java.awt.BorderLayout;
import java.rmi.RemoteException;

import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JTabbedPane;
import javax.swing.SwingConstants;

import ngat.opsgui.base.Perspective;
import ngat.opsgui.services.ServiceAvailabilityListener;
import ngat.opsgui.xcomp.GroupDisplayPanel;
import ngat.opsgui.xcomp.OperationsHistoryPanel;
import ngat.rcs.ops.OperationsEvent;
import ngat.rcs.ops.OperationsEventListener;
import ngat.rcs.ops.OperationsModeChangedEvent;
import ngat.rcsgui.stable.ColorStatePanel2;
import ngat.rcsgui.stable.ObservationPanel;
import ngat.rcsgui.stable.OperationsPanel;
import ngat.rcsgui.stable.RcsStatePanel;

/**
 * @author eng
 *
 */
public class OperationsPerspective extends Perspective implements OperationsEventListener, ServiceAvailabilityListener {
	
	/** Scheduling master tab panel. */
	private JTabbedPane operationsPane;
	
	private ColorStatePanel2 csp;
	private RcsStatePanel rsp;
	private OperationsPanel opspanel;
	//private ObservationPanel obspanel;
	private GroupDisplayPanel gdp; // TESTING
	private OperationsHistoryPanel ohx;
	
	/** Create the Ops perspective. unusual in that most of its sub-panels are pre-built and come with their
	 * own updating mechanisms.
	 * @param frame
	 */
	public OperationsPerspective(JFrame frame, ColorStatePanel2 csp, OperationsPanel opspanel, RcsStatePanel rsp, GroupDisplayPanel gdp) {
		super(frame);
		this.csp = csp;
		this.opspanel = opspanel;
		//this.obspanel = obspanel;
		this.rsp = rsp;
		this.gdp = gdp;
		
		perspectiveName = "O";
		
		setLayout(new BorderLayout());

		operationsPane = new JTabbedPane(SwingConstants.TOP, SwingConstants.HORIZONTAL);
		
		// Old History and current state 
		JPanel hpanel = new JPanel();
		hpanel.setLayout(new BorderLayout());
		hpanel.add(csp, BorderLayout.CENTER);
		hpanel.add(rsp,BorderLayout.NORTH);
		operationsPane.addTab("History", hpanel);
		
		// new history panel - not actively updating
		// - needs more work on feeds due to irregular rate of updates
		ohx = new OperationsHistoryPanel();
		operationsPane.addTab("H2", ohx);
		
		// Ops table view
		operationsPane.addTab("Table", opspanel);
		
		// Old Group group details display
		//operationsPane.addTab("Current", obspanel);
		
		// Group group details display
		operationsPane.addTab("Current", gdp);
		
		add(operationsPane, BorderLayout.CENTER);
		
	}

	/** Create menus. */

	private void createMenus() {

	}

	@Override
	public void serviceAvailable(String serviceName, long time, boolean available) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void operationsEventNotification(OperationsEvent oe)
			throws RemoteException {
		
		System.err.println("OPS: Received: "+oe);
		
		if (oe instanceof OperationsModeChangedEvent) {
			OperationsModeChangedEvent omc = (OperationsModeChangedEvent)oe;
			
			String oldm = omc.getOldMode();
			String newm = omc.getNewMode(); // update Ops2 panel
			
			int mode = 0;
			if (newm.equalsIgnoreCase("SOCA"))
				mode = 1;
			else if
			(newm.equalsIgnoreCase("BGCA"))
				mode = 2;
			else if
			(newm.equalsIgnoreCase("CAL"))
				mode = 3;
			else if
			(newm.equalsIgnoreCase("TOCA"))
				mode = 6;
			
			ohx.updateState(mode);
		}
			
		
	}

	
}
