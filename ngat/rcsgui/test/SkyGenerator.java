/**
 * 
 */
package ngat.rcsgui.test;

import java.rmi.Naming;

import ngat.ems.DefaultMutableSkyModel;

/**
 * @author eng
 *
 */
public class SkyGenerator {

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		
		try {
			DefaultMutableSkyModel dsm = new DefaultMutableSkyModel(5, 20*60*1000L);
			Naming.rebind("SkyModel", dsm);
			
			while (true) {
				try {Thread.sleep(2000L);} catch (InterruptedException e) {}
				double seeing = 0.35+Math.random();
				dsm.updateSeeing(seeing, 550.0, 0.5*Math.PI, Math.PI, System.currentTimeMillis(), true, "test", "star");
			}
			
		} catch (Exception e){
			e.printStackTrace();
		}
	}

}
