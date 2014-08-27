/**
 * 
 */
package ngat.opsgui.util;

import javax.swing.JTextField;

/**
 * @author eng
 *
 */
public class DataField extends JTextField {

	/** Formatting string. Usually a double type format but can be integer if floating is false.*/
	private String format;
	
	/** True (default) if the data is expected to be floating.*/
	private boolean floating = true;

	/**
	 * @param format
	 */
	public DataField(int w, String format) {
		super(w);
		this.format = format;
	}
	
	/** Create a data field.
	 * @param format
	 * @param floating
	 */
	public DataField(int w, String format, boolean floating) {
		super(w);
		this.format = format;
		this.floating = floating;
	}
	
	
	public void updateData(double value) {
		if (floating)
			setText(String.format(format, value));
		else
			setText("?F");
	}
	
	public void updateData(int value) {
		if (!floating)
			setText(String.format(format, value));
		else
			setText("?F");
	}
	
}
