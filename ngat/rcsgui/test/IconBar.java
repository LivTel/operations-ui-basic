/**
 * 
 */
package ngat.rcsgui.test;

import java.awt.Dimension;
import java.awt.FlowLayout;
import java.util.HashMap;
import java.util.Map;
import javax.swing.JButton;
import javax.swing.JPanel;

/**
 * @author eng
 *
 */
public class IconBar extends JPanel {

	public static final int WIDTH_PER_CELL = 24;
	
	private Dimension SIZE;
	
	/** Number of icons to display.*/
	private int numberIcons;
	
	private Map<String, IconButton> icons;
	
	JButton left;
	JButton right;

	/**
	 * @param numberIcons
	 */
	public IconBar(int numberIcons) { //, JButton left, JButton right) {
		super(true);
		this.numberIcons = numberIcons;
		//this.left = left;
		//this.right = right;
		SIZE = new Dimension(WIDTH_PER_CELL*numberIcons, WIDTH_PER_CELL);
		icons = new HashMap<String, IconButton>();
		setPreferredSize(SIZE);
		setLayout(new FlowLayout(FlowLayout.LEFT));
		
	}
	
	/** Add an IconButton to the bar. Returns silently if already added.
	 * @param ibtn The IconButton to add.
	 */
	public void addIcon(IconButton ibtn) {
		if (icons.containsKey(ibtn.getName()))
			return;
		icons.put(ibtn.getName(), ibtn);
		add(ibtn);
		// if got max amount add l and r
	}
	
	public void removeIcon(String btnName) {
		if (! icons.containsKey(btnName))
			return;
		IconButton ibtn = icons.get(btnName);
		icons.remove(btnName);
		remove(ibtn);
		// repaint(); invalidate(), validate(), revalidate().
	}
}
