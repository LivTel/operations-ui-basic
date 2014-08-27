package ngat.opsgui.util;

import java.awt.Color;
import java.awt.Graphics;

import javax.swing.JPanel;

public class ValueHistoryPanel extends JPanel implements TimeDisplay {

    private double lowValue;

    private double highValue;

    private Color lowColor;

    private Color highColor;
    
    private long start;

    private long end;

  

	/**
	 * @param lowValue
	 * @param highValue
	 * @param lowColor
	 * @param highColor
	 */
	public ValueHistoryPanel(double lowValue, double highValue, Color lowColor, Color highColor) {
		super();
		this.lowValue = lowValue;
		this.highValue = highValue;
		this.lowColor = lowColor;
		this.highColor = highColor;
	}



	@Override
	public void displayTime(long windowStart, long windowEnd) {
		start = windowStart;
		end = windowEnd;		
	}



	/* (non-Javadoc)
	 * @see javax.swing.JComponent#paint(java.awt.Graphics)
	 */
	@Override
	public void paint(Graphics g) {		
		super.paint(g);
		
		
		
	}

  
}