/**
 * 
 */
package ngat.opsgui.util;

import java.awt.Color;

/** State color map entry. Records a mapping from a state to a color and label.
 * @author eng
 *
 */
public class StateColorMapEntry {

	public int state;
	
	public Color color;
	
	public String label;

	/**
	 * @param state
	 * @param color
	 * @param label
	 */
	public StateColorMapEntry(int state, Color color, String label) {
		super();
		this.state = state;
		this.color = color;
		this.label = label;
	}
	
	
	
}
