/**
 * 
 */
package ngat.opsgui.services.test;

import java.rmi.Naming;

import ngat.sms.SchedulingArchiveGateway;
import ngat.sms.SchedulingStatusProvider;

/**
 * @author eng
 *
 */
public class CreateSchedulerGateway {

	/**
	 * @param args
	 */
	public static void main(String[] args) {
	
		try {

			String remoteHost = args[0];
			
			SchedulingStatusProvider ssp = (SchedulingStatusProvider)Naming
					.lookup("rmi://"+remoteHost+"/Scheduler");
		    
		    System.err.println("Found: "+ssp);

		    SchedulingArchiveGateway shag = new SchedulingArchiveGateway(ssp);
		   
		    Naming.rebind("SchedulerGateway", shag);
		    System.err.println("Bound: "+shag);

		    shag.startProcessor();

		    while (true){try{Thread.sleep(60000L);} catch (InterruptedException ix) {}}

		} catch (Exception e) {
		    e.printStackTrace();
		}
	}

}
