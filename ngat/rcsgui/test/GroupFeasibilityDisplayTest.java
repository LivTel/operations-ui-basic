package ngat.rcsgui.test;

import ngat.astrometry.*;
import ngat.phase2.*;
import javax.swing.*;

public class GroupFeasibilityDisplayTest {

    public static void main(String args[]) {

	try {
	    
	    double soff = Double.parseDouble(args[0]);
	    long start = System.currentTimeMillis() + (long)(soff*3600.0*1000.0);

	    double ra  = Math.toRadians(Double.parseDouble(args[1]));
	    double dec = Math.toRadians(Double.parseDouble(args[2]));

	    XExtraSolarTarget target = new XExtraSolarTarget("star");
	    target.setRa(ra);
	    target.setDec(dec);



	BasicSite site = new BasicSite("LT", Math.toRadians(28.0),  Math.toRadians(-17.0));

	GroupFeasibilityDisplay display = new GroupFeasibilityDisplay(site);

	display.displayGroup(target, start);

	JFrame f = new JFrame("Group Feasibility Display");
	f.getContentPane().add(display);

	f.pack();
	f.setVisible(true);
	
	} catch (Exception e) {
	    e.printStackTrace();
	}
    }
   
}