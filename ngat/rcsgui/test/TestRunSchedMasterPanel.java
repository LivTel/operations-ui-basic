/**
 * 
 */
package ngat.rcsgui.test;

import java.awt.BorderLayout;

import javax.swing.JFrame;

import ngat.phase2.XGroup;
import ngat.phase2.XProposal;
import ngat.phase2.XTag;
import ngat.phase2.XUser;
import ngat.sms.GroupItem;
import ngat.sms.ScheduleItem;
import ngat.sms.bds.TestScheduleItem;

/** Test a ScheduleMasterPanel.
 * @author eng
 *
 */
public class TestRunSchedMasterPanel {

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		
		ScheduleSweepMasterPanel smp = new ScheduleSweepMasterPanel();
		
		JFrame f = new JFrame("Sweep Test");
		f.getContentPane().setLayout(new BorderLayout());
		f.getContentPane().add(smp, BorderLayout.CENTER);
		f.pack();
		f.setVisible(true);
		
		// setup some sweeps...
		
		for (int is = 0; is < 50; is++) {
			
			try {Thread.sleep(1000);} catch (InterruptedException ix) {}
			
			System.err.println("Start sweep: "+is);
			
			smp.startSweep(is);
			
			try {Thread.sleep(1000);} catch (InterruptedException ix) {}
			
			int nc = (int)(Math.random()*20.0);
			
			if (Math.random() > 0.75)
				nc = 0;
			
			if (nc == 0) {
				smp.sweepCompleted(null);
			} else {
				
				for (int ic = 0; ic < nc; ic++) {
					XGroup group = new XGroup();
					group.setName("Gr-"+is+"_"+ic);
					XProposal prop = new XProposal("Testing"+ic);
					prop.setPriority((int)(Math.random()*5.0));
					XTag tag = new XTag();
					tag.setName("JMU");
					XUser u = new XUser("Bert Smithers");
					GroupItem g = new GroupItem(group, null);
					g.setProposal(prop);
					g.setTag(tag);
					g.setUser(u);
					smp.addCandidate(g, Math.random()*3.0);
					System.err.println("Add candidate: "+group);
				}
				
				try {Thread.sleep(1000);} catch (InterruptedException ix) {}
				ScheduleItem sc = new TestScheduleItem(new GroupItem(new XGroup(), null), null);
				System.err.println("Sweep completed");
				smp.sweepCompleted(sc);
			}
			
		}
		
		
		
	}

}
