/**
 * 
 */
package ngat.opsgui.test;

import java.awt.BorderLayout;

import javax.swing.JFrame;
import javax.swing.SwingUtilities;

import ngat.astrometry.BasicSite;
import ngat.astrometry.ISite;
import ngat.opsgui.base.Resources;
import ngat.opsgui.perspectives.phase2.Phase2GroupFeasibilityPane;
import ngat.phase2.ISequenceComponent;
import ngat.phase2.XExecutiveComponent;
import ngat.phase2.XExtraSolarTarget;
import ngat.phase2.XGroup;
import ngat.phase2.XIteratorComponent;
import ngat.phase2.XIteratorRepeatCountCondition;
import ngat.phase2.XMonitorTimingConstraint;
import ngat.phase2.XTargetSelector;
import ngat.sms.GroupItem;

/**
 * @author eng
 *
 */
public class CreateFeasibilityPanel {

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		
		Resources.setDefaults("/home/eng/rcsgui");
		
		XGroup g = new XGroup();
		g.setName("TestGroup");
		
		long s = System.currentTimeMillis()-3*86400*1000L;
		long e = System.currentTimeMillis()+3*86400*1000L;
		
		long p = 6*3600*1000L;
		long w = 3*3600*1000L;
		
		XMonitorTimingConstraint xmon = new XMonitorTimingConstraint(s,e,p,w);
		g.setTimingConstraint(xmon);
		
		XIteratorComponent root = new XIteratorComponent("root", new XIteratorRepeatCountCondition(1));
		
		XExtraSolarTarget star = new XExtraSolarTarget("star");
		star.setRa(Math.random()*Math.PI*2.0);
		star.setDec(Math.random()*0.5*Math.PI);
		
		ISequenceComponent starget = new XExecutiveComponent("", new XTargetSelector(star));
		root.addElement(starget);
				
		GroupItem group = new GroupItem(g, root);
		
		ISite site = new BasicSite("", Math.toRadians(28.0), Math.toRadians(-17.0));
		
		Phase2GroupFeasibilityPane fp = new Phase2GroupFeasibilityPane(site);
		
		JFrame f = new JFrame("Feasible Panel");
		f.getContentPane().setLayout(new BorderLayout());
		f.getContentPane().add(fp);
		
		f.pack();
		f.setVisible(true);
		
		final Phase2GroupFeasibilityPane  ffp = fp;
		final GroupItem fgroup = group;
		SwingUtilities.invokeLater(new Runnable() {
			@Override
			public void run() {
				ffp.updateGroup(fgroup);
				ffp.displayGroup(System.currentTimeMillis(), System.currentTimeMillis()+24*3600*1000L);
				ffp.repaint();
			}
		});
	
			
		
		
	}

}
