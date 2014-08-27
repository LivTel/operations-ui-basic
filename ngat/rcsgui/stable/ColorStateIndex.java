/**
 * 
 */
package ngat.rcsgui.stable;

import java.awt.Dimension;
import java.awt.GridLayout;

import javax.swing.JLabel;
import javax.swing.JPanel;

/**
 * @author eng
 *
 */
public class ColorStateIndex extends JPanel {
	
	public static final Dimension DIMENSION = new Dimension(200, 50);
		
	public ColorStateIndex() {
		super(true);
		setLayout(new GridLayout(4, 4));
		
		add(new JLabel("SOCA/OPS"));
		add(new ColorIndexKey(ColorStatePanel.OPS_SOCA_COLOR));
		add(new JLabel(" WEATHER"));
		add(new ColorIndexKey(ColorStatePanel.BAD_WEATHER_COLOR));
		
		add(new JLabel("BGCA/OPS"));
		add(new ColorIndexKey(ColorStatePanel.OPS_BGCA_COLOR));
		add(new JLabel(" DAYTIME"));
		add(new ColorIndexKey(ColorStatePanel.DAYTIME_COLOR));
		
		add(new JLabel("TOCA/OPS"));
		add(new ColorIndexKey(ColorStatePanel.OPS_TOCA_COLOR));
		add(new JLabel("ENGINEER"));
		add(new ColorIndexKey(ColorStatePanel.ENG_COLOR));
		
		add(new JLabel(" CAL/OPS"));
		add(new ColorIndexKey(ColorStatePanel.OPS_CAL_COLOR));		
		add(new JLabel(" SUSPEND"));
		add(new ColorIndexKey(ColorStatePanel.SYSTEM_SUSPEND_COLOR));
		
	}
	
}
