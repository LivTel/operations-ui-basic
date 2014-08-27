/**
 * 
 */
package ngat.opsgui.util;

import javax.swing.JTextField;

/**
 * @author eng
 *
 */
public class TimeField extends JTextField {
	
	/** Formatting string.*/
	private String format;
	
	public TimeField(int width, String format) {
		super(width);
		this.format = format;
	}
	
	public void updateData(long value) {	
		setText(String.format(format, value));		
	}
}
