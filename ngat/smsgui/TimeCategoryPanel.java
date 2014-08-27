/**
 * 
 */
package ngat.smsgui;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;

import javax.swing.JPanel;

/**
 * @author eng
 *
 */
public abstract class TimeCategoryPanel extends JPanel {
	
	private TimePanel container;

	
	/**
	 * @param container
	 */
	public TimeCategoryPanel(TimePanel container) {
		super();
		this.container = container;		
	}


	/* (non-Javadoc)
	 * @see javax.swing.JComponent#paint(java.awt.Graphics)
	 */
	@Override
	public void paint(Graphics g) {	
		super.paint(g);
		
		// we only need to know the time range
		long start = container.getStart();
		long end = container.getEnd();
		
		renderGraphics(g, start, end);	
		
		long now = System.currentTimeMillis();
		
		if ((start < now) && (now < end)) {
			g.setColor(container.mycolor);	
			double xscale = getSize().getWidth()/(end - start);
			int x = (int)(xscale*(now-start));
			g.fillRect( x, 0, 2, getSize().height);
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


	/** Render the panel via the graphics supplied between start and end times.*/
	protected abstract void renderGraphics(Graphics g, long start, long end);
		
	protected Color gradient(Color a, Color b, double fraction) {
		
		int ra = a.getRed();
		int rb = b.getRed();		
		int rc = ra + (int)(fraction*(rb-ra));
		
		int ba = a.getBlue();
		int bb = b.getBlue();		
		int bc = ba + (int)(fraction*(bb-ba));
		
		int ga = a.getGreen();
		int gb = b.getGreen();		
		int gc = ga + (int)(fraction*(gb-ga));
		
		return new Color(rc, gc, bc);
		
	}

}
