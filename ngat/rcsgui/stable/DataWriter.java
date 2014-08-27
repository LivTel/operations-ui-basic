package ngat.rcsgui.stable;

//package ngat.net.datalogger;

import ngat.net.datalogger.*;
import ngat.message.RCS_TCS.*;
import ngat.message.GUI_RCS.*;
import ngat.util.*;
import ngat.rcs.scm.collation.*;

public class DataWriter implements DataLoggerUpdateListener {

    /** Create a DataWriter.*/
    public DataWriter() {

 
    }

    @Override
	public void dataUpdate(Object data) {
	
	try {
	    
	    if (data instanceof StatusInfo) {
		
		StatusInfo info = (StatusInfo)data;
		
		StatusCategory status = info.getStatus();
		
		System.err.println("Status class: "+status.getClass().getName());
		
		if (status instanceof TCS_Status.Segment) {
		    
		    TCS_Status.Segment seg = (TCS_Status.Segment)status;
		    
		    //System.err.println("Segment:["+seg+"]");
		    
		} else if 
		    (status instanceof ngat.rcs.scm.collation.MappedStatusCategory) {
		    
		    MappedStatusCategory msc = (MappedStatusCategory)status;
		    
		    System.err.println("MSC: Cat="+info.getCat()+" : "+msc);
		    
		}

	    } else {

		System.err.println("Other class: "+data.getClass().getName());
		
	    }
	    
	} catch (Exception e) {
	    System.err.println("Error: "+e);
	}
    }
    
}
