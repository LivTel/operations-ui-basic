/**
 * 
 */
package ngat.opsgui.base;

import javax.swing.JMenu;

/** The view menu contains a number of checkbox menu items to control whether each
 * perspective is attached to the main gui or in its own frame.
 * @author eng
 *
 */
public class ViewMenu extends JMenu {

	/** Reference to the main gui.*/
	private Gui gui;

	/**
	 * @param gui
	 */
	public ViewMenu(Gui gui) {
		super("View");
		this.gui = gui;
	}
	
	
	
}
