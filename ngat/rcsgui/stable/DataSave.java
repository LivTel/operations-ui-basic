package ngat.rcsgui.stable;

import ngat.net.datalogger.*;
import java.io.*;

public class DataSave {

    public static void main(String[] args) {

	try {
	    
	    int port = Integer.parseInt(args[0]);

	    DataLogger logger = new DataLogger();

	    DataReceiverThread drt = new DataReceiverThread(logger, port);
	   
	    DataFileWriter dfw = new DataFileWriter(new File(args[1]));
	    logger.addUpdateListener(dfw);
	    
	    logger.start();

	    drt.start();

	} catch (Exception e) {
	    System.err.println("Exception: "+e);
	    return;
	}

    }

}
