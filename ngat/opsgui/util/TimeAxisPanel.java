/**
 * 
 */
package ngat.opsgui.util;

import java.awt.Color;
import java.awt.Graphics;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.SimpleTimeZone;

import javax.swing.JPanel;

/**
 * @author eng
 * 
 */
public class TimeAxisPanel extends JPanel implements TimeDisplay {

	public static final SimpleTimeZone UTC = new SimpleTimeZone(0, "UTC");
	public static final SimpleDateFormat sdf = new SimpleDateFormat("HH:mm");
	public static final SimpleDateFormat daydf = new SimpleDateFormat("dd");
	public static final SimpleDateFormat monthdf = new SimpleDateFormat("dd-MMM-yy");
	
	/** The current (latest) time to display. */
	// private long time;

	private Calendar scalendar;
	private Calendar ecalendar;
	
	private long start;
	
	private long end;
	
	/**
	 * @param duration
	 */
	public TimeAxisPanel() {
		super(true);
	
		scalendar = Calendar.getInstance();
		scalendar.setTimeZone(UTC);
		ecalendar = Calendar.getInstance();
		ecalendar.setTimeZone(UTC);
		sdf.setTimeZone(UTC);
		daydf.setTimeZone(UTC);
		monthdf.setTimeZone(UTC);
		
		// default to next hour
		start = System.currentTimeMillis();
		end = System.currentTimeMillis() + 3600*1000L;
		
	}

    @Override
	public void displayTime(long windowStart, long windowEnd) {
    	start = windowStart;
    	end = windowEnd; 	
    	//System.err.println("TAP:display "+start+" - "+end);
    	repaint();
    }


	/*
	 * (non-Javadoc)
	 * 
	 * @see javax.swing.JComponent#paint(java.awt.Graphics)
	 */
	@Override
	public void paint(Graphics g) {
		super.paint(g);
		long time = start;

		long duration = end - start;
		
		//System.err.println("TDC: paint: "+new Date(start)+" "+new Date(end));
		
		int ww = getSize().width;
		int hh = getSize().height;

		// find the most recent hour before end time
		ecalendar.setTimeInMillis(end);
		int ehour = ecalendar.get(Calendar.HOUR_OF_DAY);
		
		int eayear = ecalendar.get(Calendar.YEAR);
		int eamonth = eayear*12+ecalendar.get(Calendar.MONTH);
		int eaweek = eayear*52+ecalendar.get(Calendar.WEEK_OF_YEAR);
		int eaday = eayear*365+ecalendar.get(Calendar.DAY_OF_YEAR);
		int eahour = eaday*24+ecalendar.get(Calendar.HOUR_OF_DAY);
		
		// zero the spare fields
		ecalendar.set(Calendar.MINUTE, 0);
		ecalendar.set(Calendar.SECOND, 0);
		ecalendar.set(Calendar.MILLISECOND, 0);

		scalendar.setTimeInMillis(start);
		int shour = scalendar.get(Calendar.HOUR_OF_DAY) + 1;
	
		int sayear = scalendar.get(Calendar.YEAR);
		int samonth = sayear*12+scalendar.get(Calendar.MONTH);
		int saweek = sayear*52+scalendar.get(Calendar.WEEK_OF_YEAR);
		int saday = sayear*365+scalendar.get(Calendar.DAY_OF_YEAR);
		int sahour = saday*24+scalendar.get(Calendar.HOUR_OF_DAY);
		
		// zero the spare fields
		scalendar.set(Calendar.HOUR_OF_DAY, shour);
		scalendar.set(Calendar.MINUTE, 0);
		scalendar.set(Calendar.SECOND, 0);
		scalendar.set(Calendar.MILLISECOND, 0);

		int ydiff = eayear - sayear;
		int mdiff = eamonth - samonth;
		int wdiff = eaweek - saweek;
		int ddiff = eaday - saday;
		int hdiff = eahour - sahour;
		
		System.err.printf("Diffs: Y(%4d) M(%4d) W(%4d) D(%4d) H(%4d)\n",ydiff, mdiff, wdiff, ddiff, hdiff);
		
		// TODO WE CANNOT COPE WITH DAY WRAPS YET !!!!
		// where shour > ehour

		scalendar.set(Calendar.HOUR_OF_DAY, 0);
		long t0 = scalendar.getTimeInMillis();

		// draw a base line
		g.setColor(Color.black);
		g.drawLine(0,  hh / 2, ww, hh / 2);
		//System.err.println("TAP:s = " + shour + " e=" + ehour);

		/*long t = t0;		
		while (t <= end) {
			int x = (int) ((double) ww * (double) (t - start) / (double) (end - start));
			g.drawLine(x, hh / 4, x, 3 * hh / 4);
			String text = sdf.format(t);
			g.drawString(text, x + 5, hh / 2);
			t = t + 1800 * 1000L;
			x = (int) ((double) ww * (double) (t - start) / (double) (end - start));
			g.drawLine(x, hh / 4, x, 3 * hh / 4);
			text = sdf.format(t);
			g.drawString(text, x + 5, hh / 2);
			t = t + 1800 * 1000L;
		}*/
		
		// stick the month in the middle - this will change - it has
		//g.drawString(monthdf.format((start+end)/2), ww/2, hh);
		
		long tday = duration;
		long t1 = 0L;
		long t2 = 0L;
		long t3 = 0L;
		
		if (duration > 14*24*3600*1000L) {
			// into boxes now....
			tday = 2*24*3600*1000L;
			paintBoxes(g, t0, end, tday);
			return;
	
		} else if 
		(duration > 7*24*3600*1000L) {
			// into boxes now....
			tday = 24*3600*1000L;
			paintBoxes(g, t0, end, tday);
			return;
		} else if 
		(duration >= 3*24*3600*1000L) {
			tday = 24*3600*1000L;
			t1 = 24*3600*1000L;
			t2 = 12*3600*1000L;
			t3 = 6*3600*1000L;
		} else if 
		(duration >= 24*3600*1000L) {
			t1 = 24*3600*1000L;
			t2 = 12*3600*1000L;
			t3 = 3*3600*1000L;
		} else if
		(duration >= 12*3600*1000L) {
			t1 = 3*3600*1000L;
			t2 = 1*3600*1000L;
			t3 = 1800*1000L;
		} else if
		(duration >= 6*3600*1000L) {
			t1 = 2*3600*1000L;
			t2 = 1*3600*1000L;
			t3 = 900*1000L;
		} else if
		(duration >= 3*3600*1000L) {
			t1 = 1*3600*1000L;
			t2 = 1800*1000L;
			t3 = 300*1000L;
		} else if
		(duration >= 1*3600*1000L) {
			t1 = 1800*1000L;
			t2 = 600*1000L;
			t3 = 120*1000L;
		} else if
		(duration >= 1800*1000L) {
			t1 = 600*1000L;
			t2 = 300*1000L;
			t3 = 60*1000L;
		} else if
		(duration >= 600*1000L) {
			t1 = 300*1000L;
			t2 = 120*1000L;
			t3 = 60*1000L;
		} else {
			t1 = 120*1000L;
			t2 = 60*1000L;
			t3 = 30*1000L;
		}
		paintAxis0(g, t0, end, t1, t2, t3);
		paintAxis1(g, t0, end, tday);
	}
	
	private void paintBoxes(Graphics g, long t0, long end, long tday) {
		
		// get bounds - these are for the whole plot not just this axis
		int ww = getSize().width;
		int hh = getSize().height;

		long t = t0;	
		while (t <= end) {
			// Day of week/month	
				int x = (int) ((double) ww * (double) (t - start) / (end - start));
				g.drawLine(x, 0, x, hh / 2);
				t = t + tday/2; // half way along box	
				String text = daydf.format(t);
				x = (int) ((double) ww * (double) (t - start) / (end - start));
				g.drawString(text, x , hh / 2 );
				
				t = t + tday/2;
		}
		
	}
	
	private void paintAxis0(Graphics g, long t0, long end, long t1, long t2, long t3) {
		
		// get bounds - these are for the whole plot not just this axis
		int ww = getSize().width;
		int hh = getSize().height;
		
		
		
		long t = t0;		
		while (t <= end) {
			// Primary		
				int x = (int) ((double) ww * (double) (t - start) / (end - start));
				g.drawLine(x, hh / 8, x, hh / 2);
				String text = sdf.format(t);
				g.drawString(text, x + 5, hh / 4);
				t = t + t1;
		}
		t = t0;		
		while (t <= end) {
			// Secondary		
				int x = (int) ((double) ww * (double) (t - start) / (end - start));
				g.drawLine(x, hh / 4, x, hh / 2);				
				t = t + t2;
		}
		t = t0;		
		while (t <= end) {
			// Tertiary		
				int x = (int) ((double) ww * (double) (t - start) / (end - start));
				g.drawLine(x, 3*hh / 8, x, hh / 2);				
				t = t + t3;
		}
	}
	
	private void paintAxis1(Graphics g, long t0, long end, long tday) {
		
		
		// get bounds - these are for the whole plot not just this axis
		int ww = getSize().width;
		int hh = getSize().height;
		
		// TODO we should alternate bg color for larger runs
		
		long t = t0;	
		while (t <= end) {
			// Day of week/month	
				int x = (int) ((double) ww * (double) (t - start) / (end - start));
				g.drawLine(x, hh / 2, x, hh);
				t = t + tday/2; // half way along box	
				String text = daydf.format(t);
				x = (int) ((double) ww * (double) (t - start) / (end - start));
				g.drawString(text, x , hh );
				
				t = t + tday/2;
		}
		
		
		
	}
	
}
