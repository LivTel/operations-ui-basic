/**
 * 
 */
package ngat.rcsgui.stable;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.SimpleTimeZone;

import javax.swing.JPanel;

import ngat.rcsgui.test.TimeScale;

/**
 * @author eng
 *
 */
public class TimeAxisRenderer extends JPanel {
	
	public static final SimpleTimeZone UTC = new SimpleTimeZone(0, "UTC");
	public static SimpleDateFormat sdf = new SimpleDateFormat("HH:mm");
	
	/** Records the start time of this renderer.*/
	Calendar cal;
	
	/** The total period the axis represents.*/
	private TimeScale scale;
	
	/**
	 * 
	 */
	public TimeAxisRenderer(TimeScale scale) {
		super(true);
		this.scale = scale;
		cal = Calendar.getInstance();
		cal.setTimeZone(UTC);
		sdf.setTimeZone(UTC);
	}

	/* (non-Javadoc)
	 * @see javax.swing.JComponent#paint(java.awt.Graphics)
	 */
	@Override
	public void paint(Graphics g) {	
		super.paint(g);

		g.setColor(Color.black);
		
		int w = getSize().width;
		int h = getSize().height;
		
		g.drawLine(100, 0, w, 0);
		
		//long now = System.currentTimeMillis();
		long start = scale.getStart();
		long end = scale.getEnd();
		long period = end-start;
		
		cal.setTimeInMillis(end);
		
		cal.set(Calendar.MINUTE, 0);
		cal.set(Calendar.SECOND, 0);
		cal.set(Calendar.MILLISECOND, 0);
		
		long lastHour = cal.getTimeInMillis();
		
		// now backup to first hour on or after last - p + 1 hour
		long firstHour = lastHour - period;
		cal.setTimeInMillis(firstHour);
		cal.set(Calendar.MINUTE, 0);
		cal.set(Calendar.SECOND, 0);
		cal.set(Calendar.MILLISECOND, 0);
		firstHour = cal.getTimeInMillis();
		
		
		long t = firstHour;
		while (t <= lastHour) {			
			
			int atx = 100 + (int)((double)(w-100)*(double)(t - start)/ (period));
			String strtime = sdf.format(new Date(t));
			g.drawString(strtime, atx-25, 25);
			g.drawLine(atx, 0, atx, 5);
			t += 3600*1000L;
		}
		
		
	}

	
	/* (non-Javadoc)
	 * @see javax.swing.JComponent#getPreferredSize()
	 */
	@Override
	public Dimension getPreferredSize() {
		// TODO Auto-generated method stub
		return new Dimension(500, 25);
	}

	
}
