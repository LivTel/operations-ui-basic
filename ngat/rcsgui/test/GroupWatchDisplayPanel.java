/**
 * 
 */
package ngat.rcsgui.test;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.util.Calendar;
import java.util.List;
import java.util.Vector;

import javax.swing.JPanel;

import ngat.sms.GroupItem;

/** A panel to display a group's schedule history in the night
 * @author eng
 *
 */
public class GroupWatchDisplayPanel extends JPanel {

	public static Dimension SIZE = new Dimension(350, 300);
	
	public static final double MAX_SCORE = 1.0;
	public static final int HX = 30;
	public static final int BY = 40;
	public static final int TY = 30;
	
	private long start;	
	private long end;
	
	private GroupItem group;

	private List<WatchEntry> entries;
	
	
	/**
	 * @param group
	 * @param start
	 * @param end
	 */
	public GroupWatchDisplayPanel(GroupItem group,long start, long end) {
		super(true);
		this.group = group;
		this.start = start;
		this.end = end;
		entries= new Vector<WatchEntry>();
	}

	@Override
	public Dimension getPreferredSize() {
		return SIZE;
	}


	/* (non-Javadoc)
	 * @see javax.swing.JComponent#paint(java.awt.Graphics)
	 */
	@Override
	public void paint(Graphics g) {		
		super.paint(g);
		
		int w = getSize().width;
		int h = getSize().height;
	
		// draw a grid maybe?
		
		// draw the time axis (should be roughly a whole night)
		Calendar cal = Calendar.getInstance();
		cal.setTimeInMillis(start);
		cal.set(Calendar.MINUTE, 0);
		cal.set(Calendar.SECOND, 0);
		cal.add(Calendar.HOUR_OF_DAY, 1);
		long th0 = cal.getTimeInMillis();
		cal.setTimeInMillis(end);
		cal.set(Calendar.MINUTE, 0);
		cal.set(Calendar.SECOND, 0);
		long th1 = cal.getTimeInMillis();
	
		System.err.printf("%tT %tT  ",th0, th1);
		long tc = th0;
		while (tc <= th1) {		
			g.setColor(Color.black);
			int xx = HX + (int)((double)(w-2*HX)*(double)(tc-start)/(end-start));
			g.drawLine(xx, h-BY, xx, h-BY+10);
			g.drawString(String.format("%tH:%tM", tc, tc), xx-20, h-BY+25);
			System.err.printf("%tH:%tM\n",tc, tc);
			// grid lines
			g.setColor(Color.cyan);
			g.drawLine(xx, TY, xx, h-BY);
			tc += 7200000L;
		}
				
		// draw the score axis
		double s = 0.0;
		while (s <= MAX_SCORE) {
			g.setColor(Color.black);
			int yy = TY + (int)((h-TY-BY)*(1.0 - s/MAX_SCORE));
			g.drawLine(HX-5, yy, HX, yy);
			s+=0.1;
		}
		s = 0.0;
		while (s <= MAX_SCORE) {
			g.setColor(Color.black);
			int yy = TY + (int)((h-TY-BY)*(1.0 - s/MAX_SCORE));
			g.drawLine(HX-10, yy, HX, yy);
			g.drawString(String.format("%1.1f", s), HX-30, yy-10);
			g.setColor(Color.cyan);
			g.drawLine(HX, yy, w-HX, yy);
			s+=0.2;
		}
		
		// draw a border
		g.setColor(Color.black);
		g.drawRect(HX, TY, w-2*HX, h-TY-BY);
		
		
		// write the group's name somewhere
		 
		// run over entries and plot something
		for (int i = 0; i < entries.size(); i++) {
			
			WatchEntry we = entries.get(i);
			long t = we.time;
			
			int xx = HX + (int)((double)(w-2*HX)*(double)(t-start)/(end-start));
			int yy = TY + (int)((h-TY-BY)*(1.0 - we.score/MAX_SCORE));
			int wy = TY + (int)((h-TY-BY)*(1.0 - we.wscore/MAX_SCORE));
			if (we.selected) {
				g.setColor(Color.magenta);
				g.fillOval(xx-3, yy-3, 6, 6);				
			} else {
				g.setColor(Color.red);
				g.drawRect(xx-3, yy-3, 6, 6);
				// DONT draw the error between gscore and max score for now, later we might plot wscore seperately
				//g.setColor(Color.blue);
				//g.drawLine(xx, yy, xx, wy);
				//g.drawLine(xx-5, wy, xx+5, wy);
			}
			// for non-candidate entries we will use a special symbol on a special level
			// with a tooltip or mouselistener which can retrieve the relevant information
			
		}
		
	}
	
	public void clearWatchEntries() {
		entries.clear();
	}
	
	public void addWatchEntry(long time, boolean selected, double score, String errorTypeName, double wscore) {
		WatchEntry w = new WatchEntry(time, selected, score, errorTypeName, wscore);
		entries.add(w);
		System.err.println("new watch entry");
		repaint();
	}
	
	
	
	
}
