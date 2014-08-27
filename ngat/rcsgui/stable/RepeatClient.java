package ngat.rcsgui.stable;

//package ngat.rcs.gui;

import java.io.*;
import java.net.*;

import ngat.net.*;
import ngat.net.camp.*;
import ngat.message.base.*;
import ngat.message.GUI_RCS.*;

/** Implements the CAMP protocol as client and response handler.*/
public abstract class RepeatClient implements Runnable, CAMPResponseHandler {

    public static final long DEFAULT_TIMEOUT = 20000L;

    RcsGUI gui;

    long timeout;
    
    volatile boolean valid = true;

    GUI_TO_RCS command;

    public RepeatClient( RcsGUI gui, GUI_TO_RCS command) {
	this.gui = gui;
	this.command = command;
	timeout = DEFAULT_TIMEOUT;
    }

    public void setTimeout(long timeout) { this.timeout = timeout; }

    public void terminate() { valid = false; }

    @Override
	public void run() {

	IConnection conn = gui.createConnection("RCS_CMD");

	if (conn == null) {
	    failed(new IOException("No connection resource: RCS_CMD"), conn);
	    return;
	}

	try {
	    conn.open();
	} catch (ConnectException cx) {
	    failed(cx, conn);
	    return;
	}
	
	try {
	    conn.send(command);
	} catch (IOException iox) {
	    failed(iox, conn);
	    return;
	}
	
	while (valid) {
	    
	    try {
		Object obj = conn.receive(timeout);
		
		COMMAND_DONE update = (COMMAND_DONE)obj;	   
		handleUpdate(update, conn);
	    } catch (ClassCastException cx) {
		failed(cx, conn);
		return;
	    } catch (IOException iox) {
		failed(iox, conn);
		return;
	    }

	}

    }

}
