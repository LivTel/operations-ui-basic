/**
 * 
 */
package ngat.opsgui.test;

import java.awt.BorderLayout;

import javax.swing.JFrame;

import ngat.astrometry.BasicSite;
import ngat.opsgui.perspectives.tracking.RaDecPlot;

/**
 * @author eng
 *
 */
public class CreateSkyPlot {

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		
		try {
			
			double lat = Math.toRadians(28.0);
			double lon = Math.toRadians(-17.0);
			
			BasicSite site = new BasicSite("test", lat, lon);
			RaDecPlot radec = new RaDecPlot(site);
			radec.setShowAltazGrid(true);
			
			JFrame f = new JFrame("Ra/Dec Plot: Overlay Test");
			f.getContentPane().setLayout(new BorderLayout());
			f.getContentPane().add(radec, BorderLayout.CENTER);
			f.pack();
			f.setBounds(50, 50, 800, 800);
			f.setVisible(true);
			
			radec.setSimulation(true);
			long start = System.currentTimeMillis();
			long time = start;
			while (time < start +24*3600*1000L) {
				radec.setSimulationTime(time);
				radec.repaint();
				time += 10*60*1000L;
				try {Thread.sleep(1000L); } catch (InterruptedException x) {}
			}
			
		} catch (Exception e) {
			e.printStackTrace();
		}
		

	}

}
