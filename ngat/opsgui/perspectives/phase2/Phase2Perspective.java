/**
 * 
 */
package ngat.opsgui.perspectives.phase2;

import java.awt.BorderLayout;

import javax.swing.JFrame;
import javax.swing.JTabbedPane;
import javax.swing.SwingConstants;

import ngat.astrometry.ISite;
import ngat.opsgui.base.Perspective;
import ngat.opsgui.services.ServiceAvailabilityListener;
import ngat.sms.GroupItem;

/**
 * @author eng
 *
 */
public class Phase2Perspective extends Perspective implements Phase2CacheUpdateListener, ServiceAvailabilityListener {
	
	private ISite site;
	
	/** Phase2 tab panel.*/
	private JTabbedPane phase2Pane;
	
	private Phase2TreePane treePane;

	private Phase2GroupDisplayPane groupDisplayPane;
	
	private Phase2GroupFeasibilityPane feasibilityPane;
	
	private Phase2TimeSlicePane timeSlicePane;
	
	private GroupSelection selection;
	
	/**
	 * @param frame
	 */
    public Phase2Perspective(JFrame frame, ISite site) {
		super(frame);
		this.site = site;
		perspectiveName = "P";
		setLayout(new BorderLayout());

		selection = new GroupSelection();
		
		phase2Pane = new JTabbedPane(SwingConstants.TOP, SwingConstants.HORIZONTAL);
		
		treePane = new Phase2TreePane(selection);	
		phase2Pane.addTab("Tree", treePane);

		groupDisplayPane = new Phase2GroupDisplayPane();
		phase2Pane.addTab("Group", groupDisplayPane);
		selection.addGroupSelectionListener(groupDisplayPane);
		
		feasibilityPane = new Phase2GroupFeasibilityPane(site);
		phase2Pane.addTab("Feasibility", feasibilityPane);
		selection.addGroupSelectionListener(feasibilityPane);
		
		timeSlicePane = new Phase2TimeSlicePane(site);
		phase2Pane.addTab("Slice", timeSlicePane);
		selection.addGroupSelectionListener(timeSlicePane);
		
		// MAYBE put in super constructor ????
		createMenus();

		add(phase2Pane, BorderLayout.CENTER);

		Phase2.getPhase2().addPhase2CacheUpdateListener(treePane);
		
		
	}

	protected void createMenus() {}
	
	// various add methods to add proposal and group info and nodes
	// groups hang off proposals.
	
	public void addGroup(GroupItem group) {
		//
	}

	@Override
	public void phase2GroupAdded(GroupItem group) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void serviceAvailable(String serviceName, long time, boolean available) {
		// TODO Auto-generated method stub
		
	}
	
	
	
	
}
