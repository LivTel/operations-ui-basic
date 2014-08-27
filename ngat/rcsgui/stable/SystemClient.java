package ngat.rcsgui.stable;

//package ngat.rcs.gui;

import javax.swing.*;
import ngat.net.*;
import ngat.net.camp.*;
import ngat.message.base.*;
import ngat.message.GUI_RCS.*;

/** Implements the CAMP protocol as client and response handler.*/
public class SystemClient extends GUIClient implements CAMPResponseHandler {

    public SystemClient(RcsGUI gui, GUI_TO_RCS command) {
	super(gui, command);
    }
    
    
    @Override
	public void handleUpdate(COMMAND_DONE update, IConnection connection) {
	
	System.err.println(command.getId()+" Closing connection.");
	connection.close();
	
	//System.err.println(command.getId()+" Received update: "+update);
	
	if (! (update instanceof START_DONE) &&
	    ! (update instanceof SYSTEM_DONE)) {
	    System.err.println(command.getId()+" CAMP Error: Unexpected class: "+update);
	    return;
	}
	
	if (update instanceof START_DONE) {
	    
	    START_DONE sd = (START_DONE)update;
	    
// 	    JOptionPane.showMessageDialog(gui.getFrame(), 
// 					  "<html>"+					 
// 					  "<p>Error: "+sd.getErrorNum()+
// 					  "<p>Message: "+sd.getErrorString(), 
// 					  "Start RCS", JOptionPane.INFORMATION_MESSAGE);
	    
	} else if
	    (update instanceof SYSTEM_DONE) {

	    SYSTEM_DONE sd = (SYSTEM_DONE)update;

	    if (! sd.getSuccessful())
		JOptionPane.showMessageDialog(gui.getFrame(), 
					      "<html>"+					 
					      "<p>Error: "+sd.getErrorNum()+
					      "<p>Message: "+sd.getErrorString()+
					      "<p>Command: "+command,
					      "System Ctrl", JOptionPane.INFORMATION_MESSAGE);
	    
	    
	}
    }
    
    
    @Override
	public void failed(Exception e, IConnection connection) {
	System.err.println(command.getId()+" CAMP Error: "+e);  
	e.printStackTrace();	
	if (connection != null)
	    connection.close();
    }



}
