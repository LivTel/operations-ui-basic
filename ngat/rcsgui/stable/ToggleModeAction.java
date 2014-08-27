package ngat.rcsgui.stable;

//package ngat.rcs.gui;

import javax.swing.*;
import java.awt.event.*;
import ngat.message.GUI_RCS.*;

/** Implements the toggle-init-mode button action.*/
public class ToggleModeAction extends AbstractAction {
    
    RcsGUI gui;

    /** True if the switch is to be ENG otherwise AUTO.*/
    boolean engineering;

    static int count = 0;

    public ToggleModeAction(RcsGUI gui, String text, Icon icon) {
	super(text, icon);
	this.gui = gui;

    }

    public void setEngineering(boolean engineering) { this.engineering = engineering; }

    @Override
	public void actionPerformed(ActionEvent ae) {
	
	count++;

	SWITCH_MODE swm = new SWITCH_MODE ("rcsgui:"+gui.getLocalHost()+":"+gui.getLoginUserName()+":"+count);
	// rcsgui:lttmc:ops1:4
     
	if (engineering) {
	
	    int reply = JOptionPane.showConfirmDialog(null,
						      "Confirm request to put RCS in Engineering mode",
						      "Confirm Engineering Request",
						      JOptionPane.OK_CANCEL_OPTION);
	    if (reply == JOptionPane.CANCEL_OPTION)
		return;
	    
	    swm.setMode(SWITCH_MODE.ENGINEERING);

	} else {

	    int reply = JOptionPane.showConfirmDialog(null,
						      "Confirm request to put RCS in Operational mode",
						      "Confirm Operational Request",
						      JOptionPane.OK_CANCEL_OPTION);
	    if (reply == JOptionPane.CANCEL_OPTION)
		return;
	    
	    swm.setMode(SWITCH_MODE.AUTOMATIC);

	}
	  
	setEnabled(false);

	final GUIClient client = new ToggleModeClient(gui, swm);
	
	(new Thread(client)).start();

    }


}
