package ngat.rcsgui.stable;

import ngat.util.*;
import ngat.net.*;
import ngat.net.camp.*;
import ngat.message.base.*;
import ngat.message.GUI_RCS.*;
import ngat.message.RCS_TCS.*;

import java.io.*;

public class TestClient {
    
    public static void main(String args[]) {
	
	try {
	    // (required for proper decoding of TCS replies).
	    TCS_Status.mapCodes();
	    
	    String CAT = "MECHANISM"; // mechanisms
	    //String KEY = "enclosure.shutter.1.status"; // enclosure shutter 1 status
	    
	    //String HOST = "192.168.1.30"; // ftn occ
	    //int    PORT = 9120;           // rcs control port
	    
	    //long interval = 600000L; // 10 minutes.
	    
	    String HOST     = args[0];
	    int    PORT     = Integer.parseInt(args[1]);	 
	    String KEY      = args[2];

	    IConnection connection = new SocketConnection(HOST, PORT);

	    GET_STATUS getStat = new GET_STATUS("test");
	    getStat.setCategory(CAT);
	    
	    //System.err.println("Setting up QT");

	    CAMPResponseHandler handler = new MyStatusHandler(KEY);

	    try {
		connection.open();
	    } catch (IOException cx) {
		handler.failed(cx, connection);
		return;
	    }
	    
	    try {
		connection.send(getStat);
	    } catch (IOException iox) {
		handler.failed(iox, connection);
		return;
	    }
	    
	    try {
		Object obj = connection.receive(10000L);
		System.err.println("Object recvd: "+obj);
		COMMAND_DONE update = (COMMAND_DONE)obj;
		handler.handleUpdate(update, connection);
	    } catch (ClassCastException cx) {
		handler.failed(cx, connection);
		return;
	    } catch (IOException iox) {
		handler.failed(iox, connection);
		return;
	    }

	} catch (Exception e) {
	    e.printStackTrace();
	    return;
	}
    }



    //Here is an example of a handler which handles a specific status key, more likely want to handle
    //all the keys in a given status category.

    public static class MyStatusHandler implements CAMPResponseHandler {
	
	// The key we are after. e.g. "enclosure.1.status"
	String key;
	
	MyStatusHandler(String key) {
	    this.key = key;
	}
	
        // This is called when a response is received.
	@Override
	public void handleUpdate(COMMAND_DONE update, IConnection connection) {
	    
	    if (connection != null)
		connection.close();
	    
	    if (! (update instanceof GET_STATUS_DONE)) {
		System.err.println("CAMP Error: Unexpected class: "+update);
		return;
	    }
      
	    //System.err.println("Received: "+update.getClass().getName()+
	    //         "Success: "+update.getSuccessful()+
	    //         "ErrNO:   "+update.getErrorNum()+
	    //         "Message: "+update.getErrorString());
	    
	    StatusCategory status = ((GET_STATUS_DONE)update).getStatus();

	    // ***** Note: This will throw an exception if the key is unknown
	    //             or of the wrong type.   	
	    int code = status.getStatusEntryInt(key);
	    
	    // ***** This is where we actually dump the value out.
	    
	    System.err.println("###: "+code+" / "+TCS_Status.codeString(code));
	    
	    
	    // ***** Alternatively just ignore the key and print the lot.
	    //System.err.println("The value of all keys in category were: "+status.toString());
	    
	}
	
	// This is called if an exception occurs during protocol implementation.
	@Override
	public void failed(Exception e, IConnection connection) {  
	    e.printStackTrace();  
	    //System.err.println("Close connection.");
	    if (connection != null)
		connection.close();    
	}
	
    } // [MyStatusHandler]
    
}
