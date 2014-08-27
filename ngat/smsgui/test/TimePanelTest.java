/**
 * 
 */
package ngat.smsgui.test;

import java.util.List;
import java.util.Vector;

import javax.swing.JFrame;

import ngat.astrometry.BasicSite;
import ngat.astrometry.ISite;
import ngat.phase2.XTimePeriod;
import ngat.sms.Disruptor;
import ngat.smsgui.DayNightPanel;
import ngat.smsgui.DisruptorPanel;
import ngat.smsgui.MoonPanel;
import ngat.smsgui.TimePanel;

/**
 * @author eng
 *
 */
public class TimePanelTest {

	/**
	 * @param args
	 */
	public static void main(String[] args) {
	
		TimePanel tp = new TimePanel();
		
		long start = System.currentTimeMillis() - 9*3600*1000L;
		long end = start + 18*3600*1000L;
		tp.setTimeLimits(start, end);
		
		double lat = Math.toRadians(28.0);
		double lon = Math.toRadians(-2.0);
		
		ISite site = new BasicSite("obs", lat, lon);
		DayNightPanel dnp = new DayNightPanel(tp, site);
		tp.addCategoryPanel(dnp);
		
		MoonPanel mp = new MoonPanel(tp, site);
		tp.addCategoryPanel(mp);
		
		// some disruptors	
		List<Disruptor> dlist = new Vector<Disruptor>();
		for (int i=0; i < 10; i++) {
			long t1 = start+ (long)(Math.random()*(end-start));
			long t2 = t1 + (long)(Math.random()*3600000.0);
			Disruptor d = new Disruptor("test", "testclass", new XTimePeriod(t1, t2));
			dlist.add(d);
		}
		DisruptorPanel dp = new DisruptorPanel(tp, dlist);
		tp.addCategoryPanel(dp);
		
		JFrame f = new JFrame("Time test");
		f.getContentPane().add(tp);
		f.pack();
		f.setBounds(50,50,1000,80);
		f.setVisible(true);
		
	}

}
