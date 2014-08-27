/**
 * 
 */
package ngat.rcsgui.test;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.util.List;
import java.util.Vector;

import javax.swing.JPanel;
import javax.swing.JTabbedPane;
import javax.swing.SwingConstants;

import ngat.rcsgui.stable.ScheduleCandidatePanel;
import ngat.sms.GroupItem;
import ngat.sms.ScheduleItem;

/**
 * @author eng
 *
 */
public class ScheduleSweepMasterPanel extends JPanel {

	JTabbedPane tabs;
	
	List<Candidate> candidates;
	
	int sn;
	
	public ScheduleSweepMasterPanel() {
		super(true);
		
		setLayout(new BorderLayout());
		setPreferredSize(new Dimension(600, 420));
		tabs = new JTabbedPane(SwingConstants.NORTH);
		candidates = new Vector<Candidate>();
		
		add(tabs, BorderLayout.CENTER);
	}
	
	/** Start a new sweep. Starts to record information and may create a new Tab.
	 * @param sn The sweep number.
	 */
	public void startSweep(int sn) {
		candidates.clear();
		this.sn = sn;
	}
	
	public void addCandidate(GroupItem group, double score) {
		candidates.add(new Candidate(group, score));
	}
	
	public void sweepCompleted(ScheduleItem sched) {
		
		// no schedule generated, no new tab
		if (sched == null)
			return;
		
		ScheduleCandidatePanel scp = new ScheduleCandidatePanel();
		for (int ic = 0; ic < candidates.size(); ic++) {
			Candidate c = candidates.get(ic);
			
			scp.candidateAdded(c.group, c.score);
		}
		
		tabs.addTab(""+sn, scp);
		
	}
	
	
	private class Candidate {
		
		public GroupItem group;
		
		public double score;

		/**
		 * @param group
		 * @param score
		 */
		public Candidate(GroupItem group, double score) {
			super();
			this.group = group;
			this.score = score;
		}
		
	}
	
}
