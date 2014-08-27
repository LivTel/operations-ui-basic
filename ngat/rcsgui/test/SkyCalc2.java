/**
 * 
 */
package ngat.rcsgui.test;

import ngat.astrometry.AstrometryCalculator;
import ngat.astrometry.BasicAstrometryCalculator;
import ngat.astrometry.Coordinates;
import ngat.astrometry.ISite;
import ngat.astrometry.LunarCalculator;

/**
 * @author eng
 *
 */
public class SkyCalc2 {

	static final double H0 = 1.0;
	static final double A0 = Math.toRadians(30.0);
	static final double M0 = 2.0;
	static final double AM0 = Math.toRadians(90.0);
	
	private ISite site;
	
	private AstrometryCalculator astro;

	/**
	 * @param site
	 */
	public SkyCalc2(ISite site) {
		super();
		this.site = site;
		astro = new BasicAstrometryCalculator();
	}
	
	
	public double calcB(long t, double alt, double azm) throws Exception {
	
		// target airmass
		double air = 1/Math.cos(0.5*Math.PI - alt);
		
		LunarCalculator lunar = new LunarCalculator(site);
		Coordinates moon = lunar.getCoordinates(t);
		
		double lazm = astro.getAzimuth(moon, site, t);
		double lalt = astro.getAltitude(moon, site, t);
				
		// distance from moon to target
		double lda = Math.acos( Math.sin(alt)*Math.sin(lalt) + Math.cos(alt)*Math.cos(lalt)*Math.cos(azm-lazm));
		
		// brightness made up of combination of: distance to moon, moonphase, distance above horizon
		
		return H0*Math.exp(-alt/A0) + M0*Math.exp(-lda/AM0);
		
	}

}
