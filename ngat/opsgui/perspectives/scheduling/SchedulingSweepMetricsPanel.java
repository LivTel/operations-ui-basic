package ngat.opsgui.perspectives.scheduling;

import java.awt.BorderLayout;
import java.util.List;

/** Displays candidates during a despatcher sweep.
 * @author eng
 *
 */
public class SchedulingSweepMetricsPanel extends SchedulingSweepDataDisplayPanel {

    /**
     * @param sweeps
     */
    public SchedulingSweepMetricsPanel(List<SweepEntry> sweeps) {
	super(sweeps);
	
	setLayout(new BorderLayout());
	
    }
    
    
    
    @Override
	public void displaySweep(int d, int l, boolean s) {
	// TODO Auto-generated method stub
	
    }


	

}
