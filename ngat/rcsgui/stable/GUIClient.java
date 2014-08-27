package ngat.rcsgui.stable;

//package ngat.rcs.gui;

import ngat.net.camp.*;
import ngat.message.GUI_RCS.*;

/** Implements the CAMP protocol as client and response handler.*/
public abstract class GUIClient extends CAMPClient {

    RcsGUI gui;

    public GUIClient(RcsGUI gui, GUI_TO_RCS command) {
	super(gui.getConnectionFactory(), "RCS_CMD", command);	
	this.gui = gui;
    }

}
