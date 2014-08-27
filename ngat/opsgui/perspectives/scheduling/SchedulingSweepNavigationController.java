package ngat.opsgui.perspectives.scheduling;

import java.util.List;
import java.util.Vector;

/**
 * Acts as a controller for the various sweep display panels to allow them to
 * synchronize.
 */
public class SchedulingSweepNavigationController {

	/** True if the panels are synchronized to the latest sweep update. */
	private volatile boolean synchToSweep;

	/** Records the currently displayed sweep number. */
	private volatile int displaySweepCount;

	/** Records the latest sweep number. */
	private volatile int latestSweepCount;

	/** The sweep data. */
	private List<SweepEntry> sweeps;

	/** A list of components which display sweep information. */
	private List<SweepDisplay> displays;

	/**
	 * Create a new SchedulingSweepNavigationController linked to the sweep
	 * data.
	 * 
	 * @param sweeps
	 *            The sweep data.
	 */
	public SchedulingSweepNavigationController(List<SweepEntry> sweeps) {
		this.sweeps = sweeps;

		displays = new Vector<SweepDisplay>();

		synchToSweep = true;
		displaySweepCount = -1;
		latestSweepCount = -1;

	}

	/**
	 * Add a SweepDisplay to the list of displays.
	 * 
	 * @param display
	 *            The display to add.
	 */
	public void addSweepDisplay(SweepDisplay display) {
		displays.add(display);
	}

	/**
	 * Update the controller with new sweep information. The sweep data is
	 * available both to the controller and to any displays, ie we can just tell
	 * them to show-sweep #n.
	 */
	public void updateSweep() {

		// NOTE: The first sweep count is DISPLAYED as 1 NOT zero
		// but the table is zero based ... first call sets this to ZERO.
		latestSweepCount++;

		if (synchToSweep)
			displaySweepCount = latestSweepCount;

		// NOTE if we are not synched the displays will not update but the nav
		// panel will.
		// tell all the displays to re-display as required
		// TODO run as swingutil.later
		notifyDisplays();

	}

	/**
	 * @param The index - the actual index NOT the displayed index.
	 * @return sweep time for the specified sweep.
	 */
	public long getSweepTime(int i) {
		SweepEntry sweep = sweeps.get(i);
		long time = sweep.getTime();
		return time;
	}
	
	/**
	 * Requests the controller to synchronize to latest sweep or not.
	 * 
	 * @param synch
	 *            Whether to synchronize.
	 */
	public void requestSynchronize(boolean synch) {

		synchToSweep = synch;
		if (synch) {
			displaySweepCount = latestSweepCount;
		}
		// tell all the displays to re-display as required
		// TODO run as swingutil.later
		notifyDisplays();

	}

	/**
	 * Request the controller to display a particular sweep.
	 * 
	 * @param number
	 */
	public void requestSweepAbsolute(int number) {
		if (synchToSweep)
			return;

		displaySweepCount = number;
		// tell all the displays to re-display as required
		// TODO run as swingutil.later
		notifyDisplays();

	}

	/**
	 * Request the controller to move backward or forward throught he list of
	 * sweeps.
	 * 
	 * @param number
	 */
	public void requestSweepRelative(int delta) {
		if (synchToSweep)
			return;

		int newDisplayCount = displaySweepCount + delta;

		if (newDisplayCount < 0)
			return;

		if (newDisplayCount > latestSweepCount)
			return;

		displaySweepCount = newDisplayCount;

		// tell all the displays to re-display as required
		// TODO run as swingutil.later
		notifyDisplays();

	}

	/**
	 * Request the controller to display the latest sweep.
	 * 
	 * @param number
	 */
	public void requestSweepLatest() {

		if (synchToSweep)
			return;

		displaySweepCount = latestSweepCount;
		// tell all the displays to re-display as required
		// TODO run as swingutil.later
		notifyDisplays();

	}

	private void notifyDisplays() {

		for (int id = 0; id < displays.size(); id++) {

			SweepDisplay display = displays.get(id);

			// tell the display what to show
			display.displaySweep(displaySweepCount, latestSweepCount, synchToSweep);

		}

	}

}