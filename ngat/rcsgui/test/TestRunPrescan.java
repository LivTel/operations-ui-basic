/**
 * 
 */
package ngat.rcsgui.test;

import java.rmi.Naming;

import ngat.sms.FeasibilityPrescanController;

/** Run a prescan on remote scheduler.
 * @author eng
 *
 */
public class TestRunPrescan {

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		
		try {
			FeasibilityPrescanController prescan = (FeasibilityPrescanController) 
			Naming.lookup("rmi://localhost/FeasibilityPrescanner");
		
			prescan.prescan(System.currentTimeMillis(), 5*60*1000L);
		
		} catch (Exception e) {
			e.printStackTrace();
		}

	}

}
