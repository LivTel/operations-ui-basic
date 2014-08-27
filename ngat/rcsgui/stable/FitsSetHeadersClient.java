package ngat.rcsgui.stable;

//package ngat.rcs.gui;

import javax.swing.*;
import ngat.net.*;
import ngat.net.camp.*;
import ngat.message.base.*;
import ngat.message.GUI_RCS.*;

/** Implements the CAMP protocol as client and response handler.*/
public class FitsSetHeadersClient extends GUIClient implements CAMPResponseHandler {

    public FitsSetHeadersClient(RcsGUI gui, GUI_TO_RCS command) {
	super(gui, command);
    }
    
    
    @Override
	public void handleUpdate(COMMAND_DONE fitsdone, IConnection connection) {
	
	System.err.println(command.getId()+" Closing connection.");
	if (connection != null)
	    connection.close();
	
	//System.err.println(command.getId()+" Received update: "+update);
	
	if (! (fitsdone instanceof ISS_SET_HEADERS_DONE)) {
	    System.err.println(command.getId()+" CAMP Error: Unexpected class: "+fitsdone);
	    JOptionPane.showMessageDialog(gui.getFrame(), 
					  "<html>"+					 
					  "<p>Error: "+fitsdone.getErrorNum()+
					  "<p>Message: "+fitsdone.getErrorString(),
					  "FITS Header mode", 
					  JOptionPane.INFORMATION_MESSAGE);
	    
	    return;
	}
 
	if (! fitsdone.getSuccessful())
	    JOptionPane.showMessageDialog(gui.getFrame(), 
					  "<html>"+					 
					  "<p>Error: "+fitsdone.getErrorNum()+
					  "<p>Message: "+fitsdone.getErrorString(),
					  "FITS Header mode", 
					  JOptionPane.INFORMATION_MESSAGE);
	
	// ### TBD This would be best in a status area at bottom of gui.
	System.err.println("** Successfully set FITS Header mode");
	
    }
     
    @Override
	public void failed(Exception e, IConnection connection) {
	System.err.println(command.getId()+" CAMP Error: "+e);  
	e.printStackTrace();	
	if (connection != null)
	    connection.close();
    }



}
