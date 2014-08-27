/**
 * 
 */
package ngat.opsgui.test;

import java.awt.BorderLayout;
import java.awt.Color;

import javax.swing.JFrame;

import ngat.opsgui.util.StateColorMap;
import ngat.opsgui.util.TimeSlicePanel;

/**
 * @author eng
 *
 */
public class CreateTimeSlicer {

	/**
	 * @param args
	 */
	public static void main(String[] args) {
	
		try {
		
			StateColorMap map = new StateColorMap(Color.pink, "unknown");
			map.addColorLabel(1, Color.red, "BAD");
			map.addColorLabel(2, Color.blue, "OK");
			map.addColorLabel(3, Color.green, "FAB");
			map.addColorLabel(4, Color.orange, "WEIRD");
			map.addColorLabel(5, Color.magenta, "NASTY");
			
			int nd = 90;
			
			long now = System.currentTimeMillis();
			TimeSlicePanel tsp = new TimeSlicePanel("Testing the time-slicer", now, 17, 8, nd);
			tsp.setMap(map);
			
			JFrame f = new JFrame("Time slice test");
			f.getContentPane().setLayout(new BorderLayout());
			f.getContentPane().add(tsp);
			f.pack();
			f.setVisible(true);
			
			long start = now;
			long time = start;
			
			int state = (int)(Math.random()*5.0)+1;
			while (time < start + nd * 24*3600*1000L) {
			
				if (Math.random() < 0.08)
					state = (int)(Math.random()*5.0)+1;
			
				tsp.addHistory(time, state);	
				
				time += 60*1000L;
			}
		
			System.err.printf("Last time: %tF %tT \n",time, time);
		} catch (Exception e) {
			e.printStackTrace();
		}

	}

}
