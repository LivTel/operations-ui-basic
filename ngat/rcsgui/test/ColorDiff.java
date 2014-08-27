/**
 * 
 */
package ngat.rcsgui.test;

import java.awt.Color;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;

/**
 * @author eng
 *
 */
public class ColorDiff {

	JLabel jl;
	
	public ColorDiff() {
		
		JFrame f = new JFrame();
		jl = new JLabel("A little test");
		jl.setFont(new Font("serif", Font.PLAIN, 45));
		jl.setOpaque(true);
		JButton bb = new JButton("next >>");
		bb.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent e) {
				
				int r = (int)(255*Math.random());
				int g = (int)(255*Math.random());
				int b = (int)(255*Math.random());
				 
				Color color = new Color(r, g, b);
				System.err.println("BG Color: "+color);
				float[] hsb = new float[3];
				
				hsb = Color.RGBtoHSB(r, g, b, null);
				
				System.err.println("HSB="+hsb[0]+", "+hsb[1]+", "+hsb[2]);
				
				float h2 = (hsb[0]+1);
				if (h2 > 1.0f)
					h2 = h2 - 1.0f;
				
				float s2 = 1.0f - hsb[1];
				
				float l2 = 1.0f - hsb[2];
				
				Color cont = Color.getHSBColor(h2,  s2, l2);
				System.err.println("FG Color: "+cont);
				
				jl.setBackground(color);
				jl.setForeground(cont);
				jl.repaint();
			}
		});
				
		f.getContentPane().setLayout(new FlowLayout(FlowLayout.LEADING));
		f.getContentPane().add(jl);
		f.getContentPane().add(bb);
		f.pack();
		f.setVisible(true);
		
	}
	
	/**
	 * @param args
	 */
	public static void main(String[] args) {
	
		ColorDiff cd = new ColorDiff();
		
	}

}
