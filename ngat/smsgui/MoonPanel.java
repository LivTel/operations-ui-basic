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
import ngat.astrometry.LunarCalculator;

/**
 * @author eng
 * 
 */
public class MoonPanel extends TimeCategoryPanel {

	final Color darkColor = Color.cyan;
	final Color brightColor = Color.black;

	ISite site;

	private AstrometryCalculator astro;

	/**
	 * @param container
	 */
	public MoonPanel(TimePanel container, ISite site) {
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

		double xscale = getSize().getWidth() / (end - start);

		LunarCalculator moon = new LunarCalculator(site);
		double alt = 0.0;
		long t = start;
		while (t < end) {
			try {
				alt = astro.getAltitude(moon.getCoordinates(t), site, t);
				if (alt < 0.0) {
					g.setColor(Color.black);
				} else {
					double frac = alt / Math.toRadians(90.0);
					int red = (int) (frac * (darkColor.getRed() - brightColor.getRed()) + brightColor
							.getRed());
					int grn = (int) (frac * (darkColor.getGreen() - brightColor.getGreen()) + brightColor
							.getGreen());
					int blu = (int) (frac * (darkColor.getBlue() - brightColor.getBlue()) + brightColor
							.getBlue());
					Color color = new Color(red, grn, blu);
					g.setColor(color);
				}
			} catch (AstrometryException ax) {

				ax.printStackTrace();
				g.setColor(Color.pink);
			}
			int x = (int) (xscale * (t - start));
			g.fillRect(x, 5, 1, getSize().height-5);
			//System.err.println("Moon alt: " + Math.toDegrees(alt) + "Fill: " + x);
			t += step;
		}
	}
}
