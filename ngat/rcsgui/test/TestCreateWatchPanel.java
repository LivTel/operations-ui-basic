/**
 * 
 */
package ngat.rcsgui.test;

import java.util.Vector;

import javax.swing.JFrame;

import ngat.phase2.XGroup;
import ngat.sms.GroupItem;

/** Create a watch panel
 * @author eng
 *
 */
public class TestCreateWatchPanel {

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		
		GroupWatchMasterPanel gmp = new GroupWatchMasterPanel();
		
		JFrame f = new JFrame("Test watch");
		f.getContentPane().add(gmp);
		f.pack();
		f.setVisible(true);
		
		Vector gs = new Vector();
		
		for (int i = 0; i < 5; i++) {
			XGroup g = new XGroup();
			g.setName("Testgroup:"+(int)(Math.random()*6000));	
			GroupItem gi = new GroupItem(g, null);
			gmp.addWatchGroup(gi);
			gs.add(gi);
			System.err.println("Adding group: "+g);
		}
		
		for (int j = 0; j < 400; j++) {
			
			// randomly pick a group from list
			int indx = (int)(Math.random()*5.0);
			GroupItem gi = (GroupItem)gs.get(indx);
			gmp.candidateUpdate(gi, false, Math.random());
			System.err.println("Adding update for: "+gi.getName());
			try {Thread.sleep(20000);} catch (InterruptedException d) {}
		}
		
		
		
		
	}

}
