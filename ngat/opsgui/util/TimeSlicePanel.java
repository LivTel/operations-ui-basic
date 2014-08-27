/**
 * 
 */
package ngat.opsgui.util;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Graphics;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.SimpleTimeZone;
import java.util.Vector;

import javax.swing.BorderFactory;
import javax.swing.JPanel;

/**
 * @author eng
 * 
 */
public class TimeSlicePanel extends JPanel {

	public static final int TOP = 40;
	public static final int BASE = 20;
	public static final int LEFT = 80;
	public static final int RIGHT = 20;

	public static final SimpleTimeZone UTC = new SimpleTimeZone(0, "UTC");
	public static final SimpleDateFormat sdf = new SimpleDateFormat("HH");
	public static final SimpleDateFormat ddf = new SimpleDateFormat("E (dd/MM)");

	/** Color mapping. */
	private StateColorMap map;

	private Calendar scalendar;
	private Calendar ecalendar;
	private Calendar tcalendar;

	/** The start date - any time during that day (or night). */
	private long start;

	/** Start hour of day. */
	private int hs;

	/** End hour of day. */
	private int he;

	/** Number of days to stack. */
	private int ndays;

	/** The plot title. */
	private String title;

	/** True if this is a night slide, otherwise a day slice. */
	private boolean night;

	/** Size of the slice in time. */
	private long timeSize;

	/** The time-sorted list of historic states. */
	private List<StateHistory> history;

	/**
	 * @param title
	 *            The title of the plot.
	 * @param start
	 *            The start date - any time during that day (or night).
	 * @param hs
	 *            Start hour of day.
	 * @param he
	 *            End hour of day.
	 * @param ndays
	 *            Number of days to stack.
	 */
	public TimeSlicePanel(String title, long start, int hs, int he, int ndays) {
		super();
		this.title = title;
		this.start = start;
		this.hs = hs;
		this.he = he;
		this.ndays = ndays;
		history = new Vector<StateHistory>();

		// day or night period ?
		if (hs > he) {
			night = true;
			int nh = 24 - hs + he;
			timeSize = 3600 * 1000 * (nh);
			System.err.println("TimeSlice: s=" + hs + " e=" + he + " nh=" + nh + " ts=" + timeSize);
		}

		scalendar = Calendar.getInstance();
		scalendar.setTimeZone(UTC);
		ecalendar = Calendar.getInstance();
		ecalendar.setTimeZone(UTC);
		tcalendar = Calendar.getInstance();
		tcalendar.setTimeZone(UTC);

		sdf.setTimeZone(UTC);
		ddf.setTimeZone(UTC);

		scalendar.setTimeInMillis(start);
		scalendar.set(Calendar.HOUR_OF_DAY, hs);
		scalendar.set(Calendar.MINUTE, 0);
		scalendar.set(Calendar.SECOND, 0);
		scalendar.set(Calendar.MILLISECOND, 0);

		this.start = scalendar.getTimeInMillis();
		System.err.printf("Start at: %tF %tT \n", start, start);
		setBorder(BorderFactory.createLineBorder(Color.black));
		setPreferredSize(new Dimension(500, 800));

	}

	/**
	 * @param map
	 *            the map to set
	 */
	public void setMap(StateColorMap map) {
		this.map = map;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see javax.swing.JComponent#paint(java.awt.Graphics)
	 */
	@Override
	public void paint(Graphics g) {
		super.paint(g);

		int ww = getSize().width - LEFT - RIGHT;
		int hh = getSize().height;

		// thickness of a slice
		int slice = (int) ((double) (hh - TOP - BASE) / (double) ndays);

		g.setColor(Color.black);

		// title
		g.setFont(new Font("serif", Font.PLAIN, 12));
		g.drawString(title, LEFT + ww / 3, 15);

		// hours axis
		g.setFont(new Font("serif", Font.PLAIN, 9));
		if (night) {
			long t = start;
			// System.err.printf("start axis: %tF %tT \n", t, t);
			while (t <= start + timeSize) {
				// System.err.printf("draw axis: %tF %tT \n", t, t);
				int x1 = LEFT + (int) ((double) (ww) * (double) (t - start) / (timeSize));
				g.drawLine(x1, TOP, x1, TOP - 5);
				g.drawString(sdf.format(new Date(t)), x1, TOP - 8);
				t += 3600 * 1000L;
			}

		} else {

		}

		// days axis - lets only display every mth time so we end up with roughly (10-20 total) ?
		int nmod = 1;
		if (ndays > 14) {
			nmod = (int)Math.rint(ndays/15.0)+1;
		}
		for (int id = 0; id < ndays; id++) {
			int y = TOP + id * slice;
			g.drawLine(LEFT, y, LEFT - 8, y);
			y += (int) (0.5 * slice);
			long t = start + id * 24 * 3600 * 1000L;
			if ((id % nmod) == 0)
			g.drawString(ddf.format(new Date(t)), LEFT - 58, y);
		}
		// last marker
		int y = TOP + ndays * slice;
		g.drawLine(LEFT, y, LEFT - 8, y);

		// data
		long tlast = start;
		int slast = -999;
		int ilast = -1;
		System.err.println("Plotting upto: "+history.size()+" items");
		for (int i = 0; i < history.size(); i++) {

			StateHistory h = history.get(i);
			long t = h.getTime();
			int s = h.getState();

			int is = (int) ((t - start) / (24 * 3600 * 1000.0));

			int yoffset = TOP + is * slice;

			long sstart = start + is * 24 * 3600 * 1000L;

			if (is != ilast) {
				System.err.println("Start new slice: "+is+" was "+ilast);
				tlast = sstart;
				slast = -999;
			}

			//Color color = map.getColor(slast);
			Color color = map.getColor(s);
			g.setColor(color);

			int x1 = LEFT + (int) ((double) (ww) * (double) (tlast - sstart) / (timeSize));
			int x2 = LEFT + (int) ((double) (ww) * (double) (t - sstart) / (timeSize));
			g.fillRect(x1, yoffset, x2 - x1, slice);

			tlast = t;
			slast = s;
			ilast = is;

		}

		g.setColor(Color.black);
		g.drawRect(LEFT, TOP, ww, ndays * slice);

	}

	public void addHistory(long time, int state) {
		// TODO allow merging of state series where no change
		// StateHistory last = history.get(history.size()-1);
		// if (last.getState() == state)
		// last.setTime(time);
		// else

		// TODO which slice does it belong to ?
		tcalendar.setTimeInMillis(time);
		int h = tcalendar.get(Calendar.HOUR_OF_DAY);
		// System.err.println("add history: s="+state+" hour = "+h);
		if (night) {
			// which day is this ?
			int is = (int) ((time - start) / (24 * 3600 * 1000.0));
			long ts = start + is * 24 * 3600 * 1000L;
			long te = ts + timeSize;
			// System.err.printf("calculated is: %4d in %tF %tT - %tF %tT\n",is,
			// ts,ts,te,te);
			if (time > ts && time <= te) {
				history.add(new StateHistory(time, state));
				// System.err.println("h("+is+") size: "+history[is].size());
			}
		}
		// otherwise ignore it its a daytime stamp

		// this.time = time;
	}

}
