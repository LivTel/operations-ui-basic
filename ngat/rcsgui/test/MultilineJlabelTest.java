/**
 * 
 */
package ngat.rcsgui.test;

import java.awt.Font;
import java.awt.GridLayout;
import java.awt.Insets;

import javax.swing.BorderFactory;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;

/**
 * @author eng
 *
 */
public class MultilineJlabelTest {

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		
		Insets ii = new Insets(3,2,3,2);
		
		JPanel p = new JPanel(true);
	
		p.setLayout(new GridLayout(2, 2,3,3));
		
		JLabel l = new JLabel();
		l.setText("<html><P>Predicted seeing <br> at <font color=\"red\">zenith</font> ( \" )");
		l.setFont(new Font("Serif", Font.PLAIN, 11));
		l.setBorder(BorderFactory.createRaisedBevelBorder());		
		p.add(l);
		p.add(new JTextField(8));
		
		JLabel l2 = new JLabel();
		l2.setText("<html><P>Last seeing<P> <font color=\"blue\">sample</font> ( \" )");
		l2.setFont(new Font("Serif", Font.PLAIN, 11));
		l2.setBorder(BorderFactory.createRaisedBevelBorder());
		p.add(l2);
		p.add(new JTextField(8));
		
		JFrame f = new JFrame("Multilinejlabel");
		f.getContentPane().add(p);
		f.pack();
		f.setVisible(true);
		
	}

}
