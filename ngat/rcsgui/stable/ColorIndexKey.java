/**
 * 
 */
package ngat.rcsgui.stable;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;

import javax.swing.JPanel;

/**
 * @author eng
 *
 */
public class ColorIndexKey extends JPanel {
	
	public static Dimension SIZE = new Dimension(30, 6);
	
	private Color keyColor;

	/**
	 * @param keyColor
	 */
	public ColorIndexKey(Color keyColor) {
		super();
		this.keyColor = keyColor;
	}
	
	@Override
	public Dimension getPreferredSize() {
		return SIZE;
	}
	
	@Override
	public void paint(Graphics g) {
		super.paint(g);
		g.setColor(keyColor);
		g.fillRect(0,0, SIZE.width, SIZE.height);		
	}
	
}
