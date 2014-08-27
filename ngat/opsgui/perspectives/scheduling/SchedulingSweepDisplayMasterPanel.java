/**
 * 
 */
package ngat.opsgui.perspectives.scheduling;

import java.awt.BorderLayout;
import java.util.List;

import javax.swing.JPanel;

/**
 * @author eng
 *
 */
public class SchedulingSweepDisplayMasterPanel extends JPanel implements SweepDisplay {

	private List <SweepEntry> sweeps;
	
	/** Controls and synchronizes the display.*/
	private SchedulingSweepNavigationController controller;
	
	/** Navigation panel.*/
	private SchedulingSweepNavigationPanel navigationPanel;
	
	/** The data display panel.*/
	private SchedulingSweepDataDisplayPanel dataPanel;
	
	/**
	 * @param sweeps
	 */
	public SchedulingSweepDisplayMasterPanel(SchedulingSweepNavigationController controller, SchedulingSweepDataDisplayPanel dataPanel) {
		super(true);
		this.controller = controller;
		this.sweeps = sweeps;
		this.dataPanel = dataPanel;

		setLayout(new BorderLayout());
		
		navigationPanel = new SchedulingSweepNavigationPanel(controller);
		
		add(dataPanel, BorderLayout.CENTER);
		add(navigationPanel, BorderLayout.NORTH);
		
	}

	@Override
	public void displaySweep(int d, int l, boolean s) {
		navigationPanel.displaySweep(d, l, s);
		dataPanel.displaySweep(d, l, s);
	}


}
