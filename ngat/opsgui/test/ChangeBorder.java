/**
 * 
 */
package ngat.opsgui.test;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.border.TitledBorder;

/**
 * @author eng
 *
 */
public class ChangeBorder {

	/**
	 * @param args
	 */
	public static void main(String[] args) {
	
		TitledBorder border = BorderFactory.createTitledBorder("Testing");
		
		JPanel p = new JPanel();
		
		p.setBorder(border);
		
		p.setLayout(new BorderLayout());
		
		JButton red = new JButton("Make it red");
		red.addActionListener(new Tweak(Color.red, border, p));
		p.add(red, BorderLayout.CENTER);
		
		JButton green = new JButton("Make it green");
		green.addActionListener(new Tweak(Color.green, border, p));
		p.add(green, BorderLayout.WEST);
		
		JButton blue = new JButton("Make it blue");
		blue.addActionListener(new Tweak(Color.blue, border, p));
		p.add(blue, BorderLayout.EAST);
		
		JFrame f = new JFrame();
		f.getContentPane().add(p);
		f.pack();
		f.setVisible(true);
		
	}
	
	public static class Tweak implements ActionListener {

		Color mycolor;
		TitledBorder myborder;
		JPanel mypanel;
	
		public Tweak(Color mycolor, TitledBorder myborder, JPanel mypanel) {
			super();
			this.mycolor = mycolor;
			this.myborder = myborder;
			this.mypanel = mypanel;
		}



		@Override
		public void actionPerformed(ActionEvent e) {
			myborder.setTitleColor(mycolor);
			myborder.setBorder(BorderFactory.createLineBorder(mycolor));
			mypanel.repaint();
		}
		
	}

}
