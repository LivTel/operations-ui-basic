/**
 * 
 */
package ngat.opsgui.perspectives.reactive;

import java.awt.BorderLayout;
import java.rmi.RemoteException;

import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.JTabbedPane;
import javax.swing.JTextField;
import javax.swing.SwingConstants;

import ngat.opsgui.base.Perspective;
import ngat.opsgui.services.ServiceAvailabilityListener;
import ngat.rcs.ers.ReactiveSystemStructureProvider;
import ngat.rcs.ers.ReactiveSystemUpdateListener;

/**
 * @author eng
 *
 */
public class ReactiveSystemPerspective extends Perspective implements
		ReactiveSystemUpdateListener, ServiceAvailabilityListener {
	
	private ReactiveSystemStructureProvider ersStructureProvider;
	
	private ReactiveSystemFilterDisplayPanel fdp;
	
	private ReactiveSystemCriterionDisplayPanel cdp;
	
	private ReactiveSystemRuleDisplayPanel rdp;
	
	private ReactiveSystemTreePanel rtp;
	
	/** Master tab panel. */
	private JTabbedPane reactivePane;
	/**
	 * @param frame
	 */
	public ReactiveSystemPerspective(JFrame frame, ReactiveSystemStructureProvider ersStructureProvider) throws Exception {
		super(frame);
		this.ersStructureProvider = ersStructureProvider;
	
		perspectiveName = "R";
		
		setLayout(new BorderLayout());

		reactivePane = new JTabbedPane(SwingConstants.TOP, SwingConstants.HORIZONTAL);
		
		// add various tab panes to reactive pane
		
		// Reactive systems summary - 
		ReactiveSystemsSummaryPanel rsp = new ReactiveSystemsSummaryPanel(ersStructureProvider);
		
		reactivePane.addTab("Summary", rsp);
		
		// Filter displays
		fdp = new ReactiveSystemFilterDisplayPanel(ersStructureProvider);
		
		reactivePane.addTab("Filters", fdp);
		
		// Crits
		cdp = new ReactiveSystemCriterionDisplayPanel(ersStructureProvider);
		
		reactivePane.addTab("Criteria", cdp);
		
		// Rules
		rdp = new ReactiveSystemRuleDisplayPanel(ersStructureProvider);
		
		reactivePane.addTab("Rules", rdp);
		
		try {
		// Reactive systems network layout as tree
		rtp = new ReactiveSystemTreePanel(ersStructureProvider);
		
		reactivePane.addTab("Tree", rtp);
		} catch (Exception e) {
			e.printStackTrace();
			System.err.println("Erorr creating rtp");
			JOptionPane.showMessageDialog(null, "RTP Error "+e.fillInStackTrace());
		}
		
		add(reactivePane, BorderLayout.CENTER);
		
	}
	
	/** Create menus. */

	private void createMenus() {

	}

	/* (non-Javadoc)
	 * @see ngat.opsgui.services.ServiceAvailabilityListener#serviceAvailable(java.lang.String, long, boolean)
	 */
	@Override
	public void serviceAvailable(String serviceName, long time,
			boolean available) {
		// TODO Auto-generated method stub

	}

	@Override
	public void criterionUpdated(String critName, long time, boolean satisfied)
			throws RemoteException {
		cdp.handleCriterionUpdate(critName, time, satisfied);
		
	}

	@Override
	public void filterUpdated(String filterName, long time, Number input, Number output)
			throws RemoteException {
		fdp.handleFilterUpdate(filterName, time, input, output);
		
	}

	@Override
	public void ruleUpdated(String ruleName, long time, boolean ruleTriggered)
			throws RemoteException {
		rdp.handleRuleUpdate(ruleName, time, ruleTriggered);
		
	}
	
	

}
