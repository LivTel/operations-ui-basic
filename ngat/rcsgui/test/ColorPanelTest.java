/**
 * 
 */
package ngat.rcsgui.test;

import java.util.Calendar;
import java.util.SimpleTimeZone;

import javax.swing.JFrame;

import ngat.smsgui.TimePanel;
import ngat.smsgui.TimeScalePanel;

/**
 * @author eng
 *
 */
public class ColorPanelTest {
	
	static SimpleTimeZone UTC = new SimpleTimeZone(0,"UTC");
	
	/**
	 * @param args
	 */
	public static void main(String[] args) {
		try {
			
			TimePanel tp = new TimePanel();
			
			TimeScalePanel tsp = new TimeScalePanel(tp);
			
			// work out where the limits are
			// at 1500 switch to night mode (1500 to 0900)
			// at 0900 switch to day mode (0300 to 2100)
			
			Calendar cal = Calendar.getInstance();
			cal.setTimeZone(UTC);
			Calendar sc = Calendar.getInstance();
			sc.setTimeZone(UTC);
			sc.set(Calendar.MINUTE, 0);
			sc.set(Calendar.SECOND, 0);
			
			Calendar ec = Calendar.getInstance();
			ec.setTimeZone(UTC);
			ec.set(Calendar.MINUTE, 0);
			ec.set(Calendar.SECOND, 0);
			
			int hh = cal.get(Calendar.HOUR_OF_DAY);
			
			if (hh > 9 && hh < 15) {
				// Day mode
				sc.set(Calendar.HOUR_OF_DAY, 3);
				ec.set(Calendar.HOUR_OF_DAY, 21);
			} else {
				// Night mode		
				sc.set(Calendar.HOUR_OF_DAY, 15);
				ec.set(Calendar.HOUR_OF_DAY, 9);
				if (hh >= 15 && hh <= 24) 
					// next day
					ec.roll(Calendar.DAY_OF_YEAR, true);
				else
					// prev day
					sc.roll(Calendar.DAY_OF_YEAR, false);				
			}
			
			long sd = sc.getTimeInMillis();
			long ed = ec.getTimeInMillis();
			
			tp.setTimeLimits(sd, ed);
			
			tp.addCategoryPanel(tsp);
			
			// make a nice little frame to display it all
			JFrame f = new JFrame("Time test");
			f.getContentPane().add(tp);
			f.pack();
			f.setBounds(50,50,1000,80);
			f.setVisible(true);
			
		} catch (Exception e) {
			e.printStackTrace();			
		}

	}

}
