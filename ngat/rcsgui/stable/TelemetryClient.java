package ngat.rcsgui.stable;

//package ngat.rcs.gui;

import ngat.net.*;
import ngat.net.camp.*;
import ngat.message.base.*;
import ngat.message.GUI_RCS.*;

/** Implements the CAMP protocol as client and response handler.*/
public class TelemetryClient extends CAMPClient implements CAMPResponseHandler {

    String host;

    int    port;

    public TelemetryClient(IConnection connection, GUI_TO_RCS command) {
	super(connection, command);
	host = ((TELEMETRY)command).getConnect().host;
	port = ((TELEMETRY)command).getConnect().port;
    }

    @Override
	public void handleUpdate(COMMAND_DONE update, IConnection connection) {
		
	System.err.println(command.getId()+" Closing connection.");
	connection.close();
       
	if (! (update instanceof TELEMETRY_DONE)) {
	    System.err.println(command.getId()+" CAMP Error: Unexpected class: "+update);
	    return;
	}

	if (update instanceof TELEMETRY_DONE) {
	    
	    TELEMETRY_DONE sd = (TELEMETRY_DONE)update;


	    if (update.getSuccessful()) {
		System.err.println("TelemetryRequestorClient: Connected");
// 		JOptionPane.showMessageDialog(null,
// 					      "Telemetry Established to RCS",
// 					      "Telemetry Request",
// 					      JOptionPane.INFORMATION_MESSAGE);
		
	    } else {
		
		int    errno  = update.getErrorNum();
		String errmsg = update.getErrorString();
		System.err.println("TelemetryRequestorClient: Error establishing connection to RCS");
	// 	JOptionPane.showMessageDialog(null,
// 					      "<html>"+
// 					      "<p>Unable to establish telemetry with RCS: "+host+" : "+port+
// 					      "<table border = 0>"+
// 					      " <tr><td>Error</td>  <td>"+errno+"</td></tr>"+
// 					      " <tr><td>Message</td> <td>"+errmsg+"</td></tr>"+
// 					      "</table>",					  
// 					      "Telemetry Request",
// 					      JOptionPane.ERROR_MESSAGE);	
		
		
	    }

	} 


    }
    
    
    @Override
	public void failed(Exception e, IConnection connection) {
	System.err.println(command.getId()+"TELEMETRY:CAMP Error: "+e);  
	e.printStackTrace();
	if (connection != null)
	    connection.close();	
	System.err.println("TelemetryRequestorClient: Error establishing connection to RCS");
// 	JOptionPane.showMessageDialog(null,
// 				      "<html>"+
// 				      "<p>Unable to establish telemetry with RCS: "+host+" : "+port+
// 				      "<table border = 0>"+
// 				      " <tr><td>Error</td>  <td> CONNECT</td></tr>"+
// 				      " <tr><td>Message</td> <td>"+e+"</td></tr>"+
// 				      "</table>",					  
// 				      "Telemetry Request",
// 				      JOptionPane.ERROR_MESSAGE);
		
    }        

}
