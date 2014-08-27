/**
 * 
 */
package ngat.opsgui.test;

import javax.swing.JFrame;

import ngat.astrometry.BasicSite;
import ngat.opsgui.base.Resources;
import ngat.opsgui.perspectives.tracking.TrackingPerspective;

/**
 * @author eng
 *
 */
public class CrearteTrackingPerspective {

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		
		Resources.setDefaults("/home/eng/Pictures");
		
		JFrame f = new JFrame("Tracking perspective");

		TrackingPerspective tracking = new TrackingPerspective(f, new BasicSite("LT", Math.toRadians(28.0), Math.toRadians(-17.0)));
				
		f.getContentPane().add(tracking);
		f.pack();
		f.setVisible(true);

	}

}
