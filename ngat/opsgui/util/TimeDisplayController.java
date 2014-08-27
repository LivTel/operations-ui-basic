package ngat.opsgui.util;

import java.util.List;
import java.util.Vector;

public class TimeDisplayController {

	private boolean synchronizeTime;

	private long dataStart;

	private long dataEnd;

	private long windowStart;

	private long windowEnd;

	private List<TimeDisplay> displays;

	private TimeDisplayController() {
		displays = new Vector<TimeDisplay>();
	}

	/**
	 * Create a live updating controller ending now with specified window size.
	 * 
	 * @param windowSize
	 *            The size of the display window.
	 */
	public TimeDisplayController(long windowSize) {
		this();
		synchronizeTime = true;
		windowEnd = System.currentTimeMillis();
		windowStart = windowEnd - windowSize;
	}

	/**
	 * Create a non-live-updating controller with specified window.
	 * 
	 * @param start
	 *            The start of the window.
	 * @param end
	 *            The end of the window.
	 */
	public TimeDisplayController(long start, long end) {
		this();
		synchronizeTime = false;
		windowEnd = end;
		windowStart = start;
	}

	/** Notification of a time update from (usually) a display. */
	public void timeUpdate(long time) {

		//System.err.println("TDC: timeUpdate "+time);
		
		// we may not need these at all ?
		if (time > dataEnd)
			dataEnd = time;
		if (time < dataStart)
			dataStart = time;

		// synched - move the end times and reset the display window
		if (synchronizeTime) {

			long windowSize = windowEnd - windowStart;
			windowEnd = time;
			windowStart = windowEnd - windowSize;

		}

		// always update displays - as one of them has new data
		// and the data may be inside the viewing window.
		updateDisplays(windowStart, windowEnd);

	}

	/**
	 * Zoom the display window by scaleFactor. Unlike panning we can zoom in and
	 * out even when synchronized.
	 * 
	 * @param scaleFactor
	 *            The scaling factor. Less than 1 means zoom in.
	 */
	public void requestZoom(double scaleFactor) {
		System.err.println("TDC: zoom: "+scaleFactor);
		long windowSize = windowEnd - windowStart;
		long newWindowSize = (long) (windowSize * scaleFactor);

		if (synchronizeTime) {

			windowStart = windowEnd - newWindowSize;

		} else {

			long windowMid = (windowStart + windowEnd) / 2;
			windowStart = windowMid - newWindowSize / 2;
			windowEnd = windowStart + newWindowSize;

		}

		updateDisplays(windowStart, windowEnd);

	}

	/**
	 * Pan the display window left or right by a fraction of the current view
	 * window. We cannot pan when synchronized - (live updating).
	 * 
	 * @param scaleFactor
	 *            The fraction of the view window to pan by.
	 */
	public void requestPan(double scaleFactor) {
		System.err.println("TDC: pan: "+scaleFactor);
		// scale should be in range [-1.0 : 1.0]
		// a 1.0 value means pan a full display width
		// 0.1 means pan 1/10th of a full display width
		// a negative value is a pan left, positive is pan right

		long windowSize = windowEnd - windowStart;
		long panSize = (long) (windowSize * Math.abs(scaleFactor));

		// if we are synched there is no panning...
		if (!synchronizeTime) {
			if (scaleFactor < 0.0) {
				// pan left
				windowStart -= panSize;
				windowEnd -= panSize;
			} else {
				// pan right
				windowStart += panSize;
				windowEnd += panSize;
			}

			updateDisplays(windowStart, windowEnd);

		}

	}
	
	/** Pan back to start of data. Not allowed when synched.*/
	public void requestPanToStart() {
		
		// TODO once ive worked out how.....
		
	}

	public void requestSynchronize(boolean synch) {
		this.synchronizeTime = synch;

		// only reset displays if we are synched now...
		if (synchronizeTime) {
			// reset data and window end to NOW...
			dataEnd = System.currentTimeMillis();
			long windowSize = windowEnd - windowStart;
			windowEnd = dataEnd;
			windowStart = windowEnd - windowSize;

			// now update all displays
			updateDisplays(windowStart, windowEnd);

		}

	}

	public void addTimeDisplay(TimeDisplay display) {
		if (displays.contains(display))
			return;
		displays.add(display);
		//System.err.println("Add display: "+displays.size()+" "+display);
	}

	private void updateDisplays(long windowStart, long windowEnd) {

		for (int id = 0; id < displays.size(); id++) {
			//System.err.println("Update displaY: "+id);
			TimeDisplay display = displays.get(id);
			display.displayTime(windowStart, windowEnd);

		}

	}

}