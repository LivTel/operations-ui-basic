/**
 * 
 */
package ngat.opsgui.perspectives.scheduling;

import java.util.List;

import javax.swing.JPanel;

/**
 * @author eng
 *
 */
public abstract class SchedulingSweepDataDisplayPanel extends JPanel implements SweepDisplay {

	/** the sweep data vector.*/
	protected List<SweepEntry> sweeps;
	
	/**
	 * @param sweeps
	 */
	public SchedulingSweepDataDisplayPanel(List<SweepEntry> sweeps) {
		super(true);
		this.sweeps = sweeps;
	}	

}
