/**
 * 
 */
package ngat.rcsgui.stable;

import java.awt.Color;

import javax.swing.JButton;



/**
 * @author eng
 *
 */
public class ColorStateButton extends JButton {

	Color color;
	String text;
	
	public ColorStateButton() {
		super();
	}
	
	public void update(Color color, String text) {
		setBackground(color);
		setText(text);		
	}
}
