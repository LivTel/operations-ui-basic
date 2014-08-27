package ngat.rcsgui.stable;

//package ngat.rcs.gui;

import javax.swing.*;
import java.awt.event.*;
import ngat.message.GUI_RCS.*;

/** Implements the event menu.*/
public class WeatherOverrideListener implements ActionListener {

    RcsGUI gui;

    final String[] overrides = new String[] {
	"RAIN_CLEAR_CONFIRM",
	"HUMIDITY_LOW_CONFIRM",
	"WIND_CLEAR_CONFIRM",
	"TEMPERATURE_HIGH_CONFIRM",
	"UH_WIND_LOW_CONFIRM",
	"UH_HUM_LOW_CONFIRM",
	"UH_TEMP_HIGH_CONFIRM ",
	"UH_CLOUD_LOW_CONFIRM",
	"CAPS_FOR_LOW_CONFIRM"
    };

    public WeatherOverrideListener (RcsGUI gui) {
	this.gui = gui;
    }

    @Override
	public void actionPerformed(ActionEvent ae) {

	String cmd = ae.getActionCommand();
	
	int reply = JOptionPane.showConfirmDialog(null,
						  "Confirm request to Override Weather alerts",
						  "Confirm Weather Override",
						  JOptionPane.OK_CANCEL_OPTION);
	if (reply == JOptionPane.CANCEL_OPTION)
	    return;

	for (int i = 0; i < overrides.length; i++) {
	    SEND_EVENT send = new SEND_EVENT("rcsgui:");
	    send.setTopic(overrides[i]);

	    final GUIClient client = new SendClient(gui, send);
	    (new Thread(client)).start();

	}

    }

}
