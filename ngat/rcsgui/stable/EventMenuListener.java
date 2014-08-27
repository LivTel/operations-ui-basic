package ngat.rcsgui.stable;

//package ngat.rcs.gui;

import javax.swing.*;
import java.awt.event.*;
import ngat.message.GUI_RCS.*;

/** Implements the event menu.*/
public class EventMenuListener implements ActionListener {

    RcsGUI gui;

    GUI_TO_RCS command;

    static int count = 0;

    public EventMenuListener(RcsGUI gui) {
	this.gui = gui;
    }

    @Override
	public void actionPerformed(ActionEvent ae) {

	String cmd = ae.getActionCommand();

	int reply = JOptionPane.showConfirmDialog(null,
						  "Confirm signal request ["+cmd+"]",
						  "Confirm Signal Request",
						  JOptionPane.OK_CANCEL_OPTION);
	if (reply == JOptionPane.CANCEL_OPTION)
	    return;
	
	count++;

	SEND_EVENT send = new SEND_EVENT("rcsgui:"+count);
	send.setTopic(cmd);

	command = send;

	final GUIClient client = new SendClient(gui, command);
	(new Thread(client)).start();

    }

}
