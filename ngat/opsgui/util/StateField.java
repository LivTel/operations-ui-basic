/**
 * 
 */
package ngat.opsgui.util;

import java.awt.Color;

import javax.swing.JTextField;

/**
 * @author eng
 *
 */
public class StateField extends JTextField {
	
	public static final String NYA_MESSAGE = "N-Y-K";
	
	public static final Color NYA_BACKGROUND = new Color(229, 223, 240);
	
	public static final Color NYA_FOREGROUND = new Color(91, 1, 130);
	
	/** Color mapping.*/
	private StateColorMap map;

	/**
	 * 
	 */
	public StateField(int w) {
		super(w);
		setBackground(NYA_BACKGROUND);
		setForeground(NYA_FOREGROUND);
		setText(NYA_MESSAGE);
	}

	/**
	 * @param map the color map to set
	 */
	public void setMap(StateColorMap map) {
		this.map = map;
	}
	
	
	
	
	public void updateState(int state) {
		Color bg = map.getColor(state);
		Color fg = new Color(bg.getRed()^255, bg.getBlue()^255, bg.getGreen()^255);
		setBackground(bg);
		if(bg.getBlue() > 200 && bg.getRed() < 100 && bg.getGreen() < 100)
                    setForeground(Color.yellow);
		else
		    setForeground(Color.blue);
		String text = map.getLabel(state);
		setText(text);		
		//repaint(); // needed ?
	}
	
}
