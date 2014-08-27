package ngat.rcsgui.stable;

//package ngat.rcs.gui;

import javax.swing.*;
import ngat.util.*;

/** Displays sky information. */
public class SkyPlotPanel extends JPanel {


	public SkyPlotPanel() {
		super(true);

	}

	public static void main(String args[]) {
		
		SpherePlot sky = new SpherePlot(Math.toRadians(30.), Math.toRadians(40.0));
		
		JFrame f = new JFrame("Sky plot");
		f.getContentPane().add(sky);
		f.setBounds(100, 100, 800, 600);
		f.setVisible(true);

		for (int i = 0; i < 200; i++) {

			double alt = 0.5 * Math.random() * Math.PI;
			double az = 2.0 * Math.random() * Math.PI;

			sky.putPoint((float)alt, (float)az);
			try {
				Thread.sleep(1000);
			} catch (Exception e) {
			}
		}
	}

}
