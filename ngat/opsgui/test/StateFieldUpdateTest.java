/**
 * 
 */
package ngat.opsgui.test;

import java.awt.BorderLayout;
import java.awt.Color;

import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;

import ngat.opsgui.util.StateColorMap;
import ngat.opsgui.util.StateField;

/**
 * @author eng
 *
 */
public class StateFieldUpdateTest {

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		
		StateColorMap map = new StateColorMap(Color.gray, "UNKNOWN");
		map.addColorLabel(1, Color.green, "OKAY");
		map.addColorLabel(2, Color.red, "FAIL");
		map.addColorLabel(3, Color.cyan, "INIT");
		map.addColorLabel(4, Color.blue, "RUNNING");
		map.addColorLabel(5, Color.pink, "WARNING");
		
		StateField f = new StateField(8);
		f.setMap(map);
		
		JFrame fr = new JFrame("State field");
		fr.setLayout(new BorderLayout());
		JPanel p = new JPanel(true);
		p.setLayout(new BorderLayout());
		p.add(new JLabel("Data"), BorderLayout.WEST);
		p.add(f, BorderLayout.CENTER);
		fr.getContentPane().add(p);
		fr.setVisible(true);
		
		for (int i = 0; i < 100; i++) {
			
			int k = 1+(int)(Math.random()*5.0);
			
			f.updateState(k);
			
			try {Thread.sleep(1000L);} catch (InterruptedException ix) {}
		}
		
		
		
		
	}

}
