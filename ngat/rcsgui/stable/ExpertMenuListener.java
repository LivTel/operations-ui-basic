package ngat.rcsgui.stable;

//package ngat.rcs.gui;

import java.util.*;
import javax.swing.*;
import java.awt.event.*;
import ngat.message.GUI_RCS.*;

/** Implements the Expert menu.*/
public class ExpertMenuListener implements ActionListener {

    RcsGUI gui;

    GUI_TO_RCS command;

    protected Vector overrides;

    public ExpertMenuListener(RcsGUI gui) {
	this.gui = gui;
	overrides = new Vector();
    }
    
    public void addWeatherOverride(String signal) {
	overrides.add(signal);
    }

    @Override
	public void actionPerformed(ActionEvent ae) {

	String cmd = ae.getActionCommand();

	if (cmd.equals("weather-override")) {
	    	
	    int reply = JOptionPane.showConfirmDialog(null,
						      "Confirm request to Override Weather alerts",
						      "Confirm Weather Override",
						      JOptionPane.OK_CANCEL_OPTION);
	    if (reply == JOptionPane.CANCEL_OPTION)
		return;
	    
	    Iterator it = overrides.iterator();
	    while (it.hasNext()) {

		String ovr = (String)it.next();
	    
		SEND_EVENT send = new SEND_EVENT("rcsgui:");
		send.setTopic(ovr);
		
		final GUIClient client = new SendClient(gui, send);
		(new Thread(client)).start();
		
	    }
	    
	} else if
	    (cmd.equals("rci-command")) {
	    
	    String rciCommand = JOptionPane.showInputDialog(null,
							    "Please enter an RCI Command",
							    "RCI Command",
							    JOptionPane.QUESTION_MESSAGE);
	    
	    SEND_RCI srci = new SEND_RCI("rcsgui:");
	    srci.setCommand(rciCommand);
	    
	    final GUIClient client = new SendRCIClient(gui, srci);
	    (new Thread(client)).start();

	} else if
	(cmd.equals("tcs-command")) {
	    
		// popup a TCS CIL dialog
		
		String command = JOptionPane.showInputDialog(null, "Enter a TCS Command", "TCS Command via CIL", JOptionPane.QUESTION_MESSAGE);
						
	} else if
	    (cmd.startsWith("ext")) {


	    int    see    = SET_EXTINCTION.SPECTROSCOPIC;
	    String seeStr = "UNKNOWN";
	    int    seeSrc = SET_EXTINCTION.MANUAL_SOURCE;
	    String srcName = "Unknown";

	    if (cmd.equals("ext-photom")) {
		see    = SET_EXTINCTION.PHOTOMETRIC;
		seeStr = "PHOTOMETRIC";
		srcName = "RCSGUI@"+gui.getLocalHost()+"/"+gui.getLoginUserName();
	    } else if
		(cmd.equals("ext-spec")) {
		see    = SET_EXTINCTION.SPECTROSCOPIC;
		seeStr = "SPECTROSCOPIC";
		srcName = "RCSGUI@"+gui.getLocalHost()+"/"+gui.getLoginUserName();
	    } else if 
		  (cmd.equals("ext-ext")) {
		seeSrc = SET_EXTINCTION.EXTERNAL_SOURCE;
		seeStr = "EXTERNAL";		
	    } else {
		
		JOptionPane.showMessageDialog(null,
					      "Unknown option",
					      "Set Extinction for sky model",
					      JOptionPane.ERROR_MESSAGE);
		return;

	    }

	    int reply = JOptionPane.showConfirmDialog(null,
						      "Confirm request to set sky model extinction to: "+seeStr,
						      "Confirm Set sky model Extinction",
						      JOptionPane.OK_CANCEL_OPTION);
	    if (reply == JOptionPane.CANCEL_OPTION)
		return;
	    
	    
	    SET_EXTINCTION set = new SET_EXTINCTION("rcsgui:");
	    set.setExtinction(see);
	    set.setSource(seeSrc);
	    set.setSourceName(srcName);
	    
	    final GUIClient client = new SetSeeingClient(gui, set);
	    (new Thread(client)).start();

	} else if
	    (cmd.startsWith("see")) {


	    int    see    = SET_SEEING.SEEING_POOR;
	    String seeStr = "UNKNOWN";


	    if (cmd.equals("see-good")) {
		see    = SET_SEEING.SEEING_EXCELLENT;
		seeStr = "GOOD";
	
	    } else if
		(cmd.equals("see-aver")) {
		see    = SET_SEEING.SEEING_AVERAGE;
		seeStr = "AVERAGE";
      
	    } else if 
		  (cmd.equals("see-poor")) {
		see    = SET_SEEING.SEEING_POOR;
		seeStr = "POOR";	

	    } else if
		  (cmd.equals("see_usab")) {
		//see    = SET_SEEING.SEEING_USABLE;
		//seeStr = "USABLE";	

	    } else {
		
		JOptionPane.showMessageDialog(null,
					      "Unknown option",
					      "Set Sky model seeing",
					      JOptionPane.ERROR_MESSAGE);
		return;

	    }

	    int reply = JOptionPane.showConfirmDialog(null,
						      "Confirm request to set sky model seeing to: "+seeStr,
						      "Confirm Set sky model Seeing",
						      JOptionPane.OK_CANCEL_OPTION);
	    if (reply == JOptionPane.CANCEL_OPTION)
		return;
	    
	    
	    SET_SEEING set = new SET_SEEING("rcsgui:");
	    set.setSeeing(see);

	    
	    final GUIClient client = new SetSeeingClient(gui, set);
	    (new Thread(client)).start();
	    

	    
	}
	
	
    }

}
