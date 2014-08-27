package ngat.rcsgui.stable;

//package ngat.rcs.gui;

import javax.swing.*;
import ngat.net.*;
import ngat.net.camp.*;
import ngat.message.base.*;
import ngat.message.GUI_RCS.*;

/** Implements the CAMP protocol as client and response handler.*/
public class SetSeeingClient extends GUIClient implements CAMPResponseHandler {

    public SetSeeingClient(RcsGUI gui, GUI_TO_RCS command) {
	super(gui, command);
    }
    
    
    @Override
	public void handleUpdate(COMMAND_DONE update, IConnection connection) {
	
	System.err.println(command.getId()+" Closing connection.");
	connection.close();
	
	//System.err.println(command.getId()+" Received update: "+update);
	
	if (! (update instanceof SET_SEEING_DONE)) {
	    System.err.println(command.getId()+" CAMP Error: Unexpected class: "+update);
	    return;
	}

	if (!update.getSuccessful()) {
	    JOptionPane.showMessageDialog(null,
					  "Set seeing/extinction : FAILED "+update.getErrorString(),
					  "Set Seeing/Extinction Response",
					  JOptionPane.INFORMATION_MESSAGE);
	    
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
