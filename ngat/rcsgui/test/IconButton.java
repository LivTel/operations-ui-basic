/**
 * 
 */
package ngat.rcsgui.test;

import java.awt.Dimension;

import javax.swing.Icon;
import javax.swing.JButton;

/**
 * @author eng
 *
 */
public class IconButton extends JButton {

	/** Name by which this IconButton is called.*/
	private String name;
	
	/** Short description of function as used in tool-tips.*/
	private String shortDescription;
	
	/** Long description of function.*/
	private String longDescription;

	/**
	 * @param name Name by which this IconButton is called.
	 * @param shortDescription Short description of function as used in tool-tips.
	 * @param longDescription Long description of function.
	 */
	public IconButton(String name, Icon icon, String text, String shortDescription, String longDescription) {
		super(text, icon);	
		this.name = name;
		this.shortDescription = shortDescription;
		this.longDescription = longDescription;
		setPreferredSize(new Dimension(20, 20));
	}

	/**
	 * @return the name
	 */
	@Override
	public String getName() {
		return name;
	}

	/**
	 * @param name the name to set
	 */
	@Override
	public void setName(String name) {
		this.name = name;
	}

	/**
	 * @return the shortDescription
	 */
	public String getShortDescription() {
		return shortDescription;
	}

	/**
	 * @param shortDescription the shortDescription to set
	 */
	public void setShortDescription(String shortDescription) {
		this.shortDescription = shortDescription;
	}

	/**
	 * @return the longDescription
	 */
	public String getLongDescription() {
		return longDescription;
	}

	/**
	 * @param longDescription the longDescription to set
	 */
	public void setLongDescription(String longDescription) {
		this.longDescription = longDescription;
	}
	
	// add some handler to recieve data callbacks to display
	
	
	
}
