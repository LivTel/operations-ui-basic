package ngat.rcsgui.stable;

//package ngat.rcs.gui;

import javax.swing.*;
import ngat.net.*;
import ngat.net.camp.*;
import ngat.message.base.*;
import ngat.message.GUI_RCS.*;

/** Implements the CAMP protocol as client and response handler.*/
public class ToggleModeClient extends GUIClient implements CAMPResponseHandler {

    public ToggleModeClient(RcsGUI gui, GUI_TO_RCS command) {
	super(gui, command);
    }
        
    @Override
	public void handleUpdate(COMMAND_DONE update, IConnection connection) {
	
	System.err.println(command.getId()+" Closing connection.");
	connection.close();
	
	//System.err.println(command.getId()+" Received update: "+update);
	
	if (! (update instanceof SWITCH_MODE_DONE)) {
	    System.err.println(command.getId()+" CAMP Error: Unexpected class: "+update);
	    return;
	}
	
	SWITCH_MODE_DONE sd = (SWITCH_MODE_DONE)update;

	
	if (! sd.getSuccessful())
	    JOptionPane.showMessageDialog(gui.getFrame(), 
					  "<html>"+					 
					  "<p>Error: "+sd.getErrorNum()+
					  "<p>Message: "+sd.getErrorString(), 
					  "Switch Mode", JOptionPane.INFORMATION_MESSAGE);
	
	
    }
    
    
    @Override
	public void failed(Exception e, IConnection connection) {
	System.err.println(command.getId()+" CAMP Error: "+e);  
	e.printStackTrace();	
	if (connection != null)
	    connection.close();
    }



}
