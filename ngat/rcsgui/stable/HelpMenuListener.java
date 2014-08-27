package ngat.rcsgui.stable;

//package ngat.rcs_gui;

import java.awt.event.*;
import javax.swing.*;

/** Implements the event menu.*/
public class HelpMenuListener implements ActionListener {

    RcsGUI gui;

    public HelpMenuListener(RcsGUI gui) {
	this.gui = gui;
    }

    @Override
	public void actionPerformed(ActionEvent ae) {

	VersionInfo v = gui.rcsVersion;

	JOptionPane.showMessageDialog(gui.getFrame(), 
				      "RCS: "+v.majorVersion+"."+v.minorVersion+"."+v.patchVersion+
				      " ( "+v.releaseName+" ): Build: "+v.buildNumber+" On: "+v.buildDate, 
				      "RCS Version Information",
				      JOptionPane.INFORMATION_MESSAGE);


	// RCS: 2.3.4 (Gyrfalcon): Build: 239 On: 20090812_0832 
	
	
    }
    
}
