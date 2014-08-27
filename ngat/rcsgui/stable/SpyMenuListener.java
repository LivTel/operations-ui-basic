package ngat.rcsgui.stable;

//package ngat.rcs.gui;

import java.awt.event.*;
import ngat.message.GUI_RCS.*;

/** Implements the Spy menu.*/
public class SpyMenuListener implements ActionListener {

    RcsGUI gui;

    /** Spy category.*/
    int spyCat;

    GUI_TO_RCS command;


    public SpyMenuListener(RcsGUI gui, int spyCat) {
	this.gui    = gui;
	this.spyCat = spyCat;
    }

    @Override
	public void actionPerformed(ActionEvent ae) {

	String cmd = ae.getActionCommand();

	switch (spyCat) {

	case SPY.SENSOR:

	    break;
	case SPY.FILTER:

	    break;

	case SPY.RULE:

	    break;
	}

	SPY spy = new SPY("rcsgui");
	spy.setCategory(spyCat);
	spy.setTarget(cmd);
		
	final GUIClient client = new SpyClient(gui, spy);
	(new Thread(client)).start();

    }

}
