/**
 * 
 */
package ngat.opsgui.test;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;

import javax.swing.JFrame;
import javax.swing.JLabel;

import ngat.opsgui.util.StateColorMap;
import ngat.opsgui.util.StatusHistoryPanel;
import ngat.opsgui.util.TimeDisplayController;

/** Test the state history panel
 * @author eng
 *
 */
public class StateHistoryTest {

	/**
	 * @param args
	 */
	public static void main(String[] args) {
	
		TimeDisplayController tdc = new TimeDisplayController(300000L);
		
		StatusHistoryPanel p = new StatusHistoryPanel(tdc);
		tdc.addTimeDisplay(p);
		
		StateColorMap map = new StateColorMap(Color.cyan, "UNKNOWN");	
		map.addColorLabel(1, Color.pink, "1");
		map.addColorLabel(2, Color.blue, "2");
		map.addColorLabel(3, Color.yellow, "3");
		map.addColorLabel(4, Color.magenta, "4");
		
		p.setMap(map);
		p.setPreferredSize(new Dimension(200, 20));
		
		JFrame f = new JFrame("State history test");
		f.getContentPane().setLayout(new BorderLayout());
		f.getContentPane().add(p, BorderLayout.CENTER);
		f.getContentPane().add(new JLabel("Status"), BorderLayout.WEST);
		
		f.pack();
		f.setVisible(true);
		
		// start adding statii
		
		int state = 1;
		for (int i = 0; i < 1000; i++) {
			try {Thread.sleep(1000L);} catch (InterruptedException ix){}
			
			
			
			long now = System.currentTimeMillis();
			if (Math.random() > 0.98) 
				state = (int)(Math.random()*5.0);
			System.err.println("Add state: "+state);
			p.addHistory(now, state);
			p.repaint();
		}
		
		
	}

}
