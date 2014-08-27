/**
 * 
 */
package ngat.opsgui.test;

import java.awt.BorderLayout;

import javax.swing.JFrame;

import ngat.opsgui.components.AxisSummaryPanel;

/**
 * @author eng
 *
 */
public class CreateAxisSummaryPanel {

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		
		AxisSummaryPanel asp = new AxisSummaryPanel("Axes");
		asp.createPanel();
		
		JFrame f = new JFrame("Axis Summary");
		f.getContentPane().setLayout(new BorderLayout());
		f.getContentPane().add(asp, BorderLayout.CENTER);
		f.pack();
		f.setVisible(true);
		
		for (int i = 0; i < 50; i++) {
			
			int azm = (int)(Math.random()*4.0)+1;
			int alt = (int)(Math.random()*4.0)+1;
			int rot = (int)(Math.random()*4.0)+1;
			
			//asp.updateAxes(azm, alt, rot);
			
			try {Thread.sleep(2000);}catch(InterruptedException ix) {}
			
		}

	}

}
