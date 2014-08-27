/**
 * 
 */
package ngat.opsgui.util;

import java.awt.Color;
import java.awt.Graphics;
import java.util.List;
import java.util.Vector;

import javax.swing.BorderFactory;
import javax.swing.JPanel;

/**
 * A panel which shows time history of a state by colors.
 * 
 * @author eng
 * 
 */
public class StatusHistoryPanel extends JPanel implements TimeDisplay {

	/** The current (latest) time to display. */
	private long time;

	private long start;

	private long end;

	/** Color mapping. */
	private StateColorMap map;

	/** The time-sorted list of historic states. */
	private List<StateHistory> history;

	/** The controller for this panel. */
	private TimeDisplayController controller;

	/**
	 * 
	 */
	public StatusHistoryPanel() {
		super(true);
		history = new Vector<StateHistory>();
		setBorder(BorderFactory.createLineBorder(Color.black));
	}

	/**
	 * @param controller
	 *            The TimeDisplayController which this panel notifies.
	 */
	public StatusHistoryPanel(TimeDisplayController controller) {
		this();
		this.controller = controller;
	}

	/**
	 * @return the controller
	 */
	public TimeDisplayController getController() {
		return controller;
	}

	/**
	 * @param controller
	 *            the controller to set
	 */
	public void setController(TimeDisplayController controller) {
		this.controller = controller;
	}

	public void addHistory(long time, int state) {
		// TODO allow merging of state series where no change
		// StateHistory last = history.get(history.size()-1);
		// if (last.getState() == state)
		// last.setTime(time);
		// else

		history.add(new StateHistory(time, state));

		// notify controller, it may tell us to repaint.
		controller.timeUpdate(time);

		this.time = time;
	}

	public void addHistory(List<StateHistory> subhistory) {
		history.addAll(subhistory);
		// TODO advance the time here
	}

	public void clearHistory() {
		history.clear();
	}

	@Override
	public void displayTime(long windowStart, long windowEnd) {
		this.start = windowStart;
		this.end = windowEnd;
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

		int ww = getSize().width;
		int hh = getSize().height;

		// special treatment for before map is setup - e.g. for IDE rendering
		if (map == null) {
			int w = 0;
			int dw = ww / 10;
			for (int i = 0; i < 5; i++) {
				g.setColor(Color.gray.brighter());
				g.fillRect(w, 0, w+dw, hh);
				w += dw;
				g.setColor(Color.gray.darker());
				g.fillRect(w, 0, w+dw, hh);
				w += dw;
			}
			return;
		}

		g.setColor(map.getDefaultColor());
		g.fillRect(0, 0, ww, hh);

		long tlast = start;
		int slast = -999;
		for (int i = 0; i < history.size(); i++) {

			StateHistory h = history.get(i);
			long t = h.getTime();
			int s = h.getState();

			// only display stuff between start and end of display window
			if (t >= start && t <= end) {

				// choose the color for here back to last time point
				Color color = map.getColor(slast);
				g.setColor(color);
				int x1 = (int) ((double) (ww) * (double) (tlast - start) / (end - start));
				int x2 = (int) ((double) (ww) * (double) (t - start) / (end - start));
				g.fillRect(x1, 0, x2 - x1, hh);

			}

			// record the latest value
			tlast = t;
			slast = s;
		}

	}

	/**
	 * @return the time
	 */
	public long getTime() {
		return time;
	}

	/**
	 * @param time
	 *            the time to set
	 */
	public void setTime(long time) {
		this.time = time;
	}

	/**
	 * @return the map
	 */
	public StateColorMap getMap() {
		return map;
	}

	/**
	 * @param map
	 *            the map to set
	 */
	public void setMap(StateColorMap map) {
		this.map = map;
	}

}
