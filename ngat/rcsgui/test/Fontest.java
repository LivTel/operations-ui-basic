/**
 * 
 */
package ngat.rcsgui.test;

import java.awt.Dimension;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.font.FontRenderContext;
import java.awt.geom.Rectangle2D;

import javax.swing.JFrame;
import javax.swing.JPanel;

/**
 * @author eng
 *
 */
public class Fontest extends JPanel {

	static Dimension SIZE = new Dimension(600, 50);
	int fs = 200;
	Font f; 
	String label; 
	
	public Fontest(String label){
		this.label = label;
		setPreferredSize(SIZE);	
		f = new Font("serif", Font.PLAIN, 12);
		
	}
	
	
	
	/* (non-Javadoc)
	 * @see javax.swing.JComponent#paint(java.awt.Graphics)
	 */
	@Override
	public void paint(Graphics g) {		
		super.paint(g);
		fs = 200;
		// how much space have we got ?
		
		int w = getSize().width;
		int h = getSize().height;
		
		// we need 10% for the label
		
		int mlw = w/5;
		int alw = w;
		
		FontRenderContext frc = ((Graphics2D)g).getFontRenderContext();
		Rectangle2D d = null;
		
		while (alw > mlw) {
			d = f.getStringBounds(label, frc);		
			alw = (int)(d.getWidth());
			fs--;
			f = new Font("serif", Font.PLAIN, fs);
		}
		g.setFont(f);
		g.drawString(label, 0, h);
		
		g.fillRect(alw, 5, w-alw, h-5);
		
	}



	/**
	 * @param args
	 */
	public static void main(String[] args) {
		JFrame f = new JFrame("Test");
		f.getContentPane().add(new Fontest("Test"));
		f.pack();		
		f.setVisible(true);
	}

}
