package ngat.rcsgui.stable;

//package ngat.rcs.gui;

import java.awt.event.*;
import ngat.message.GUI_RCS.*;

/** Implements the control menu.*/
public class ControlMenuListener implements ActionListener {

    RcsGUI gui;

    GUI_TO_RCS command;

    static int count = 0;

    public ControlMenuListener(RcsGUI gui) {
	this.gui = gui;
    }

    @Override
	public void actionPerformed(ActionEvent ae) {

	String cmd = ae.getActionCommand();

	count++;

	if (cmd.equals("start-rcs-eng")) {

	    // START ENG

	    START start = new START("rcsgui:control:"+count);
	    start.setEngineering(true);

	    command = start;

	} else if
	    (cmd.equals("start-rcs-auto")) {

	    // START AUTO
	  
	    START start = new START("rcsgui:control:"+count);
	    start.setEngineering(false);

	    command = start;

	} else if
	    (cmd.equals("restart-rcs-eng")) {	   
	    
	    // RESTART ENG

	    SYSTEM sys = new SYSTEM("rcsgui:control:"+count);
	    sys.setLevel(SYSTEM.RESTART_ENGINEERING);
	    
	    command = sys;

	} else if
	    (cmd.equals("restart-rcs-auto")) {
	   
	    // RESTART AUTO

	    SYSTEM sys = new SYSTEM("rcsgui:control:"+count);
	    sys.setLevel(SYSTEM.RESTART_AUTOMATIC);

	    command = sys;

	} else if
	    (cmd.equals("halt-rcs")) {

	    // HALT

	    SYSTEM sys = new SYSTEM("rcsgui:control:"+count);
	    sys.setLevel(SYSTEM.HALT);

	    command = sys;

	} else if
	    (cmd.equals("reboot-occ")) {

	    // REBOOT

	    SYSTEM sys = new SYSTEM("rcsgui:control:"+count);
	    sys.setLevel(SYSTEM.REBOOT);

	    command = sys;

	} else if
	    (cmd.equals("shutdown-occ")) {

	    // SHUTDOWN

	    SYSTEM sys = new SYSTEM("rcsgui:control:"+count);
	    sys.setLevel(SYSTEM.SHUTDOWN);

	    command = sys;

	} else {
	    System.err.println("CTRL MENU: Unknown option: "+cmd);
	}

	final GUIClient client = new SystemClient(gui, command);
	(new Thread(client)).start();

    }

}
