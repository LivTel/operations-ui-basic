/**
 * 
 */
package ngat.smsgui;

import java.awt.Color;
import java.awt.Graphics;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * @author eng
 *
 */
public class TimeScalePanel extends TimeCategoryPanel {
	
	private static SimpleDateFormat sdf = new SimpleDateFormat("HH:mm");
	
	/**
	 * @param container
	 */
	public TimeScalePanel(TimePanel container) {
		super(container);
	
	}

	/* (non-Javadoc)
	 * @see ngat.smsgui.TimeCategoryPanel#renderGraphics(java.awt.Graphics, long, long)
	 */
	@Override
	protected void renderGraphics(Graphics g, long start, long end) {
		
		double xscale = getSize().getWidth()/(end - start);
		g.setColor(Color.blue);
		for (int i = 0; i < 18; i++){
			long t = start + i*(end-start)/18;
			
			int x = (int)(xscale*(t-start));
			
			g.fillRect( x, 3, 1, getSize().height-6);
			g.drawString(sdf.format(new Date(t)), x, getSize().height-2);
			
		}
		
	}

}
