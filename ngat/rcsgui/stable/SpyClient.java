package ngat.rcsgui.stable;

//package ngat.rcs.gui;

import javax.swing.*;
import ngat.net.*;
import ngat.net.camp.*;
import ngat.message.base.*;
import ngat.message.GUI_RCS.*;

/** Implements the CAMP protocol as client and response handler.*/
public class SpyClient extends GUIClient implements CAMPResponseHandler {

    public SpyClient(RcsGUI gui, GUI_TO_RCS command) {
	super(gui, command);
    }
    
    
    @Override
	public void handleUpdate(COMMAND_DONE update, IConnection connection) {
	
	System.err.println(command.getId()+" Closing connection.");
	connection.close();
	
	//System.err.println(command.getId()+" Received update: "+update);
	
	if (! (update instanceof SPY_DONE)) {
	    System.err.println(command.getId()+" CAMP Error: Unexpected class: "+update);
	    return;
	}

	int    cat   = ((SPY)command).getCategory();

	String catty = null;

	String target = ((SPY)command).getTarget();

	switch (cat) {
	case SPY.SENSOR:
	    catty = "Sensor";
	    break;
	case SPY.FILTER:
	    catty = "Filter";
	    break;
	case SPY.RULE:
	    catty = "Rule";
	    break;
	}


	if (update.getSuccessful()) {
	    JOptionPane.showMessageDialog(null,
					  "Spy logging is enabled For: "+catty+": "+target,
					  "Spy Request",
					  JOptionPane.INFORMATION_MESSAGE);		    
	} else {
	    int    errno  = update.getErrorNum();
	    String errmsg = update.getErrorString();

	    JOptionPane.showMessageDialog(null,
					  "<html>"+
					  "<p>Spy logging is Not enabled For: "+catty+": "+target+
					  "<table border = 0>"+
					  " <tr><td>Error</td>  <td>"+errno+"</td></tr>"+
					  " <tr><td>Message</td> <td>"+errmsg+"</td></tr>"+
					  "</table>",					  
					  "Spy Request",
					  JOptionPane.ERROR_MESSAGE);		
	    	    
	}

	
    }
    
    
    @Override
	public void failed(Exception e, IConnection connection) {
	System.err.println(command.getId()+" CAMP Error: "+e);  
	e.printStackTrace(); 
	System.err.println("Close connection.");
	if (connection != null)
	    connection.close();

    }
        
}
