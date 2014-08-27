/**
 * 
 */
package ngat.smsgui;

import java.awt.Color;
import java.awt.Graphics;

import ngat.astrometry.AstrometryCalculator;
import ngat.astrometry.AstrometryException;
import ngat.astrometry.BasicAstrometryCalculator;
import ngat.astrometry.ISite;
import ngat.astrometry.SolarCalculator;

/**
 * @author eng
 * 
 */
public class DayNightPanel extends TimeCategoryPanel {

	private static final double NIGHT_LIMIT = Math.toRadians(-18.0);

	private static final double ASTRO_LIMIT = Math.toRadians(-12.0);

	private static final double NAUTICAL_LIMIT = Math.toRadians(-6.0);

	private static final double CIVIL_LIMIT = Math.toRadians(0.0);

	private ISite site;

	private AstrometryCalculator astro;

	/**
	 * @param container
	 */
	public DayNightPanel(TimePanel container, ISite site) {
		super(container);
		this.site = site;
		astro = new BasicAstrometryCalculator();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see ngat.smsgui.TimeCategoryPanel#renderGraphics(java.awt.Graphics,
	 * long, long)
	 */
	@Override
	protected void renderGraphics(Graphics g, long start, long end) {

		int ns = getSize().width;
		long step = (end - start) / ns;

		double xscale = getSize().getWidth()/(end - start);
		
		SolarCalculator sun = new SolarCalculator();
		double alt = 0.0;
		long t = start;
		while (t < end) {
			try {
				alt = astro.getAltitude(sun.getCoordinates(t), site, t);
				if (alt < NIGHT_LIMIT) 
					g.setColor(Color.blue);
				else if 
				(alt > 0.0)
					g.setColor(Color.orange);
				else
					g.setColor(gradient(Color.orange, Color.blue, alt/Math.toRadians(-18.0)));
				
			} catch (AstrometryException ax) {
				ax.printStackTrace();				
				g.setColor(Color.pink);
			}
			int x = (int)(xscale*(t-start));
			g.fillRect( x, 3, 1, getSize().height-6);
			//System.err.println("SUN alt: " + Math.toDegrees(alt)+"Fill: "+x);
			t += step;
		}
	}
}
