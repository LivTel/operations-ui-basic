/**
 * 
 */
package ngat.rcsgui.test;

import java.awt.FlowLayout;

import javax.swing.ImageIcon;
import javax.swing.JFrame;
import javax.swing.JPanel;

/**
 * @author eng
 *
 */
public class CreateRoundButtonBar {

	/**
	 * @param args
	 */
	public static void main(String[] args) {
	
		JPanel p = new JPanel(true);
		p.setLayout(new FlowLayout(FlowLayout.LEFT));
		
		RoundButton b1 = new RoundButton(new ImageIcon("/home/eng/PicturesDeviantArt-icon.png"));
		p.add(b1);
		RoundButton b2 = new RoundButton(new ImageIcon("/home/eng/Pictures/WifiTrak-icon.png"));
		p.add(b2);
		RoundButton b3 = new RoundButton(new ImageIcon("/home/eng/Pictures/Newsfeed-RSS-icon2.png"));
		p.add(b3);
		RoundButton b4 = new RoundButton(new ImageIcon("/home/eng/rcsgui/icons/Button-Refresh-icon.png"));
		p.add(b4);
		RoundButton b5 = new RoundButton(new ImageIcon("/home/eng/rcsgui/icons/Button-Rewind-icon.png"));
		p.add(b5);
		RoundButton b6 = new RoundButton(new ImageIcon("/home/eng/rcsgui/icons/Zoom-In-icon.png"));
		p.add(b6);
		RoundButton b7 = new RoundButton(new ImageIcon("i/home/eng/rcsgui/cons/Zoom-Out-icon.png"));
		p.add(b7);
		RoundButton b8 = new RoundButton(new ImageIcon("/home/eng/rcsgui/icons/info-icon.png"));
		p.add(b8);
		JFrame f = new JFrame("Round buttons");
		f.getContentPane().add(p);
		f.pack();
		f.setVisible(true);
		
		
		
	}

}
