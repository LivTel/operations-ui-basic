/**
 * 
 */
package ngat.opsgui.test;

import java.awt.Color;
import java.awt.Graphics;
import javax.swing.JPanel;

/**
 * @author eng
 *
 */
public class CirclePanel extends JPanel {

	private Color color1;
	
	private Color color2;
	
	/**
	 * 
	 */
	public CirclePanel() {
		color1 = Color.red;
		color2 = Color.green;
	}

	
	
	@Override
	public void paint(Graphics g) {	
		super.paint(g);
		
		int w = getSize().width;
		int h = getSize().height;
		
		g.setColor(color1);
		g.fillOval(w/4, h/4, w/2, h/2);
		
		g.setColor(color2);
		g.fillOval(3*w/4, 3*h/4, w/4, h/4);
		
		
	}



	public Color getColor1() {
		return color1;
	}

	public void setColor1(Color color1) {
		this.color1 = color1;
	}

	public Color getColor2() {
		return color2;
	}

	public void setColor2(Color color2) {
		this.color2 = color2;
	}

	
}
