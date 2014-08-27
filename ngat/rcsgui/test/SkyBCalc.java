/**
 * 
 */
package ngat.rcsgui.test;

import ngat.astrometry.AstroLib;
import ngat.astrometry.Coordinates;
import ngat.astrometry.ISite;
import ngat.astrometry.JAstroSlalib;

/**
 * @author eng
 *
 */
public class SkyBCalc {
	
	
	public static final double S2 = Math.toRadians(62.6);
	public static final double S8 = Math.toRadians(282.25);
	public static final double ee = Math.toRadians(23.439);
	public static final double s0 = 1.0; //sun bright
	
	
	private ISite site;
	
	/**
	 * @param site
	 */
	public SkyBCalc(ISite site) {
		super();
		this.site = site;
	}

	public double calcB(long t, double alt, double azm) throws Exception {
		
		double phi = site.getLatitude();
		double l = site.getLongitude();
		
			AstroLib astro = new JAstroSlalib();
		
			double air = 1.0/Math.cos(0.5*Math.PI - alt);
		    
			    //ra,dec from az,el
			    double sa = Math.sin(azm);
			    double ca = Math.cos(azm);
			    double se = Math.sin(alt);
			    double ce = Math.cos(alt);
			    double sp = Math.sin(phi);
			    double cp = Math.cos(phi);

			    double x = - ca * ce * sp + se * cp;
			    double y = - sa * ce;
			    double z = ca * ce * cp + se * sp;

			    double r = Math.sqrt ( x * x + y * y );
			    double ha = ( r == 0.0f ) ? 0.0 : Math.atan2 ( y, x ) ;
			    double dec = Math.atan2 ( z, r );
			    
			    double lst = astro.getLST(t, l);
			    double ra = lst - ha;
			    
			    // galactic from ra,dec
			    double sb = Math.sin(dec)*Math.cos(S2) - Math.cos(dec)*Math.sin(ra-S8)*Math.sin(S2);
			    double b = Math.asin(sb);
			    
			    Coordinates gal = eqgal(ra,dec);
			    double gl = gal.getRa();
			    double gb = gal.getDec();
			    
			    // ecliptic from ra,dec
			    double sbet = Math.sin(dec)*Math.cos(ee) - Math.cos(dec)*Math.sin(ra)*Math.sin(ee);
			    double bet = Math.asin(sbet);
			   
			    
			    // calculate skyb
			    double sba = 0.0;
			    double sbz = 0.0;
			    double sbs = 0.0;
			    
			    sa = 145 + 130*(s0-0.8)*air/1.2;
			    
			    if (bet < Math.toRadians(60.0)) {
			    	sbz = 140 - 90*Math.sin(bet);
			    } else {
			    	sbz = 60.0;
			    }
			    
			    sbs = 100*Math.exp(-Math.abs(Math.toDegrees(b))/10.0);
			    double s = sba + sbz + sbs;
			    
			    
			    //System.err.printf("Alt: %6.2f, Azm: %6.2f, ra: %6.2f, Dec: %6.2f, b: %6.2f, B:%6.2f -> %4.2f, %4.2f, %4.2f %4.2f\n", 
			    	//	Math.toDegrees(alt), 
			    	//	Math.toDegrees(azm), 
			    	//	Math.toDegrees(ra), 
			    	//	Math.toDegrees(dec), 
			    	//	Math.toDegrees(b), 
			    	//	Math.toDegrees(bet),
			    	//	sa,sz,ss,s);
		
			    return s;
	
	}

	private Coordinates eqgal(double ra, double dec) {
		
		double rmat[][] = new double[3][3];
	
	   rmat[0][0] = -0.054875539726;
	   rmat[0][1] = -0.873437108010;
	   rmat[0][2] = -0.483834985808;
	   rmat[1][0] =  0.494109453312;
	   rmat[1][1] = -0.444829589425;
	   rmat[1][2] =  0.746982251810;
	   rmat[2][0] = -0.867666135858;
	   rmat[2][1] = -0.198076386122;
	   rmat[2][2] =  0.455983795705;

	/* Spherical to Cartesian */
	   double[] v1 = new double[3];
	   double cosb = Math.cos ( dec );
	   v1[0] = Math.cos ( ra ) * cosb;
	   v1[1] = Math.sin ( ra ) * cosb;
	   v1[2] = Math.sin ( dec );	   

	/* Equatorial to Galactic */
	   double[] v2 = new double[3];
	   double w = 0.0;
	   for ( int j = 0; j < 3; j++ ) {
		      w = 0.0;
		      for ( int i = 0; i < 3; i++ ) {
		         w += rmat[j][i] * v1[i];
		      }
		      v2[j] = w;
		   }

		/* Vector vw -> vector vb */
		   for ( int j = 0; j < 3; j++ ) {
		      v2[j] = v1[j];
		   }
	   
	/* Cartesian to spherical */
	  
	   double x = v2[0];
	   double y = v2[1];
	   double z = v2[2];
	   double r = Math.sqrt ( x * x + y * y );

	   double l = ( r == 0.0 ) ? 0.0f : Math.atan2 ( y, x );
	   double b = ( z == 0.0 ) ? 0.0f : Math.atan2 ( z, r );


	/* Express in conventional ranges */
	
	   if (l< 0.0)
		   l+=2*Math.PI;
	
	   return new Coordinates(l, b);
	}
}
