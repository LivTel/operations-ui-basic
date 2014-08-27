package ngat.rcsgui.stable;


//package ngat.rcs.gui;

import javax.swing.*;
import ngat.net.*;
import ngat.net.camp.*;
import ngat.message.base.*;
import ngat.message.GUI_RCS.*;

/** Implements the CAMP protocol as client and response handler.*/
public class SendRCIClient extends GUIClient implements CAMPResponseHandler {

    public SendRCIClient(RcsGUI gui, GUI_TO_RCS command) {
	super(gui, command);
    }
    
    
    @Override
	public void handleUpdate(COMMAND_DONE update, IConnection connection) {
	
	System.err.println(command.getId()+" Closing connection.");
	connection.close();
	
	//System.err.println(command.getId()+" Received update: "+update);
	
	if (! (update instanceof SEND_RCI_DONE)) {
	    System.err.println(command.getId()+" CAMP Error: Unexpected class: "+update);
	    return;
	}

	String rciCommand  = ((SEND_RCI)command).getCommand(); 
	String rciResponse = ((SEND_RCI_DONE)update).getResponse();

	JOptionPane.showMessageDialog(null,
				      "<html>"+
				      "<table border = 0>"+
				      " <tr><td>Command</td>  <td>"+rciCommand+"</td></tr>"+
				      " <tr><td>Response</td> <td>"+rciResponse+"</td></tr>"+
				      "</table>",
				      "RCI Response",
				      JOptionPane.INFORMATION_MESSAGE);
	
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
