package ngat.rcsgui.stable;

//package ngat.rcs.gui;

import java.io.*;
import java.net.*;

import ngat.net.*;
import ngat.net.camp.*;
import ngat.message.base.*;
import ngat.message.GUI_RCS.*;

/** Implements the CAMP protocol as client and response handler.*/
public abstract class Client implements Runnable, CAMPResponseHandler {

    public static final long DEFAULT_TIMEOUT = 20000L;

    RcsGUI gui;

    long timeout;

    IConnection conn = null;

    GUI_TO_RCS command;

    public Client( RcsGUI gui, GUI_TO_RCS command) {
	this.gui = gui;
	this.command = command;
	timeout = DEFAULT_TIMEOUT;
    }

    public void setConnection(IConnection conn) { this.conn = conn; }

    public void setTimeout(long timeout) { this.timeout = timeout; }

    @Override
	public void run() {

	if (conn == null)
	    conn = gui.createConnection("RCS_CMD");

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
	
	try {
	    Object obj = conn.receive(timeout);
	    System.err.println("Object recvd: "+(obj != null ? obj.getClass().getName() : "NULL"));
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
