package ngat.opsgui.services.test;

import java.rmi.Naming;

import ngat.tcm.TelescopeStatusProvider;
import ngat.rcs.telemetry.TelescopeArchiveGateway;

public class CreateTelescopeGateway {

    public static void main(String args[]) {

	try {

		String remoteHost = args[0];
		
	    TelescopeStatusProvider tsp = (TelescopeStatusProvider)Naming
	    		.lookup("rmi://"+remoteHost+"/Telescope");
	    System.err.println("Found: "+tsp);

	    TelescopeArchiveGateway tag = new TelescopeArchiveGateway(tsp);

	    Naming.rebind("TelescopeGateway", tag);
	    System.err.println("Bound: "+tag);

	    tag.startProcessor();

	    while (true){try{Thread.sleep(60000L);} catch (InterruptedException ix) {}}

	} catch (Exception e) {
	    e.printStackTrace();
	}
    }

}