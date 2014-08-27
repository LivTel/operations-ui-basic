/**
 * 
 */
package ngat.opsgui.test;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;

import javax.swing.JPanel;

/**
 * @author eng
 * 
 */
public class LookaheadSequencePanel extends JPanel {

	static final int PENDING_STATE = 0;

	static final int SELECTED_STATE = 1;

	static final int SKIPPED_STATE = 2;

	static final int COMPLETED_STATE = 3;

	static final int FAILED_STATE = 4;

	private static final Color FEASIBLE_COLOR = new Color(185, 226, 70);

	private static final Color ACTIVE_COLOR = new Color(200, 126, 237);
	
	private static final Color NOW_TIME_COLOR = Color.cyan;

	Dimension DEF = new Dimension(450, 30);

	String groupName;

	long start;

	long end;

	long feasibilityStart;

	long feasibilityEnd;

	long activeStart;

	long activeEnd;

	long selectionTime;

	long completionTime;

	boolean selected = false;

	int state = PENDING_STATE;

	long now;

	/**
	 * @param groupName
	 * @param start
	 * @param end
	 * @param feasibilityStart
	 * @param feasibilityEnd
	 * @param activeStart
	 * @param activeEnd
	 */
	public LookaheadSequencePanel(String groupName, long start, long end, long feasibilityStart, long feasibilityEnd,
			long activeStart, long activeEnd) {
		super();
		this.groupName = groupName;
		this.start = start;
		this.end = end;
		this.feasibilityStart = feasibilityStart;
		this.feasibilityEnd = feasibilityEnd;
		this.activeStart = activeStart;
		this.activeEnd = activeEnd;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see javax.swing.JComponent#paint(java.awt.Graphics)
	 */
	@Override
	public void paint(Graphics g) {
		super.paint(g);

		int w = getSize().width;
		int h = getSize().height;

		// the feasibility window
		int fstart = (int) (w * (feasibilityStart - start) / (end - start));
		if (fstart < 0)
			fstart = 0;
		int fend = (int) (w * (feasibilityEnd - start) / (end - start));
		if (fend > w)
			fend = w;

		// color depends on (a) state, (b) environment relative to requested
		g.setColor(FEASIBLE_COLOR);
		g.fillRect(fstart, 1, fend - fstart, h - 2);

		// the active window
		int astart = (int) (w * (activeStart - start) / (end - start));
		if (astart < 0)
			astart = 0;
		int aend = (int) (w * (activeEnd - start) / (end - start));
		if (aend > w)
			aend = w;

		// color depends on ?
		g.setColor(ACTIVE_COLOR);
		g.fillRect(astart, 1, aend - astart, h - 2);

		// selection time if selected or completed or failed
		if (selected) {
			int sstart = (int) (w * (selectionTime - start) / (end - start));
			// draw the selection time mark - a vertical line or narrow box
			g.setColor(Color.blue);
			g.fillRect(sstart, 1, 2, h - 2);
			// draw line section to now or end time
			long etime = now;
			if (state == COMPLETED_STATE || state == FAILED_STATE) {
				etime = completionTime;
			}
			// draw it
			int send = (int) (w * (etime - start) / (end - start));
			g.drawLine(sstart, h / 2, send, h / 2);
			// now draw the correct line end point
			switch (state) {
			case SELECTED_STATE:
				// draw an arrow backwards
				g.drawLine(send - 3, h/2 - 3, send, h / 2);
				g.drawLine(send - 3, h/2 + 3, send, h / 2);
				break;
			case COMPLETED_STATE:
				// filled circle - green
				g.setColor(Color.green);
				g.fillOval(send - 3, h/2 - 3, 6, 6);
				break;
			case FAILED_STATE:
				// open circle - red
				g.setColor(Color.red);
				g.fillOval(send - 3, h/2 - 3, 6, 6);
				break;
			}

		}
		
		// current time marker
		int fnow = (int) (w * (now - start) / (end - start));
		g.setColor(NOW_TIME_COLOR);
		g.drawLine(fnow, 0, fnow, h);
	}

	public void updateTime(long time) {
		now = time;
		repaint();
	}
	
	public void sequenceSelected(long time) {
		selectionTime = time;
		selected = true;
		state = SELECTED_STATE;
		repaint();
	}
	
	public void sequenceFailed(long time) {
		completionTime = time;
		state = FAILED_STATE;
		repaint();
	}
	
	public void sequenceCompleted(long time) {
		completionTime = time;
		state = COMPLETED_STATE;
		repaint();
	}
	
	/*
	 * (non-Javadoc)
	 * 
	 * @see javax.swing.JComponent#getPreferredSize()
	 */
	@Override
	public Dimension getPreferredSize() {
		return DEF;
	}

}
