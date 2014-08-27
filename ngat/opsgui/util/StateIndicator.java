/**
 * 
 */
package ngat.opsgui.util;

import java.awt.Color;

import javax.swing.BorderFactory;
import javax.swing.JLabel;
import javax.swing.SwingConstants;


/**
 * @author eng
 *
 */
public class StateIndicator extends JLabel {

	/** Color mapping.*/
	private StateColorMap map;
	
	private String stateText;

	private int state;
	
	
	
	/**
	 * 
	 */
	public StateIndicator() {
		super();
		setOpaque(true);
		setBorder(BorderFactory.createBevelBorder(javax.swing.border.BevelBorder.LOWERED));
		setHorizontalAlignment(SwingConstants.CENTER);
	}

	/**
	 * @param state the state to set
	 */
	public void setState(int state) {
		this.state = state;
	}

	/**
	 * @param map the map to set
	 */
	public void setMap(StateColorMap map) {
		this.map = map;
	}

	/**
	 * @param stateText the stateText to set
	 */
	public void setStateText(String stateText) {
		this.stateText = stateText;
	}
	
	public void updateState(int state, String stateText) {
		this.state = state;
		this.stateText = stateText;		
		Color bg = map.getColor(state);
		Color fg = new Color(bg.getRed()^255, bg.getBlue()^255, bg.getGreen()^255);
		setBackground(bg);
		setForeground(Color.blue);
		setText(stateText);		
		repaint(); // needed ?
	}
	
}
