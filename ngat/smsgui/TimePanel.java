/**
 * 
 */
package ngat.smsgui;

import java.awt.Color;
import java.util.Iterator;
import java.util.List;
import java.util.Vector;

import javax.swing.BoxLayout;
import javax.swing.JPanel;

/**
 * @author eng
 *
 */
public class TimePanel extends JPanel {

	private long start;
	
	private long end;

	List<TimeCategoryPanel> subPanels;
	
	public Color mycolor;

	/**
	 * 
	 */
	public TimePanel() {
		super();
		subPanels = new Vector<TimeCategoryPanel>();
		setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));
	}

	/**
	 * @return the start
	 */
	public long getStart() {
		return start;
	}

	
	/**
	 * @return the end
	 */
	public long getEnd() {
		return end;
	}

	/** Reset the start and end times and repaint.
	 * @param start The start time.
	 * @param end the end time.
	 */
	public void setTimeLimits(long start, long end) {
		this.start = start;
		this.end = end;
		repaint();
	}

	public void addCategoryPanel(TimeCategoryPanel tcp) {
		subPanels.add(tcp);
		// clear all 
		this.removeAll();
		Iterator<TimeCategoryPanel> itc = subPanels.iterator();
		while (itc.hasNext()) {
			TimeCategoryPanel atcp = itc.next();
			add(atcp);
			validate();
		}
		//repaint();
	}

	public void repaint(Color color) {
		mycolor = color;
		repaint();
	}
	
}
