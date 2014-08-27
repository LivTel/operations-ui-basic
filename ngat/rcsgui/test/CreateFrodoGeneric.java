/** Create a frodo generic panel.
 * 
 */
package ngat.rcsgui.test;

import java.util.Hashtable;

import javax.swing.JFrame;

/**
 * @author eng
 *
 */
public class CreateFrodoGeneric {

	/**
	 * @param args
	 */
	public static void main(String[] args) {
	
		JFrame frame = new JFrame("Test FRODO generic");
		frame.setBounds(100,100,200,200);
		FrodoGeneralPanel fp = new FrodoGeneralPanel();
		frame.getContentPane().add(fp);
		frame.pack();
		frame.setVisible(true);

		Hashtable h = new Hashtable();
		fp.update(h);
		
	}

}
